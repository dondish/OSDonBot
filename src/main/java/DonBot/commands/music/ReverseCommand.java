package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ReverseCommand extends DonCommand {
    private MusicBotSetup musicBot;
    public ReverseCommand(MusicBotSetup setup) {
        this.musicBot = setup;
        this.name = "reverse";
        this.help = "Reverses the entire queue";
        this.usage = "^reverse";
        this.category = new Category("music");
        this.ID = 1037;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        manager.reverse();
        event.reply("Queue reversed!");
    }
}
