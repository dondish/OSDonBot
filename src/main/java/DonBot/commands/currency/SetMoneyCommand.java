package DonBot.commands.currency;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class SetMoneyCommand extends DonCommand {
    public SetMoneyCommand() {
        this.name = "setmoney";
        this.ownerCommand = true;
        this.guildOnly = false;
        this.hidden = true;
        this.ID = 5;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            provider.updateCash(Long.parseLong(event.getArgs().split("\\s+")[0]), Integer.parseInt(event.getArgs().split("\\s+")[1]));
            event.reply(":ok_hand:");
        } catch (SQLException e) {
            event.reply(e.getMessage());
        }
    }
}
