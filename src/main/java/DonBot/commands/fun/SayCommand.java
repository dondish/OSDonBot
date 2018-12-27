package DonBot.commands.fun;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

public class SayCommand extends DonCommand {
    public SayCommand() {
        this.name = "say";
        this.usage = "^say <echo>";
        this.help = "Make the bot say something";
        this.category = new Category("fun");
        this.guildOnly = true;
        this.aliases = new String[]{"echo"};
        this.ID = 70;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        event.reply(event.getArgs().equals("") ? "Message cannot be empty!" : event.getArgs());
    }
}
