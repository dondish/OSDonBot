package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

public class MonochromeCommand extends GenericImageCommand {
    public MonochromeCommand() {
        this.name = "monochrome";
        this.usage = "^monochrome <member mention/name>";
        this.help = "Black and white";
        this.type = 4;
        this.ID = 262;
    }
}
