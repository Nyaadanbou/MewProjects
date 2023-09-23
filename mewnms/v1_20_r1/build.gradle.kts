plugins {
    alias(libs.plugins.paperdev)
}

dependencies {
    compileOnly(project(":mewnms:api"))
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)

    paperweight.paperDevBundle("1.20.1-R0.1-SNAPSHOT")
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }
    /*reobfJar {
        outputJar.set(layout.buildDirectory.file("${ext.get("name")}-${project.version}.jar"))
    }*/
}
