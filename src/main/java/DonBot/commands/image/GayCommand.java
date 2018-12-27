package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

public class GayCommand extends GenericImageCommand {
    public GayCommand() {
        this.name = "gay";
        this.usage = "^gay <member mention/name>";
        this.help = "You know he is gay.";
        this.type = 1;
        this.url = "https://dondish.ml/images/gay.PNG";
        this.alpha = 0.3f;
        this.ID = 260;
    }
}
