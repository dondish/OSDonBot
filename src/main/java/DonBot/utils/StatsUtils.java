package DonBot.utils;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;

public class StatsUtils {
    private static long startTime;

    public static void init(long startTime) {
        StatsUtils.startTime = startTime;
    }

    public static long getUptime() {
        return System.currentTimeMillis() - startTime;
    }

    /**
     * gets the correct category of a command ID
     * @param commandID - the ID
     * @return the category
     */
    public static Command.Category getCategoryFromID(int commandID) {

        if ((commandID & 64) != 0) {
            return new Command.Category("fun");
        } else if ((commandID & 128) != 0) {
            return new Command.Category("general");
        } else if ((commandID & 256) != 0) {
            return new Command.Category("image");
        } else if ((commandID & 512) != 0) {
            return new Command.Category("moderation");
        } else if ((commandID & 1024) != 0) {
            return new Command.Category("music");
        } else if ((commandID & 2056) != 0) {
            return new Command.Category("other");
        }
        return new Command.Category("currency");
    }

    /**
     * gets the command from a name
     * @param client - the CommandClient
     * @param name - the name of the command
     * @return the command
     */
    public static Command getCommandByName(CommandClient client, String name) {
        for (Command command: client.getCommands()) {
            if (command.getName().equals(name))
                return command;
        }
        return null;
    }

    /**
     * gets the command from an id
     * @param client - the CommandClient
     * @param ID - the ID of the command
     * @return the command
     */
    public static Command getCommandByID(CommandClient client, int ID) {
        for (Command command: client.getCommands()) {
            if (!(command instanceof DonCommand))
                continue;
            if (((DonCommand) command).getID() == ID)
                return command;
        }
        return null;
    }
}
