package DonBot.features;

import DonBot.utils.StatsUtils;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CommandStats {
    private int commandID;
    private int moduleID;
    static DatabaseManager manager;

    public CommandStats(int commandID) {
        this.commandID = commandID;
        if ((commandID&64)!=0)
            moduleID = 64;
        else if ((commandID&128)!=0)
            moduleID = 128;
        else if ((commandID&256)!=0)
            moduleID = 256;
        else if ((commandID&512)!=0)
            moduleID = 512;
        else if ((commandID&1024)!=0)
            moduleID = 1024;
        else if ((commandID&2056)!=0)
            moduleID = 2056;
        else moduleID = -1;
    }

    public Command.Category getCategory() {
        return StatsUtils.getCategoryFromID(commandID);
    }

    public int getCommandID() {
        return commandID;
    }

    public int getTotalUsages() throws SQLException {
        return manager.getTotalCommandUsage(commandID);
    }

    public static int getTotalDailyUsage() throws SQLException {
        return manager.getTotalDailyUsage();
    }

    public int getUsagesLastDay() throws SQLException {
        return manager.getDailyCommandUsage(commandID);
    }

    public int getModuleUsagesLastDay() throws SQLException {
        return manager.getDailyModuleUsage(moduleID);
    }

    public int getTotalModuleUsages() throws SQLException {
        return manager.getTotalModuleUsage(moduleID);
    }

    public void use() throws SQLException {
        manager.addCommandUsage(commandID);
    }


    public static List<Command> getTopUsedCommands(CommandClient client, int amount) {

        List<Command> topused = new ArrayList<>();
        try {
            for (int i = 1; i<=amount; i++) {
                topused.add(StatsUtils.getCommandByID(client, manager.getCommandIdByRank(i)));
            }
        } catch (SQLException ignored) { }
        return topused;
    }
}
