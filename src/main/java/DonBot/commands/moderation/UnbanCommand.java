package DonBot.commands.moderation;

import DonBot.api.GenericModerationCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

public class UnbanCommand extends GenericModerationCommand {
    public UnbanCommand() {
        this.name = "unban";
        this.aliases = new String[] {"ub"};
        this.help = "Unbans a user";
        this.usage = "^unban <user> <reason>";
        this.casetype = 4;
        this.ID = 519;
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
    }
}
