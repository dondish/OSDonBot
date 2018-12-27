package DonBot.commands.general;

import DonBot.api.DonCommand;
import DonBot.api.LoLApi;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.HashMap;

public class LeagueCommand extends DonCommand {
    private HashMap<String, Integer> regions = new HashMap<>();

    public LeagueCommand() {
        this.name = "league";
        this.usage = "^league <summonername> <region>";
        this.help = "Get summoner data from LoL";
        this.guildOnly = true;
        this.category = new Category("general");
        this.cooldown = 1;
        regions.put("BR", 0);
        regions.put("EUNE", 1);
        regions.put("EUW", 2);
        regions.put("JP", 3);
        regions.put("KR", 4);
        regions.put("LAN", 5);
        regions.put("LAS", 6);
        regions.put("NA", 7);
        regions.put("OCE", 8);
        regions.put("TR", 9);
        regions.put("RU", 10);
        this.ID = 130;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (event.getArgs().split("\\s+").length==2) {
            String sname = event.getArgs().split("\\s+")[0];
            String region = event.getArgs().split("\\s+")[1];
            if (regions.containsKey(region.toUpperCase())) {
                int r = regions.get(region.toUpperCase());
                try {
                    event.reply(LoLApi.getSummoner(sname, r));
                } catch (Exception e) {
                    event.reply("Summoner not found or Riot has problems with their servers.");
                }
            } else {
                event.reply("Region not found! make sure it is in this list: https://developer.riotgames.com/regional-endpoints.html");
            }
        } else {
            event.reply("correct usage");
        }
    }
}
