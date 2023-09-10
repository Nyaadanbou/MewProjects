plugins {
    id("cc.mewcraft.java-conventions")
    id("cc.mewcraft.repository-conventions")
}

project.ext.set("name", "CronScheduler")

version = "1.0.0"
description = "A Java job scheduler based on cron-utils library. Specially made for Linux cron format!"

dependencies {
    compileOnly(libs.guava)
    compileOnly(libs.cronutils)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.guava)
    testImplementation(libs.cronutils)
}
