package com.labijie.application.aot

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import io.github.classgraph.ScanResult
import org.springframework.aot.hint.*
import java.lang.reflect.Modifier
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * @author Anders Xiao
 * @date 2025/6/21
 */
fun TypeHint.Builder.withMembersForJackson(): TypeHint.Builder {
    return this.withMembers(
        MemberCategory.PUBLIC_FIELDS,
        MemberCategory.INVOKE_PUBLIC_METHODS,
        MemberCategory.INVOKE_PUBLIC_CONSTRUCTORS,
        MemberCategory.INTROSPECT_PUBLIC_CONSTRUCTORS,
        MemberCategory.INTROSPECT_PUBLIC_METHODS
    )
}

fun ReflectionHints.registerType(type: KClass<*>, vararg memberCategories: MemberCategory) {
    this.registerType(type.java, *memberCategories)
}

fun ReflectionHints.registerAnnotations(vararg annotationClasses: KClass<*>): ReflectionHints {
    return this.registerTypes(annotationClasses.map {
        TypeReference.of(it.java)
    }) {
        it.withMembers(
            MemberCategory.INVOKE_PUBLIC_METHODS,
            MemberCategory.INTROSPECT_PUBLIC_METHODS
        )
    }
}

fun ReflectionHints.registerForJackson(vararg jacksonObjects: KClass<*>): ReflectionHints {
    return this.registerTypes(jacksonObjects.map {
        TypeReference.of(it.java)
    }) {
        it.withMembersForJackson()
    }
}

fun ReflectionHints.registerPackageForJackson(classInPackage: Class<*>): ReflectionHints {
    val packageName = classInPackage.packageName
    return registerPackageForJackson(packageName)
}

fun ReflectionHints.registerPackageForJackson(vararg packageNames: String): ReflectionHints {
    return registerPackage(packageNames = packageNames, { result ->
        result.allClasses.filter {
            classInfo ->
            if (classInfo.isEnum) {
                return@filter true
            }
            if (classInfo.isInterface || classInfo.isAbstract) {
                return@filter false
            }
            val hasNoArgPublicCtor =
                classInfo.constructorInfo.any { it.parameterInfo.size == 0 && Modifier.isPublic(it.modifiers) }
            if (hasNoArgPublicCtor) {
                return@filter false
            }

            val hasJsonCreatorCtor = classInfo.declaredConstructorInfo.any { ctor ->
                ctor.hasAnnotation(com.fasterxml.jackson.annotation.JsonCreator::class.java)
            }

            return@filter hasJsonCreatorCtor
        }
    }) {
        it.withMembersForJackson()
    }
}

fun ReflectionHints.registerPackage(
    packageName: String,
    classFilter: ((ScanResult) -> ClassInfoList)? = null,
    typeHint: Consumer<TypeHint.Builder>
): ReflectionHints {
    return registerPackage(packageNames = arrayOf(packageName), classFilter, typeHint)
}

fun ReflectionHints.registerPackage(
    packageNames: Array<out String>,
    classFilter: ((ScanResult) -> ClassInfoList)? = null,
    typeHint: Consumer<TypeHint.Builder>
): ReflectionHints {
    val scanResult = ClassGraph()
        .acceptPackages(*packageNames) // 你自己的包
        .enableClassInfo()
        .scan()

    val classes = classFilter?.invoke(scanResult) ?: scanResult.allClasses

    val jacksonUsableClasses = classes
        .mapNotNull { classInfo ->
            try {
                classInfo.loadClass()
            } catch (ex: Throwable) {
                return@mapNotNull null
            }
        }

    registerTypes(jacksonUsableClasses.map { TypeReference.of(it) }, typeHint)
    return this
}

fun ReflectionHints.registerPackage(
    packageClass: Class<*>,
    classFilter: ((ScanResult) -> ClassInfoList)? = null,
    typeHint: Consumer<TypeHint.Builder>
): ReflectionHints {
    val packageName = packageClass.packageName

    return registerPackage(packageNames = arrayOf(packageName), classFilter, typeHint)
}

fun RuntimeHints.registerGitInfo() {
    this.resources().registerPattern("git-info/git.properties")
}