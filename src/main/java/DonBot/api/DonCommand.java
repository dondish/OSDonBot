package DonBot.api;

import DonBot.features.CommandStats;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;

public abstract class DonCommand extends Command {
    public String usage = "";
    protected int ID;
    private CommandStats stats;
    boolean nsfw;

    /**
     * Get the statistics of the command
     * @return the statistics of the command
     */
    public CommandStats getStats() {
        if (stats == null)
            stats = new CommandStats(ID);
        return stats;
    }

    public int getID() {
        return ID;
    }

    @Override
    protected void execute(CommandEvent event) {
        Logger log = LoggerFactory.getLogger(this.getClass());
        try {
            getStats().use();
        } catch (SQLException e) {
            event.reply("Problems contacting the database. if this message keeps on appearing please contact dondish#7155 immediately.");
            log.error(e.getMessage());
        }
    }
}
