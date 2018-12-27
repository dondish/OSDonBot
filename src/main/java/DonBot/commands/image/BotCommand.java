package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

import java.awt.*;

public class BotCommand extends GenericImageCommand {
    public BotCommand() {
        this.name = "bot";
        this.usage = "^bot <member mention/name>";
        this.help = "He has certain \"problems\" with captchas";
        this.type = 0;
        this.url = "https://dondish.ml/images/captcha.png";
        this.lx = 238;
        this.ly = 554;
        this.bx = 433;
        this.by = 795;
        this.fillRect = false;
        this.color = Color.RED;
        this.ID = 259;
    }
}