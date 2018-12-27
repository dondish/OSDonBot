package DonBot.commands.moderation;

import DonBot.api.GenericModerationCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

public class BanCommand extends GenericModerationCommand {
    public BanCommand() {
        this.name = "ban";
        this.aliases = new String[] {"b"};
        this.help = "Bans a user";
        this.usage = "^ban <user> <reason>";
        this.casetype = 1;
        this.ID = 512;
        this.userPermissions = new Permission[]{Permission.BAN_MEMBERS};
        this.botPermissions = new Permission[]{Permission.BAN_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
    }
}
