package DonBot.commands.other;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

public class CreateKeyCommand extends DonCommand {
    public CreateKeyCommand() {
        this.name = "createkey";
        this.guildOnly = false;
        this.hidden = true;
        this.ownerCommand = true;
        this.ID = 2057;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
//        try {
//            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
//            MessageDigest digest = MessageDigest.getInstance("MD5");
//            byte[] a = new byte[10];
//            new Random().nextBytes(a);
//            String s = new String(Hex.encodeHex(digest.digest(a)));
//            event.reply(s);
//            provider.addKey(s);
//
//        } catch (Exception e) {
//            event.reply(e.getMessage());
//        }
    }
}
