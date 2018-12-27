package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

public class InvertCommand extends GenericImageCommand {
    public InvertCommand() {
        this.name = "invert";
        this.usage = "^invert <member mention/name>";
        this.help = "Invert the color to make a weird look";
        this.type = 3;
        this.ID = 261;
    }
}
