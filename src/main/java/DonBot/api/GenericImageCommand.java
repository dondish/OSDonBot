package DonBot.api;

import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.ImageUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.User;

import java.awt.*;
import java.util.List;

public abstract class GenericImageCommand extends DonCommand {

    protected byte type;
    protected String url;
    protected int lx;
    protected int ly;
    protected int bx;
    protected int by;
    protected boolean fillRect;
    protected Color color;
    protected float alpha;

    protected GenericImageCommand() {
        this.cooldown = 5;
        this.category = new Category("image");
        this.botPermissions = new Permission[]{Permission.MESSAGE_ATTACH_FILES, Permission.MESSAGE_WRITE};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        List<User> users = event.getMessage().getMentionedUsers();
        User u;
        if (event.getArgs().equals("")) {
            u = event.getAuthor();
        } else if (users.isEmpty()) {
            List<User> us = event.getJDA().getUsersByName(event.getArgs(), true);
            if (us.isEmpty()) {
                event.reply("No users mentioned and no valid use was entered");
                return;

            } else {
                u = us.get(0);
            }
        } else {
            u = users.get(0);
        }
        String avatarurl = u.getAvatarUrl();
        switch (type) {
            case 0:
                ImageUtils.putAvatarOnImage(event, avatarurl, url, lx, ly, bx, by, fillRect, color);
                break;
            case 1:
                ImageUtils.filterImageOnAvatar(event, avatarurl, url, alpha);
                break;
            case 2:
                ImageUtils.blurpleAvatar(event, avatarurl);
                break;
            case 3:
                ImageUtils.invert(event, avatarurl);
                break;
            case 4:
                ImageUtils.monochrome(event, avatarurl);
                break;
            case 5:
                ImageUtils.warp(event, avatarurl);
                break;
            case 6:
                ImageUtils.thanos(event, avatarurl);
                break;
            default:
                event.reply("Unsupported type.e <DonImageLib Error>");
        }
    }
}
