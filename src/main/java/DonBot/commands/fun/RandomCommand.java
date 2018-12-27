package DonBot.commands.fun;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RandomCommand extends DonCommand {
    public RandomCommand(){
        this.name = "random";
        this.help = "Generates a random number";
        this.usage = "^random (Optional) <start> <end>";
        this.guildOnly = true;
        this.category = new Category("fun");
        this.ID = 68;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (event.getArgs().equals("")) {
            event.reply(String.valueOf(((int)(Math.random()*20000000))-10000000));
        } else if (event.getArgs().split("\\s+").length==2) {
            try {
                int start = Integer.parseInt(event.getArgs().split("\\s+")[0]);
                int end = Integer.parseInt(event.getArgs().split("\\s+")[1]);
                int diff = Math.abs(start-end);
                event.reply(String.valueOf(((int)(Math.random()*diff))+start));
            } catch (Exception ignored) {}
        } else {
            event.reply("Correct use: ^random <start> <end>");
        }
    }
}
