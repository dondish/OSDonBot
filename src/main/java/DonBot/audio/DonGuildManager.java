package DonBot.audio;

import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayer;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.event.AudioEventAdapter;
import com.sedmelluq.discord.lavaplayer.source.beam.BeamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.http.HttpAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.soundcloud.SoundCloudAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.twitch.TwitchStreamAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioSourceManager;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrackEndReason;
import com.sedmelluq.discord.lavaplayer.track.playback.AudioFrame;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audio.AudioSendHandler;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.managers.AudioManager;

import java.util.Collections;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

/**
 * Guild Manager for DonBot's music!!!
 */
public class DonGuildManager extends AudioEventAdapter implements AudioSendHandler {
    private MusicBotSetup setup;
    private AudioFrame last;
    private final Guild guild;
    private final JDA jda;
    private TextChannel channel;
    public final AudioPlayerManager pmanager;
    /**
     * repeat mode - song
     */
    private boolean loop;
    /**
     * repeat mode - queue
     */
    private boolean loopq;
    public final LinkedList<SongInfo> before;
    private AudioPlayer player;
    public final LinkedList<SongInfo> queue;
    private SongInfo current;

    public DonGuildManager(JDA jda, Guild guild, MusicBotSetup setup) {
        this.guild = guild;
        AudioManager manager = guild.getAudioManager();
        manager.setSendingHandler(this);
        pmanager = setup.pmanager;
        this.jda = jda;
        this.queue = new LinkedList<>();
        this.before = new LinkedList<>();
        this.setup = setup;
    }

    public void setChannel(TextChannel channel) {
        this.channel = channel;
    }

    /**
     * My new secret for player managing, this step will make sure a player is being made and is never stuck since a new player can be made.
     */
    private void createPlayer() {
        player = pmanager.createPlayer();
        player.addListener(this);
    }

    /**
     * Fixes most problems with tracks stuck by ruining the old player and recreating a new one it will also ease the memory
     */
    private void destroyPlayer() {
        if (player == null)
            return;
        player.destroy();
        player = null;
    }

    /**
     * not only the getter for player but also makes sure that the player is never stuck
     * @return the player
     */
    public AudioPlayer getPlayer() {
        if (player == null)
            createPlayer();
        return player;
    }

