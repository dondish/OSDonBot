package DonBot.commands.moderation;

import DonBot.api.GenericModerationCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

public class MuteCommand extends GenericModerationCommand {
    public MuteCommand() {
        this.name = "mute";
        this.help = "Mutes a member so they cannot send messages";
        this.usage = "^mute <memberid/mention> <reason>";
        this.casetype = 0;
        this.ID = 514;
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
    }
}
