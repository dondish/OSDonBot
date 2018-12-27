package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import DonBot.audio.SongInfo;
import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.RoleUtils;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RemoveCommand extends DonCommand {
    private MusicBotSetup musicBot;

    public RemoveCommand(MusicBotSetup setup) {
        musicBot = setup;
        this.name = "remove";
        this.usage = "^remove <index>";
        this.help = "Remove a song from the queue at the given index";
        this.guildOnly = true;
        this.category = new Category("music");
        this.ID = 1034;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
        if (!(manager.isPlaying())) {
            event.reply("Not playing anything...");
        } else if (manager.queue.isEmpty()) {
            event.reply("Queue is empty.");
        } else {
            if ((!provider.issOnlyDJModerates()&&event.getMember().getVoiceState().getChannel().equals(event.getGuild().getAudioManager().getConnectedChannel()))&& RoleUtils.isDJorMod(event)) {
                int j = Integer.parseInt(event.getArgs()) - 1;
                if (j<0 || j>=manager.queue.size()) {
                    event.reply("Index out of range");
                    return;
                }
                SongInfo a = manager.queue.remove(j);
                event.reply("Song " + TextUtils.trackToString(a.track) + " was removed from the queue.");
            }
        }
    }
}
