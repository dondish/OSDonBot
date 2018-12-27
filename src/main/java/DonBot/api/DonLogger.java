package DonBot.api;

import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandClient;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.events.channel.category.CategoryCreateEvent;
import net.dv8tion.jda.core.events.channel.category.CategoryDeleteEvent;
import net.dv8tion.jda.core.events.channel.category.GenericCategoryEvent;
import net.dv8tion.jda.core.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.GuildReadyEvent;
import net.dv8tion.jda.core.events.guild.GuildUnbanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberNickChangeEvent;
import net.dv8tion.jda.core.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GenericGuildMessageEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageDeleteEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent;
import net.dv8tion.jda.core.events.message.guild.GuildMessageUpdateEvent;
import net.dv8tion.jda.core.events.role.GenericRoleEvent;
import net.dv8tion.jda.core.events.role.RoleCreateEvent;
import net.dv8tion.jda.core.events.role.RoleDeleteEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateColorEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdateNameEvent;
import net.dv8tion.jda.core.events.role.update.RoleUpdatePermissionsEvent;

import java.sql.SQLException;

class DonLogger {
    private static String getMemberName(Member member) {
        return String.format("%s#%s", member.getUser().getName(), member.getUser().getDiscriminator());
    }


    public static void handleGuildEvent(GenericGuildEvent event, CommandClient client) {
        try {
            if (event instanceof GuildReadyEvent) {
                return;
            }
            DonGuildSettingsProvider provider =  client.getSettingsFor(event.getGuild());
            int log = provider.getLog();
            TextChannel logchannel = provider.getLogChannel();
            if (logchannel == null) {
                return;
            }
            if (event instanceof GuildMemberJoinEvent && LogType.ifLog(log, LogType.MEMBERJOIN)) {
                logchannel.sendMessage(LogType.MEMBERJOIN.buildLog().addField("Member", getMemberName(((GuildMemberJoinEvent) event).getMember()), false).setThumbnail(((GuildMemberJoinEvent) event).getUser().getAvatarUrl()).build()).queue();
            } else if (event instanceof GuildMemberLeaveEvent && LogType.ifLog(log, LogType.MEMBERLEAVE)) {
                logchannel.sendMessage(LogType.MEMBERLEAVE.buildLog().addField("Member", getMemberName(((GuildMemberLeaveEvent) event).getMember()), false).setThumbnail(((GuildMemberLeaveEvent) event).getUser().getAvatarUrl()).build()).queue();
            } else if (event instanceof GenericGuildMessageEvent && !(event instanceof GuildMessageReceivedEvent)) {
                if (event instanceof GuildMessageDeleteEvent && LogType.ifLog(log, LogType.MESSAGEDELETED)) {
                    logchannel.sendMessage(LogType.MESSAGEDELETED.buildLog()
                            .addField("Channel", ((GuildMessageDeleteEvent) event).getChannel().getName(), false).build())
                            .queue();
                } else if (event instanceof GuildMessageUpdateEvent && LogType.ifLog(log, LogType.MESSAGEEDITED)) {
                    logchannel.sendMessage(LogType.MESSAGEEDITED.buildLog()
                            .addField("Author", getMemberName(((GuildMessageUpdateEvent) event).getMember()), false)
                            .setTimestamp(((GuildMessageUpdateEvent) event).getMessage().getEditedTime()).build()).queue();
                }
            } else if (event instanceof GuildMemberNickChangeEvent && LogType.ifLog(log, LogType.NICKNAMECHANGE)) {
                logchannel.sendMessage(LogType.NICKNAMECHANGE.buildLog().addField("Previous Nickname", ((GuildMemberNickChangeEvent) event).getPrevNick() == null ? "" : ((GuildMemberNickChangeEvent) event).getPrevNick(), false)
                        .addField("New Nickname", ((GuildMemberNickChangeEvent) event).getNewNick() == null ? "" : ((GuildMemberNickChangeEvent) event).getNewNick(), false).build()).queue();
            } else if (event instanceof GuildBanEvent && LogType.ifLog(log, LogType.MEMBERBANNED)) {
                logchannel.sendMessage(LogType.MEMBERBANNED.buildLog().addField("User", ((GuildBanEvent) event).getUser().getName() + "#" + ((GuildBanEvent) event).getUser().getDiscriminator(), false).build()).queue();
            } else if (event instanceof GuildUnbanEvent && LogType.ifLog(log, LogType.MEMBERUNBANNED)) {
                logchannel.sendMessage(LogType.MEMBERUNBANNED.buildLog().addField("User", ((GuildUnbanEvent) event).getUser().getName() + "#" + ((GuildUnbanEvent) event).getUser().getDiscriminator(), false).build()).queue();
            } else if (event instanceof GuildVoiceMoveEvent && LogType.ifLog(log, LogType.VOICEUPDATE)) {
                logchannel.sendMessage(LogType.VOICEUPDATE.buildLog().addField("User", getMemberName(((GuildVoiceMoveEvent) event).getMember()), false)
                        .addField("Channel Left", ((GuildVoiceMoveEvent) event).getChannelLeft().getName(), false)
                        .addField("Channel Joined", ((GuildVoiceMoveEvent) event).getChannelJoined().getName(), false).build()).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleTextChannelEvent(GenericTextChannelEvent event, CommandClient client) {
        try {
            DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
            int log = provider.getLog();
            TextChannel logchannel = provider.getLogChannel();
            if (logchannel == null) {
                return;
            }
            if (event instanceof TextChannelCreateEvent && LogType.ifLog(log, LogType.CHANNELCREATED)) {
                logchannel.sendMessage(LogType.CHANNELCREATED.buildLog().addField("Channel", event.getChannel().getName(), false).addField("Type", "Text Channel", false).build()).queue();
            } else if (event instanceof TextChannelDeleteEvent && LogType.ifLog(log, LogType.CHANNELDELETED)) {
                logchannel.sendMessage(LogType.CHANNELDELETED.buildLog().addField("Channel", event.getChannel().getName(), false).addField("Type", "Text Channel", false).build()).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public static void handleVoiceChannelEvent(GenericVoiceChannelEvent event, CommandClient client) {
        try {
            DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
            int log = provider.getLog();
            TextChannel logchannel = provider.getLogChannel();
            if (logchannel == null) {
                return;
            }
            if (event instanceof VoiceChannelCreateEvent && LogType.ifLog(log, LogType.CHANNELCREATED)) {
                logchannel.sendMessage(LogType.CHANNELCREATED.buildLog().addField("Channel", event.getChannel().getName(), false).addField("Type", "Voice Channel", false).build()).queue();
            } else if (event instanceof VoiceChannelDeleteEvent && LogType.ifLog(log, LogType.CHANNELDELETED)) {
                logchannel.sendMessage(LogType.CHANNELDELETED.buildLog().addField("Channel", event.getChannel().getName(), false).addField("Type", "Voice Channel", false).build()).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleCategoryEvent(GenericCategoryEvent event, CommandClient client) {
        try {
            DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
            int log = provider.getLog();
            TextChannel logchannel = provider.getLogChannel();
            if (logchannel == null) {
                return;
            }
            if (event instanceof CategoryCreateEvent && LogType.ifLog(log, LogType.CHANNELCREATED)) {
                logchannel.sendMessage(LogType.CHANNELCREATED.buildLog().addField("Category", event.getCategory().getName(), false).addField("Type", "Category", false).build()).queue();
            } else if (event instanceof CategoryDeleteEvent && LogType.ifLog(log, LogType.CHANNELDELETED)) {
                logchannel.sendMessage(LogType.CHANNELDELETED.buildLog().addField("Category", event.getCategory().getName(), false).addField("Type", "Category", false).build()).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleMessageBulkDeleteEvent(MessageBulkDeleteEvent event, CommandClient client) {
        try {
            DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
            int log = provider.getLog();
            TextChannel logchannel = provider.getLogChannel();
            if (logchannel == null || !LogType.ifLog(log, LogType.MESSAGEPURGED)) {
                return;
            }
            logchannel.sendMessage(LogType.MESSAGEPURGED.buildLog().addField("Amount", String.valueOf(event.getMessageIds().size()), false).addField("Channel", event.getChannel().getName(), false).build()).queue();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void handleRoleEvent(GenericRoleEvent event, CommandClient client) {
        try {
            DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
            int log = provider.getLog();
            TextChannel logchannel = provider.getLogChannel();
            if (logchannel == null) {
                return;
            }
            if (event instanceof RoleCreateEvent && LogType.ifLog(log, LogType.ROLECREATED)) {
                logchannel.sendMessage(LogType.ROLECREATED.buildLog().addField("Role", event.getRole().getName(), false).build()).queue();
            } else if (event instanceof RoleDeleteEvent && LogType.ifLog(log, LogType.ROLEDELETED)) {
                logchannel.sendMessage(LogType.ROLEDELETED.buildLog().addField("Role", event.getRole().getName(), false).build()).queue();
            } else if (event instanceof RoleUpdateNameEvent && LogType.ifLog(log, LogType.ROLEEDITED)) {
                logchannel.sendMessage(LogType.ROLEEDITED.buildLog().addField("Previous Name", ((RoleUpdateNameEvent) event).getOldName(), false)
                        .addField("New Name", ((RoleUpdateNameEvent) event).getNewName(), false).build()).queue();
            } else if (event instanceof RoleUpdatePermissionsEvent && LogType.ifLog(log, LogType.ROLEEDITED)) {
                logchannel.sendMessage(LogType.ROLEEDITED.buildLog().setDescription("Permission Changed!").addField("Role", event.getRole().getName(), false).build()).queue();
            } else if (event instanceof RoleUpdateColorEvent && LogType.ifLog(log, LogType.ROLEEDITED)) {
                logchannel.sendMessage(LogType.ROLEEDITED.buildLog().setDescription("Color Changed!").addField("Role", event.getRole().getName(), false).build()).queue();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
