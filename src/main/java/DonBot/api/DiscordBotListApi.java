package DonBot.api;

import DonBot.Config;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.User;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscordBotListApi {
    private static final String url = "https://discordbots.org/api";

    public static void update_stats(ShardManager jda) {
        OkHttpClient client = new OkHttpClient();
        JSONObject json = new JSONObject();
        json.put("server_count", jda.getGuildCache().size());
        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json.toString());
        Request r = new Request.Builder()
                .url(url + "/bots/"+Config.BOTID+"/stats")
                .addHeader("Authorization", Config.DBLAPIKEY)
                .post(body)
                .build();
        try {
            client.newCall(r).execute().close();
            System.out.println("Discord Bot List server count updated!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
