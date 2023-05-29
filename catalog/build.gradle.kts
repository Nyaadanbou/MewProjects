plugins {
    `version-catalog`
    `maven-publish`
}

catalog {
    versionCatalog {
        // Create this catalog by simply importing the one defined the in the gradle folder
        from(files("../gradle/libs.versions.toml"))
    }
}

publishing {
    repositories {
        maven {
            url = uri("${System.getenv("HOME")}/MewcraftRepository")
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["versionCatalog"])

            group = "cc.mewcraft"
            artifactId = "catalog"
            version = "1.0.0"
            description = "Shared version catalogs"
        }
    }
}
