package DonBot.commands.fun;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RollCommand extends DonCommand {
    public RollCommand(){
        this.name = "roll";
        this.help = "Rolls the DonBot dice";
        this.usage = "^roll";
        this.guildOnly = true;
        this.category = new Category("fun");
        this.ID = 69;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        event.reply(String.valueOf(((int)(Math.random()*6)+1)));
    }
}
