package DonBot.commands.moderation;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

public class PurgeCommand extends DonCommand {
    public PurgeCommand(){
        this.name = "purge";
        this.usage= "^purge <amountOfMessagesToPurge>";
        this.help = "Purges an amount of messages in the channel";
        this.category = new Category("moderation");
        this.guildOnly = true;
        this.ID = 515;
        this.userPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
        this.botPermissions = new Permission[]{Permission.MESSAGE_MANAGE};
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        if (event.getArgs().equals("")) {
            event.reply("Correct use is: " + this.usage);
        } else {
            try {
                event.getChannel().getIterableHistory().takeAsync(Math.min(50, Integer.parseInt(event.getArgs()))).thenAccept(event.getChannel()::purgeMessages);
            } catch (Exception ignored) {}
        }
    }
}
