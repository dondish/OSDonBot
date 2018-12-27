package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SeekCommand extends DonCommand {
    private MusicBotSetup musicBot;
    private Logger log = LoggerFactory.getLogger(SeekCommand.class);

    public SeekCommand (MusicBotSetup setup) {
        this.musicBot = setup;
        this.name = "seek";
        this.usage = "^seek [hh:[mm:[ss]]]";
        this.help = "Seeks to a point in the track";
        this.category = new Category("music");
        this.aliases = new String[]{"sk"};
        this.guildOnly = true;
        this.ID = 1038;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        Pattern p1 = Pattern.compile("(\\d){2}:(\\d){2}:(\\d){2}");
        Pattern p2 = Pattern.compile("(\\d){2}:(\\d){2}");
        Pattern p3 = Pattern.compile("(\\d){2}");
        if (event.getArgs().matches(p1.toString())) {
            Matcher matcher = p1.matcher(event.getArgs());
            matcher.find();
            int hours = Integer.parseInt(matcher.group(1));
            int minutes = Integer.parseInt(matcher.group(2));
            int seconds = Integer.parseInt(matcher.group(3));
            long total = TimeUnit.HOURS.toSeconds(hours) + TimeUnit.MINUTES.toSeconds(minutes) + seconds;
            musicBot.getManagerFromGuild(event.getGuild()).seek(total);
        } else if (event.getArgs().matches(p2.toString())){
            Matcher matcher = p2.matcher(event.getArgs());
            matcher.find();
            int minutes = Integer.parseInt(matcher.group(1));
            int seconds = Integer.parseInt(matcher.group(2));
            long total = TimeUnit.MINUTES.toSeconds(minutes) + seconds;
            musicBot.getManagerFromGuild(event.getGuild()).seek(total);
        } else if (event.getArgs().matches(p3.toString())) {
            long total = Long.parseLong(event.getArgs());
            musicBot.getManagerFromGuild(event.getGuild()).seek(total);
        } else {
            event.reply("Correct usage: " + usage);
        }
    }
}
