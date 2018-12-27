package DonBot.features;

import DonBot.api.DonCase;
import DonBot.audio.MusicBotSetup;
import com.jagrosh.jdautilities.command.GuildSettingsProvider;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.Permission;
import net.dv8tion.jda.core.entities.*;
import net.dv8tion.jda.core.requests.restaction.order.RoleOrderAction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nullable;
import java.sql.SQLException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class DonGuildSettingsProvider implements GuildSettingsProvider {
    private Guild guild;
    private String prefix = "^";
    private Logger log;
    private DatabaseManager manager;
    private final MusicBotSetup setup;


    public DonGuildSettingsProvider(Guild guild, DatabaseManager manager, MusicBotSetup setup){
         log = LoggerFactory.getLogger("DonGuildSettingsProvider- guild = "+ guild.getName());
        this.guild = guild;
        this.manager = manager;
        this.setup = setup;
        try {
            this.prefix = manager.prefix(guild);
        } catch (SQLException e) {
            log.error(e.getMessage());
        }
    }

    /*

    Prefixes

     */


    @Nullable
    @Override
    public Collection<String> getPrefixes() {
        try {
            List<String> list = new ArrayList<>();
            prefix =  manager.prefix(guild).replaceAll("\\^", "^");
            list.add(prefix);
            return list;
        } catch (SQLException e) {
            List<String> list = new ArrayList<>();
            list.add("^");
            return list;
        }
    }

    /*

    Tag System

     */

    public void addTag(String key, String tag) throws SQLException {
        manager.addTag(guild.getIdLong(), key, tag);
    }

    public String getTag(String key) throws SQLException {
        return manager.getTag(guild.getIdLong(), key);
    }

    public void delTag(String key) throws SQLException {
        manager.delTag(guild.getIdLong(), key);
    }

    public List<String[]> getAllTags() throws SQLException {
        return manager.getAllTags(guild.getIdLong());
    }

    /*

    Music

     */


    public Role getDJ() {
        try {
            return manager.getDJRole(guild);
        } catch (SQLException ignored) {
            return null;
        }
    }

    public boolean issOnlyDJModerates() {
        try {
            return manager.getOnlyDJCanModerate(guild);

        } catch (SQLException e) {
            return false;
        }
    }

    /*

    Auto Role Welcoming and Goodbyeing

     */

    public Role getAutoRole() throws SQLException {
        return manager.getAutoRole(guild);
    }

    public String getWmessage() throws SQLException {
        return (String) manager.getWelcomer(guild).get(2);
    }

    public boolean isGenabled() throws SQLException {
        return (boolean) manager.getGoodbyer(guild).get(0);
    }

    public TextChannel getGchannel() throws SQLException {
        return (TextChannel) manager.getGoodbyer(guild).get(1);
    }

    public String getGmessage() throws SQLException {
        return (String) manager.getGoodbyer(guild).get(2);
    }

    public boolean isWenabled() throws SQLException {
        return (boolean) manager.getWelcomer(guild).get(0);
    }

    public TextChannel getWchannel() throws SQLException {
        return (TextChannel) manager.getWelcomer(guild).get(1);
    }

    /*
    Auto Mod and logging
     */

    public TextChannel getModchannel() throws SQLException {
        return manager.getModChannel(guild);
    }

    public TextChannel getLogChannel() throws SQLException {
        return guild.getTextChannelById(manager.getLogChannel(guild.getIdLong()));
    }

    public int getLog() throws SQLException {
        return manager.getLog(guild.getIdLong());
    }

    public ArrayList<Long> getIgnored() throws SQLException {
        return manager.getIgnoredChannels(guild);
    }

    public int getAutoMod() throws SQLException {
        return manager.getAutoMod(guild);
    }

    public int getAutomute() throws SQLException {
        return manager.getAutoMute(guild);
    }

    public int getWarnings(long userid) throws SQLException{
        return manager.getWarning(guild.getIdLong(), userid);
    }

    public void addWarning(long userid) throws SQLException {
        manager.addWarning(guild.getIdLong(), userid);
    }

    public void clearWarnings(long userid) throws SQLException {
        manager.clearWarnings(guild.getIdLong(), userid);
    }

    public boolean spam(long user, OffsetDateTime created) {
        return manager.Spam(guild.getIdLong(), user, created);
    }

    /*

    Currency

     */

    public int getCash(long user_id) throws SQLException {
        return manager.getCash(user_id);
    }

    public void updateCash(long user_id, int cash) throws SQLException {
        manager.UpdateCash(user_id, cash);
    }

    public boolean claimDaily(long user) throws SQLException{
        return manager.claimDaily(user);
    }

    /*

    DonBot Casing

    */

    public DonCase getCase(long guild_id, int case_id, JDA jda) throws SQLException {
        return manager.getCase(guild_id, case_id, jda);
    }

    public DonCase addCase(long guild_id, int type, User mod, User user, String reason) throws SQLException {
        return manager.addCase(guild_id, type, mod, user, reason);
    }

    public void updateCase(long guild_id, int id , long mod, String reason) throws SQLException {
        manager.updateCase(guild_id, id, mod, reason);
    }

    /*

    Utilities

    */

    public boolean mute(Member mu, Guild guild) {
        if (guild.getRolesByName("Muted", false).isEmpty()) {
            createMuted(mu, guild);
            return true;
        } else if (guild.getMembersWithRoles(guild.getRolesByName("Muted", false)).contains(mu)) {
            return false;
        } else {
            guild.getRolesByName("Muted",false).get(0).getManager().setHoisted(true).queue();
            guild.getController().addRolesToMember(mu, guild.getRolesByName("Muted", false)).queue();
            return true;
        }
    }


    private void createMuted(Member member, Guild guild) {
        guild.getController().createRole().setHoisted(true).setName("Muted").queue(role -> {
            RoleOrderAction action = guild.getController().modifyRolePositions(true);
            int pls = action.selectPosition(guild.getSelfMember().getRoles().get(0)).getSelectedPosition();
            action.selectPosition(role).moveTo(pls - 1).queue();
            for (TextChannel channel : guild.getTextChannels()) {
                channel.createPermissionOverride(role).setDeny(Permission.MESSAGE_WRITE).queue();
            }
            guild.getController().addRolesToMember(member, role).queue();
        });
    }

    public int getActivePlayers() {
        return setup.guildPlayingCount();
    }
}
