package DonBot.utils;

import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.BasicAudioPlaylist;
import net.dv8tion.jda.core.managers.AudioManager;

import javax.annotation.Nonnull;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class SearchUtils {
    private long cooldown;

    public static class CooldownException extends Exception {

        CooldownException(String message) {
            super(message);
        }
    }

    public static void hastebinPlaylist(CommandEvent event, MusicBotSetup musicBot) throws IOException {
        String url = event.getArgs().split("\\s+")[0];
        String modified = "https://hastebin.com/raw/" + url.split("/")[3].split("\\.")[0];
        URL uri = new URL(modified);
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.addRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<String> lines = new ArrayList<>();
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.contains("package")) continue;
            lines.add(line);
        }
        event.reply("Loading `"+lines.size()+"` songs from playlist `"+url.split("/")[3].split("\\.")[0]+"`, it might take some time.");
        for (String line: lines) {
            try {
                new URL(line);
                DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
                AudioManager mng = event.getGuild().getAudioManager();
                manager.pmanager.loadItemOrdered(mng, line, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        manager.setChannel(event.getTextChannel());
                        manager.nextSong(track, event.getAuthor(), "");
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        AudioTrack track = playlist.getSelectedTrack();

                        if (track == null) {
                            track = playlist.getTracks().get(0);
                        }

                        manager.nextSong(track, event.getAuthor(), "");

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
                event.reply("Error loading url: " + line);
            }
        }
    }

    public static void wastebinPlaylist(CommandEvent event, MusicBotSetup musicBot) throws IOException {
        String url = event.getArgs().split("\\s+")[0];
        String modified = "https://wastebin.party/raw/" + url.split("/")[3].split("\\.")[0];
        URL uri = new URL(modified);
        HttpURLConnection connection = (HttpURLConnection) uri.openConnection();
        connection.setDoOutput(true);
        connection.addRequestProperty("User-Agent",
                "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.0)");
        connection.setConnectTimeout(10000);
        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        List<String> lines = new ArrayList<>();
        while (reader.ready()) {
            String line = reader.readLine();
            if (line.contains("package")) continue;
            lines.add(line);
        }
        event.reply("Loading `"+lines.size()+"` songs from playlist `"+url.split("/")[3].split("\\.")[0]+"`, it might take some time.");
        for (String line: lines) {
            try {
                new URL(line);
                DonGuildManager manager = musicBot.getManagerFromGuild(event.getGuild());
                AudioManager mng = event.getGuild().getAudioManager();
                manager.pmanager.loadItemOrdered(mng, line, new AudioLoadResultHandler() {
                    @Override
                    public void trackLoaded(AudioTrack track) {
                        manager.setChannel(event.getTextChannel());
                        manager.nextSong(track, event.getAuthor(), "1");
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        AudioTrack track = playlist.getSelectedTrack();

                        if (track == null) {
                            track = playlist.getTracks().get(0);
                        }

                        manager.nextSong(track, event.getAuthor(), "1");

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
                event.reply("Error loading url: " + line);
            }
        }
    }

    public AudioPlaylist searchYoutube(String query, AudioPlayerManager manager) throws CooldownException, SearchingException{
        if (System.currentTimeMillis() < cooldown) {
            throw new CooldownException("Youtube is on cooldown!");
        }
        try {
            AudioPlaylist result = new SearchResultHandler().searchSync(manager, query);
            if (!result.getTracks().isEmpty()) {
                return result;
            }
            return new BasicAudioPlaylist("No Matches", Collections.emptyList(), null, true);
        } catch (Http503Exception e) {
            cooldown = System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(10);
            throw e;
        }
    }

    public static class SearchingException extends Exception {
        private static final long serialVersionUID = -1020150337258395420L;

        SearchingException(String message) {
            super(message);
        }

        SearchingException(String message, Exception cause) {
            super(message, cause);
        }
    }

    public static class Http503Exception extends SearchingException {
        private static final long serialVersionUID = -2698566544845714550L;

        public Http503Exception(String message) {
            super(message);
        }

        Http503Exception(String message, Exception cause) {
            super(message, cause);
        }
    }

    class SearchResultHandler implements AudioLoadResultHandler {
        Exception exception;
        AudioPlaylist result;

        @Nonnull
        AudioPlaylist searchSync(AudioPlayerManager playerManager, String query) throws SearchingException{
            try {
                playerManager.loadItem("ytsearch:" + query, this).get(20, TimeUnit.SECONDS);

            } catch (ExecutionException|InterruptedException e) {
                exception = e;
                e.printStackTrace();
            } catch (TimeoutException e) {
                throw new SearchingException(String.format("Failed to search youtube for %s", query));
            }

            if (exception != null) {
                if (exception instanceof FriendlyException && exception.getCause() != null) {
                    if (exception.getCause().getMessage().contains("java.io.IOException: Invalid status code for search response: 503")) {
                        // error 503
                        throw new Http503Exception("Lavaplayer search returned 503 error code", exception);
                    }
                }
                throw new SearchingException("Problem searching youtube.");
            }

            if (result != null) {
                return result;
            }
            throw new SearchingException(String.format("Result from youtube for query %s is unexpectedly null", query));

        }

        @Override
        public void trackLoaded(AudioTrack track) {
            exception = new UnsupportedOperationException("Looking for playlists only");
        }

        @Override
        public void playlistLoaded(AudioPlaylist playlist) {
            result = playlist;
        }

        @Override
        public void noMatches() {
            result = new BasicAudioPlaylist("No Matches", Collections.emptyList(), null, true);
        }

        @Override
        public void loadFailed(FriendlyException exception) {
            this.exception = exception;
        }
    }

}
