package DonBot.commands.general;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class MathCommand extends DonCommand {
    private ScriptEngineManager manager = new ScriptEngineManager();
    private ScriptEngine engine = manager.getEngineByName("js");
    public MathCommand() {
        this.name = "math";
        this.help = "Calulates simple math queries, uses JS operators";
        this.usage = "^math <query>";
        this.guildOnly= true;
        this.ID = 131;
        this.category = new Category("general");
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        Pattern pattern = Pattern.compile("((\\d)|(\\W))+");
        if (event.getArgs().matches(pattern.toString())) {
            final ExecutorService service = Executors.newSingleThreadExecutor();
            try {
                final Future future = service.submit(()->{
                    try {
                        Object result = engine.eval(event.getArgs());
                        event.reply(result.toString());
                    } catch (ScriptException e) {
                        event.reply("`"+e.getMessage()+"`");
                    }
                });
                future.get(20, TimeUnit.SECONDS);

            } catch (Exception e) {
                event.reply("Got an error: "+e.getMessage());
                if(!service.isTerminated())
                    service.shutdownNow();
            }
        }
    }
}
