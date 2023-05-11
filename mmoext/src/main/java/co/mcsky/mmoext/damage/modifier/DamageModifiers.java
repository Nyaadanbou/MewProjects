package co.mcsky.mmoext.damage.modifier;

import io.lumine.mythic.lib.damage.DamageType;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class DamageModifiers implements Iterable<DamageModifier> {

    private final Set<DamageModifier> damageModifiers;

    public DamageModifiers() {
        damageModifiers = new HashSet<>();
    }

    public void addModifier(DamageType damageType, double multiplier) {
        damageModifiers.add(new DamageModifier(damageType, multiplier));
    }

    public void removeModifier(DamageType damageType) {
        damageModifiers.removeIf(mod -> mod.getDamageType().equals(damageType));
    }

    /**
     * @return unmodifiable set of DamageModifiers
     */
    public Set<DamageModifier> getDamageModifiers() {
        return Collections.unmodifiableSet(damageModifiers);
    }

    @NotNull
    @Override
    public Iterator<DamageModifier> iterator() {
        return damageModifiers.iterator();
    }

}