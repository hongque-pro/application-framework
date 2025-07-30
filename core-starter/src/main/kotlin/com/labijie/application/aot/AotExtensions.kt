package com.labijie.application.aot

import io.github.classgraph.ClassGraph
import io.github.classgraph.ClassInfoList
import io.github.classgraph.ScanResult
import org.springframework.aot.hint.*
import java.lang.annotation.RetentionPolicy
import java.lang.reflect.Type
import java.util.function.Consumer
import kotlin.reflect.KClass

/**
 * @author Anders Xiao
 * @date 2025/6/21
 */


fun ReflectionHints.registerType(type: KClass<*>, vararg memberCategories: MemberCategory) {
    this.registerType(type.java, *memberCategories)
}

fun ReflectionHints.registerType(className: String, vararg memberCategories: MemberCategory) {
    this.registerType(TypeReference.of(className), *memberCategories)
}

fun ReflectionHints.registerAnnotations(vararg annotationClasses: KClass<*>): ReflectionHints {
    return registerAnnotations(*annotationClasses.map { it.java }.toTypedArray())
}

fun ReflectionHints.registerAnnotations(vararg annotationClasses: Class<*>): ReflectionHints {
    val visited = mutableSetOf<Type>()

    // 判断是否是注解且具有运行时保留策略（Java或Kotlin）
    fun Class<*>.isRuntimeRetentionAnnotation(): Boolean {
        if (!this.isAnnotation) return false

        //kotlin 默认保留 runtime
        val kotlinRetention = this.getAnnotation(Retention::class.java)?.value ?: AnnotationRetention.RUNTIME

        //java 默认保留 class
        val javaRetention = this.getAnnotation(java.lang.annotation.Retention::class.java)?.value ?: RetentionPolicy.CLASS

        val javaRuntime = javaRetention == RetentionPolicy.RUNTIME
        val kotlinRuntime = kotlinRetention == AnnotationRetention.RUNTIME

        return javaRuntime || kotlinRuntime
    }

    fun ReflectionHints.registerAnnotationRecursive(clazz: Class<*>): ReflectionHints {
        if (!clazz.isRuntimeRetentionAnnotation() || !visited.add(clazz)) return this

        this.registerType(TypeReference.of(clazz)) {
            it.withMembers(
                MemberCategory.INVOKE_PUBLIC_METHODS,
                MemberCategory.INTROSPECT_PUBLIC_METHODS,
            )
        }

        // 递归注册元注解，排除 JDK/Kotlin 标准注解
        clazz.annotations
            .map { it.annotationClass.java }
            .filter { it.isRuntimeRetentionAnnotation() }
            .filterNot { it.name.startsWith("java.lang.annotation.") || it.name.startsWith("kotlin.annotation.") }
            .forEach { this.registerAnnotationRecursive(it) }

        return this
    }

    annotationClasses.forEach { this.registerAnnotationRecursive(it) }
    return this
}

fun ReflectionHints.registerForJackson(vararg jacksonObjects: KClass<*>): ReflectionHints {
    BindingReflectionHintsRegistrar().registerReflectionHints(this, *jacksonObjects.map { it.java }.toTypedArray())
    return this
}

fun ReflectionHints.registerPackageForJackson(classInPackage: Class<*>): ReflectionHints {
    val packageName = classInPackage.packageName
    return registerPackageForJackson(packageName)
}

fun ReflectionHints.registerPackageForJackson(vararg packageNames: String): ReflectionHints {
    return registerPackage(packageNames = packageNames, classFilter = null, typeHint =  null)
}

fun ReflectionHints.registerPackageForJackson(packageNames: Collection<String>, classFilter: ((ScanResult) -> ClassInfoList)? = null): ReflectionHints {
    return registerPackage(packageNames = packageNames.toTypedArray(), classFilter = classFilter, typeHint =  null)
}


fun ReflectionHints.registerPackage(
    packageNames: Array<out String>,
    classFilter: ((ScanResult) -> ClassInfoList)? = null,
    typeHint: Consumer<TypeHint.Builder>? = null
): ReflectionHints {
    val scanResult = ClassGraph()
        .acceptPackages(*packageNames) // 你自己的包
        .enableClassInfo()
        .scan()

    val classes = classFilter?.invoke(scanResult) ?: scanResult.allClasses

    val classesOnPath = classes
        .mapNotNull { classInfo ->
            try {
                classInfo.loadClass()
            } catch (ex: Throwable) {
                return@mapNotNull null
            }
        }

    if(typeHint == null) {
        BindingReflectionHintsRegistrar().registerReflectionHints(this, *classesOnPath.toTypedArray())
    }else {
        registerTypes(classesOnPath.map { TypeReference.of(it) }, typeHint)
    }
    return this
}

fun RuntimeHints.registerGitInfo() {
    this.resources().registerPattern("git-info/git.properties")
}