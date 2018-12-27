package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

import java.awt.*;

public class AutistCommand extends GenericImageCommand {
    public AutistCommand() {
        this.name = "autist";
        this.usage = "^autist <member mention/name>";
        this.help = "Autist someone you hate.";
        this.aliases = new String[]{"aut"};
        this.url = "https://dondish.ml/images/aut.PNG";
        this.type = 0;
        this.lx = 38;
        this.ly = 57;
        this.bx = 254;
        this.by = 218;
        this.fillRect = true;
        this.color = new Color(219, 253, 254);
        this.ID = 256;
    }
}
