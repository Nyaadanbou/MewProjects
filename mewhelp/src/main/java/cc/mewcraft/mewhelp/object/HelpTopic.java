package cc.mewcraft.mewhelp.object;

import cc.mewcraft.mewcore.util.UtilComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record HelpTopic(@NotNull String key, @NotNull List<String> messages) {
    public List<Component> components() {
        return UtilComponent.asComponent(messages);
    }

    public List<Component> components(TagResolver resolver) {
        return UtilComponent.asComponent(messages, resolver);
    }
}
