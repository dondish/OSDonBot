package DonBot.commands.fun;

import DonBot.api.DonCommand;
import DonBot.api.DonXkcd;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class XKCDCommand extends DonCommand {
    public XKCDCommand() {
        this.name = "xkcd";
        this.help = "Get a random xkcd comic or fetch by number";
        this.usage = "^xkcd <comic number>";
        this.category = new Category("fun");
        this.guildOnly = true;
        this.ID = 71;
    }
    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        int num;
        int anum;
        try {
            HttpURLConnection conn= (HttpURLConnection) new URL("https://xkcd.com/info.0.json").openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            JSONObject json = new JSONObject(sb.toString());
            conn.disconnect();
            anum = json.getInt("num");
        } catch (Exception e) {
            event.reply(e.getMessage());
            return;
        }
        if (event.getArgs().equals("")) {
            num = ((int)(Math.random()*anum))+1;
        } else {
            try {
            num = Integer.parseInt(event.getArgs());
            } catch (NumberFormatException e) {
                event.reply("invalid number");
                return;
            }
        }
        try {
            HttpURLConnection conn= (HttpURLConnection) new URL("https://xkcd.com/" + num +"/info.0.json").openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:47.0) Gecko/20100101 Firefox/47.0");
            BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }
            br.close();
            JSONObject json = new JSONObject(sb.toString());
            conn.disconnect();
            DonXkcd xkcd = new DonXkcd(json);
            event.reply(xkcd.toEmbed());
        } catch (Exception e) {
            event.reply(e.getMessage());
            return;
        }
    }
}
