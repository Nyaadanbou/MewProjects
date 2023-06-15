package cc.mewcraft.townylink.messager;

import java.util.List;

public class EmptyMessenger implements Messenger {

    @Override public void sendMessage(final String action, final List<String> data) {}

}
