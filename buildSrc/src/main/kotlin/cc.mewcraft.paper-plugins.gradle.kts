// plugins {
//     id("net.minecrell.plugin-yml.paper")
// }

plugins {
    `java-library`
}

tasks {
    // Process the paper-plugin.yml file using gradle scripts
    // TODO switch to plugin-yml when it supports new dependency format
    processResources {
        filesMatching("**/paper-plugin.yml") {
            val properties = mapOf(
                "version" to "${project.version}",
                "description" to project.description
            )
            expand(properties)
        }
    }
}