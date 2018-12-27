package DonBot.api;


import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.json.JSONObject;

import java.io.IOException;

/**
 * A simple api that uses Telky's awesome random api of pandas!
 *
 */
public class SomeRandomAPI {
    private static String FACT_URL = "https://some-random-api.glitch.me/pandafact/api";
    private static String IMAGE_URL = "https://some-random-api.glitch.me/pandaimg/api";
    private static OkHttpClient client = new OkHttpClient();

    /**
     * @return a random fact about pandas
     * @throws IOException Problems executing the get request
     * @throws NullPointerException You get a null response
     */
    public static String getPandaFact() throws IOException, NullPointerException {
        Request request = new Request.Builder()
                .url(FACT_URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        return (String) new JSONObject(response.body().string()).get("fact");
    }

    /**
     * @return a link to a random panda image
     * @throws IOException Problems executing the get request
     * @throws NullPointerException You get a null response
     */
    public static String getPandaImage() throws IOException, NullPointerException {
        Request request = new Request.Builder()
                .url(IMAGE_URL)
                .get()
                .build();
        Response response = client.newCall(request).execute();
        return (String) new JSONObject(response.body().string()).get("link");
    }
}
