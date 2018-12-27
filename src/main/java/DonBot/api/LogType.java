package DonBot.api;

import net.dv8tion.jda.core.EmbedBuilder;

import java.awt.*;

public enum LogType {
    MEMBERJOIN (1, Color.CYAN, "Member Joined"),
    MEMBERLEAVE (2, Color.yellow, "Member Left"),
    CHANNELCREATED (4, Color.GREEN, "Channel Created"),
    CHANNELDELETED (8, Color.red, "Channel Deleted"),
    VOICEUPDATE(16, Color.blue, "Voice Channel Updated"),
    MESSAGEDELETED (32, Color.orange, "Message Deleted"),
    MESSAGEEDITED (64, Color.yellow, "Message Edited"),
    MESSAGEPURGED (128, Color.red, "Messages Purged"),
    ROLECREATED (256, Color.GREEN, "Role Created"),
    ROLEEDITED (512, Color.yellow, "Role Edited"),
    ROLEDELETED (1024, Color.red, "Role Deleted"),
    NICKNAMECHANGE (2048, Color.cyan, "Nickname Changed"),
    MEMBERBANNED (4096, Color.red, "Member Banned"),
    MEMBERUNBANNED (8192, Color.green, "Member Unbanned");

    private int id;
    private Color color;
    private String title;
    LogType (int id, Color color, String title) {
        this.id = id;
        this.color = color;
        this.title = title;
    }

    public static boolean ifLog(int log, LogType type) {
        return (log & type.id) != 0;
    }

    public EmbedBuilder buildLog() {
        return new EmbedBuilder().setColor(color).setTitle(title);
    }
}