    /**
     * Use this to enqueue a track
     * @param track the track to enqueue
     * @param requester the user who requested it
     */
    public void nextSong(AudioTrack track, User requester) {
        SongInfo info = new SongInfo(requester, track);
        if (current == null)
            current = info;
        if (!player.startTrack(track, true)) {
            queue.offer(info);
            if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) { // SUPPORT FOR NO EMBEDS
                channel.sendMessage(TextUtils.getEnqueuedMessage(info)).queue();
            } else {
                channel.sendMessage("Enqueued: " + TextUtils.trackToString(track)).queue();
            }
        } else {
            current = info;
        }
    }

    /**
     * Enqueue a track without printing the 'Enqueued: bla bla bla'
     * @param track the track to enqueue
     * @param requester the user who requested it
     * @param s irrelevant
     */
    public void nextSong(AudioTrack track, User requester, String s) {
        SongInfo info = new SongInfo(requester, track);
        if (current == null)
            current = info;
        if (!player.startTrack(track, true)) {
            queue.offer(info);
        } else {
            current = info;
        }

    }

    public void nextTopSong(AudioTrack track, User requester) {
        SongInfo info = new SongInfo(requester, track);
        if (current == null)
            current = info;
        if (!player.startTrack(track, true)) {
            queue.addFirst(info);
            if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS)) { // SUPPORT FOR NO EMBEDS
                channel.sendMessage(TextUtils.getEnqueuedMessage(info)).queue();
            } else {
                channel.sendMessage("Enqueued: " + TextUtils.trackToString(track)).queue();
            }
        } else {
            current = info;
        }
    }

    /**
     * Stop the track and disconnect from the voice call
     */
    public void stop() {
        /*
            Reset the repeat modes!!! this is a very important step to make sure it won't be stuck in an infinite loop.
         */
        loop = false;
        loopq = false;
        if (player.getPlayingTrack() != null || player.isPaused())
            channel.sendMessage("Player stopped. " + (queue.isEmpty() ? "" : "`" + queue.size() + "` songs have been cleared from the queue." )).queue((msg)->{
                this.channel = null;
                this.current = null;
                player.stopTrack();
                queue.clear();
                before.clear();
                destroyPlayer();
            });
        if (guild.getAudioManager().isConnected())
            guild.getAudioManager().closeAudioConnection();
        setup.stopGuildManager(guild);
    }

    /**
     * Plays a track if there is one or stops the player completely
     */
    private void forward() {
        if (loop) {
            current = before.peekLast();
            player.startTrack(current.track.makeClone(), false);
        }
        if (queue.isEmpty()) {
            if (loopq) {
                queue.addAll(before);
                current = queue.poll();
                assert current != null;
                player.startTrack(current.track.makeClone(), false);
            } else {
                this.channel = null;
            }
        }  else {
            current = queue.poll();
            player.startTrack(current.track.makeClone(), false);
        }
    }

    /**
     * Skip the current song
     * @param member the member that wants to skip
     * @param event the commandevent
     */
    public void skip(Member member, CommandEvent event) {
        if (current == null)
            return;
        TextChannel channel = (TextChannel) event.getChannel();
        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(guild);
        if (current.skips.size()==5 || (!current.skips.contains(member.getUser())&&current.skips.size()==4) || (!provider.issOnlyDJModerates()&&event.getMember().getVoiceState().getChannel().equals(event.getGuild().getAudioManager().getConnectedChannel()))||(provider.issOnlyDJModerates())) {
            if (queue.isEmpty()&&!loopq) {
                stop();
            } else if (loopq) {
                if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS))
                    channel.sendMessage(TextUtils.getSkippingMessage(current)).queue();
                else
                    channel.sendMessage("Skipping: " + TextUtils.trackToString(current.track)).queue();
                if (queue.isEmpty()) {
                    queue.addAll(before);
                }
                player.stopTrack();
                current = queue.poll();
                player.startTrack(current.track.makeClone(), false);
            } else {
                if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS))
                    channel.sendMessage(TextUtils.getSkippingMessage(current)).queue();
                else
                    channel.sendMessage("Skipping: " + TextUtils.trackToString(current.track)).queue();
                player.stopTrack();
                current = queue.poll();
                player.startTrack(current.track.makeClone(), false);
            }
        } else if (current.skips.contains(member.getUser())) {
            channel.sendMessage(String.format("You already voted to skip. [%d/5] votes were made", current.skips.size())).queue();
        } else if (!current.skips.contains(member.getUser())){
            current.skips.add(member.getUser());
            channel.sendMessage(String.format("Added your vote to skips [%d/5]", current.skips.size())).queue();
        }
    }

    /**
     * Skips multiple songs
     * @param member the member that wants to skip
     * @param event the commandevent
     * @param songs the amount of songs to skip
     */
    public void skip(Member member, CommandEvent event, int songs) {
        if (queue.size() < songs) {
            event.reply("More songs to skip then there are in the queue!");
        } else {
            for (int i =0; i<songs-1; i++){
                current = queue.poll();
            }
            event.reply("Skipped `"+songs+"` songs.");
            player.startTrack(current.track.makeClone(), false);
        }
    }

    /**
     * This is called when a track starts
     * @param player the player
     * @param track the track
     */
    @Override
    public void onTrackStart(AudioPlayer player, AudioTrack track) {
        if (guild.getSelfMember().hasPermission(channel, Permission.MESSAGE_EMBED_LINKS))
            channel.sendMessage(TextUtils.getNowPlayingMessage(current)).queue();
        else
            channel.sendMessage("Now Playing: " + TextUtils.trackToString(current.track)).queue();
    }

    /**
     * This is called when a track ends
     * @param player the player
     * @param track the track
     * @param endReason the reason it ended
     */
    @Override
    public void onTrackEnd(AudioPlayer player, AudioTrack track, AudioTrackEndReason endReason) {
        before.add(current);
        current = null;
        if (endReason.mayStartNext)
            forward();
    }

    /**
     * RECREATE THE PLAYER WHEN STUCK
     * it is super needed!
     * @param player the player
     * @param track the stuck track
     * @param thresholdMs the amount of milliseconds for checking if stuck not relevant
     */
    @Override
    public void onTrackStuck(AudioPlayer player, AudioTrack track, long thresholdMs) {
        super.onTrackStuck(player, track, thresholdMs);
        destroyPlayer();
        createPlayer();
        forward();
    }

    /*
        Here are a bunch of setters and getters:
     */

    public void setCurrent(SongInfo current) {
        this.current = current;
    }

    public boolean isLoop() {
        return loop;
    }

    public boolean isLoopq() {
        return loopq;
    }

    public SongInfo getCurrent() {
        return current;
    }

    public long getPosition() {
        return current.track.getPosition();
    }

    public boolean isPlaying(){
        return getCurrent() != null;
    }

    public void setLoop(boolean loop) {
        this.loop = loop;
    }

    public void setLoopq(boolean loopq) {
        this.loopq = loopq;
    }

    /**
     * Shuffles the queue
     */
    public void shuffle() {
        Collections.shuffle(queue);
    }

    /**
     * Reverses the queue
     */
    public void reverse() {Collections.reverse(queue);}

    /**
     * Seek to a point in a track
     * @param seconds the amount in seconds to pass to
     */
    public void seek(long seconds) {
        current.track.setPosition(TimeUnit.SECONDS.toMillis(seconds));
    }

    /**
     * @return whether the player can provide audio packets
     */
    @Override
    public boolean canProvide() {
        last = player.provide();
        return last != null;
    }

    /**
     * @return packet data
     */
    @Override
    public byte[] provide20MsAudio() {
        return last.getData();
    }

    /**
     * @return Whether the transferred packets are Opus encoded or bare pcm
     */
    @Override
    public boolean isOpus() {
        return true;
    }
}
