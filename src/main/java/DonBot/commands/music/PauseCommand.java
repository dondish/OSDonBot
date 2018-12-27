package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;

public class PauseCommand extends DonCommand {
    private MusicBotSetup musicBot;
    public PauseCommand(MusicBotSetup musicBot) {
        this.musicBot = musicBot;
        this.name = "pause";
        this.usage= "^pause";
        this.category = new Category("music");
        this.help = "Pauses the currently playing track";
        this.guildOnly = true;
        this.ID = 1030;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        AudioPlayer player = manager.getPlayer();
        if (manager.isPlaying()&&(event.getMember().getVoiceState().getChannel().equals(event.getGuild().getAudioManager().getConnectedChannel()))) {
            player.setPaused(true);
            event.reply("Player paused.");
        } else if (!(event.getMember().getVoiceState().getChannel().equals(event.getGuild().getAudioManager().getConnectedChannel()))) {
            event.reply("Must be connected to the voice channel.");
        }
    }
}
