package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import DonBot.audio.SongInfo;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandEvent;

public class NowPlayingCommand extends DonCommand {
    private MusicBotSetup musicBot;

    public NowPlayingCommand(MusicBotSetup musicBot) {
        this.musicBot = musicBot;
        this.name = "np";
        this.aliases = new String[]{"playing", "nowplaying"};
        this.category = new Category("music");
        this.help = "Shows the currently playing song.";
        this.guildOnly = true;
        this.usage = "^np";
        this.ID = 1029;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        if (!manager.getPlayer().isPaused()&&manager.getCurrent()==null) {
            event.reply("Not playing anything...");
        } else {
            SongInfo track = manager.getCurrent();
            event.reply(TextUtils.getNowPlayingMessage(track));
        }
    }
}
