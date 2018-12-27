package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

import java.awt.*;

public class SlavCommand extends GenericImageCommand {
    public SlavCommand() {
        this.name = "slav";
        this.usage = "^slav <member mention/name>";
        this.help = "Slav another gopnik";
        this.type = 0;
        this.url = "https://dondish.ml/images/squat.jpg";
        this.lx = 300;
        this.ly = 40;
        this.bx = 420;
        this.by = 164;
        this.fillRect = false;
        this.color = Color.green;
        this.ID = 263;
    }
}
