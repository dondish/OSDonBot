package DonBot.commands.music;

import DonBot.api.DonCommand;
import DonBot.audio.DonGuildManager;
import DonBot.audio.MusicBotSetup;
import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.SearchUtils;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioLoadResultHandler;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.tools.FriendlyException;
import com.sedmelluq.discord.lavaplayer.track.AudioPlaylist;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.managers.AudioManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class PlayTopCommand extends DonCommand {
    private EventWaiter waiter;
    private MusicBotSetup musicbot;
    private Logger log = LoggerFactory.getLogger(PlayTopCommand.class);

    public PlayTopCommand(EventWaiter waiter, MusicBotSetup setup) {
        this.musicbot = setup;
        this.name = "playtop";
        this.waiter = waiter;
        this.category = new Category("music");
        this.help = "Queues to the top of the queue";
        this.guildOnly = true;
        this.aliases = new String[] {"pt"};
        this.usage = "^play <url/words>";
        this.ID = 1043;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        DonGuildManager manager = musicbot.getManagerFromGuild(event.getGuild());
        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
        Role DJ = provider.getDJ();
        if (DJ != null && !event.getMember().getRoles().contains(DJ) && provider.issOnlyDJModerates()) {
            event.reply("In this server only DJs can use this command!");
            return;
        }
        AudioPlayer player = manager.getPlayer();
        AudioManager mng = event.getGuild().getAudioManager();
        /*
         * Check if user is connected to a voice call
         */
        if (!mng.isConnected() && !mng.isAttemptingToConnect()) {
            if (!event.getMember().getVoiceState().inVoiceChannel()) {
                event.reply("You are not in a voice channel. please join then try again.");
                return;
            }
            mng.openAudioConnection(event.getMember().getVoiceState().getChannel());
        }

        /*
         * if user is connected and not in the same voicechannel you should move to his
         */
        if (mng.isConnected() && mng.getConnectedChannel().getIdLong() != event.getMember().getVoiceState().getChannel().getIdLong()) {
            player.setPaused(true);
            event.getGuild().getAudioManager().openAudioConnection(event.getMember().getVoiceState().getChannel());
            player.setPaused(false);
            return;
        }
        if (Objects.equals(event.getArgs(), "")) {
            // Resume player
            if (player.isPaused()) {
                player.setPaused(false);
                event.reply("The player has been resumed.");
            } else if (manager.getCurrent() != null) {
                event.reply("The player is already playing!");
            } else if (manager.queue.isEmpty()) {
                event.reply("The queue is empty!");
            }
        } else {
            try {
                // Check for a URL to play.
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
                        manager.nextTopSong(track, event.getAuthor());
                    }

                    @Override
                    public void playlistLoaded(AudioPlaylist playlist) {
                        AudioTrack track = playlist.getSelectedTrack();
                        manager.setChannel(event.getTextChannel());

                        if (track == null) {
                            track = playlist.getTracks().get(0);
                        }

                        manager.nextTopSong(track, event.getAuthor());

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
                // Search in Youtube and SoundCloud
                event.getChannel().sendMessage("Searching...").queue((message -> {
                    try {
                        AudioPlaylist results = musicbot.utils.searchYoutube(event.getArgs(), manager.pmanager);
                        message.delete().queue();
                        if (results.getTracks().isEmpty()) {
                            event.reply("Got no results from `"+event.getArgs()+"`\n");
                        } else {
                            List<AudioTrack> tracks = results.getTracks().subList(0, Math.min(results.getTracks().size(), 5));
                            StringBuilder sb = new StringBuilder();
                            sb.append("Choose one of the following songs: **[1-5]**\n");
                            int i = 1;
                            for (AudioTrack track : tracks) {
                                sb.append(String.format("%d. %s\n", i, TextUtils.trackToString(track)));
                                i+=1;
                            }
                            sb.append("\n**type `cancel` to cancel**");
                            event.getChannel().sendMessage(sb.toString()).queue(message1 -> waiter.waitForEvent(MessageReceivedEvent.class,
                                    ev -> (ev.getAuthor().equals(event.getAuthor()) && ev.getChannel().equals(event.getChannel()))&&
                                            ev.getMessage().getContentDisplay().matches("[12345]|(cancel)"),
                                    ev -> {
                                        if (ev.getMessage().getContentDisplay().equals("cancel")) {
                                            try {
                                                message1.delete().queue();
                                                ev.getMessage().delete().queue();
                                            } catch (Exception ignored) {}
                                            return;
                                        }

                                        int j;
                                        try {j = Integer.parseInt(ev.getMessage().getContentDisplay())-1;} catch (Exception eve) {return;}
                                        message1.delete().queue();
                                        try {ev.getMessage().delete().queue();} catch (Exception ignored) {}
                                        manager.setChannel(ev.getTextChannel());
                                        manager.nextTopSong(tracks.get(j), event.getAuthor());
                                    }, 20, TimeUnit.SECONDS, message1.delete()::queue));
                        }
                    } catch (SearchUtils.CooldownException ex) {
                        event.reply("Youtube blocked this bot right now, I am working on a fix, please try again later.");
                    } catch (SearchUtils.SearchingException ex) {
                        event.reply("Sorry but there was an error searching `" + event.getArgs() + "`");
                    }
                }));
            }
        }
    }
}
