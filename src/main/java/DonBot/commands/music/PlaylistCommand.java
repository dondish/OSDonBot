package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.managers.AudioManager;

import java.net.MalformedURLException;
import java.net.URL;

public class PlaylistCommand extends DonCommand {
    private MusicBotSetup musicbot;

    public PlaylistCommand(MusicBotSetup setup) {
        this.name = "playlist";
        this.usage = "^playlist <youtube playlist url>";
        this.help = "Plays a youtube playlist";
        this.category = new Category("music");
        this.cooldown = 3;
        this.guildOnly = true;
        this.musicbot = setup;
        this.ID = 1032;
    }
    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicbot.getManagerFromGuild(event.getGuild());
        AudioPlayer player = manager.getPlayer();
        AudioManager mng = event.getGuild().getAudioManager();
        if (!mng.isConnected() && !mng.isAttemptingToConnect()) {
            if (!event.getMember().getVoiceState().inVoiceChannel()) {
                event.reply("You are not in a voice channel. please join then try again.");
                return;
            }
            mng.openAudioConnection(event.getMember().getVoiceState().getChannel());
        }
        if (mng.isConnected() && mng.getConnectedChannel().getIdLong() != event.getMember().getVoiceState().getChannel().getIdLong()) {
            player.setPaused(true);
            event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
            player.setPaused(false);
        }
        try {
            String url = event.getArgs();
            if (event.getArgs().startsWith("<") && event.getArgs().endsWith(">")){
                // Discord usual prevention
                url = url.substring(1, url.length() - 1);
            }
            final String uri = url;
            new URL(url);
            manager.pmanager.loadItemOrdered(mng, url, new AudioLoadResultHandler() {
                @Override
                public void trackLoaded(AudioTrack track) {
                    manager.setChannel(event.getTextChannel());
                    manager.nextSong(track, event.getAuthor());
                }

                @Override
                public void playlistLoaded(AudioPlaylist playlist) {
                    event.reply("Loading playlist " + playlist.getName() + " with "+playlist.getTracks().size()+" songs.");
                    manager.setChannel(event.getTextChannel());
                    for (AudioTrack track : playlist.getTracks()) {
                        manager.nextSong(track, event.getAuthor(), "");
                    }
                }

                @Override
                public void noMatches() {
                    event.reply("No results from url " + uri);
                }

                @Override
                public void loadFailed(FriendlyException exception) {
                    event.reply("Could not play: " + exception.getMessage());

                }
            });
        } catch (MalformedURLException e) {
            event.reply("Please type in a valid url.");
        }
    }
}
