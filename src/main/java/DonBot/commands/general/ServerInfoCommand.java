package DonBot.commands.general;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Member;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ServerInfoCommand extends DonCommand {
    public ServerInfoCommand(){
        this.name = "serverinfo";
        this.aliases = new String[]{"si", "sinfo"};
        this.usage = "^serverinfo";
        this.category = new Category("general");
        this.guildOnly = true;
        this.help = "Shows info about the server.";
        this.ID = 135;
    }

    private List<Member> NonBotMembers(List<Member> members) {
        List<Member> reals = new ArrayList<>();
        for (Member member : members) {
            if (!member.getUser().isBot()) {
                reals.add(member);
            }
        }
        return reals;
    }

    private List<Member> OnlineMembers(List<Member> members) {
        List<Member> online = new ArrayList<>();
        for (Member member :members) {
            if (member.getOnlineStatus().equals(OnlineStatus.ONLINE)) {
                online.add(member);
            }
        }
        return online;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        EmbedBuilder builder = new EmbedBuilder()
                .setThumbnail(event.getGuild().getIconUrl())
                .setTitle("Info:")
                .addField("Online Members", ""+OnlineMembers(event.getGuild().getMembers()).size(), true)
                .addField("Members", ""+event.getGuild().getMemberCache().size(), true)
                .addField("Non-Bot Members", ""+NonBotMembers(event.getGuild().getMembers()).size(), true)
                .addField("Owner", event.getGuild().getOwner().getEffectiveName(), true)
                .addField("Roles", ""+event.getGuild().getRoles().size(), true)
                .addField("Text Channels", ""+event.getGuild().getTextChannels().size(), true)
                .addField("Categories", ""+event.getGuild().getCategories().size(), true)
                .addField("Voice Channels", ""+event.getGuild().getVoiceChannels().size(), true)
                .addField("Creation date", event.getGuild().getCreationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replaceAll("T", " "), true)
                .setFooter("Server ID: " + event.getGuild().getId() + " " + event.getMessage().getCreationTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME).replaceAll("T", " ") , event.getSelfUser().getAvatarUrl());
        event.reply(builder.build());
    }
}
