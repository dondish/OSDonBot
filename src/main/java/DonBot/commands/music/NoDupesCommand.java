package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import DonBot.audio.SongInfo;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.entities.Role;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;

public class NoDupesCommand extends DonCommand {
    private MusicBotSetup setup;

    public NoDupesCommand(MusicBotSetup musicBot) {
        setup = musicBot;
        name = "nodupes";
        aliases = new String[] {"removedupes", "cleardupes"};
        usage = "^nodupes";
        help = "Removes duplicate entries inside the queue";
        category = new Category("music");
        guildOnly = true;
        ID = 1042;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = setup.getManagerFromGuild(event.getGuild());
        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
        Role DJ = provider.getDJ();
        if (DJ != null && !event.getMember().getRoles().contains(DJ) && provider.issOnlyDJModerates()) {
            event.reply("In this server only DJs can use this command!");
            return;
        }
        Set<String> ids = new HashSet<>();
        int oldsize = manager.queue.size();
        LinkedList<SongInfo> newq = new LinkedList<>();
        for (int i = 0;i<manager.queue.size();i++) {
            SongInfo song = manager.queue.get(i);
            if (!ids.contains(song.track.getIdentifier())) {
                newq.add(song);
            }
            ids.add(song.track.getIdentifier());
        }
        manager.queue.clear();
        manager.queue.addAll(newq);
        event.reply("Duplicates cleared! removed `" + (oldsize - manager.queue.size()) + "` songs!");
    }
}
