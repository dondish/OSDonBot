package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RepeatCommand extends DonCommand {
    private MusicBotSetup musicBot;

    public RepeatCommand(MusicBotSetup setup) {
        musicBot = setup;
        this.name = "repeat";
        this.usage = "^repeat <none/song/queue>";
        this.help = "Toggle between repeat modes";
        this.guildOnly = true;
        this.category = new Category("music");
        this.ID = 1035;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        String args = event.getArgs();
        switch (args) {
            case "off":
            case "none":
                manager.setLoop(false);
                manager.setLoopq(false);
                event.reply("Repeat turned off");
                break;
            case "song":
            case "track":
                manager.setLoop(true);
                manager.setLoopq(false);
                event.reply("Repeat mode is now song");
                break;
            case "queue":
            case "list":
                manager.setLoopq(true);
                manager.setLoop(false);
                event.reply("Repeat mode is now queue");
                break;
            default:
                String repeatmode = "off";
                if (manager.isLoop())
                    repeatmode = "song";
                if (manager.isLoopq())
                    repeatmode = "queue";
                event.reply("Repeat mode is currently `" + repeatmode + "` to change this mode do `"+ this.usage +"`");
        }
    }
}
