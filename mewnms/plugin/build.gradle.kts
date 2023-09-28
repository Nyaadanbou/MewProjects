plugins {
    id("cc.mewcraft.deploy-conventions")
    id("cc.mewcraft.paper-plugins")
}

project.ext.set("name", "MewNMS")

dependencies {
    compileOnly(project(":mewcore"))
    compileOnly(libs.helper)
    compileOnly(libs.server.paper)

    implementation(project(":mewnms:api"))
    // implementation(project(":mewnms:v1_19_r3", configuration = "reobf")) // Not used anymore
    implementation(project(":mewnms:v1_20_r1", configuration = "reobf"))
}
