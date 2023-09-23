plugins {
    // id("cc.mewcraft.deploy-conventions")
}

project.ext.set("name", "WorldReset")

group = "cc.mewcraft.worldreset"
version = "1.0.0"
description = "Reset server worlds with cron expressions!"

dependencies {
    // server api
    compileOnly(libs.proxy.velocity)

    // libs that present as other plugins
    compileOnly(libs.minipapi)
}
