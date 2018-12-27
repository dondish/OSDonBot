package DonBot.commands.moderation;

import DonBot.api.GenericModerationCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

public class SoftbanCommand extends GenericModerationCommand {
    public SoftbanCommand() {
        this.name = "softban";
        this.aliases = new String[] {"sb"};
        this.help = "Bans and then unbans a user to clear his messages.";
        this.usage = "^softban <user> <reason>";
        this.casetype = 5;
        this.ID = 518;
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
    }
}
