package DonBot.api;
import com.google.gson.Gson;
import com.jagrosh.jdautilities.command.Command;
import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static spark.Spark.*;

public class WebAPI {
    public static void loadServer(CommandClient client, ShardManager sm) {
        get("/commands", (req, res) -> {
            res.type("application/json");
            List<SimplifiedCommand> commands = convert(client.getCommands());
            return new Gson().toJson(commands);
        });
        get("/guilds", (req, res)->String.valueOf(sm.getGuildCache().size()));
        get("/guild/:id", ((request, response) -> {
            response.type("application/json");
            Guild guild = sm.getGuildById(request.params(":id"));
            if (guild == null) {
                HashMap<String, Boolean> hm = new HashMap<>();
                hm.put("error", true);
                return new Gson().toJson(hm);
            } else {
                HashMap<String, Object> result = new HashMap<>();
                result.put("roles", convertR(guild.getRoles()));
                result.put("channels", convertS(guild.getTextChannels()));
                return new Gson().toJson(result);
            }
        }));
    }

    /**
     * To speed up things I give only vital information
     * @param commands the commands to simplify
     * @return a new list of simplified commands
     */
    private static List<SimplifiedCommand> convert(List<Command> commands) {
        List<SimplifiedCommand> result = new ArrayList<>();
        for (Command command: commands) {
            result.add(new SimplifiedCommand((DonCommand) command));
        }
        return result;
    }

    private static List<SimplifiedRole> convertR(List<Role> roles) {
        List<SimplifiedRole> result = new ArrayList<>();
        for (Role role: roles) {
            result.add(new SimplifiedRole(role));
        }
        return result;
    }

    private static List<SimplifiedChannel> convertS(List<TextChannel> channels) {
        List<SimplifiedChannel> result = new ArrayList<>();
        for (TextChannel channel: channels) {
            result.add(new SimplifiedChannel(channel));
        }
        return result;
    }


    @SuppressWarnings("unused")
    private static class SimplifiedCommand {
        String name;
        String help;
        String usage;
        String[] aliases;
        String category;

        SimplifiedCommand(DonCommand command) {
            this.name = command.getName();
            this.help = command.getHelp();
            this.aliases = command.getAliases();
            this.usage = command.usage;
            this.category = command.getCategory() == null ? "" : command.getCategory().getName();
        }
    }

    @SuppressWarnings("unused")
    private static class SimplifiedRole {
        String name;
        String id;

        SimplifiedRole(Role role) {
            this.name = role.getName();
            this.id = role.getId();
        }
    }

    @SuppressWarnings("unused")
    private static class SimplifiedChannel {
        String name;
        String id;

        SimplifiedChannel(TextChannel channel) {
            this.name = channel.getName();
            this.id = channel.getId();
        }
    }
}
