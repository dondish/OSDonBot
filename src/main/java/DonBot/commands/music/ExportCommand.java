package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import DonBot.audio.SongInfo;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ExportCommand extends DonCommand {
    private MusicBotSetup musicBot;
    private Logger log = LoggerFactory.getLogger(ExportCommand.class);

    public ExportCommand(MusicBotSetup musicBot) {
        this.musicBot = musicBot;
        this.name = "export";
        this.category = new Category("music");
        this.help = "exports the playlist to hastebin or wastebin";
        this.usage = "^export";
        this.guildOnly = true;
        this.ID = 1026;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (!musicBot.getManagerFromGuild(event.getGuild()).isPlaying()){
            event.reply("Not playing anything...");
        } else {
            DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
            StringBuilder s = new StringBuilder(manager.getCurrent().track.getInfo().uri + "\n");
            if (!manager.queue.isEmpty()){
                for (SongInfo info : manager.queue) {
                    s.append(info.track.getInfo().uri).append("\n");
                }
            HttpURLConnection connection = null;
            try {
                //Create connection
                URL url = new URL("https://hastebin.com/documents");
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("POST");
                connection.setDoInput(true);
                connection.setDoOutput(true);

                //Send request
                DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                wr.writeBytes(s.toString());
                wr.flush();
                wr.close();

                //Get Response
                BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                event.reply("https://hastebin.com/" + new JSONObject(rd.readLine()).getString("key")+".donbot");

            } catch (IOException e) {
                connection = null;
                try {
                    //Create connection
                    URL url = new URL("https://wastebin.party/documents");
                    connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("POST");
                    connection.setDoInput(true);
                    connection.setDoOutput(true);

                    //Send request
                    DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
                    wr.writeBytes(s.toString());
                    wr.flush();
                    wr.close();

                    //Get Response
                    BufferedReader rd = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                    event.reply("https://wastebin.party/" + new JSONObject(rd.readLine()).getString("key")+".donbot");

                } catch (IOException ev) {
                    log.error(ev.getMessage());
                } finally {
                    if (connection != null)
                        connection.disconnect();
                }
            } finally {
                if (connection != null)
                connection.disconnect();
            }
        }
        }
    }
}
