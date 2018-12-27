package DonBot.commands.currency;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class DailyCommand extends DonCommand {
    public DailyCommand() {
        this.name = "daily";
        this.help = "Get your daily amount of DonCoins (500)";
        this.usage = "^daily";
        this.category = new Category("currency");
        this.ID = 3;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            if (provider.claimDaily(event.getAuthor().getIdLong())) {
                event.reply("500 DonCoins added to your account! currently you have " + provider.getCash(event.getAuthor().getIdLong()) + " coins.");
            } else {
                event.reply("Sorry you have claimed your daily coins. please wait 24 hours.");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
