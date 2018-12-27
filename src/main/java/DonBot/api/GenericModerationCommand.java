package DonBot.api;

import DonBot.features.DonGuildSettingsProvider;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.TextChannel;
import net.dv8tion.jda.core.entities.User;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public abstract class GenericModerationCommand extends DonCommand {
    protected int casetype = -1;

    protected GenericModerationCommand() {
        this.category = new Category("moderation");
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        try {
            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
            if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("You are not an admin.");
                return;
            }
            List<User> users = event.getMessage().getMentionedUsers();
            User u;
            if (users.size()==0) {
                u = event.getJDA().getUserById(event.getArgs());
                if (u==null) {
                    event.reply("No users mentioned and no valid id was entered.");
                    return;
                }
            } else {
                u = users.get(0);
            }
            if (event.getGuild().getMember(u).hasPermission(Permission.MANAGE_SERVER)) {
                event.reply("This user is a Admin/Moderator, you can't use this command on him.");
                return;
            }

            String reason = "";
            if (event.getArgs().split("\\s+").length > 1) {
                reason = String.join(" ", Arrays.copyOfRange(event.getArgs().split("\\s+"), 1, event.getArgs().split("\\s+").length));
            }
            DonCase donCase = provider.addCase(event.getGuild().getIdLong(), casetype, event.getAuthor(), u, reason);
            if (reason.equals("")) {
                donCase.reason = "Mod please type `^reason " + donCase.id + " <reason>`";
            }
            TextChannel modlog;
            modlog = provider.getModchannel();
            if (modlog!=null && casetype != 1 && casetype != 2 && casetype != 4){
                modlog.sendMessage(TextUtils.getModLog(donCase)).queue();
            }
            Member mu = event.getGuild().getMember(u);
            switch (casetype) {
                case 0:
                    if (provider.mute(mu, event.getGuild())) {
                        event.reply(mu.getAsMention() + " has been muted.");
                    } else {
                        event.reply(mu.getAsMention() + " is already muted.");
                    }
                    break;
                case 1:
                    event.reply(mu.getAsMention() + " has been banned.");
                    event.getGuild().getController().ban(mu, 5, reason).queue();
                    break;
                case 2:
                    event.reply(mu.getAsMention() + " has been kicked.");
                    event.getGuild().getController().kick(mu).queue();
                    break;
                case 3:
                    if (event.getGuild().getMembersWithRoles(event.getGuild().getRolesByName("Muted", false)).contains(mu)) {
                        event.getGuild().getController().removeRolesFromMember(mu, event.getGuild().getRolesByName("Muted", false)).queue();
                        event.reply(mu.getAsMention() + " has been unmuted.");
                    } else {
                        event.reply(mu.getAsMention() + " is not muted!");
                    }
                    break;
                case 4:
                    event.reply(mu.getAsMention() + " has been unbanned.");
                    event.getGuild().getController().unban(u).queue();
                    break;
                case 5:
                    event.reply(mu.getAsMention() + " has been softbanned!");
                    event.getGuild().getController().ban(mu, 5, reason).queue((user) -> event.getGuild().getController().unban(u).queue());
                    break;
            }
        } catch (SQLException e) {
            event.reply("We had an unknown error during this command. full error report was sent to the dev.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            event.reply("Invalid id");
        }

    }
}
