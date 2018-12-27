package DonBot.api;

import DonBot.Config;
import net.dv8tion.jda.bot.sharding.ShardManager;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONObject;

import java.io.IOException;

public class DiscordBoatsAPI {
    private static final String URL = "https://discordboats.xyz/api";

    public static void update_stats(ShardManager jda) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("server_count", jda.getGuildCache().size());
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request r = new Request.Builder()
                .url(URL + "/bot/"+Config.BOTID)
                .addHeader("Authorization", Config.DBoatsAPIKEY)
                .post(body)
                .build();
        try {
            client.newCall(r).execute().close();
            System.out.println("Discord Boats server count published");

        } catch (IOException ignored) {

        }
    }
}
