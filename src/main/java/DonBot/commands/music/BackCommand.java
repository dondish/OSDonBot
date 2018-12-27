package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import DonBot.audio.SongInfo;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BackCommand extends DonCommand {
    private MusicBotSetup musicBot;
    Logger log = LoggerFactory.getLogger(BackCommand.class);

    public BackCommand(MusicBotSetup setup) {
        musicBot = setup;
        this.name = "back";
        this.help = "Returns one song backwards in the queue";
        this.usage = "^back";
        this.guildOnly = true;
        this.category = new Category("music");
        this.ID = 1024;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        AudioPlayer player = manager.getPlayer();
        if (!manager.isPlaying()) {
            event.reply("Not playing anything...");
        } else {
            if (manager.before.isEmpty()) {
                event.reply("No songs to go back!");
            } else {
                SongInfo prevsong = manager.before.pollLast();
                manager.queue.addFirst(manager.getCurrent());
                player.stopTrack();
                manager.before.pollLast();
                manager.setCurrent(prevsong);
                assert prevsong != null;
                player.startTrack(prevsong.track.makeClone(), false);
            }
        }
    }
}
