package DonBot.commands.general;

import DonBot.api.DonCommand;
import DonBot.features.CommandStats;
import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.StatsUtils;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import java.sql.SQLException;

public class InfoCommand extends DonCommand {
    public InfoCommand() {
        this.name = "info";
        this.help = "Shows info about the bot";
        this.category = new Category("general");
        this.guildOnly = false;
        this.usage = "^info";
        this.ID = 128;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        try {
            event.reply(new EmbedBuilder().setTitle("DonBot#9040").setColor(event.getSelfMember().getColor())
                    .setDescription("A multi-purpose bot designed for many servers.")
                    .addField("Guilds", Long.toString(event.getJDA().getGuildCache().size()), true)
                    .addField("Author", "dondish#7155", true)
                    .addField("Invite", "[Click me](https://discordapp.com/api/oauth2/authorize?client_id=420517675509350400&permissions=36924481&scope=bot)", true)
                    .addField("Support Server", "[Click me](https://discord.gg/pEKkg9r)", true)
                    .addField("Users", "" + event.getJDA().getUserCache().size(), true)
                    .addField("Donation Link", "[Click me](https://www.patreon.com/dondish)", true)
                    .addField("Website", "[Click me](https://donbot.space)", true)
                    .addField("Dashboard", "[Click me](https://donbot.space/dashboard)", true)
                    .addField("Amount of Commands", String.valueOf(event.getClient().getCommands().size()), true)
                    .addField("Total Daily Usage", String.format("%d commands used", CommandStats.getTotalDailyUsage()), true)
                    .addField("Playing Music", String.format("On %d guilds", ((DonGuildSettingsProvider)event.getClient().getSettingsFor(event.getGuild())).getActivePlayers()), true)
                    .addField("Shard", String.format("[%d/%d]", event.getJDA().getShardInfo().getShardId(), event.getJDA().getShardInfo().getShardTotal()), true)
                    .addField("Uptime", TextUtils.durationToWordedString(StatsUtils.getUptime()), false)
                    .build());
        } catch (SQLException ignored) {}
    }
}
