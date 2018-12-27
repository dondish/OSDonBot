package DonBot.commands.other;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.*;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class HelpCommand implements Consumer<CommandEvent> {
    @Override
    public void accept(CommandEvent event) {
        Logger log = LoggerFactory.getLogger(HelpCommand.class);
        if (event.getArgs().equals("")) {
            if (event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
                if (provider.getPrefixes() == null) {
                    return;
                }
                String prefix = provider.getPrefixes().iterator().next();
                EmbedBuilder builder = new EmbedBuilder().setTitle("Hi! I am DonBot!").setColor(Color.cyan);
                builder.setDescription("Hello! I am a multipurpose bot and I have a lot to offer!\n" +
                        "Use " + prefix + "commands all to see all of my commands!\n" +
                        "I can do from music, moderation, and memes to give you information about the world cup!");
                builder.setThumbnail(event.getSelfUser().getEffectiveAvatarUrl());
                builder.addField("Invite Me!", "[Click Me!](https://discordapp.com/api/oauth2/authorize?client_id=420517675509350400&permissions=36924481&scope=bot)", true);
                builder.addField("Website", "[Click Me!](https://donbot.space)", false);
                builder.addField("Dashboard", "[Click me](https://donbot.space/dashboard)", true);
                builder.addField("Support Server", event.getClient().getServerInvite(), false);
                builder.addField("Commands", event.getClient().getCommands().size() + " commands", false);
                builder.addField("Server Prefix", prefix, false);
                event.reply(builder.build());
            } else {
                event.reply("You can ask `^help <Module>` to get the commands of the module or `^help <Command>` for more help on a command!\n" +
                        "You can also use `^commands all` to get all the commands.\n" +
                        "The available modules are **music**, **general**, **fun**, **image**, **currency** and **moderation**.\n" +
                        "For further assistance join the support server: " + event.getClient().getServerInvite());
            }
        } else {
            // Check if the arguements are a command
            for (Command comm : event.getClient().getCommands()) {
                DonCommand command = (DonCommand) comm;
                if (command.getName().equals(event.getArgs())) {
                    EmbedBuilder builder = new EmbedBuilder()
                            .setTitle("Command " + command.getName() + ":")
                            .setDescription(command.getHelp())
                            .addField("Usage", command.usage, true)
                            .setColor(event.getSelfMember().getColor());
                    try {
                        builder.addField("Total Usage", String.valueOf(command.getStats().getTotalUsages()), true);
                        builder.addField("Daily Usage", String.valueOf(command.getStats().getUsagesLastDay()), true);
                    } catch (SQLException ignored) { }
                    event.reply(builder.build());
                    return;
                }
            }
            // it is not a command let's search for a category.
            List<DonCommand> commands= new ArrayList<>();
            for (Command command:event.getClient().getCommands()) {
                if (command.getCategory() == null) {
                    continue;
                }
                if (command.getCategory().getName().equals(event.getArgs())) {
                    commands.add((DonCommand) command);
                }
            }
            if (!commands.isEmpty()) {
                EmbedBuilder builder = new EmbedBuilder().setTitle("Module **" + commands.get(0).getCategory().getName() + "**");
                StringBuilder s = new StringBuilder();
                for (DonCommand command : commands) {
                    s.append("`^").append(command.getName()).append("`  ").append(command.getHelp()).append("\n");
                }
                s.append("\nUse `^help <Command>` for more info about each command!");
                builder.setDescription(s.toString())
                        .setColor(event.getSelfMember().getColor());
                try {
                    builder.addField("Daily Usage", String.valueOf(commands.get(0).getStats().getModuleUsagesLastDay()), true);
                    builder.addField("Total Usage", String.valueOf(commands.get(0).getStats().getTotalModuleUsages()),true);
                } catch (SQLException ignored) {}
                event.reply(builder.build());
            } else {
                event.reply("Module or command " + event.getArgs() + " not found.\nuse `^help` for more info.");
            }
        }
    }
}
