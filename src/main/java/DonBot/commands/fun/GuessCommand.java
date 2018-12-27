package DonBot.commands.fun;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;

public class GuessCommand extends DonCommand {
    private Logger logger = LoggerFactory.getLogger(GuessCommand.class);
    private EventWaiter waiter;
    private int high;
    private int low;
    private boolean done;

    public GuessCommand(EventWaiter waiter) {
        this.waiter = waiter;
        this.name = "guess";
        this.help = "Guesses your number.";
        this.usage = "^guess";
        this.category = new Category("fun");
        this.guildOnly = true;
        this.ID = 66;
    }

    private void waiter(CommandEvent event) {
        if (high - low <= 1){
            event.reply("Your number is " + high);
            return;
        }
        int n = (high+low+1)/2;
        event.reply("Is it " + n + "?");
        waiter.waitForEvent(MessageReceivedEvent.class,
                e -> event.getAuthor().equals(e.getAuthor())&&event.getChannel().equals(e.getChannel()),
                e -> {
                    switch (e.getMessage().getContentDisplay().toLowerCase()){
                        case "lower":
                            setHigh(n);
                            break;
                        case "higher":
                            setLow(n);
                            break;
                        case "equal":
                            event.reply("Ha! Guessed right one more time!");
                            return;

                    }
                    waiter(event);}, 20, TimeUnit.SECONDS, ()->{});
    }

    private void setHigh(int val) {
        high = val;
        done = false;
    }

    private void setLow(int val) {
        low = val;
        done = false;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        event.reply("I will guess your number!\nThink of a number between 1 to 100, I am going to show a number and you will tell me if your number is higher, lower or equal.");
        high = 100;
        low = 0;
        waiter(event);

    }
}
