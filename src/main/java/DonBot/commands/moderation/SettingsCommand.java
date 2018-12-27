package DonBot.commands.moderation;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import net.dv8tion.jda.core.Permission;

public class SettingsCommand extends DonCommand{

    public SettingsCommand() {
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.name = "settings";
        this.category = new Category("moderation");
        this.help = "Settings has been deprecated. use the web dashboard instead. https://donbot.space/dashboard";
//        this.usage = "\n```Options\n" +
//                "\tsetPrefix-change guild prefix\n" +
//                "\tsetDJ-set DJ role\n" +
//                "\tsetAutoRole- set auto role\n" +
//                "\tonlyDJCanModerate-if only djs can stop/skip/seek.\n" +
//                "\tDJ - display the dj role\n" +
//                "\tprefix - display the current prefix\n" +
//                "\tsetModLogChannel - Sets up log for moderation commands\n" +
//                "\tAutoMod - toggle AutoMod for protecting chats\n" +
//                "\tsetAutoMute - type after how many offenses the author will be muted. 0 to turn off. 5 max.\n" +
//                "\taddIgnoredChannel - add channel to be ignored for anti invs.\n" +
//                "\tclearIgnoredChannels - clear ignored messages.\n```"+
//                "^settings <option> <optionArguments>";
        this.usage = "Settings has been deprecated. use the web dashboard instead. https://donbot.space/dashboard";
        this.guildOnly = true;
        this.cooldown = 5;
        this.ID = 517;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        event.reply("Settings has been deprecated. use the web dashboard instead. https://donbot.space/dashboard");
//        CommandClient client = event.getClient();
//        DonGuildSettingsProvider provider = client.getSettingsFor(event.getGuild());
//        String args = event.getArgs();
//        String setting = args.split("\\s+")[0];
//        String first;
//        String value;
//        if (args.split("\\s+").length > 1) {
//            first = args.split("\\s+")[1];
//            value = String.join(" ", Arrays.copyOfRange(args.split("\\s+"), 1, args.split("\\s+").length));
//        } else {
//            first = "";
//            value = "";
//        }
//        if (!event.getMember().hasPermission(Permission.MANAGE_SERVER)){
//            event.reply("You must have manage server permission to change settings.");
//            return;
//        }
//        if (args.equals("")) {
//            event.reply("Correct usage is settings <option> <optionArguments>");
//            return;
//        }
//        switch (setting){
//            case "DJ":
//                event.reply("The DJ Role is " + provider.getDJ().getName());
//                break;
//            case "prefix":
//                try {
//                    event.reply("The current guild prefix is " + provider.getPrefixes().toArray()[0]);
//                } catch (NullPointerException e) {
//                    event.reply("Got error: "+e.getMessage());
//                }
//                break;
//            case "setPrefix":
//                try {
//                    if (args.split("\\s+").length > 1) {
//                        provider.setPrefix(first);
//                        event.reply("Guild prefix has been set to "+ first);
//                    } else {
//                        event.reply("Correct use is `settings setPrefix <prefix>`");
//                    }
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case "setDJ":
//                try {
//                    if (value.length() != 0 && first.equals("off")) {
//                        provider.setDJ(null);
//                        event.reply("Deleted DJ role.");
//                        break;
//                    }
//                    if (event.getMessage().getMentionedRoles().size()>0) {
//                        if (event.getMember().hasPermission(Permission.MANAGE_SERVER)) {
//                            Role role = event.getMessage().getMentionedRoles().get(0);
//                            provider.setDJ(role);
//                            event.reply("The DJ role is now: " + role.getName());
//                            break;
//                        }
//                    } else {
//                        List<Role> roles = event.getGuild().getRolesByName(event.getArgs().substring(6), true);
//                        if (roles.size()==0){
//                            event.reply("Correct use is `settings setDJ (role mention or name)`");
//                        } else {
//                            provider.setDJ(roles.get(0));
//                            event.reply("The DJ role is now: "+ roles.get(0).getName());
//                            break;
//                        }
//                    }
//                } catch (Exception e) {
//                    event.reply("Correct use is `settings setDJ (role mention or name)`");
//                }
//                break;
//            case "onlyDJCanModerate":
//                try {
//                    if (provider.getDJ()==null){
//                        event.reply("Sorry! you must first configure a DJ role, `settings setDJ <role>`");
//                        break;
//                    }
//                    provider.setOnlyDJModerates(!provider.issOnlyDJModerates());
//                    String s = "Only DJs can stop/skip turned ";
//                    if (provider.issOnlyDJModerates()) {
//                        s += "on";
//                    } else {
//                        s += "off";
//                    }
//                    event.reply(s);
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case "setAutoRole":
//                try {
//                    if (event.getArgs().split("\\s+").length >1 && event.getArgs().split("\\s+")[1].equals("off")) {
//                        provider.setAutoRole(null);
//                        event.reply("Deleted AutoRole.");
//                        break;
//                    }
//                    if (event.getMessage().getMentionedRoles().size()==0) {
//                        List<Role> roles = event.getGuild().getRolesByName(event.getArgs().substring(12), true);
//                        if (roles.size()==0){
//                            event.reply("Role " + event.getArgs().substring(12) + " not found");
//                        } else {
//                            provider.setAutoRole(roles.get(0));
//                            event.reply("Autorole is now: "+ roles.get(0).getName());
//                        }
//                    } else {
//                        Role role = event.getMessage().getMentionedRoles().get(0);
//                        provider.setAutoRole(role);
//                        event.reply("Autorole is now: "+ role.getName());
//                    }
//
//                } catch (SQLException e) {
//                    e.printStackTrace();
//                }
//                break;
//            case "setModLogChannel":
//                try {
//                    if (first.length() > 0 && first.equals("off")) {
//                        provider.setModchannel(null);
//                        event.reply("Disabled logging.");
//                        break;
//                    }
//                    if (event.getMessage().getMentionedChannels().size()==0){
//                        event.reply("Please mention a channel");
//                        return;
//                    }
//                    TextChannel channel = event.getMessage().getMentionedChannels().get(0);
//                    provider.setModchannel(channel);
//                } catch (SQLException ignored) {}
//                event.reply("Mod Log channel has been set :ok_hand:");
//                break;
//            case "setAutoMute":
//                try {
//                    if (first.length() == 0){
//                        event.reply("Automute is turned " + (provider.getAutomute()==0 ?  "off" : "to after " + provider.getAutomute() + " following offenses."));
//                        return;
//                    }
//                    int automute = Integer.parseInt(first);
//                    if (automute < 0 || automute > 5)
//                        throw new Exception();
//                    provider.setAutomute(automute);
//                    event.reply("Automute has been turned " + (automute == 0 ?  "off" : "to after " + automute + " following offenses."));
//                    break;
//                } catch (Exception ignored) {
//                    ignored.printStackTrace();
//                    event.reply("Please provide an integer between 0 to 5");
//                    break;
//                }
//            case "addIgnoredChannel":
//                try {
//                    if (event.getMessage().getMentionedChannels().size()==0){
//                        event.reply("Please mention a channel");
//                        return;
//                    }
//                    TextChannel channel = event.getMessage().getMentionedChannels().get(0);
//                    provider.addIgnoredChannel(channel.getIdLong());
//                    event.reply("Ignored Channel added! :ok_hand:");
//                } catch (SQLException e) {e.printStackTrace();}
//                break;
//            case "clearIgnoredChannels":
//                try {
//                    provider.clearIgnoredChannels();
//                    event.reply("Ignored Channels list has been cleared :ok_hand:");
//                } catch (SQLException e) {e.printStackTrace();}
//                break;
//            default:
//                event.reply("Unknown option, ^help settings");
//        }
    }
}
