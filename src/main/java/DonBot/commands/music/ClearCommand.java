package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.MusicBotSetup;
import DonBot.utils.RoleUtils;
import com.jagrosh.jdautilities.command.CommandEvent;

public class ClearCommand extends DonCommand {
    private MusicBotSetup musicBot;
    public ClearCommand(MusicBotSetup musicBot){
        this.musicBot = musicBot;
        this.name = "clear";
        this.help = "Clears the queue";
        this.usage = "^clear";
        this.category = new Category("music");
        this.guildOnly = true;
        this.ID = 1025;
    }
    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (!musicBot.getManagerFromGuild(event.getGuild()).isPlaying()) {
            event.reply("Not playing anything...");
        } else {
            if (RoleUtils.isDJorMod(event)) {
                int count = musicBot.getManagerFromGuild(event.getGuild()).queue.size();
                musicBot.getManagerFromGuild(event.getGuild()).queue.clear();
                event.reply("Queue cleared! `"+count+"` songs removed.");
            } else {
                event.reply("Sorry DJ role is required to clear queue.");
            }
        }
    }
}
