package cc.mewcraft.townylink.messager;

import java.util.Comparator;
import java.util.stream.Stream;

public final class Action {
    public static final String ADD_TOWN = "add_town";
    public static final String ADD_NATION = "add_nation";
    public static final String DELETE_TOWN = "delete_town";
    public static final String DELETE_NATION = "delete_nation";
    public static final String FETCH_TOWN = "fetch_towns";
    public static final String FETCH_NATION = "fetch_nations";
    public static final int LONGEST_STR;

    static { // Get the longest string length for nicer console logs
        LONGEST_STR = Stream.of(
            ADD_TOWN, ADD_NATION, DELETE_TOWN, DELETE_NATION, FETCH_TOWN, FETCH_NATION
        ).map(String::length).max(Comparator.naturalOrder()).orElse(0);
    }

    private Action() {}
}
