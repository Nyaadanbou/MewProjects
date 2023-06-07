package cc.mewcraft.mythiclibext.object;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * This class keeps the status of whether ItemsAdder has fully loaded its data.
 */
public final class ItemsAdderStatus {

    private static final AtomicBoolean complete = new AtomicBoolean(false);
    private static final AtomicInteger completeCount = new AtomicInteger(0);

    /**
     * Marks ItemsAdder as fully loaded, incrementing {@link #completeCount} by 1.
     */
    public static void markAsComplete() {
        complete.set(true);
        completeCount.incrementAndGet();
    }

    /**
     * Marks ItemsAdder as not fully loaded.
     */
    public static void markAsIncomplete() {
        complete.set(false);
    }

    /**
     * @return true if ItemsAdder has fully loaded its data; otherwise false
     */
    public static boolean complete() {
        return complete.get();
    }

    /**
     * @return the number of times that ItemsAdder has loaded its data
     */
    public static int completeCount() {
        return completeCount.get();
    }

    private ItemsAdderStatus() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
