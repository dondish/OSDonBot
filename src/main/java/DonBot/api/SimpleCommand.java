package DonBot.api;

import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.TextChannel;

public class SimpleCommand extends DonCommand {
    private String reply;
    private boolean mention;

    public SimpleCommand(String name, String help, String reply, String cat, boolean nsfw) {
        this.name = name;
        this.usage = "^"+name;
        this.help = help;
        this.category = new Category(cat);
        this.guildOnly = true;
        this.reply = reply;
        this.nsfw = nsfw;
    }
    public SimpleCommand(String name, String help, String reply, String cat, boolean mention, boolean nsfw) {
        this.name = name;
        this.usage = "^"+name;
        this.help = help;
        this.category = new Category(cat);
        this.guildOnly = true;
        this.reply = reply;
        this.mention = mention;
        this.nsfw = nsfw;
    }


    @Override
    protected void execute(CommandEvent event) {
        if (nsfw && event.getChannelType().isGuild() && !((TextChannel) event.getChannel()).isNSFW()) {
            event.reply("This is not a nsfw channel. you can't use this command here.");
            return;
        }
        reply = reply.replaceAll("%USERMENTION%", mention ? (event.getMessage().getMentionedUsers().isEmpty() ? "You must mention a user!" : event.getMessage().getMentionedUsers().get(0).getAsMention()) : "");

        event.reply(reply);
    }
}
