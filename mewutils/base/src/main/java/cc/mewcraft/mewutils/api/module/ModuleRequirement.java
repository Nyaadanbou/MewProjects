package cc.mewcraft.mewutils.api.module;

public interface ModuleRequirement {

    /**
     * Checks whether this module meets all requirements to be enabled.
     * <p>
     * Defaults to true.
     *
     * @return true if this module has all requirements met
     */
    default boolean checkRequirement() {
        return true;
    }

}
