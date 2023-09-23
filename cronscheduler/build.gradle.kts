plugins {
    id("cc.mewcraft.repo-conventions")
    id("cc.mewcraft.kotlin-conventions")
}

version = "1.0.0"
description = "A Java job scheduler based on cron-utils library. Specially made for Linux cron format!"

dependencies {
    compileOnly(libs.guava)
    compileOnly(libs.cronutils)
    testImplementation(libs.guava)
    testImplementation(libs.cronutils)
}
