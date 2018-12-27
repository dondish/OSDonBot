package DonBot.commands.general;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

import java.time.temporal.ChronoUnit;

public class PingCommand extends DonCommand {

    public PingCommand() {
        this.name = "ping";
        this.help = "Checks the bot's latency";
        this.category = new Category("general");
        this.guildOnly = false;
        this.usage = "^ping";
        this.ID = 132;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        event.reply("Ping: ...", m -> {
            long ping = event.getMessage().getCreationTime().until(m.getCreationTime(), ChronoUnit.MILLIS);
            m.editMessage("Ping: " + ping  + "ms | Websocket: " + event.getJDA().getPing() + "ms").queue();
        });
    }
}
