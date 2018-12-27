package DonBot.commands.other;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

public class CommandsCommand extends DonCommand {
    public CommandsCommand() {
        this.name = "commands";
        this.ID = 2056;
    }

    private List<Command> filterCommands(List<Command> commands, String category) {
        commands = new ArrayList<>(commands);
        commands.removeIf((command -> command.getCategory() == null || !command.getCategory().getName().equals(category)));
        return commands;
    }

    private List<String> getCommandNames(List<Command> commands) {
        List<String> a = new ArrayList<>();
        for (Command command: commands) {
            a.add(command.getName());
        }
        return a;
    }

    private void addFieldCategory(EmbedBuilder embed, CommandClient client, String name, String category) {
        embed.addField(name, String.join(", ", getCommandNames(filterCommands(client.getCommands(), category))), false);
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (!event.getArgs().equals("all")){
            new HelpCommand().accept(event);
        } else {
            if (event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                EmbedBuilder builder = new EmbedBuilder().setTitle("All Commands").setColor(Color.cyan);
                addFieldCategory(builder, event.getClient(), "✨General✨", "general");
                addFieldCategory(builder, event.getClient(), ":notes:Music:notes:", "music");
                addFieldCategory(builder, event.getClient(), ":grinning:Fun:grinning:", "fun");
                addFieldCategory(builder, event.getClient(), ":lock:Moderation:lock:", "moderation");
                addFieldCategory(builder, event.getClient(), ":camera:Image:camera:", "image");
                addFieldCategory(builder, event.getClient(), ":moneybag:Currency:moneybag:", "currency");
                builder.setDescription("use ^help <command> for more info about each command.\n" +
                        "use ^help <module> for more info about each module");
                event.reply(builder.build());
            } else {
                StringBuilder sb = new StringBuilder();
                sb.append("All Commands:\n\n");
                sb.append("✨General✨\n");
                for (Command command : event.getClient().getCommands()) {
                    if (command.getCategory() != null && command.getCategory().getName().equals("general")) {
                        sb.append("`^").append(command.getName()).append("`  ").append(command.getHelp()).append("\n");
                    }
                }
                sb.append("\n:notes:Music:notes:\n");
                for (Command command : event.getClient().getCommands()) {
                    if (command.getCategory() != null && command.getCategory().getName().equals("music")) {
                        sb.append("`^").append(command.getName()).append("`  ").append(command.getHelp()).append("\n");
                    }
                }
                sb.append("\n:grinning:Fun:grinning:\n");
                for (Command command : event.getClient().getCommands()) {
                    if (command.getCategory() != null && command.getCategory().getName().equals("fun")) {
                        sb.append("`^").append(command.getName()).append("`  ").append(command.getHelp()).append("\n");
                    }
                }
                sb.append("\n:lock:Moderation:lock:\n");
                for (Command command : event.getClient().getCommands()) {
                    if (command.getCategory() != null && command.getCategory().getName().equals("moderation")) {
                        sb.append("`^").append(command.getName()).append("`  ").append(command.getHelp()).append("\n");
                    }
                }
                sb.append("\n:camera:Image:camera:\n");
                for (Command command : event.getClient().getCommands()) {
                    if (command.getCategory() != null && command.getCategory().getName().equals("image")) {
                        sb.append("`^").append(command.getName()).append("`  ").append(command.getHelp()).append("\n");
                    }
                }
                sb.append("\n:moneybag:Currency:moneybag:\n");
                for (Command command : event.getClient().getCommands()) {
                    if (command.getCategory() != null && command.getCategory().getName().equals("currency")) {
                        sb.append("`^").append(command.getName()).append("`  ").append(command.getHelp()).append("\n");
                    }
                }
                sb.append("\nuse `^help command` for more info about each command.");
                event.reply(sb.toString());
            }
        }
    }
}
