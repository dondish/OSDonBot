package DonBot.commands.currency;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;

public class TransferCommand extends DonCommand {

    public TransferCommand() {
        this.name = "transfer";
        this.help = "Transfers money from your account to another";
        this.usage = "^transfer <user mention> <amount of money to transfer>";
        this.category = new Category("currency");
        this.cooldown = 3;
        this.ID = 6;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            if (event.getArgs().split("\\s+").length == 2&&!event.getMessage().getMentionedUsers().isEmpty()) {
                User u = event.getMessage().getMentionedUsers().get(0);
                int acash = provider.getCash(event.getAuthor().getIdLong());
                int cash = provider.getCash(u.getIdLong());
                int amount = Integer.parseInt(event.getArgs().split("\\s+")[1]);
                if (amount < 0) {
                    event.reply("You cannot take money from the other guy :face_palm:");
                    return;
                }
                if (acash < amount) {
                    event.reply("You don't have that money! Did you steal some?!?");
                    return;
                }
                acash -= amount;
                cash += amount;
                provider.updateCash(event.getAuthor().getIdLong(), acash);
                provider.updateCash(u.getIdLong(), cash);
                event.reply("Money transferred! " + u.getName() + " now has " + cash + " coins. you now have " + acash + " coins.");
            }

        } catch (SQLException | NumberFormatException e) {
            event.reply("Correct usage: " + usage);
        }
    }
}
