package cc.mewcraft.reforge.gui.object;


/**
 * Represents an abstract ingredient that will be consumed when reforging.
 *
 * @param <T> an object type that the ingredient information is derived from
 */
public interface ReforgeIngredient<T> {
    boolean has(T object);

    void consume(T object);
}
