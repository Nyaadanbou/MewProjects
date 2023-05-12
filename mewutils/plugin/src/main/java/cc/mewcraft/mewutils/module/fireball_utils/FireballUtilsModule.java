package cc.mewcraft.mewutils.module.fireball_utils;

import cloud.commandframework.arguments.standard.BooleanArgument;
import cloud.commandframework.arguments.standard.DoubleArgument;
import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.bukkit.parsers.PlayerArgument;
import cc.mewcraft.mewutils.MewUtils;
import cc.mewcraft.mewutils.api.MewPlugin;
import cc.mewcraft.mewutils.api.module.ModuleBase;
import com.google.common.collect.ImmutableMap;
import com.google.inject.Inject;
import me.lucko.helper.Schedulers;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.IntStream;

public class FireballUtilsModule extends ModuleBase {

    public static final String FIREBALL_META_KEY = "mew_fireball_projectile";
    public static final int MAX_SPEED = 8;
    public static final Map<String, Class<? extends Projectile>> types;
    public static final String[] typesArray;
    public static final String[] speedsArray;

    static {
        final ImmutableMap.Builder<String, Class<? extends Projectile>> builder = ImmutableMap.<String, Class<? extends Projectile>>builder()
            .put("fireball", Fireball.class)
            .put("small", SmallFireball.class)
            .put("large", LargeFireball.class)
            .put("arrow", Arrow.class)
            .put("skull", WitherSkull.class)
            .put("egg", Egg.class)
            .put("snowball", Snowball.class)
            .put("expbottle", ThrownExpBottle.class)
            .put("dragon", DragonFireball.class)
            .put("trident", Trident.class);

        types = builder.build();
        typesArray = types.keySet().toArray(String[]::new);
        speedsArray = IntStream.range(0, MAX_SPEED + 1).boxed().map(String::valueOf).toArray(String[]::new);
    }

    @Inject
    public FireballUtilsModule(final MewPlugin parent) {
        super(parent);
    }

    @Override protected void enable() {
        registerCommand(commandRegistry -> commandRegistry
            .commandBuilder("mewutils")
            .permission("mew.admin")
            .literal("fireball")
            .argument(StringArgument.<CommandSender>builder("projectile").withSuggestionsProvider((context, sting) -> List.of(typesArray)))
            .argument(DoubleArgument.<CommandSender>builder("speed").withMax(MAX_SPEED).withSuggestionsProvider((context, string) -> List.of(speedsArray)))
            .argument(BooleanArgument.of("ride"))
            .argument(PlayerArgument.optional("player"))
            .handler(context -> {
                String projectile = types.containsKey((String) context.get("projectile")) ? (String) context.get("projectile") : "fireball";
                double speed = context.get("speed");
                boolean ride = context.get("ride");
                Optional<Player> player = context.getOptional("player");
                if (player.isPresent()) {
                    launch(projectile, speed, ride, player.get());
                } else if (context.getSender() instanceof Player sender) {
                    launch(projectile, speed, ride, sender);
                }
            })
        );
    }

    private void launch(String proj, double speed, boolean ride, LivingEntity player) {
        final Vector direction = player.getEyeLocation().getDirection().multiply(speed);
        final Projectile projectile = player.getWorld().spawn(player.getEyeLocation().add(direction.getX(), direction.getY(), direction.getZ()), types.get(proj));
        projectile.setShooter(player);
        projectile.setVelocity(direction);
        projectile.setMetadata(FIREBALL_META_KEY, new FixedMetadataValue(MewUtils.INSTANCE, true));

        if (ride) {
            projectile.addPassenger(player);
        }

        Schedulers.sync().runLater(projectile::remove, 100).bindWith(this);
    }

    @Override public boolean checkRequirement() {
        return true;
    }

}
