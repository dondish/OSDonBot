package DonBot.commands.fun;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;

import java.awt.*;

public class CookieCommand extends DonCommand {
    private String[] cookiegifs = new String[]{
            "https://media.giphy.com/media/hpfInSxkkLdpS/giphy.gif", "https://media.giphy.com/media/14mTI5MQbroFIA/giphy.gif",
            "https://media.giphy.com/media/bAlYQOugzX9sY/giphy.gif", "https://media.giphy.com/media/l3vRmjIZpfYp8MLwA/giphy.gif",
            "https://media.giphy.com/media/6Nt5hRfsh3g4g/giphy.gif", "https://media.giphy.com/media/3oKIPg389gX7QPRYs0/giphy.gif",
            "https://media.giphy.com/media/a1hawnhMgx212/giphy.gif", "https://media.giphy.com/media/S3HBZs20Up1UQ/giphy.gif"};
    private String[] cookieTitles = new String[]{
            "Here is a cookie for you %!", "Hey! %! The cookies are out of the oven for you!",
            "Come on %! Eat some cookies!"
    };
    public CookieCommand() {
        this.name = "cookie";
        this.usage = "^cookie <user mention>";
        this.help = "Give someone a cookie!";
        this.category = new Category("fun");
        this.botPermissions = new Permission[] {Permission.MESSAGE_EMBED_LINKS};
        this.guildOnly = true;
        this.ID = 65;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (event.getMessage().getMentionedUsers().isEmpty()) {
            event.reply("You have to mention someone!");
            return;
        }
        String gif = cookiegifs[(int)(Math.random()*cookiegifs.length)];
        String title = cookieTitles[(int)(Math.random()*cookieTitles.length)].replaceAll("%", event.getMessage().getMentionedUsers().get(0).getName());
        event.reply(new EmbedBuilder().setColor(Color.ORANGE).setTitle(title).setImage(gif).build());
    }
}
