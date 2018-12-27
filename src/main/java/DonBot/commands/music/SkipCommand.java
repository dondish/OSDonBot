package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.MusicBotSetup;
import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.RoleUtils;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Objects;

public class SkipCommand extends DonCommand{
    private MusicBotSetup musicBot;

    public SkipCommand(MusicBotSetup musicBot){
        this.musicBot = musicBot;
        this.name = "skip";
        this.help = "Skip the currently playing song.";
        this.category = new Category("music");
        this.guildOnly = true;
        this.usage = "^skip <(Optional, Requires DJ) amount of songs to skip>";
        this.ID = 1040;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
        if (!musicBot.getManagerFromGuild(event.getGuild()).isPlaying()) {
            event.reply("Not playing anything...");
        } else {
            if (!Objects.equals(event.getArgs(), "")) {
                if ((!provider.issOnlyDJModerates()&&event.getMember().getVoiceState().getChannel().equals(event.getGuild().getAudioManager().getConnectedChannel()))&&RoleUtils.isDJorMod(event)) {
                    int j = Integer.parseInt(event.getArgs());
                    musicBot.getManagerFromGuild(event.getGuild()).skip(event.getMember(), event, j);
                } else {
                    event.reply("Sorry DJ role is required to skip multiple songs.");
                }
            } else if (!(event.getMember().getVoiceState().getChannel().equals(event.getGuild().getAudioManager().getConnectedChannel()))) {
                event.reply("Must be connected to the voice channel.");
            } else {
                musicBot.getManagerFromGuild(event.getGuild()).skip(event.getMember(), event);
            }
        }
    }
}
