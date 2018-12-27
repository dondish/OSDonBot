package DonBot.commands.currency;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;
import java.util.List;

public class CoinsCommand extends DonCommand {
    public CoinsCommand() {
        this.name = "coins";
        this.help = "Shows the amount of coins you have or a user";
        this.usage = "^coins <Optional: user>";
        this.category = new Category("currency");
        this.cooldown = 1;
        this.ID = 2;
    }

    @Override
    protected void execute(CommandEvent event) {
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            if (event.getArgs().equals("")) {
                int cash = provider.getCash(event.getAuthor().getIdLong());
                event.reply("You have " + cash + " coins.");
            } else {
                List<User> users = event.getMessage().getMentionedUsers();
                User u;
                if (users.size()==0) {
                    u = event.getJDA().getUsersByName(event.getArgs(), true).get(0);
                    if (u==null) {
                        event.reply("No users mentioned and no valid use was entered");
                        return;
                    }
                } else {
                    u = users.get(0);
                }
                int cash = provider.getCash(u.getIdLong());
                event.reply(u.getName() + " has " + cash + " coins.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
