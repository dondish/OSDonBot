package DonBot.api;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;

import java.awt.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class WorldCupApi {
    private String API_URL = "https://raw.githubusercontent.com/lsv/fifa-worldcup-2018/master/data.json";
    private OkHttpClient client = new OkHttpClient();
    private Logger log;
    private JSONObject json;

    public WorldCupApi() {
        ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
        service.scheduleAtFixedRate(() ->{
            try {
                Request request = new Request.Builder().get().url(API_URL).build();
                Response response = client.newCall(request).execute();
                ResponseBody body = response.body();
                if (body == null)
                    return;
                json = new JSONObject(body.string());
                body.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }, 0, 20, TimeUnit.MINUTES);
    }

    /**
     * @return a list of all of the teams
     */
    public List<Team> getTeams() {
        JSONArray teams = json.getJSONArray("teams");
        List<Team> teamlist = new ArrayList<>();
        for (Object object: teams) {
            JSONObject jsobj = (JSONObject) object;
            teamlist.add(new Team(
                    jsobj.getInt("id"),
                    jsobj.getString("name"),
                    jsobj.getString("fifaCode"),
                    jsobj.getString("iso2"),
                    jsobj.getString("flag"),
                    jsobj.getString("emojiString")));
        }

        return teamlist;
    }

    /**
     * @param name - The name of the Country or the FIFA code (Russia or RUS)
     * @return the Team with that name
     */
    public Team getTeam(String name) {
        List<Team> teams = getTeams();
        for (Team team: teams) {
            if (team.name.toLowerCase().equals(name.toLowerCase())||team.fifa_code.toLowerCase().equals(name.toLowerCase())) {
                return team;
            }
        }
        return null;
    }

    /**
     * @return a list of all of the stadiums
     */
    public List<Stadium> getStadiums() {
        JSONArray stadiums = json.getJSONArray("stadiums");
        List<Stadium> stadiumList = new ArrayList<>();
        for (Object object: stadiums) {
            JSONObject jsobj = (JSONObject) object;
            stadiumList.add(new Stadium(
                    jsobj.getInt("id"),
                    jsobj.getString("name"),
                    jsobj.getString("city"),
                    jsobj.getString("image")));
        }

        return stadiumList;
    }

    /**
     * @param name the name of the stadium
     * @return the Stadium with that name
     */
    public Stadium getStadium(String name) {
        List<Stadium> stadiums = getStadiums();
        for (Stadium stadium: stadiums) {
            if (stadium.name.toLowerCase().equals(name.toLowerCase()))
                return stadium;
        }
        return null;
    }


    /**
     * @param id the number of the match (chronologically)
     * @return the Match of the corresponding id or null if not match found
     * @throws ParseException Invalid date (Should not happen since all dates are already known and set)
     */
    public Match getMatchByID(int id) throws ParseException { // <------ USE THIS FOR THE BINARY SEARCH LATER
        if (id > 48) { // knockout
            if (id < 57) { // round_16
                List<Match> matches = getMatches("round_16", false);
                if (matches == null)
                    return null;
                return matches.get(id-49);
            } else if (id < 61) { //round_8
                List<Match> matches = getMatches("round_8", false);
                if (matches == null)
                    return null;
                return matches.get(id-57);
            } else if (id < 63) { // round_4
                List<Match> matches = getMatches("round_4", false);
                if (matches == null)
                    return null;
                return matches.get(id-61);
            } else if (id == 63) { // round_2_loser
                List<Match> matches = getMatches("round_2_loser", false);
                if (matches == null)
                    return null;
                return matches.get(0);
            } else { // round_2
                List<Match> matches = getMatches("round_2", false);
                if (matches == null)
                    return null;
                return matches.get(0);
            }
        } else { // group
            String group = new String[] {"a", "b", "c", "d", "e", "f", "g", "h"}[((id-1)/2)%8]; // nice little algorithm, fast and good
            List<Match> matches = getMatches(group, true);
            if (matches == null)
                return null;
            return matches.get(2*((id-1)/16)-id%2+1); // another nice algorithm
        }
    }


    /**
     * IMPORTANT: Might use recursion.
     * @param name if group then the name of the group else the name of the knockout step
     * @param group if this is a group match or a knockout match
     * @return a list of matches in that group or round of knockout
     * @throws ParseException Invalid date (Should not happen since all dates are already known and set)
     */
    private List<Match> getMatches(String name, boolean group) throws ParseException {
        JSONArray matches;
        if (group) { // a group match
            JSONObject groups = json.getJSONObject("groups");
            matches = groups.getJSONObject(name).getJSONArray("matches");
            List<Match> matchesList = new ArrayList<>();
            DateFormat df = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ssXXX");
            List<Team> teams = getTeams();
            List<Stadium> stadiums = getStadiums();
            for (Object object: matches) {
                JSONObject jsobj = (JSONObject) object;
                matchesList.add(new Match(
                        jsobj.getInt("name"),
                        jsobj.getString("type"),
                        teams.get(jsobj.getInt("home_team")-1),
                        teams.get(jsobj.getInt("away_team")-1),
                        jsobj.optInt("home_result", 0), // if the result is null, make the int 0
                        jsobj.optInt("away_result", 0), // if the result is null, make the int 0
                        df.parse(jsobj.getString("date")), // Parse String to Date Object
                        stadiums.get(jsobj.getInt("stadium")-1),
                        jsobj.getBoolean("finished"),
                        jsobj.getInt("matchday"),
                        true));

            }
            return matchesList;
        } else { // a knockout match
            JSONObject knockout = json.getJSONObject("knockout");
            matches = knockout.getJSONObject(name).getJSONArray("matches");
            List<Match> matchesList = new ArrayList<>();
            DateFormat df = new SimpleDateFormat("yy-MM-dd'T'HH:mm:ssXXX");
            List<Team> teams = getTeams();
            List<Stadium> stadiums = getStadiums();
            for (Object object: matches) {
                JSONObject jsobj = (JSONObject) object;
                matchesList.add(new Match(
                        jsobj.getInt("name"),
                        jsobj.getString("type"),
                        teams.get(jsobj.getInt("home_team")-1),
                        teams.get(jsobj.getInt("away_team")-1),
                        jsobj.optInt("home_result", 0), // if the result is null, make the int 0
                        jsobj.optInt("away_result", 0), // if the result is null, make the int 0
                        df.parse(jsobj.getString("date")), // Parse String to Date Object
                        stadiums.get(jsobj.getInt("stadium")-1),
                        jsobj.getBoolean("finished"),
                        jsobj.getInt("matchday"),
                        true));

            }
            return matchesList;
        }

    }


    public Match getLatestMatch() throws ParseException{
        Match latest = getMatchByID(1);
        for (int i = 1;i<65;i++) {
            Match m = getMatchByID(i);
            if (latest.date.before(m.date)&&m.finished)
                latest = m;
        }
        return latest;
    }

    public List<Match> getUpcomingMatches() throws ParseException{
        Date today = new Date();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DAY_OF_MONTH, 1);
        Date t = new Date(tomorrow.toInstant().toEpochMilli());
        List<Match> res = new ArrayList<>();
        int i = 1;
        while (res.size()<3&&i<64) {
            Match match = getMatchByID(i);
            if (match.date.before(t)&&match.date.after(today))
                res.add(match);
            i++;
        }
        res.sort(Comparator.comparing(obj -> obj.date));
        return res;
    }

    public class Team {
        private int id;
        private String name;
        private String fifa_code;
        private String iso_2;
        private String flag_url;
        private String emoji;

        Team(int id, String name, String fifa_code, String iso_2, String flag_url, String emoji) {
            this.id = id;
            this.name = name;
            this.fifa_code = fifa_code;
            this.iso_2 = iso_2;
            this.flag_url = flag_url;
            this.emoji = emoji;
        }

        public MessageEmbed embedify() {
            return new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle(name)
                    .setThumbnail(flag_url)
                    .addField("Fifa Code:", fifa_code, true)
                    .addField("ISO_2", iso_2, true)
                    .build();
        }

        String display() {
            return name + " " + emoji + "  ";
        }

        @Override
        public String toString() {
            return name + " " + emoji + "\n";
        }
    }

    public class Match{
        private int name;
        private String type;
        private Team home_team;
        private Team away_team;
        private int home_result;
        private int away_result;
        private Date date;
        private Stadium stadium;
        private boolean finished;
        private int matchday;
        private boolean available;

        Match(int name, String type, Team home_team, Team away_team, int home_result, int away_result, Date date, Stadium stadium, boolean finished, int matchday, boolean available){
            this.name = name;
            this.type = type;
            this.home_team = home_team;
            this.away_team = away_team;
            this.home_result = home_result;
            this.away_result = away_result;
            this.date = date;
            this.stadium = stadium;
            this.finished = finished;
            this.matchday = matchday;
            this.available = available;
        }

        public MessageEmbed embedify() {
            if (!available)
                return new EmbedBuilder().setTitle("Matching teams are not available at the moment")
                        .setTimestamp(date.toInstant())
                        .setThumbnail(stadium.image)
                        .setColor(Color.GREEN).build();
            if (finished) {
                Team winner = getWinner();
                EmbedBuilder builder = new EmbedBuilder()
                        .setTitle(home_team.display() + home_result + " - " + away_result + "  " + away_team.display())
                        .setColor(Color.GREEN)
                        .setTimestamp(date.toInstant()) // show the date of the game in the Embed
                        .setThumbnail(stadium.image)
                        .setImage(winner.flag_url)
                        .setDescription("Winning team: " + winner.display());
                return builder.build();
            }
            return new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle(home_team.display() + "-  " + away_team.display())
                    .setTimestamp(date.toInstant())  // show the date of the game in the Embed
                    .setThumbnail(stadium.image)
                    .build();
        }

        Team getWinner() {
            if (home_result > away_result)
                return home_team;
            else if (away_result>home_result)
                return away_team;
            else
                return null;
        }

        Team getLoser() {
            if (away_result < home_result)
                return away_team;
            else if (home_result< away_result)
                return home_team;
            else
                return null;
        }

    }

    public class Stadium {
        private int id;
        private String name;
        private String city;
        private String image;

        Stadium(int id, String name, String city, String image) {
            this.id = id;
            this.name = name;
            this.city = city;
            this.image = image;
        }

        public MessageEmbed embedify() {
            return new EmbedBuilder()
                    .setColor(Color.GREEN)
                    .setTitle(toString())
                    .setThumbnail(image)
                    .build();
        }

        @Override
        public String toString() {
            return name + " - " + city + "\n";
        }
    }
}
