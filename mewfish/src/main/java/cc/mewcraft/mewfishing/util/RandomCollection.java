package cc.mewcraft.mewfishing.util;

import me.lucko.helper.random.Weighted;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A collection where the next value can be obtained at Random.
 *
 * <p>The way this works is that each item has a weight. Everytime an item is
 * added to the collection, the weight is added to the total weight value of the collection. When the {@link #pick()}
 * method is called is calculates the probability of each item by the weight/total_weight.</p>
 *
 * <p>So if one item has a weight of 10 and another of 20, the total weight is 30. The first item has a
 * 33% chance of being selected while the second item has a 66% of spawning. If a third item is added with a weight of
 * 5, these would be the probability table:</p>
 * <table>
 *     <caption>Example</caption>
 *     <tr>
 *         <th>Item One</th>
 *         <th>Item Two</th>
 *         <th>Item Three</th>
 *         <th>Total Weight</th>
 *     </tr>
 *     <tr>
 *         <td>10/35 = 28.6%</td>
 *         <td>20/35 = 57.7%</td>
 *         <td>5/35 = 16.7%</td>
 *         <td>35</td>
 *     </tr>
 * </table>
 *
 * @param <E> The type of collection.
 */
public class RandomCollection<E extends Weighted> {
    private final NavigableMap<Double, E> map;
    private final Random random;
    private double total = 0;

    /**
     * Construct the random collection with a new random.
     */
    public RandomCollection() {
        this(ThreadLocalRandom.current());
    }

    /**
     * Construct random collection with an existing random.
     *
     * @param random The random.
     */
    public RandomCollection(Random random) {
        this.random = random;
        this.map = new TreeMap<>();
    }

    private RandomCollection(NavigableMap<Double, E> map, double total) {
        this.map = map;
        this.random = ThreadLocalRandom.current();
        this.total = total;
    }

    /**
     * Add a value to the RandomCollection.
     *
     * <p>View the class description for a detailed explanation of how the
     * probability of this collection works.</p>
     *
     * @param result The item to add.
     *
     * @return The instance of this collection.
     *
     * @see #addAll(Collection)
     */
    public RandomCollection<E> add(E result) {
        double weight = result.getWeight();
        if (weight <= 0)
            return this;
        total += weight;
        map.put(total, result);
        return this;
    }

    public RandomCollection<E> addAll(Collection<E> collection) {
        for (final E entry : collection) {
            double weight = entry.getWeight();
            if (weight <= 0) {
                continue;
            }
            total += weight;
            map.put(total, entry);
        }
        return this;
    }

    /**
     * Get the next value randomly based upon defined probabilities.
     *
     * <p>View the class description for a detailed explanation of how the
     * probability of this collection works.</p>
     *
     * @return The next value.
     */
    public E pick() {
        double key = random.nextDouble() * total;
        return map.higherEntry(key).getValue();
    }

    /**
     * Remove and return the next value randomly based upon defined probabilities.
     *
     * <p>View the class description for a detailed explanation of how the
     * probability of this collection works.</p>
     *
     * @return The next value.
     */
    public E poll() {
        double key = random.nextDouble() * total;
        Double remove = map.higherEntry(key).getKey();
        E value = map.remove(remove);
        total -= remove;
        return value;
    }

    /**
     * Check if the collection is empty.
     *
     * @return If the collection is empty.
     */
    public boolean isEmpty() {
        return map.isEmpty();
    }

    /**
     * Get the internal map.
     *
     * @return The internal map.
     */
    public Map<Double, E> getMap() {
        return map;
    }

    /**
     * Get a copy of this collection.
     *
     * @return a copy of this collection
     */
    public RandomCollection<E> copy() {
        return new RandomCollection<>(new TreeMap<>(map), total);
    }

}