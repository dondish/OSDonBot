package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.RoleUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.command.annotation.JDACommand;

@JDACommand.Module(value = "music")
public class StopCommand extends DonCommand {
    private MusicBotSetup musicBot;

    public StopCommand(MusicBotSetup setup) {
        this.musicBot = setup;
        this.name = "stop";
        this.category = new Category("music");
        this.help = "Stops the music player";
        this.aliases = new String[]{"leave", "stp"};
        this.guildOnly= true;
        this.usage = "^stop";
        this.ID = 1041;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
        if (
                (!provider.issOnlyDJModerates()
                        && event.getMember().getVoiceState() != null
                        && event.getMember().getVoiceState().getChannel()
                        .equals(event.getGuild().getAudioManager().getConnectedChannel()))
                        || (provider.issOnlyDJModerates()
                        && RoleUtils.isDJorMod(event)))
        {
            manager.getPlayer().setPaused(false);
            manager.stop();
        }
        else if (!(event.getMember().getVoiceState().getChannel().equals(event.getGuild().getAudioManager().getConnectedChannel())))
            event.reply("Must be connected to the voice channel.");
        else
            event.reply("You don't have the DJ role or above required for stopping.");
    }
}
