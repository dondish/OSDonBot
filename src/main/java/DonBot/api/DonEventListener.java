package DonBot.api;

import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.audit.ActionType;
import net.dv8tion.jda.core.audit.AuditLogEntry;
import net.dv8tion.jda.core.entities.ChannelType;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;
import net.dv8tion.jda.core.events.DisconnectEvent;
import net.dv8tion.jda.core.events.channel.category.GenericCategoryEvent;
import net.dv8tion.jda.core.events.channel.text.GenericTextChannelEvent;
import net.dv8tion.jda.core.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.core.events.guild.GenericGuildEvent;
import net.dv8tion.jda.core.events.guild.GuildBanEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberJoinEvent;
import net.dv8tion.jda.core.events.guild.member.GuildMemberLeaveEvent;
import net.dv8tion.jda.core.events.message.MessageBulkDeleteEvent;
import net.dv8tion.jda.core.events.message.MessageReceivedEvent;
import net.dv8tion.jda.core.events.role.GenericRoleEvent;
import net.dv8tion.jda.core.exceptions.InsufficientPermissionException;
import net.dv8tion.jda.core.hooks.ListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

public class DonEventListener extends ListenerAdapter {
    private CommandClient client;
    private EventWaiter waiter;
    private Logger log = LoggerFactory.getLogger(DonEventListener.class);
    private static final Pattern URL_REGEX = Pattern.compile("((https?|ftp)://|(www|ftp)\\.)?[a-z0-9-]+(\\.[a-z0-9-]+)+([/?].*)?");


    public DonEventListener(CommandClient client, EventWaiter waiter) {
        this.client = client;
        this.waiter = waiter;
    }

    private String suffixByNumber(int number) {
        switch (("" + number)) {
            case "1":
                return "st";
            case "2":
                return "nd";
            case "3":
                return "rd";
            default:
                return "th";
        }
    }


    private void AutoMute(MessageReceivedEvent event, DonGuildSettingsProvider provider) throws SQLException {
        provider.clearWarnings(event.getAuthor().getIdLong());
        provider.mute(event.getGuild().getMember(event.getMessage().getAuthor()), event.getGuild());
        event.getTextChannel().sendMessage("You have been muted for reaching maximum offenses.").queue();
        if (provider.getModchannel()!=null) {
            provider.getModchannel().sendMessage(TextUtils.getModLog(provider.addCase(event.getGuild().getIdLong(), 0, event.getJDA().getSelfUser(), event.getAuthor(), "AutoMute after following offenses"))).queue();
        }
    }

    private double capsPercentage(String message) {
        double total = message.length();
        double sum = 0;
        for (char c: message.toCharArray()) {
            if (Character.isUpperCase(c)) {
                sum++;
            }
        }
        return (sum/total)*100.;
    }

    @Override
    public void onGenericGuild(GenericGuildEvent event) {
        super.onGenericGuild(event);
        DonLogger.handleGuildEvent(event, client);
    }

    @Override
    public void onGenericTextChannel(GenericTextChannelEvent event) {
        super.onGenericTextChannel(event);
        DonLogger.handleTextChannelEvent(event, client);
    }

    @Override
    public void onGenericVoiceChannel(GenericVoiceChannelEvent event) {
        super.onGenericVoiceChannel(event);
        DonLogger.handleVoiceChannelEvent(event, client);
    }

    @Override
    public void onGenericCategory(GenericCategoryEvent event) {
        super.onGenericCategory(event);
        DonLogger.handleCategoryEvent(event, client);
    }

    @Override
    public void onMessageBulkDelete(MessageBulkDeleteEvent event) {
        super.onMessageBulkDelete(event);
        DonLogger.handleMessageBulkDeleteEvent(event, client);
    }

    @Override
    public void onGenericRole(GenericRoleEvent event) {
        super.onGenericRole(event);
        DonLogger.handleRoleEvent(event, client);
    }

