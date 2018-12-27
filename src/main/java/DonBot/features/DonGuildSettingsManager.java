package DonBot.features;

import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.GuildSettingsManager;
import net.dv8tion.jda.core.entities.Guild;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.util.HashMap;

public class DonGuildSettingsManager implements GuildSettingsManager{
    private HashMap<Guild, DonGuildSettingsProvider> mapper = new HashMap<>();
    private DatabaseManager manager;
    private final MusicBotSetup setup;

    public DonGuildSettingsManager(MusicBotSetup setup) {
        this.setup = setup;
        try {
        manager = new DatabaseManager();
        CommandStats.manager = manager;
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    @Override
    public void init() {
    }

    @Override
    public void shutdown() {
        try {
            manager.dbcp.close();
            manager.pool.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Nullable
    @Override
    public Object getSettings(Guild guild) {
        if (!mapper.containsKey(guild)) {
            mapper.put(guild, new DonGuildSettingsProvider(guild, manager, setup));
        }
        return mapper.get(guild);
    }

}
