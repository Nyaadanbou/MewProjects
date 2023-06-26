repositories {
    maven(uri("${System.getenv("HOME")}/MewcraftRepository"))

    // Locally cached projects TODO Move to the MewcraftRepository
    mavenLocal {
        content {
            includeGroup("io.lumine") // MythicLib
            includeGroup("net.Indyuce") // MMOItems
            includeGroup("net.leonardo_dgs") // InteractiveBooks
            includeGroup("com.github.DieReicheErethons") // Brewery
            includeGroup("su.nightexpress.gamepoints") // GamePoints
            includeGroup("at.helpch") // ChatChat
            includeGroup("me.hsgamer.bettergui") // MaskedGUI
        }
    }

    mavenCentral()

    // Purpur MC
    maven("https://repo.purpurmc.org/snapshots") {
        content {
            includeGroup("org.purpurmc.purpur")
        }
    }

    // Paper MC
    maven("https://repo.papermc.io/repository/maven-public/") {
        content {
            includeGroup("com.velocitypowered")
            includeGroup("io.papermc.paper")
            includeGroup("com.mojang")
            includeGroup("net.md-5")
        }
    }

    // Lucko my god
    maven("https://repo.lucko.me") {
        content {
            includeGroup("me.lucko")
        }
    }

    // ProtocolLib
    maven("https://repo.dmulloy2.net/repository/public/") {
        content {
            includeGroup("com.comphenix.protocol")
        }
    }

    // Phoenix616's stuff
    maven("https://repo.minebench.de/") {
        content {
            includeGroup("de.themoep.utils")
            includeGroup("de.themoep.connectorplugin")
        }
    }

    // All projects hosted on jitpack
    maven("https://jitpack.io") {
        content {
            includeGroup("com.github.simplix-softworks") // SimplixStorage
            includeGroup("com.github.TownyAdvanced")
            includeGroup("com.github.MilkBowl")
            includeGroup("com.github.LoneDev6")
            includeGroup("com.github.Moulberry") // adventure-binary-serializer
            includeGroup("com.github.TechFortress") // GriefPrevention
            includeGroup("com.github.angeschossen") // LandAPI
        }
    }

    // Towny
    maven("https://repo.glaremasters.me/repository/towny/") {
        content {
            includeGroup("com.palmergames.bukkit.towny")
        }
    }

    // MMO plugins
    maven("https://mvn.lumine.io/repository/maven-public/") {
        content {
            includeGroup("io.lumine")
        }
    }
    maven("https://nexus.phoenixdevt.fr/repository/maven-public/") {
        mavenContent {
            includeGroup("io.lumine")
        }
    }

    // WorldEdit & WorldGuard
    maven("https://maven.enginehub.org/repo/") {
        content {
            includeGroup("com.sk89q.worldguard")
            includeGroup("com.sk89q.worldguard.worldguard-libs")
            includeGroup("com.sk89q.worldedit")
            includeGroup("com.sk89q.worldedit.worldedit-libs")
        }
    }

    // NPCs
    maven("https://repo.citizensnpcs.co/#/") {
        content {
            includeGroup("net.citizensnpcs")
        }
    }

    // Placeholders
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/") {
        content {
            includeGroup("me.clip")
        }
    }

    // Bedrock bridge
    maven("https://repo.opencollab.dev/maven-snapshots/") {
        content {
            includeGroup("org.geysermc.floodgate")
        }
    }

    // EssentialsX
    maven("https://repo.essentialsx.net/releases/") {
        mavenContent {
            includeGroup("net.essentialsx")
        }
    }

    // AnvilGUI
    maven("https://repo.codemc.io/repository/maven-snapshots/") {
        content {
            includeGroup("net.wesjd")
        }
    }

    // BetterGUI
    maven("https://repo.codemc.io/repository/maven-public/") {
        content {
            includeGroup("me.hsgamer")
            includeGroup("me.hsgamer.bettergui")
        }
    }

    // NoCheatPlus
    maven("https://repo.md-5.net/content/repositories/snapshots/") {
        // See doc: https://github.com/NoCheatPlus/Docs/wiki/API
        content {
            includeGroup("fr.neatmonster")
        }
    }

    // BetonQuest
    maven("https://betonquest.org/nexus/repository/betonquest/") {
        content {
            includeGroup("org.betonquest")
        }
    }

    // HuskHomes, HuskTowns, HuskSync
    maven("https://repo.william278.net/releases") {
        content {
            includeGroup("net.william278")
        }
    }

    // Nova, InvGUI
    maven("https://repo.xenondevs.xyz/releases") {
        content {
            includeGroup("xyz.xenondevs.invui")
        }
    }
}
