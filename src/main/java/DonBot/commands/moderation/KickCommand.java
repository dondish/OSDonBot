package DonBot.commands.moderation;

import DonBot.api.GenericModerationCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

public class KickCommand extends GenericModerationCommand {
    public KickCommand() {
        this.name = "kick";
        this.aliases = new String[] {"k"};
        this.help = "Kicks a user";
        this.usage = "^kick <user> <reason>";
        this.casetype = 2;
        this.ID = 513;
        this.userPermissions = new Permission[]{Permission.KICK_MEMBERS};
        this.botPermissions = new Permission[]{Permission.KICK_MEMBERS};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
    }
}
