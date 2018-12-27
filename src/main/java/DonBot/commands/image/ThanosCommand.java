package DonBot.commands.image;

import DonBot.api.GenericImageCommand;

public class ThanosCommand extends GenericImageCommand {
    public ThanosCommand() {
        this.name = "thanos";
        this.help = "Thanos kills someone :eyes:";
        this.usage = "^thanos <mention/user name>``";
        this.aliases = new String[]{"helpme", "idontfeelsogood"};
        this.type = 6;
        this.ID = 265;
    }
}
