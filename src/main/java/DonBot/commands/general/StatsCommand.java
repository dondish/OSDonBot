package DonBot.commands.general;

import DonBot.api.DonCommand;
import DonBot.features.CommandStats;
import DonBot.utils.StatsUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import java.sql.SQLException;

public class StatsCommand extends DonCommand {
    public StatsCommand() {
        name = "stats";
        cooldown = 3;
        category = new Category("general");
        ID = 138;
        help = "Gives stats for DonBot commands";
        usage = "^stats <top|commandname|commandid>";
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (event.getArgs().equals("top")) {
            EmbedBuilder builder = new EmbedBuilder().setTitle("Top Used Commands").setColor(event.getSelfMember().getColor());
            try {
                int i = 1;
                for (Command command : CommandStats.getTopUsedCommands(event.getClient(), 3)) {
                    builder.addField(String.format("#%d. %s", i, command.getName()),
                            String.format("Total Usages: %d", ((DonCommand) command).getStats().getTotalUsages()),
                            false);
                    i++;
                }
            } catch (SQLException e) {
                event.reply("Error fetching database stats. if this error persists please contact dondish#7155.");
                return;
            }
            event.reply(builder.build());
            return;
        }
        // Now let's search by name or ID
        DonCommand command = (DonCommand) StatsUtils.getCommandByName(event.getClient(), event.getArgs());
        try {  // if didn't find by name let it find by id
            if (command == null)
                command = (DonCommand) StatsUtils.getCommandByID(event.getClient(), Integer.parseInt(event.getArgs()));
        } catch (NumberFormatException ignored) {}
        if (command != null) {  // if the command exists
            try {
                event.reply(new EmbedBuilder()
                        .setTitle(command.getName())
                        .addField("Total Usages", String.valueOf(command.getStats().getTotalUsages()), false)
                        .addField("Daily Usages", String.valueOf(command.getStats().getUsagesLastDay()), false)
                        .build());
                return;
            } catch (SQLException e) {
                event.reply("Error fetching database stats. if this error persists please contact dondish#7155.");
                return;
            }
        }
        // no commands found
        event.reply("Cannot find command with name: " + event.getArgs());
    }
}
