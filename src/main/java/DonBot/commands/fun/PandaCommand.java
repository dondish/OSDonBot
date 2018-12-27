package DonBot.commands.fun;

import DonBot.api.DonCommand;
import DonBot.api.SomeRandomAPI;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;
import java.io.IOException;

public class PandaCommand extends DonCommand {
    public PandaCommand() {
        this.name = "panda";
        this.help = "Gives a nice fact and pic of pandas!";
        this.usage = "^panda";
        this.guildOnly = true;
        this.category = new Category("fun");
        this.ID = 67;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        try {
            EmbedBuilder builder = new EmbedBuilder()
                    .setTitle("Panda Fact:")
                    .setDescription(SomeRandomAPI.getPandaFact())
                    .setColor(Color.green)
                    .setImage(SomeRandomAPI.getPandaImage())
                    .setFooter("Uses SomeRandomAPI", null);
            event.reply(builder.build());
        } catch (IOException | NullPointerException e) {
            event.reply("Got an unexpected error, please send a screenshot of this to #bugs in DonBot Central");
        }

    }
}
