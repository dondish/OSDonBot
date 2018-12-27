package DonBot.commands.moderation;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

import java.sql.SQLException;

public class WarnCommand extends DonCommand {
    public WarnCommand() {
        this.name = "warn";
        this.help = "Warning based commands.";
        this.usage = "^warn <@user mention/amount @user mention/clear @user mention>";
        this.category = new Category("moderation");
        this.guildOnly = true;
        this.ID = 521;
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            if (event.getMessage().getMentionedUsers().size()==0)
                event.reply("You must tag who you want to warn or check warnings of.");
            long user = event.getMessage().getMentionedMembers().get(0).getUser().getIdLong();
            switch (event.getArgs().split("\\s+")[0]) {
                case "clear":
                    provider.clearWarnings(user);
                    event.reply(":ok_hand: Warnings removed!");
                    break;
                case "amount":
                    int amount = provider.getWarnings(user);
                    event.reply("User: " + event.getMessage().getMentionedUsers().get(0).getAsMention() + " has " + amount + " warnings.");
                    break;
                default:
                    provider.addWarning(user);
                    event.reply(":ok_hand: Warnings added!");
                    break;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
