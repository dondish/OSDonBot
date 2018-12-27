package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

public class BlurpleCommand extends GenericImageCommand {
    public BlurpleCommand() {
        this.name = "blurple";
        this.usage = "^blurple <member mention/name>";
        this.help = "Favorite discord color for you avatar.";
        this.aliases = new String[]{"blurp"};
        this.type = 2;
        this.ID = 258;
    }
}
