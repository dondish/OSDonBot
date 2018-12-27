package DonBot.commands.currency;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class FlipCommand extends DonCommand {
    public FlipCommand() {
        this.name = "flip";
        this.help = "Flips a coin, 1 you receive a coin else you lose one";
        this.usage = "^flip";
        this.category = new Category("currency");
        this.cooldown = 5;
        this.ID = 4;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            int cash = provider.getCash(event.getAuthor().getIdLong());
            if (cash==0) {
                event.reply("You don't have enough money to bet!");
                return;
            }
            int random = (int) (Math.random()*2);
            if (random == 1) {
                event.reply("Flipped 1 you earned yourself a coin! you know have " + (cash+1) + " coins.");
                provider.updateCash(event.getAuthor().getIdLong(), cash+1);
            } else {
                event.reply("Flipped 0 you lost yourself a coin! you know have " + (cash-1) + " coins.");
                provider.updateCash(event.getAuthor().getIdLong(), cash-1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
