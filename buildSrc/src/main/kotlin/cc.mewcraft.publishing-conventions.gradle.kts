plugins {
    `maven-publish`
}

publishing {
    repositories {
        maven(uri("${System.getProperty("user.home")}/MewcraftRepository"))
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
