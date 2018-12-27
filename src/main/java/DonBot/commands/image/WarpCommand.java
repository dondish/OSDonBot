package DonBot.commands.image;

import DonBot.api.GenericImageCommand;
import com.jagrosh.jdautilities.command.CommandEvent;

public class WarpCommand extends GenericImageCommand {
    public WarpCommand() {
        this.name = "warp";
        this.usage = "^warp <member mention/name>";
        this.help = "Warps the avatar";
        this.type = 5;
        this.ID = 266;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
    }
}
