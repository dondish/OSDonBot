package DonBot.commands.other;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.jagrosh.jdautilities.menu.Paginator;
import net.dv8tion.jda.core.entities.Guild;

import java.util.concurrent.TimeUnit;

public class GuildsCommand extends DonCommand {
    private EventWaiter waiter;

    public GuildsCommand(EventWaiter waiter) {
        this.waiter = waiter;
        this.name = "guilds";
        this.hidden = true;
        this.ownerCommand = true;
        this.guildOnly = false;
        this.ID = 2059;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        Paginator.Builder builder = new Paginator.Builder();
        for (Guild guild : event.getJDA().getGuilds()) {
            builder.addItems(guild.getName());
        }
        builder.setColor(event.getSelfMember().getColor())
                .showPageNumbers(true)
                .setItemsPerPage(10)
                .setFinalAction(message -> message.delete().queue())
                .useNumberedItems(true)
                .waitOnSinglePage(true)
                .setEventWaiter(waiter)
                .setUsers(event.getAuthor())
                .setText("")
                .setTimeout(20, TimeUnit.SECONDS)
                .build().display(event.getChannel());
    }
}
