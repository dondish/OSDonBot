package DonBot.commands.moderation;

import DonBot.api.DonCommand;
import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Message;
import net.dv8tion.jda.core.entities.MessageEmbed;
import net.dv8tion.jda.core.entities.TextChannel;

import java.util.Arrays;
import java.util.List;

public class ReasonCommand extends DonCommand {
    public ReasonCommand() {
        this.name = "reason";
        this.help = "Claim responsibility for an action and change the reason";
        this.usage= "^reason <id> <reason>";
        this.category = new Category("moderation");
        this.ID = 516;
        this.userPermissions = new Permission[] {Permission.MANAGE_SERVER};
        this.botPermissions = new Permission[] {Permission.MANAGE_SERVER, Permission.MESSAGE_MANAGE};
    }

    private Message getMessageWithId(int id, List<Message> messages) {
        for (Message message : messages) {
            if (message.getEmbeds().isEmpty()) {
                continue;
            }
            if (message.getEmbeds().get(0).getFooter().getText().equals(String.valueOf(id)))
                return message;
            else if (Integer.parseInt(message.getEmbeds().get(0).getFooter().getText())<id)
                return null;
        }
        return null;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        try {
            if (event.getArgs().split("\\s+").length >= 2) {
                DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
                int id = Integer.parseInt(event.getArgs().split("\\s+")[0]);
                String reason = String.join(" ", Arrays.copyOfRange(event.getArgs().split("\\s+"), 1 , event.getArgs().split("\\s+").length));
                if (reason.length()>100) {
                    event.reply("Reason cannot top 100 characters");
                    return;
                }
                if (provider.getCase(event.getGuild().getIdLong(), id, event.getJDA()) == null) {
                    event.reply("Case with that id does not exist.");
                    return;
                }
                provider.updateCase(event.getGuild().getIdLong(), id, event.getAuthor().getIdLong(), reason);
                TextChannel modlog = provider.getModchannel();
                if (modlog != null) {
                    modlog.getIterableHistory().queue(messages -> {
                        Message mess = getMessageWithId(id, messages);
                        if (mess != null) {

                            EmbedBuilder builder = new EmbedBuilder(mess.getEmbeds().get(0));
                            List<MessageEmbed.Field> fields = builder.getFields();
                            fields.remove(2);
                            builder.addField("Reason", reason, false);
                            mess.editMessage(builder.build()).queue();
                            event.getMessage().delete().queue();
                        }
                    });
                }
            }
        } catch (Exception e) {
            event.reply("Correct Usage: "+ usage);
        }
    }
}
