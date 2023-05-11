package co.mcsky.mmoext.damage.modifier;

import org.bukkit.entity.Entity;

/**
 * This class manages the <b>dynamic</b> modifiers applied to MM mobs.
 * <p>
 * Dynamic modifiers are applied through skills or commands.
 */
public class DDamageModifierManager { // TODO

    private static DDamageModifierManager INSTANCE;

    public static DDamageModifierManager getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new DDamageModifierManager();
        }
        return INSTANCE;
    }

    public DamageModifiers getModifiers(Entity entity) {
        return new DamageModifiers();
    }

    public void addModifier(Entity entity, DamageModifier DamageModifier) {

    }

}