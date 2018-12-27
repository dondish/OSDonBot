package DonBot.commands.moderation;

import DonBot.api.GenericModerationCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

public class UnmuteCommand extends GenericModerationCommand {
    public UnmuteCommand() {
        this.name = "unmute";
        this.help = "Unmutes a user, reason is optional";
        this.usage = "^unmute <user> <reason>";
        this.casetype = 3;
        this.ID = 520;
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
    }
}
