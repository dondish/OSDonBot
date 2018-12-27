package DonBot.commands.other;

import DonBot.api.DonCommand;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EvalCommand extends DonCommand {
    private MusicBotSetup setup;

    public EvalCommand(MusicBotSetup setup) {
        this.name = "eval";
        this.ownerCommand = true;
        this.guildOnly = false;
        this.ID = 2058;
        this.setup = setup;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        String code = event.getArgs();
        ScriptEngine se = new ScriptEngineManager().getEngineByName("nashorn");

        se.put("event", event);
        se.put("provider", event.getClient().getSettingsFor(event.getGuild()));
        se.put("jda", event.getJDA());
        se.put("channel", event.getTextChannel());
        se.put("message", event.getMessage());
        se.put("guild", event.getGuild());
        se.put("music", setup.getManagerFromGuild(event.getGuild()));
        se.put("player", setup.getManagerFromGuild(event.getGuild()).getPlayer());
        try {
            EmbedBuilder eval = new EmbedBuilder();
            eval.addField(":inbox_tray:", "```java\n"+code+"```", false);
            eval.addField(":outbox_tray:", "```java\n" +
                    se.eval(Stream.of(code).collect(Collectors.joining("  "))) + "```", false);
            eval.setColor(Color.GREEN);

            event.reply(eval.build());
        } catch (Exception e) {
            event.getChannel().sendMessage("Error:\n```\n" + e + "```").queue();
        }

    }
}
