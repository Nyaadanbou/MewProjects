plugins {
    id("io.freefair.lombok")
    alias(libs.plugins.indra)
}

dependencies {
    compileOnly(project(":betonquestext:common"))
    compileOnly(libs.brewery)
}

indra {
    javaVersions().target(17)
}