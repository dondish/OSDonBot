package DonBot.api;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;
import org.json.JSONObject;

import java.awt.*;

public class DonXkcd {
    private String imagelink;
    private String title;
    private String alt;
    private int num;

    public DonXkcd(JSONObject json) {
        num  = json.getInt("num");
        title = json.getString("title");
        alt = json.getString("alt");
        imagelink = json.getString("img");
    }

    public MessageEmbed toEmbed() {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setTitle(title);
        builder.setDescription(alt);
        builder.setImage(imagelink);
        builder.setFooter("https://xkcd.com/"+num, null);
        builder.setColor(Color.GREEN);
        return builder.build();
    }
}
