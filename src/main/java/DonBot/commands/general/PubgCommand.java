package DonBot.commands.general;

import DonBot.api.DonCommand;
import DonBot.api.PUBGApi;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

public class PubgCommand extends DonCommand {
    public PubgCommand() {
        this.name = "pubg";
        this.help = "Fetch PUBG stats about a user.";
        this.usage = "^pubg <pc-region|xbox-region> <user>";
        this.category = new Category("general");
        this.guildOnly = true;
        this.cooldown = 3;
        this.ID = 133;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (event.getArgs().equals("")) {
            event.reply("Correct usage: " + usage);
            return;
        }
        try {
            String playerid = PUBGApi.getPlayer(event.getArgs().split("\\s+")[1], event.getArgs().split("\\s+")[0]);
            if (playerid==null) {
                event.reply("Sorry but the region or the username do not match on the PUBG server.");
                return;
            }
            EmbedBuilder result = PUBGApi.getPlayerStats(playerid, event.getArgs().split("\\s+")[0]);
            if(result == null) {
                event.reply("Sorry but the region or the username do not match on the PUBG server.");
                return;
            }
            result.setFooter(event.getArgs().split("\\s+")[1], event.getAuthor().getAvatarUrl());

            event.reply(result.build());
        } catch (IllegalStateException ignored) {

        } catch (Exception e) {
            e.printStackTrace();
            event.reply("Usage: " + usage);
        }
    }
}
