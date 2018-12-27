package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class ResumeCommand extends DonCommand {
    private MusicBotSetup musicBot;
    public ResumeCommand(MusicBotSetup musicBot){
        this.musicBot = musicBot;
        this.name = "resume";
        this.aliases = new String[]{"unpause"};
        this.category = new Category("music");
        this.help = "Resumes the track if paused";
        this.ID = 1036;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        AudioPlayer player = manager.getPlayer();
        if (player.isPaused()) {
            player.setPaused(false);
            event.reply("Player resumed.");
        } else {
            event.reply("Not playing anything.");
        }
    }
}
