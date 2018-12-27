package DonBot.features;

import DonBot.api.DonCase;
import DonBot.utils.StatsUtils;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.entities.*;
import org.apache.commons.dbcp2.BasicDataSource;
import org.postgresql.Driver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Nullable;
import java.sql.*;
import java.time.OffsetDateTime;
import java.util.*;
import java.util.Date;
import java.util.concurrent.TimeUnit;

class DatabaseManager {
    private final String username = "";
    private final String password = "";
    BasicDataSource dbcp;
    private Logger log;
    JedisPool pool;


    DatabaseManager() throws SQLException {
        log = LoggerFactory.getLogger(DatabaseManager.class);
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMaxTotal(20);
        config.setLifo(true);
        pool = new JedisPool(config, "localhost");
        dbcp = new BasicDataSource();
        dbcp.setDriver(new Driver());
        dbcp.setUrl("jdbc:postgresql://localhost:5432/DonBot");
        dbcp.setUsername(username);
        dbcp.setPassword(password);
        dbcp.setInitialSize(5);
        dbcp.setMaxTotal(20);
        log.info("Logged into the database!");
        Jedis jedis = pool.getResource();
        log.info("Jedis ping: " + jedis.ping());
        jedis.close();
        Connection connection = dbcp.getConnection();
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.ntags\n" +
                "(\n" +
                "  guild_id bigint,\n" +
                "  key character varying(100),\n" +
                "  value character varying(256)\n" +
                ")\n" +
                "WITH (\n" +
                "  OIDS=FALSE\n" +
                ");\n" +
                "ALTER TABLE public.ntags\n" +
                "  OWNER TO postgres;\n");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.prefixes\n" +
                "(\n" +
                "    guild_id bigint NOT NULL,\n" +
                "    prefix character varying(10) COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                "    CONSTRAINT prefixes_pkey PRIMARY KEY (guild_id)\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;\n" +
                "\n" +
                "ALTER TABLE public.prefixes\n" +
                "    OWNER to postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.music_preferences\n" +
                "(\n" +
                "    guild_id bigint NOT NULL,\n" +
                "    djrole bigint,\n" +
                "    djm boolean,\n" +
                "    CONSTRAINT music_preferences_pkey PRIMARY KEY (guild_id)\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;\n" +
                "\n" +
                "ALTER TABLE public.music_preferences\n" +
                "    OWNER to postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.welcomer\n" +
                "(\n" +
                "    guild_id bigint NOT NULL,\n" +
                "    wenabled boolean,\n" +
                "    wmessage character varying(256) COLLATE pg_catalog.\"default\",\n" +
                "    wchannel bigint,\n" +
                "    CONSTRAINT welcomer_pkey PRIMARY KEY (guild_id)\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;\n" +
                "\n" +
                "ALTER TABLE public.welcomer\n" +
                "    OWNER to postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.autorole\n" +
                "(\n" +
                "    guild_id bigint NOT NULL,\n" +
                "    roleid bigint,\n" +
                "    CONSTRAINT autorole_pkey PRIMARY KEY (guild_id)\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;\n" +
                "\n" +
                "ALTER TABLE public.autorole\n" +
                "    OWNER to postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.warnings\n" +
                "(\n" +
                "  user_id BIGINT,\n" +
                "  guild_id BIGINT,\n" +
                "  warnings integer" +
                ")\n" +
                "WITH (\n" +
                "  OIDS=FALSE\n" +
                ");\n" +
                "ALTER TABLE public.warnings\n" +
                "  OWNER TO postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.modchannel\n" +
                "(\n" +
                "    guild_id bigint NOT NULL,\n" +
                "    channel_id bigint,\n" +
                "    ignored_channels bigint[],\n" +
                "    automute integer,\n" +
                "    logtypes integer,\n" +
                "    logchannel bigint,\n" +
                "    automod integer,\n" +
                "    CONSTRAINT modchannel_pkey PRIMARY KEY (guild_id)\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;\n" +
                "\n" +
                "ALTER TABLE public.modchannel\n" +
                "    OWNER to postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.premium\n" +
                "(\n" +
                "  key CHARACTER VARYING(32),\n" +
                "  guild BIGINT\n" +
                ")\n" +
                "WITH (\n" +
                "  OIDS=FALSE\n" +
                ");\n" +
                "ALTER TABLE public.premium\n" +
                "  OWNER TO postgres;\n");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.currency\n" +
                "(\n" +
                "  user_id BIGINT,\n" +
                "  cash INTEGER,\n" +
                "  lastdaily timestamp with time zone\n" +
                ")\n" +
                "WITH (\n" +
                "  OIDS=FALSE\n" +
                ");\n" +
                "ALTER TABLE public.currency\n" +
                "  OWNER TO postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.cases\n" +
                "(\n" +
                "  guild_id bigint,\n" +
                "  case_id integer,\n" +
                "  type integer,\n" +
                "  mod_id bigint,\n" +
                "  user_id bigint,\n" +
                "  reason character varying(100)\n" +
                ")\n" +
                "WITH (\n" +
                "  OIDS=FALSE\n" +
                ");\n" +
                "ALTER TABLE public.cases\n" +
                "  OWNER TO postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.totalstats\n" +
                "(\n" +
                "    \"ID\" integer NOT NULL,\n" +
                "    \"totalvotes\" INTEGER NOT NULL,\n" +
                "    CONSTRAINT totalstats_pkey PRIMARY KEY (\"ID\")\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;\n" +
                "\n" +
                "ALTER TABLE public.totalstats\n" +
                "    OWNER to postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.goodbyer\n" +
                "(\n" +
                "    guild_id bigint NOT NULL,\n" +
                "    enabled boolean,\n" +
                "    message character varying(256) COLLATE pg_catalog.\"default\",\n" +
                "    channel bigint,\n" +
                "    CONSTRAINT goodbyer_pkey PRIMARY KEY (guild_id)\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;\n" +
                "\n" +
                "ALTER TABLE public.goodbyer\n" +
                "    OWNER to postgres;");
        stmt.close();
        stmt = connection.createStatement();
        stmt.executeUpdate("CREATE TABLE IF NOT EXISTS public.session\n" +
                "(\n" +
                "    sid character varying COLLATE pg_catalog.\"default\" NOT NULL,\n" +
                "    sess json NOT NULL,\n" +
                "    expire timestamp(6) without time zone NOT NULL,\n" +
                "    CONSTRAINT session_pkey PRIMARY KEY (sid)\n" +
                ")\n" +
                "WITH (\n" +
                "    OIDS = FALSE\n" +
                ")\n" +
                "TABLESPACE pg_default;\n" +
                "\n" +
                "ALTER TABLE public.session\n" +
                "    OWNER to postgres;");
        stmt.close();
        connection.close();

    }

    void addTag(long guild, String key, String value) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO ntags (guild_id, key, value) VALUES (?, ?, ?)");
        pstmt.setLong(1, guild);
        pstmt.setString(2, key);
        pstmt.setString(3, value);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
    }

    String getTag (long guild, String key) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM ntags WHERE guild_id=? AND key=?");
        pstmt.setLong(1, guild);
        pstmt.setString(2, key);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            String tag = rs.getString("value");
            pstmt.close();
            connection.close();
            return tag;

        }
        pstmt.close();
        connection.close();
        return null;
    }

    void delTag(long guild, String key) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("DELETE FROM ntags WHERE key=? AND guild_id=?");
        pstmt.setString(1, key);
        pstmt.setLong(2, guild);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
    }

    List<String[]> getAllTags (long guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM ntags WHERE guild_id=?");
        pstmt.setLong(1, guild);
        ResultSet rs = pstmt.executeQuery();
        List<String[]> result = new ArrayList<>();
        while (rs.next()) {
            result.add(new String[]{rs.getString("key"), rs.getString("value")});
        }
        pstmt.close();
        connection.close();
        return result;
    }

    boolean getOnlyDJCanModerate(Guild guild) throws SQLException{
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT djm FROM music_preferences WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            boolean djm = rs.getBoolean("djm");
            pstmt.close();
            connection.close();
            return djm;
        }
        pstmt.close();
        connection.close();
        return false;
    }

    @Nullable
    Role getDJRole(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT djrole FROM music_preferences WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        rs.next();
        long id =  rs.getLong("djrole");
        pstmt.close();
        connection.close();
        return guild.getRoleById(id);
    }

    @Nullable
    TextChannel getModChannel(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT channel_id FROM modchannel WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            long cid = rs.getLong("channel_id");
            pstmt.close();
            connection.close();
            return guild.getTextChannelById(cid);
        }
        return null;
    }

    String prefix(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT prefix FROM prefixes WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            String prefix = rs.getString("prefix");
            pstmt.close();
            connection.close();
            return prefix;
        }
        pstmt.close();
        connection.close();
        return "^";
    }

    List<Object> getWelcomer(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT wenabled, wchannel, wmessage FROM welcomer WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            List<Object> objects = new ArrayList<>();
            objects.add(rs.getBoolean("wenabled"));
            objects.add(guild.getTextChannelById(rs.getLong("wchannel")));
            objects.add(rs.getString("wmessage"));
            pstmt.close();
            connection.close();
            return objects;
        }
        List<Object> objects = new ArrayList<>();
        objects.add(false);
        objects.add(null);
        objects.add(null);
        pstmt.close();
        connection.close();
        return objects;
    }

    @Nullable
    Role getAutoRole(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT roleid FROM autorole WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            long role = rs.getLong("roleid");
            return guild.getRoleById(role);
        }
        pstmt.close();
        connection.close();
        return null;
    }

    ArrayList<Long> getIgnoredChannels(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT ignored_channels FROM modchannel WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next() ) {
            Array ignored = rs.getArray("ignored_channels");
            if (ignored == null) {
                pstmt.close();
                connection.close();
                return new ArrayList<>();
            }
            pstmt.close();
            connection.close();
            return new ArrayList<>(Arrays.asList((Long[])ignored.getArray()));
        }
        pstmt.close();
        connection.close();
        return new ArrayList<>();
    }

    int getAutoMod(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT automod FROM modchannel WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int automod = rs.getInt("automod");
            pstmt.close();
            connection.close();
            return automod;
        }
        pstmt.close();
        connection.close();
        return 0;
    }

    int getAutoMute(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM modchannel WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int automute = rs.getInt("automute");
            pstmt.close();
            connection.close();
            return automute;
        }
        pstmt.close();
        connection.close();
        return 0;
    }

    int getCash(long user) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM currency WHERE user_id=?");
        pstmt.setLong(1, user);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int cash = rs.getInt("cash");
            pstmt.close();
            connection.close();
            return cash;
        }
        pstmt.close();
        connection.close();
        return 0;
    }

    DonCase getCase(long guild_id, int id, JDA jda) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM cases WHERE guild_id=? AND case_id=?");
        pstmt.setLong(1, guild_id);
        pstmt.setInt(2, id);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int type = rs.getInt("type");
            long mod = rs.getLong("mod_id");
            long user = rs.getLong("user_id");
            String reason = rs.getString("reason");
            pstmt.close();
            connection.close();
            return new DonCase(id, type, jda.getUserById(mod), jda.getUserById(user), reason);
        }
        pstmt.close();
        connection.close();
        return null;
    }

    void updateCase(long guild_id, int id , long mod, String reason) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("UPDATE cases SET mod_id=?, reason=? WHERE guild_id=? AND case_id=?");
       pstmt.setLong(1, mod);
       pstmt.setString(2, reason);
       pstmt.setLong(3, guild_id);
       pstmt.setInt(4, id);
       pstmt.executeUpdate();
        pstmt.close();
        connection.close();
    }

    DonCase addCase(long guild_id, int type, User mod, User user, String reason) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM cases WHERE guild_id=? ORDER BY case_id DESC");
        pstmt.setLong(1, guild_id);
        ResultSet rs = pstmt.executeQuery();
        int id = 1;
        if (rs.next()) {
            id = rs.getInt("case_id") + 1;
        }
        pstmt.close();
        pstmt = connection.prepareStatement("INSERT INTO cases (guild_id, case_id, type, mod_id, user_id, reason) VALUES (?, ? ,?, ?, ?, ?)");
        pstmt.setLong(1, guild_id);
        pstmt.setInt(2, id);
        pstmt.setInt(3, type);
        pstmt.setLong(4, mod.getIdLong());
        pstmt.setLong(5, user.getIdLong());
        pstmt.setString(6, reason);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
        return new DonCase(id, type, mod, user, reason);
    }

    boolean claimDaily(long user) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM currency WHERE user_id=?");
        pstmt.setLong(1, user);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            Timestamp then = rs.getTimestamp("lastdaily");
            if (then == null) {
                then = new Timestamp(new Date().getTime()+10);
            }
            Timestamp now = new Timestamp(new Date().getTime());
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.DATE, -1);
            if (new Timestamp(cal.getTime().getTime()).after(then)) {
                int cash = getCash(user);
                pstmt = connection.prepareStatement("UPDATE currency SET cash=?, lastdaily=? WHERE user_id=?");
                pstmt.setInt(1, cash+500);
                pstmt.setTimestamp(2, now);
                pstmt.setLong(3, user);
                pstmt.executeUpdate();
                pstmt.close();
                connection.close();
                return true;
            }
            return false;
        }
        pstmt.close();
        pstmt = connection.prepareStatement("INSERT INTO currency (user_id, cash, lastdaily) VALUES (?, 500, ?)");
        pstmt.setTimestamp(2, new Timestamp(new Date().getTime()));
        pstmt.setLong(1, user);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
        return true;
    }

    int getWarning(long guild, long user) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM warnings WHERE guild_id=? AND user_id=?");
        pstmt.setLong(1, guild);
        pstmt.setLong(2, user);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int warnings = rs.getInt("warnings");
            pstmt.close();
            connection.close();
            return warnings;
        }
        pstmt = connection.prepareStatement("INSERT INTO warnings (guild_id, user_id, warnings) VALUES (?, ?, 0)");
        pstmt.setLong(1, guild);
        pstmt.setLong(2, user);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
        return 0;
    }

    void addWarning(long guild, long user) throws SQLException {
        Connection connection = dbcp.getConnection();
        int warnings = getWarning(guild, user);
        PreparedStatement pstmt = connection.prepareStatement("UPDATE warnings SET warnings = ? WHERE guild_id=? AND user_id=?");
        pstmt.setInt(1, warnings+1);
        pstmt.setLong(2, guild);
        pstmt.setLong(3, user);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
    }

    void clearWarnings(long guild, long user) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("DELETE FROM warnings WHERE guild_id=? AND user_id=?");
        pstmt.setLong(1, guild);
        pstmt.setLong(2, user);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
    }

    void UpdateCash(long user, int cash) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("UPDATE currency SET cash=? WHERE user_id=?");
        pstmt.setInt(1, cash);
        pstmt.setLong(2, user);
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
    }

    void addCommandUsage(int ID) throws SQLException {
        Connection connection = dbcp.getConnection();
        Jedis jedis = pool.getResource();
        Calendar tomorrow = Calendar.getInstance();
        tomorrow.add(Calendar.DATE, 1);
        tomorrow.set(Calendar.HOUR_OF_DAY, 0);
        tomorrow.set(Calendar.MINUTE, 0);
        tomorrow.set(Calendar.SECOND, 0);
        tomorrow.set(Calendar.MILLISECOND, 0);
        String cmdKey = "CommandDaily#"+ID;
        String modKey = "ModuleDaily#"+ Integer.highestOneBit(ID);
        jedis.incr(cmdKey);
        jedis.pexpireAt(cmdKey, tomorrow.getTimeInMillis());
        jedis.incr(modKey);
        jedis.pexpireAt(modKey, tomorrow.getTimeInMillis());
        jedis.incr("TotalDaily");
        jedis.pexpireAt("TotalDaily", tomorrow.getTimeInMillis());
        jedis.close();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM totalstats WHERE \"ID\"=?;");
        pstmt.setInt(1, ID);
        ResultSet set = pstmt.executeQuery();
        int votes = 0;
        if (set.next()) {
            votes = set.getInt("totalvotes");
            pstmt = connection.prepareStatement("UPDATE totalstats SET totalvotes=? WHERE \"ID\"=?;");
            pstmt.setInt(1, ++votes);
            pstmt.setInt(2, ID);
            pstmt.executeUpdate();
            pstmt.close();
            connection.close();
        }
        else {
            pstmt = connection.prepareStatement("INSERT INTO totalstats (\"ID\", totalvotes) VALUES (?, ?);");
            pstmt.setInt(1, ID);
            pstmt.setInt(2, ++votes);
            pstmt.executeUpdate();
            pstmt.close();
            connection.close();
        }
    }

    int getDailyCommandUsage(int ID) {
        Jedis jedis = pool.getResource();
        if (jedis.exists("CommandDaily#"+ID)) {
            return Integer.parseInt(jedis.get("CommandDaily#"+ID));
        } else {
            return 0;
        }
    }

    int getTotalCommandUsage(int ID) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM totalstats WHERE \"ID\" = ?;");
        pstmt.setInt(1, ID);
        ResultSet rs = pstmt.executeQuery();
        int votes = 0;
        if (rs.next()) {
            votes = rs.getInt("totalvotes");
        }
        pstmt.close();
        connection.close();
        return votes;
    }

    int getDailyModuleUsage(int ID) {
        Jedis jedis = pool.getResource();
        if (jedis.exists("ModuleDaily#"+ID)) {
            return Integer.parseInt(jedis.get("ModuleDaily#"+ID));
        } else {
            return 0;
        }
    }

    int getTotalModuleUsage(int ID) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT SUM(totalvotes) AS totalvotes FROM totalstats WHERE \"ID\" & ? != 0;");
        pstmt.setInt(1, ID);
        ResultSet rs = pstmt.executeQuery();
        int votes = 0;
        if (rs.next()) {
            votes = rs.getInt("totalvotes");
        }
        pstmt.close();
        connection.close();
        return votes;
    }

    int getCommandIdByRank(int rank) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM totalstats ORDER BY totalvotes DESC;");
        ResultSet set = pstmt.executeQuery();
        while (set.next() && (--rank)!=0) { }
        int ID = set.getInt("ID");
        pstmt.close();
        connection.close();
        return ID;
    }

    int getTotalDailyUsage() {
        Jedis jedis = pool.getResource();
        if (jedis.exists("TotalDaily")) {
            return Integer.parseInt(jedis.get("TotalDaily"));
        } else {
            return 0;
        }
    }

    int getLog(long guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT logtypes FROM modchannel WHERE guild_id = ?");
        pstmt.setLong(1, guild);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            int log = rs.getInt("logtypes");
            pstmt.close();
            connection.close();
            return log;
        }
        pstmt.close();
        connection.close();
        return 0;
    }

    long getLogChannel(long guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT logchannel FROM modchannel WHERE guild_id = ?");
        pstmt.setLong(1, guild);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            long log = rs.getLong("logchannel");
            pstmt.close();
            connection.close();
            return log;
        }
        pstmt.close();
        connection.close();
        return 0;
    }

    List<Object> getGoodbyer(Guild guild) throws SQLException {
        Connection connection = dbcp.getConnection();
        PreparedStatement pstmt = connection.prepareStatement("SELECT * FROM goodbyer WHERE guild_id=?");
        pstmt.setLong(1, guild.getIdLong());
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            List<Object> objects = new ArrayList<>();
            objects.add(rs.getBoolean("enabled"));
            objects.add(guild.getTextChannelById(rs.getLong("channel")));
            objects.add(rs.getString("message"));
            pstmt.close();
            connection.close();
            return objects;
        }
        pstmt = connection.prepareStatement("INSERT INTO goodbyer (guild_id, enabled, channel, message) VALUES (?, FALSE, NULL, NULL);");
        pstmt.setLong(1, guild.getIdLong());
        pstmt.executeUpdate();
        pstmt.close();
        connection.close();
        List<Object> objects = new ArrayList<>();
        objects.add(false);
        objects.add(null);
        objects.add(null);
        return objects;
    }

    boolean Spam(long guild, long user, OffsetDateTime creationtime) {
        Jedis jedis = pool.getResource();
        String key = String.format("messagecache#%d#%d", guild, user);
        jedis.rpush(key , String.valueOf(creationtime.toInstant().toEpochMilli()));
        if (jedis.llen(key) > 5) {
            jedis.lpop(key);
        }
        jedis.expire(key, 5);
        long newest = Long.parseLong(jedis.lindex(key, -1));
        long oldest = Long.parseLong(jedis.lindex(key, 0));
        if (TimeUnit.MILLISECONDS.toSeconds(newest-oldest) < 5 && jedis.llen(key) == 5){
            jedis.close();
            return true;
        } else {
            jedis.close();
            return false;
        }
    }
}
