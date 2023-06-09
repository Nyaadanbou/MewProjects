package cc.mewcraft.betonquest.variable;

import org.betonquest.betonquest.exceptions.QuestRuntimeException;

public interface ObjectResolver<T, R> {

    /**
     * <p>Resolves specified name which references an object.
     * <p>The name should not contain any unresolved variables.
     *
     * @param name an implementation-defined name to reference the object
     *
     * @return the resolved object
     *
     * @throws QuestRuntimeException if the implementation fails to resolve the object by the name
     */
    R resolve(T name) throws QuestRuntimeException;

}
