package DonBot.commands.currency;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class AddMoneyCommand extends DonCommand {
    public AddMoneyCommand() {
        this.name = "addmoney";
        this.ownerCommand = true;
        this.guildOnly = false;
        this.hidden = true;
        this.ID = 0;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            int cash = provider.getCash(Long.parseLong(event.getArgs().split("\\s+")[0]));
            provider.updateCash(Long.parseLong(event.getArgs().split("\\s+")[0]), cash + Integer.parseInt(event.getArgs().split("\\s+")[1]));
            event.reply(":ok_hand:");
        } catch (SQLException e) {
            event.reply(e.getMessage());
        }
    }
}