    @Override
    public void onGuildMemberJoin(GuildMemberJoinEvent event) {
        try {
            DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
            if (provider.isWenabled()){
                String welcome = provider.getWmessage().replaceAll("%SNAME%", event.getGuild().getName())
                        .replaceAll("%SMEMBERS%", ""+event.getGuild().getMembers().size())
                        .replaceAll("%SMEMBERSUFFIX%", suffixByNumber(event.getGuild().getMembers().size()))
                        .replaceAll("%SERVER%", event.getGuild().getName())
                        .replaceAll("%SOWNER%", event.getGuild().getOwner().getEffectiveName())
                        .replaceAll("%SOWNERNICK%", event.getGuild().getOwner().getNickname())
                        .replaceAll("%SOWNERID%", event.getGuild().getOwner().getUser().getId())
                        .replaceAll("%SOWNERNAME%", event.getGuild().getOwner().getUser().getName()+"#"+event.getGuild().getOwner().getUser().getDiscriminator())
                        .replaceAll("%MEMBERNAME%", event.getMember().getEffectiveName())
                        .replaceAll("%MEMBER%", event.getMember().getEffectiveName()+"#"+event.getMember().getUser().getDiscriminator())
                        .replaceAll("%MEMBERID%", event.getMember().getUser().getId());
                try {

                    provider.getWchannel().sendMessage(welcome).queue();
                } catch (InsufficientPermissionException ignored) {}
            }
            if (provider.getAutoRole()!=null) {
                try {
                    event.getGuild().getController().addSingleRoleToMember(event.getMember(), provider.getAutoRole()).queue();
                } catch (InsufficientPermissionException ignored) {}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        try {
            if (event.getMember() == null)
                return;
            if (event.getChannelType()== ChannelType.PRIVATE)
                return;
            if (event.getMember().hasPermission((TextChannel) event.getChannel(), Permission.MANAGE_CHANNEL, Permission.MANAGE_ROLES, Permission.MESSAGE_WRITE))
                return;
            DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
            List<Long> ignored = provider.getIgnored();
            if (ignored != null && ignored.contains(event.getChannel().getIdLong()))
                return;
            int amod = provider.getAutoMod();
            if (AutoModType.ifAutoMod(amod, AutoModType.ANTISPAM) && provider.spam(event.getAuthor().getIdLong(), event.getMessage().getCreationTime())) {
                AutoMute(event, provider);
            } else if (AutoModType.ifAutoMod(provider.getAutoMod(), AutoModType.ANTIINV) && !event.getMessage().getInvites().isEmpty()) {
                if (provider.getAutomute()==0) {
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", Don't send invite links here!").queue();
                } else {
                    User author = event.getAuthor();
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", Don't send invite links here!").queue();
                    provider.addWarning(author.getIdLong());
                    if (provider.getWarnings(author.getIdLong()) == provider.getAutomute())
                        AutoMute(event, provider);
                }
            } else if (AutoModType.ifAutoMod(amod, AutoModType.MASSMENTION) && event.getMessage().getMentions(Message.MentionType.USER, Message.MentionType.ROLE).size() > 6 || event.getMessage().getMentions(Message.MentionType.EVERYONE, Message.MentionType.HERE).size() > 1) {
                if (provider.getAutomute()==0) {
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", Don't mass mention!").queue();
                } else {
                    User author = event.getAuthor();
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", Don't mass mention!").queue();
                    provider.addWarning(author.getIdLong());
                    if (provider.getWarnings(author.getIdLong()) == provider.getAutomute())
                        AutoMute(event, provider);
                }
            } else if (AutoModType.ifAutoMod(amod, AutoModType.ANTILINK) && URL_REGEX.matcher(event.getMessage().getContentRaw()).find()) {
                if (provider.getAutomute()==0) {
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", Don't send links!").queue();
                } else {
                    User author = event.getAuthor();
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", Don't send links!").queue();
                    provider.addWarning(author.getIdLong());
                    if (provider.getWarnings(author.getIdLong()) == provider.getAutomute())
                        AutoMute(event, provider);
                }
            } else if (AutoModType.ifAutoMod(amod, AutoModType.ANTICAPS) && capsPercentage(event.getMessage().getContentRaw()) > 70 && event.getMessage().getContentRaw().length() > 8) {
                if (provider.getAutomute()==0) {
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", Calm down with the caps!").queue();
                } else {
                    User author = event.getAuthor();
                    event.getMessage().delete().queue();
                    event.getTextChannel().sendMessage(event.getAuthor().getAsMention() + ", Calm down with the caps!").queue();
                    provider.addWarning(author.getIdLong());
                    if (provider.getWarnings(author.getIdLong()) == provider.getAutomute())
                        AutoMute(event, provider);
                }
            } else if (AutoModType.ifAutoMod(amod, AutoModType.BANSELFBOTS) && event.getMessage().getEmbeds().size() > 0 && !event.getAuthor().isBot()) {
                event.getGuild().getController().ban(event.getAuthor(), 1, "Self Bot detected").queue();
            }
        } catch (SQLException e) {
            log.error("g");
            e.printStackTrace();
        }
    }

    @Override
    public void onGuildBan(GuildBanEvent event) {
        super.onGuildBan(event);
        DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(()-> event.getGuild().getAuditLogs().type(ActionType.BAN).limit(1).queue(auditLogEntries -> {
            try {
                AuditLogEntry entry = auditLogEntries.get(0);
                DonCase donCase = provider.addCase(event.getGuild().getIdLong(), 1, entry.getUser(), event.getUser(), entry.getReason());
                String reason = entry.getReason();
                if (entry.getReason() == null) {
                    reason = "Mod please type `^reason " + donCase.id + " <reason>`";
                }
                if (entry.getUser() == null) {
                    return;
                }
                donCase.reason = reason;
                provider.updateCase(event.getGuild().getIdLong(), donCase.id, entry.getUser().getIdLong(), reason);
                if (provider.getModchannel()!=null) {
                    provider.getModchannel().sendMessage(TextUtils.getModLog(donCase)).queue();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }}), 5, TimeUnit.SECONDS);

    }

    @Override
    public void onGuildMemberLeave(GuildMemberLeaveEvent event) {
        super.onGuildMemberLeave(event);
        if (event.getUser() == null) {
            return;
        }
        DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
        ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
        scheduler.schedule(()-> event.getGuild().getAuditLogs().type(ActionType.KICK).limit(1).queue(auditLogEntries -> {
            if (! auditLogEntries.isEmpty() && auditLogEntries.get(0).getTargetIdLong() == event.getUser().getIdLong()) {
                try {
                    AuditLogEntry entry = auditLogEntries.get(0);
                    DonCase donCase = provider.addCase(event.getGuild().getIdLong(), 2, entry.getUser(), event.getUser(), entry.getReason());
                    String reason = entry.getReason();
                    if (entry.getReason() == null) {
                        reason = "Mod please type `^reason " + donCase.id + " <reason>`";
                    }
                    if (entry.getUser() == null) {
                        return;
                    }
                    donCase.reason = reason;
                    provider.updateCase(event.getGuild().getIdLong(), donCase.id, entry.getUser().getIdLong(), reason);
                    if (provider.getModchannel()!=null) {
                        provider.getModchannel().sendMessage(TextUtils.getModLog(donCase)).queue();
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }), 5, TimeUnit.SECONDS);
        try {
            if (provider.isGenabled()){
                String goodbye = provider.getGmessage().replaceAll("%SNAME%", event.getGuild().getName())
                        .replaceAll("%SMEMBERS%", ""+event.getGuild().getMembers().size())
                        .replaceAll("%SMEMBERSUFFIX%", suffixByNumber(event.getGuild().getMembers().size()))
                        .replaceAll("%SERVER%", event.getGuild().getName())
                        .replaceAll("%SOWNER%", event.getGuild().getOwner().getEffectiveName())
                        .replaceAll("%SOWNERNICK%", event.getGuild().getOwner().getNickname())
                        .replaceAll("%SOWNERID%", event.getGuild().getOwner().getUser().getId())
                        .replaceAll("%SOWNERNAME%", event.getGuild().getOwner().getUser().getName()+"#"+event.getGuild().getOwner().getUser().getDiscriminator())
                        .replaceAll("%MEMBERNAME%", event.getMember().getEffectiveName())
                        .replaceAll("%MEMBER%", event.getMember().getEffectiveName()+"#"+event.getMember().getUser().getDiscriminator())
                        .replaceAll("%MEMBERID%", event.getMember().getUser().getId());
                try {

                    provider.getGchannel().sendMessage(goodbye).queue();
                } catch (InsufficientPermissionException ignored) {}
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onDisconnect(DisconnectEvent event) {
        super.onDisconnect(event);
        log.info("DISCONNECTED FROM DISCORD @ " + event.getDisconnectTime().toString());
        log.info(event.getClientCloseFrame().getOpcode() + ":" + event.getClientCloseFrame().getCloseReason());
    }
}

