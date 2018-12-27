package DonBot.commands.image;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class AvatarCommand extends DonCommand {
    public AvatarCommand() {
        this.name = "avatar";
        this.usage = "^avatar <@mention/name>";
        this.botPermissions = new Permission[]{Permission.MESSAGE_EMBED_LINKS, Permission.MESSAGE_WRITE};
        this.help = "shows you an avatar of a user";
        this.category = new Category("image");
        this.ID = 257;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        List<User> users = event.getMessage().getMentionedUsers();
        User u;
        if (event.getArgs().equals("")) {
            u = event.getAuthor();
        } else if (users.size() == 0) {
            u = event.getJDA().getUsersByName(event.getArgs(), true).get(0);
            if (u == null) {
                event.reply("No users mentioned and no valid use was entered");
                return;
            }
        } else {
            u = users.get(0);
        }
        String avatarurl = u.getAvatarUrl();
        event.reply(new EmbedBuilder().setColor(event.getSelfMember().getColor()).setImage(avatarurl).build());
    }
}
