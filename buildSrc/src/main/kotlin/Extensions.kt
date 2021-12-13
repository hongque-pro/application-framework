import org.gradle.api.Project
import org.gradle.language.jvm.tasks.ProcessResources

/**
 *
 * @Author: Anders Xiao
 * @Date: 2021/12/13
 * @Description:
 */
inline fun <reified C> Project.configureTask(name: String, configuration: C.() -> Unit) {
    (this.tasks.getByName(name) as C).configuration()
}

fun Project.processResources(configure: ProcessResources.() -> Unit) {
    this.configureTask(name ="processResources", configuration = configure)
}