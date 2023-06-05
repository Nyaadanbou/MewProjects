dependencies {
    compileOnly(project(":proxychatbridge:common"))

    compileOnly(libs.proxy.velocity)
    annotationProcessor(libs.proxy.velocity)

    compileOnly(libs.luckperms)
    compileOnly(libs.adventure.binaryserializer)
}
