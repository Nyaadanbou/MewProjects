package cc.mewcraft.mewhelp;

import me.lucko.helper.Commands;
import me.lucko.helper.terminable.TerminableConsumer;
import me.lucko.helper.terminable.module.TerminableModule;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

public class HelpCommand implements TerminableModule {

    private static final String PERM_ADMIN = "mewhelp.admin";
    private static final String PERM_HELP_PREFIX = "mewhelp.help.";

    @Override
    public void setup(@NotNull TerminableConsumer consumer) {

        Commands.create()
                .tabHandler(context -> {
                    if (context.args().size() > 1)
                        return List.of();

                    List<String> completions = new ArrayList<>(MewHelp.p.config.getCommandLabels());

                    CommandSender sender = context.sender();
                    if (sender.hasPermission(PERM_ADMIN))
                        completions.add("reload");

                    Optional<String> parse = context.arg(0).parse(String.class);
                    if (parse.isPresent()) {
                        return completions
                            .stream()
                            .filter(c -> c.startsWith(parse.get()) && sender.hasPermission(ofHelpPermission(c)))
                            .toList();
                    } else {
                        return List.of();
                    }
                })
                .handler(s -> {
                    if (s.args().isEmpty()) {
                        MewHelp.p.messages.info(s.sender(), "useTabforHelp", "command", s.label());
                        return;
                    }

                    String rawLabel = s.arg(0).parseOrFail(String.class);
                    String label = rawLabel.trim().toLowerCase(Locale.ROOT);

                    // it's a reload command
                    if (label.equalsIgnoreCase("reload")) {
                        MewHelp.p.reload();
                        MewHelp.p.messages.info(s.sender(), "reloadedConfig");
                        return;
                    }

                    // it's some help command
                    List<Component> helpMessages = MewHelp.p.config.getHelpMessageMap().get(label);
                    if (helpMessages != null && s.sender().hasPermission(ofHelpPermission(label))) {
                        helpMessages.forEach(m -> s.sender().sendMessage(m));
                    } else {
                        MewHelp.p.messages.info(s.sender(), "useTabforHelp", "command", s.label());
                    }
                })
                .registerAndBind(consumer, "mewhelp", "help", "how");

    }

    private String ofHelpPermission(String label) {
        return PERM_HELP_PREFIX + label;
    }
}
