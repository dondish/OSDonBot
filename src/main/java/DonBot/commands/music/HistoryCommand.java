package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import DonBot.audio.SongInfo;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;

import java.util.concurrent.TimeUnit;

public class HistoryCommand extends DonCommand {
    private MusicBotSetup musicBot;
    private EventWaiter waiter;

    public HistoryCommand(EventWaiter waiter, MusicBotSetup musicBot) {
        this.musicBot = musicBot;
        this.waiter = waiter;
        this.name = "history";
        this.aliases = new String[]{"h"};
        this.help = "Shows the history of played songs.";
        this.guildOnly = true;
        this.category = new Category("music");
        this.usage = "^history";
        this.ID = 1027;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        if (manager.before.isEmpty() || !event.getGuild().getAudioManager().isConnected()) {
            event.reply("No songs have been played before.");
        } else if (!event.getArgs().equals("")) {
            try {
                int i = Integer.parseInt(event.getArgs().split("\\s+")[0]) - 1;
                if (i <= 0 || i >= manager.before.size()) {
                    event.reply("Number out of range!");
                    return;
                }
                SongInfo track = manager.before.get(i);
                manager.nextSong(track.track, event.getAuthor());
            } catch (NumberFormatException e) {
                event.reply("Please select a real integer like `^history 1`");
            }
        } else {
            Paginator.Builder builder = new Paginator.Builder();
            for (SongInfo info : manager.before) {
                builder.addItems(TextUtils.trackToString(info.track));
            }
            builder.setColor(event.getSelfMember().getColor())
                    .showPageNumbers(true)
                    .setItemsPerPage(5)
                    .setFinalAction(message -> message.delete().queue())
                    .useNumberedItems(true)
                    .waitOnSinglePage(true)
                    .setEventWaiter(waiter)
                    .setText("History:")
                    .setUsers(event.getAuthor())
                    .setTimeout(20, TimeUnit.SECONDS)
                    .build().display(event.getChannel());
        }
    }
}
