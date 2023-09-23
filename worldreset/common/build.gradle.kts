group = "cc.mewcraft.worldreset"
version = "1.0.0"
description = "Reset server worlds with cron expressions!"

dependencies {
    // server api
    compileOnly(libs.server.paper)

    // plugin libs
    compileOnly(libs.helper)
    compileOnly(libs.papi)
    compileOnly(libs.minipapi)

    // shaded libs
    compileOnly(libs.cronutils)
}
