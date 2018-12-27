package DonBot.audio;

import com.sedmelluq.discord.lavaplayer.source.youtube.YoutubeAudioTrack;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.entities.User;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class SongInfo {
    Set<User> skips;
    public User requester;
    public AudioTrack track;
    public String thumbnail;

    SongInfo(User requester, AudioTrack track){
        this.requester = requester;
        this.track = track;
        if (track instanceof YoutubeAudioTrack) {
            thumbnail = "https://img.youtube.com/vi/"+track.getIdentifier()+"/0.jpg";
        }
        this.skips = new HashSet<>();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SongInfo songInfo = (SongInfo) o;
        return Objects.equals(skips, songInfo.skips) &&
                Objects.equals(requester, songInfo.requester) &&
                Objects.equals(track, songInfo.track);
    }

    @Override
    public int hashCode() {

        return Objects.hash(skips, requester, track);
    }
}
