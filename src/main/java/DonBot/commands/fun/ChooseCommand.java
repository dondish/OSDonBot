package DonBot.commands.fun;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ChooseCommand extends DonCommand {
    public ChooseCommand(){
        this.name = "choose";
        this.help = "Chooses a random item out of a list.";
        this.usage = "^choose <arg1> <arg2> ...";
        this.category = new Category("fun");
        this.guildOnly = true;
        this.ID = 64;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (event.getArgs().equals("")) {
            event.reply("Correct use: " + this.usage);
        } else {
            event.reply(event.getArgs().split("\\s+")[(int)(Math.random()*event.getArgs().split("\\s+").length)]);
        }
    }
}
