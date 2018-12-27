package DonBot.commands.general;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class TagCommand extends DonCommand {
    public TagCommand() {
        this.name = "tag";
        this.usage = "^tag <add/delete/key_to_get/all> <key>";
        this.aliases = new String[]{"t, tags"};
        this.category = new Category("general");
        this.guildOnly = true;
        this.help = "Tag based commands, `^help tags` for info.";
        this.ID = 136;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
        try {
            switch (event.getArgs().split("\\s+")[0]) {
                case "add":
                    if (event.getArgs().split("\\s+").length >= 3) {
                        List<String> list = Arrays.asList(event.getArgs().split("\\s+"));
                        String key = list.get(1);
                        List<String> tagspl = list.subList(2, list.size());
                        StringBuilder tag = new StringBuilder();
                        for (String s : tagspl) {
                            tag.append(" ").append(s);
                        }
                        provider.addTag(key, tag.toString());
                        event.reply(":ok_hand: Tag added.");
                        break;
                    }
                    event.reply("Correct use: "+this.usage+ " <value>");
                    break;
                case "remove":
                case "delete":
                    if (event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                        String key = event.getArgs().split("\\s+")[1];
                        if (provider.getTag(key) == null) {
                            event.reply(":no_entry_sign: Tag not found.");
                            break;
                        }
                        provider.delTag(key);
                        event.reply(":ok_hand: Tag deleted.");
                        break;
                    }
                    event.reply("You must have manage server permissions to delete tags.");
                    break;
                case "all":
                case "list":
                    List<String[]> result = provider.getAllTags();
                    if (event.getSelfMember().hasPermission(event.getTextChannel(), Permission.MESSAGE_EMBED_LINKS)) {
                        StringBuilder sb = new StringBuilder();
                        for (String[] pair: result) {
                            sb.append(String.format("%s: %s\n",(Object[])(pair)));
                        }
                        if (sb.toString().length() == 0) {
                            event.reply("No tags found for this server :sad:");
                            break;
                        }
                        event.reply(new EmbedBuilder().setTitle("Tags:").setDescription(sb.toString()).setColor(Color.cyan).build());
                        break;
                    }
                    StringBuilder sb = new StringBuilder("All Tags:\n");
                    for (String[] pair: result) {
                        sb.append(String.format("%s: %s\n", (Object[])pair));
                    }
                    if (sb.toString().length() == 0) {
                        event.reply("No tags found for this server :sad:");
                        break;
                    }
                    event.reply(sb.toString());
                    break;
                default:
                    String k = event.getArgs().split("\\s+")[0];
                    String tag = provider.getTag(k);
                    if (tag == null) {
                        event.reply(":no_entry_sign: Tag with key: "+ k + " not found.");
                        break;
                    }
                    event.reply(tag);
            }
        } catch (SQLException e) {e.printStackTrace();}

    }
}
