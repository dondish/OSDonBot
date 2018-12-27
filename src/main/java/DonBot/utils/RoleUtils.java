package DonBot.utils;

import DonBot.features.DonGuildSettingsProvider;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.Role;
import net.dv8tion.jda.core.entities.User;

import java.util.List;

public class RoleUtils {
    private static boolean isRoleSubset(Role role1, Role role2) {
        for (Permission permission : role1.getPermissions()) {
            if (!role2.getPermissions().contains(permission)) {
                return false;
            }
        }
        return true;
    }

    private static boolean areRolesSubset(List<Role> roles, Role role) {
        if (role == null){
            for (Role role1 : roles) {
                if (role1.hasPermission(Permission.MANAGE_SERVER)) return true;
            }
            return false;
        }
        for (Role role1: roles) {
            if (isRoleSubset(role, role1)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDJorMod(CommandEvent event) {
        DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
        Role dj = provider.getDJ();
        return (event.getMember().hasPermission(Permission.MANAGE_SERVER) || // is a moderator +
                dj != null) && (event.getMember().getRoles().contains(dj) || areRolesSubset(event.getMember().getRoles(), dj));
    }

    public static User getUserMentionorName(CommandEvent event) {
        List<User> users = event.getMessage().getMentionedUsers();
        User u;
        if (users.size()==0) {
            u = event.getJDA().getUsersByName(event.getArgs(), true).get(0);
            if (u==null) {
                event.reply("No users mentioned and no valid username was entered");
                return null;
            }
        } else {
            u = users.get(0);
        }
        return u;

    }
}
