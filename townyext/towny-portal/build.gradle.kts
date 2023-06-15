plugins {
    id("cc.mewcraft.bettergui-plugins")
}

project.ext.set("name", "TownyPortal")

group = "cc.mewcraft.townyportal"
version = "1.1"
description = "Enhance the communication between towns and nations"

dependencies {
    compileOnly(libs.towny)
    compileOnly("me.hsgamer.bettergui", "MaskedGUI", "2.2-SNAPSHOT")
}
