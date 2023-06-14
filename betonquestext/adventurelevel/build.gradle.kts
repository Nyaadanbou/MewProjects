plugins {
    id("io.freefair.lombok")
    alias(libs.plugins.indra)
}

dependencies {
    compileOnly(project(":betonquestext:common"))
    compileOnly(project(":adventurelevel:api"))
}

indra {
    javaVersions().target(17)
}