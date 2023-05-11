
package co.mcsky.mmoext.damage.modifier;

import io.lumine.mythic.api.skills.INoTargetSkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.core.utils.annotations.MythicMechanic;

@MythicMechanic(
        author = "Nailm",
        name = "damagemodifier",
        description = "Add/remove ML damage modifiers to mobs"
)
public class DamageModifierSkill implements INoTargetSkill { // TODO can dynamically add/remove damage modifiers of MM mobs

    @Override
    public SkillResult cast(SkillMetadata skillMetadata) {
        return null;
    }

}