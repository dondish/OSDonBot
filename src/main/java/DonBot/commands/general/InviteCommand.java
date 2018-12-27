package DonBot.commands.general;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;

public class InviteCommand extends DonCommand {
    public InviteCommand() {
        this.name = "invite";
        this.help = "Invite this bot to your server!";
        this.category = new Category("general");
        this.botPermissions = new Permission[]{Permission.MESSAGE_WRITE};
        this.guildOnly = false;
        this.usage = "^invite";
        this.ID = 129;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        MessageEmbed embed = new EmbedBuilder().setTitle("Invite me!", "https://discordapp.com/api/oauth2/authorize?client_id=420517675509350400&permissions=36924481&scope=bot")
            .setColor(Color.GREEN).build();
        event.reply(embed);
    }
}
