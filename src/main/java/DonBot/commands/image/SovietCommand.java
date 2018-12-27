package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

public class SovietCommand extends GenericImageCommand {
    public SovietCommand() {
        this.name = "soviet";
        this.usage = "^soviet <member mention/name>";
        this.help = "Tag a fellow comrade.";
        this.type = 1;
        this.url = "https://dondish.ml/images/ussr.jpg";
        this.alpha = 0.6f;
        this.ID = 264;
    }
}
