package DonBot.commands.general;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

public class RedeemCommand extends DonCommand {
    public RedeemCommand() {
        this.name = "redeem";
        this.help = "Redeem premium key for your server.";
        this.usage = "USE ONLY IN THE SERVER OF YOUR CHOICE. ^redeem <key>";
        this.category = new Category("general");
        this.cooldown = 10;
        this.guildOnly = true;
        this.ID = 134;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        event.reply("Premium is now free so the redeem command had been deprecated.");
//        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
//        try {
//            if (event.getArgs().length()==32&&provider.redeemKey(event.getArgs(), event.getGuild().getIdLong())) {
//                event.reply("Key has been redeemed in this server!");
//            } else {
//                event.reply("Key is used or doesn't exist.");
//            }
//        } catch (Exception e) {
//            event.reply(e.getMessage());
//        }
    }
}
