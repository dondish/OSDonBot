package DonBot.commands.currency;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.sql.SQLException;

public class BetCommand extends DonCommand {

    public BetCommand() {
        this.name = "bet";
        this.help = "Bet an amount of money";
        this.usage = "^bet <cash>";
        this.category = new Category("currency");
        this.cooldown = 5;
        this.ID = 1;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            if (event.getArgs().equals("")) {
                event.reply("You gotta bet something!");
            } else {
                int cash = provider.getCash(event.getAuthor().getIdLong());
                int random = (int) (Math.random()*2);
                int bet = Integer.parseInt(event.getArgs());
                if (cash < bet) {
                    event.reply("You don't have enough money to bet!");
                    return;
                }
                if (bet<0) {
                    event.reply("Nice try a**hole...");
                    return;
                }
                if (random == 1) {
                    event.reply("You won the bet! you now have " + (cash+bet) + " coins.");
                    provider.updateCash(event.getAuthor().getIdLong(), cash+bet);
                } else {
                    event.reply("You lost the bet! you now have " + (cash-bet) + " coins.");
                    provider.updateCash(event.getAuthor().getIdLong(), cash-bet);
                }
            }
        } catch (SQLException | NumberFormatException e) {
            event.reply("Correct Usage: " + usage);
        }
    }
}
