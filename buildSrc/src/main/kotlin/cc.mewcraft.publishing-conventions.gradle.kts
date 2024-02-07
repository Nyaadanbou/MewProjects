plugins {
    `maven-publish`
}

publishing {
    repositories {
        maven {
            url = uri("${Versions.UserHome}/MewcraftRepository")
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}
