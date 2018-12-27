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

public class QueueCommand extends DonCommand {
    private MusicBotSetup musicBot;
    private EventWaiter waiter;

    public QueueCommand(EventWaiter waiter, MusicBotSetup musicBot) {
        this.musicBot = musicBot;
        this.waiter = waiter;
        this.name = "queue";
        this.aliases = new String[]{"list", "q"};
        this.help = "Shows the queue";
        this.guildOnly = true;
        this.category = new Category("music");
        this.usage = "^queue";
        this.ID = 1033;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        if (!(manager.isPlaying())) {
            event.reply("Not playing anything...");
        } else if (manager.queue.isEmpty()) {
            event.reply("Queue is empty.");
        } else {
            SongInfo[] infos = manager.queue.toArray(new SongInfo[]{});
            Paginator.Builder builder = new Paginator.Builder();
            for (SongInfo info:infos) {
                builder.addItems(TextUtils.trackToString(info.track));
            }
            builder.setColor(event.getSelfMember().getColor())
                    .showPageNumbers(true)
                    .setItemsPerPage(5)
                    .setFinalAction(message -> message.delete().queue())
                    .useNumberedItems(true)
                    .waitOnSinglePage(true)
                    .setEventWaiter(waiter)
                    .setText("Now playing: " + TextUtils.trackToString(manager.getCurrent().track) + " at " + TextUtils.durationToString(manager.getPosition()) + "\nQueue:")
                    .setUsers(event.getAuthor())
                    .setTimeout(20, TimeUnit.SECONDS)
                    .build().display(event.getChannel());
        }
    }
}
