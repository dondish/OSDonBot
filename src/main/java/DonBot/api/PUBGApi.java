package DonBot.api;

import DonBot.Config;
import net.dv8tion.jda.core.EmbedBuilder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.annotation.Nullable;
import java.awt.*;
import java.io.IOException;
import java.util.Iterator;

public class PUBGApi {
    private static String apikey = Config.PUBG;
    private static String apiurl = "https://api.playbattlegrounds.com/shards/%REGION%/players?filter[playerNames]=%PLAYERNAME%";
    private static String currentSeason = "division.bro.official.2018-09";
    private static String apiseasoninfo = "https://api.playbattlegrounds.com/shards/%REGION%/players/%ID%/seasons/"+currentSeason;

    /**
     * GET requests the PUBG API at the given address and returns the response
     *
     * @param finalurl the url in the api to GET request to
     * @return the response of the server
     * @throws IOException HTTP request execution error.
     */
    private static Response get(String finalurl) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .addHeader("Authorization", "Bearer " + apikey)
                .addHeader("Accept", "application/vnd.api+json")
                .url(finalurl)
                .get()
                .build();

        return client.newCall(request).execute();
    }

    private static String keyFormat(String key) {
        String skey = key.replace("-", " ");
        String[] splut = skey.split(" ");
        StringBuilder result = new StringBuilder();
        boolean start = true;
        for (String i : splut) {
            String newi = i.replace(i.charAt(0), i.toUpperCase().charAt(0));
            if (start) {
                result.append(newi);
                start = false;
                continue;
            }
            result.append(" ").append(newi);
        }
        return result.toString();
    }

    /**
     * Get ID from username
     *
     * @param username The username of the player
     * @param region The region that is to be searched https://documentation.playbattlegrounds.com/en/making-requests.html#regions
     * @return The first player that matches the criteria's id as a String
     * @throws IOException HTTP request execution error.
     */
    @Nullable
    public static String getPlayer(String username, String region) throws IOException {
        String finalurl = apiurl.replaceAll("%REGION%", region).replaceAll("%PLAYERNAME%", username);

        Response resp = get(finalurl);

        if (resp.isSuccessful()) {
            ResponseBody body = resp.body();
            if (body == null) {
                return null;
            }
            JSONArray data = new JSONObject(body.string()).getJSONArray("data");
            body.close();
            resp.close();
            if (data.length() == 0)
                return null;
            return data.getJSONObject(0).getString("id");
        } else {
            return null;
        }
    }

    /**
     * @param id The ID of the player
     * @param region The region that is to be searched https://documentation.playbattlegrounds.com/en/making-requests.html#regions
     * @return The player's stats as an embed to send to Discord
     * @throws IOException HTTP request execution error.
     */
    public static EmbedBuilder getPlayerStats(String id, String region) throws IOException {
        String finalurl = apiseasoninfo.replaceAll("%REGION%", region).replaceAll("%ID%", id);

        Response resp = get(finalurl);

        if (resp.isSuccessful()) {
            ResponseBody body = resp.body();
            if (body == null) {
                return null;
            }
            EmbedBuilder builder = new EmbedBuilder();
            JSONObject data = new JSONObject(body.string()).getJSONObject("data");
            body.close();
            JSONObject stats = data.getJSONObject("attributes").getJSONObject("gameModeStats");

            builder.setTitle("PUBG Stats: " + region);

            Iterator<String> keys = stats.keys();
            while (keys.hasNext()) {
                String key = keys.next();
                JSONObject mode = stats.getJSONObject(key);
                builder.setColor(Color.orange);
                builder.addField(String.format("%s | %d matches", keyFormat(key), mode.getInt("roundsPlayed")),
                        String.format(
                                        "Wins: %d\n" +
                                        "Win %%: %.04f\n" +
                                        "Top 10s: %d\n" +
                                        "K/D: %.04f\n" +
                                        "Kills: %d\n" +
                                        "Assists: %d\n",
                                mode.getInt("wins"),
                                (mode.getInt("losses") != 0 ? ((double) mode.getInt("wins"))/((double)mode.getInt("losses")) : 0),
                                mode.getInt("top10s"),
                                (mode.getInt("losses") != 0 ?((double) mode.getInt("kills"))/((double)mode.getInt("losses")) : 0),
                                mode.getInt("kills"),
                                mode.getInt("assists")),
                        true);
            }
            resp.close();
            return builder;
        } else {
            return null;
        }
    }

}
