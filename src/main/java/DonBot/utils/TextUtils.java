package DonBot.utils;

import DonBot.api.DonCase;
import DonBot.audio.SongInfo;
import com.sedmelluq.discord.lavaplayer.track.AudioTrack;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.MessageEmbed;

import java.awt.*;
import java.util.concurrent.TimeUnit;

public class TextUtils {
    public static String trackToString(AudioTrack track) {
        return String.format("**%s** by **%s**["+ durationToString(track.getInfo().length) +"]", track.getInfo().title, track.getInfo().author);
    }

    public static String durationToString(long duration){
        String s = "";
        if ((int)TimeUnit.MILLISECONDS.toHours(duration)!=0){
            s += String.format("%02d:%02d:%02d", (int) TimeUnit.MILLISECONDS.toHours(duration), (int)TimeUnit.MILLISECONDS.toMinutes(duration)%60, (int)TimeUnit.MILLISECONDS.toSeconds(duration)%60);
        } else {
            s += String.format("%02d:%02d", (int)TimeUnit.MILLISECONDS.toMinutes(duration)%60, (int)TimeUnit.MILLISECONDS.toSeconds(duration)%60);
        }
        return s;
    }

    public static String durationToWordedString(long millis) {
        return String.format("%d days, %d hours, %d minutes, and %d seconds",
                (int)TimeUnit.MILLISECONDS.toDays(millis),
                (int)TimeUnit.MILLISECONDS.toHours(millis) % 24,
                (int)TimeUnit.MILLISECONDS.toMinutes(millis) % 60,
                (int)TimeUnit.MILLISECONDS.toSeconds(millis)% 60);
    }

    public static MessageEmbed getModLog(DonCase donCase) {
        EmbedBuilder builder = new EmbedBuilder();
        builder.setColor(TypeUtils.getTypeColor(donCase.type));
        builder.setTitle(TypeUtils.getTypeName(donCase.type) + " Case | ID: " + donCase.id);
        builder.addField("User", donCase.userid.getName()+"#"+donCase.userid.getDiscriminator(), false);
        builder.addField("Moderator", donCase.modid == null ? "Mod please type `-reason " +donCase.id + " <reason>`" : donCase.modid.getName()+"#"+donCase.modid.getDiscriminator(), false);
        builder.addField("Reason", donCase.reason, false);
        builder.setFooter(String.valueOf(donCase.id), null);
        return builder.build();
    }

    public static MessageEmbed getNowPlayingMessage(SongInfo track) {
        EmbedBuilder builder  = new EmbedBuilder()
                .setFooter("Requested by: " + track.requester.getName()+"#"+track.requester.getDiscriminator(), track.requester.getEffectiveAvatarUrl())
                .setTitle("Now Playing: " + track.track.getInfo().title)
                .setDescription(track.track.getInfo().author)
                .addField("Position", TextUtils.durationToString(track.track.getPosition())+"/"+TextUtils.durationToString(track.track.getDuration()), true)
                .addField("URL", "[Click Me!]("+track.track.getInfo().uri+")", true)
                .setColor(Color.cyan);
        if (track.thumbnail != null) {
            builder.setImage(track.thumbnail);
        }
        return builder.build();
    }

    public static MessageEmbed getEnqueuedMessage(SongInfo track) {
        EmbedBuilder builder = new EmbedBuilder()
                .setFooter("Requested by: " + track.requester.getName()+"#"+track.requester.getDiscriminator(), track.requester.getEffectiveAvatarUrl())
                .setTitle("Enqueued: " + track.track.getInfo().title)
                .setDescription(track.track.getInfo().author)
                .addField("URL", "[Click Me!]("+track.track.getInfo().uri+")", true)
                .setColor(Color.green);
        if (track.thumbnail != null) {
            builder.setImage(track.thumbnail);
        }
        return builder.build();
    }

    public static MessageEmbed getSkippingMessage(SongInfo track) {
        EmbedBuilder builder = new EmbedBuilder()
                .setFooter("Requested by: " + track.requester.getName()+"#"+track.requester.getDiscriminator(), track.requester.getEffectiveAvatarUrl())
                .setTitle("Skipping: " + track.track.getInfo().title)
                .setDescription(track.track.getInfo().author)
                .addField("Position", TextUtils.durationToString(track.track.getPosition())+"/"+TextUtils.durationToString(track.track.getDuration()), true)
                .addField("URL", "[Click Me!]("+track.track.getInfo().uri+")", true)
                .setColor(Color.yellow);
        if (track.thumbnail != null) {
            builder.setImage(track.thumbnail);
        }
        return builder.build();
    }
}
