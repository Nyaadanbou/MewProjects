package cc.mewcraft.nms;

import me.lucko.helper.plugin.ExtendedJavaPlugin;
import me.lucko.helper.reflect.MinecraftVersion;

public class MewNMSPlugin extends ExtendedJavaPlugin {
    @Override protected void load() {
        MinecraftVersion runtimeVersion = MinecraftVersion.getRuntimeVersion();
        if /*(runtimeVersion.equals(MinecraftVersion.of(1, 19, 4))) {
            MewNMSProvider.register(new V1_19_R3());
        } else if*/ (runtimeVersion.equals(MinecraftVersion.of(1, 20, 1))) {
            MewNMSProvider.register(new V1_20_R1());
        } else {
            getSLF4JLogger().error("There is no implementation on this runtime Minecraft version");
        }
    }
}
