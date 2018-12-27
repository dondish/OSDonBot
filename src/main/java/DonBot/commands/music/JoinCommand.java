package DonBot.commands.music;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.managers.AudioManager;

/**
 * Joins the requester's channel
 */
public class JoinCommand extends DonCommand {
    public JoinCommand() {
        this.name = "join";
        String[] aliases = {"connect"};
        this.category = new Category("music");
        this.aliases = aliases;
        this.guildOnly = true;
        this.help = "Joins a voice channel";
        this.usage = "^join";
        this.ID = 1028;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        AudioManager mng = event.getGuild().getAudioManager();
        if (!mng.isConnected() && !mng.isAttemptingToConnect()) {
            if (!event.getMember().getVoiceState().inVoiceChannel()) {
                event.reply("You are not in a voice channel. please join then try again.");
                return;
            }
            mng.openAudioConnection(event.getMember().getVoiceState().getChannel());
        }
    }
}
