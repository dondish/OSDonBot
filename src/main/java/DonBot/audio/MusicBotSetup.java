package DonBot.audio;

import DonBot.utils.SearchUtils;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.source.AudioSourceManagers;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.Guild;

import java.util.HashMap;
import java.util.Map;

/**
 * A class to help with the making of the music bot.
 * manager of all GuildManagers.
 */
public class MusicBotSetup {
    private final Map<Long, DonGuildManager> managers = new HashMap<>();
    AudioPlayerManager pmanager;
    public SearchUtils utils = new SearchUtils();

    /**
     * Constructor that gives basic audio output format configuration.
     * @param manager Audio player manager from where to take the player
     */
    public MusicBotSetup(AudioPlayerManager manager) {
        pmanager = manager;
        AudioSourceManagers.registerRemoteSources(manager);

        pmanager = new DefaultAudioPlayerManager();
        pmanager.registerSourceManager(new YoutubeAudioSourceManager());
        pmanager.registerSourceManager(new SoundCloudAudioSourceManager());
        pmanager.registerSourceManager(new TwitchStreamAudioSourceManager());
        pmanager.registerSourceManager(new BeamAudioSourceManager());
        pmanager.registerSourceManager(new HttpAudioSourceManager());
    }

    /**
     * A function that returns for every guild the GuildManager
     * @param guild the guild you want to take the manager from
     * @return The manager of the guild
     */
    public synchronized DonGuildManager getManagerFromGuild(Guild guild) {
        long id = guild.getIdLong();
        DonGuildManager gmanager = this.managers.get(id);
        if (gmanager == null) {
            JDA jda = guild.getJDA();
            gmanager = new DonGuildManager(jda, guild, this);
            managers.put(id, gmanager);
        }

        return gmanager;
    }

    /**
     * Clear the managers
     * @param guild
     */
    public synchronized void stopGuildManager(Guild guild) {
        managers.remove(guild.getIdLong());
    }

    public synchronized int guildPlayingCount() {
        return managers.size();
    }
}
