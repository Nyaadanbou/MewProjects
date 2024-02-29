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
