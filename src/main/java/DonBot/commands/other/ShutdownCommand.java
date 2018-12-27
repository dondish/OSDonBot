package DonBot.commands.other;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.util.Calendar;

public class ShutdownCommand extends DonCommand {
    public ShutdownCommand () {
        this.name = "shutdown";
        this.hidden = true;
        this.ownerCommand = true;
        this.guildOnly = false;
        this.ID = 2060;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        System.out.println(event.getAuthor().getName() + " requested immediate shutdown. Time: " + Calendar.getInstance().toString());
        event.getJDA().shutdown();
    }
}
