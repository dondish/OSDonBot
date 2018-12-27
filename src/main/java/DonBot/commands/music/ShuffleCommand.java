package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ShuffleCommand extends DonCommand {
    private MusicBotSetup musicBot;

    public ShuffleCommand(MusicBotSetup musicBot) {
        this.musicBot = musicBot;
        this.name = "shuffle";
        this.help = "Shuffles the queue";
        this.category = new Category("music");
        this.guildOnly = true;
        this.usage = "^shuffle";
        this.ID = 1039;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
        manager.shuffle();
        event.reply("Queue shuffled!");
    }
}
