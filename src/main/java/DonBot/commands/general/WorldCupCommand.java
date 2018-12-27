package DonBot.commands.general;

import DonBot.api.DonCommand;
import DonBot.api.WorldCupApi;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

public class WorldCupCommand extends DonCommand {
    private WorldCupApi api = new WorldCupApi();

    public WorldCupCommand() {
        this.name = "worldcup";
        this.aliases = new String[] {"fifa", "cup"};
        this.help = "Get information about the FIFA World Cup";
        this.usage = "^worldcup (upcoming|latest|teams|team TeamName|match Number/latest/upcoming|stadiums|stadium StadiumName)";
        this.guildOnly = true;
        this.cooldown = 3;
        this.category = new Category("general");
        this.ID = 137;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (event.getArgs().split("\\s+").length==0) {
            event.reply("Usage: " + usage);
        }
        else if (event.getArgs().equals("teams")) {
            List<WorldCupApi.Team> teamlist = api.getTeams();
            if (teamlist == null) {
                event.reply("Unable to find any teams");
                return;
            }
            String teams = teamlist.toString();
            event.reply(new EmbedBuilder()
                    .setTitle("Teams")
                    .setColor(Color.GREEN)
                    .setDescription(teams.substring(1, teams.length()-1).replaceAll(", ", ""))
                    .build());
        } else if (event.getArgs().split("\\s+")[0].equals("team")) {
            String[] args = event.getArgs().split("\\s+");
            String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            WorldCupApi.Team team = api.getTeam(name);
            if (team == null) {
                event.reply("Team with name "+ name+ " was not found, do `^worldcup teams` to get all the available teams");
                return;
            }
            event.reply(team.embedify());
        } else if (event.getArgs().split("\\s+")[0].equals("stadium")) {
            String[] args = event.getArgs().split("\\s+");
            String name = String.join(" ", Arrays.copyOfRange(args, 1, args.length));
            WorldCupApi.Stadium stadium = api.getStadium(name);
            if (stadium == null) {
                event.reply("Stadium with name "+ name+ " was not found, do `^worldcup stadiums` to get all the available stadiums");
                return;
            }
            event.reply(stadium.embedify());
        } else if (event.getArgs().equals("stadiums")){
            List<WorldCupApi.Stadium> stadiumList = api.getStadiums();
            if (stadiumList == null) {
                event.reply("Unable to find any stadiums");
                return;
            }
            String stadiums = stadiumList.toString();
            event.reply(new EmbedBuilder()
                    .setTitle("Stadiums")
                    .setColor(Color.GREEN)
                    .setDescription(stadiums.substring(1, stadiums.length()-1).replaceAll(", ", ""))
                    .build());
        } else if(event.getArgs().split("\\s+")[0].equals("match")){
            if (event.getArgs().split("\\s+").length == 1)
                event.reply("Usage: " + usage);
            try {
                int id = Integer.parseInt(event.getArgs().split("\\s+")[1]);
                event.reply(api.getMatchByID(id).embedify());
            } catch (NumberFormatException e) { // not a number
                try {
                    switch (event.getArgs().split("\\s+")[1]) {
                        case "latest":
                            event.reply(api.getLatestMatch().embedify());
                            break;
                        case "upcoming":
                            List<WorldCupApi.Match> matches = api.getUpcomingMatches();
                            if (matches.isEmpty())
                                event.reply("No upcoming matches");
                            else
                                for (WorldCupApi.Match match : matches)
                                    event.reply(match.embedify());
                            break;
                        default:
                            event.reply("Usage: " + usage);
                            break;
                    }
                } catch (ParseException er) {
                    e.printStackTrace();
                    event.reply("Error occurred");
                }
            } catch (Exception e) {
                event.reply("Error occurred, may be an invalid match number, a valid match number must be between 1 to 64.");
            }
        } else if (event.getArgs().split("\\s+")[0].equals("latest")) {
            try {
                event.reply(api.getLatestMatch().embedify());
            } catch (ParseException e) {
                e.printStackTrace();
                event.reply("Error occurred");
            }
        } else if (event.getArgs().split("\\s+")[0].equals("upcoming")) {
            try {
                List<WorldCupApi.Match> matches = api.getUpcomingMatches();
                if (matches.isEmpty())
                    event.reply("No upcoming matches");
                else
                    for (WorldCupApi.Match match: matches)
                        event.reply(match.embedify());
            } catch (ParseException e) {
                e.printStackTrace();
                event.reply("Error occurred");
            }
        }
    }
}
