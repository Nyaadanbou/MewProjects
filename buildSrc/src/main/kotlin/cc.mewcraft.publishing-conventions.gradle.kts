plugins {
    `maven-publish`
}

publishing {
    repositories {
        maven {
            url = uri("${System.getenv("HOME")}/MewcraftRepository")
        }
    }
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}