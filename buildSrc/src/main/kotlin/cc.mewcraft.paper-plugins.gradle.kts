plugins {
    `java-library`
}

tasks {
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