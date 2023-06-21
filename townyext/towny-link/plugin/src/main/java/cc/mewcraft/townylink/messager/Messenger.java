package cc.mewcraft.townylink.messager;

import java.util.Arrays;
import java.util.List;

public interface Messenger {

    /**
     * Sends message to other Towny servers, with specific action and data.
     *
     * @param action an {@link Action}
     * @param data   optional data to send
     */
    void sendMessage(String action, List<String> data);

    default void sendMessage(String action, String... data) {
        sendMessage(action, Arrays.asList(data));
    }

}
