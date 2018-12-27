package DonBot.commands.moderation;

import DonBot.api.DonCommand;
import com.jagrosh.jdautilities.command.CommandEvent;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import net.dv8tion.jda.core.Permission;

public class WelcomerCommand extends DonCommand {
    private EventWaiter waiter;

    public WelcomerCommand(EventWaiter waiter) {
        this.name = "welcomer";
        this.aliases = new String[]{"w"};
        this.usage = "welcomer has been deprecated. use the dashboard instead. https://donbot.space/dashboard";
        this.guildOnly = true;
        this.help = "welcomer has been deprecated. use the dashboard instead. https://donbot.space/dashboard";
        this.category = new Category("moderation");
        this.userPermissions = new Permission[]{Permission.MANAGE_SERVER};
        this.waiter = waiter;
        this.ID = 522;
    }

    @Override
    protected void execute(CommandEvent event) {
        super.execute(event);
        event.reply("welcomer has been deprecated. use the dashboard instead. https://donbot.space/dashboard");
//        try {
//            DonGuildSettingsProvider provider = event.getClient().getSettingsFor(event.getGuild());
//            if (event.getArgs().equals("enable")) {
//                if (!provider.isWenabled()) {
//                    event.reply("✨Welcome to the welcomer setup wizard!✨\n" +
//                            "First, tag the channel the bot will send the welcome message through (for example<#"+event.getChannel().getId()+">), the bot must have permissions to send there.");
//                    waiter.waitForEvent(MessageReceivedEvent.class,
//                            messageReceivedEvent -> event.getAuthor().equals(messageReceivedEvent.getAuthor())&&event.getChannel().equals(messageReceivedEvent.getChannel()),
//                            ev -> {
//                                if (ev.getMessage().getMentionedChannels().size()==0){
//                                    event.reply(":no_entry_sign: No channel was mentioned. Cancelling setup wizard...");
//                                    return;
//                                }
//                                TextChannel channel = ev.getMessage().getMentionedChannels().get(0);
//                                event.reply(":ok_hand: Great! :ok_hand:\n" +
//                                        "Now send the message you want to appear! 255 character limit!\n" +
//                                        "Formats:\n" +
//                                        "%SMEMBERS% - Server member count\n" +
//                                        "%SMEMBERSUFFIX% - Suffix for the member count(st, nd, ...)\n" +
//                                        "%SERVER% - Server name\n" +
//                                        "%SOWNER% - Server owner's name\n" +
//                                        "%SOWNERNICK% - Server owner's nickname\n" +
//                                        "%SOWNERID% - Server owner's id\n" +
//                                        "%SOWNER% - Server owner's name + discriminator (uname#disc)\n" +
//                                        "%MEMBERNAME% - Joined member's name\n" +
//                                        "%MEMBER% - Joined member's name + discriminator\n" +
//                                        "%MEMBERID% - Joined member's id\n\n" +
//                                        "<#channelid> to tag channels\n" +
//                                        "<@userid> to tag users");
//                                waiter.waitForEvent(MessageReceivedEvent.class,
//                                        messageReceivedEvent -> event.getAuthor().equals(messageReceivedEvent.getAuthor())&&event.getChannel().equals(messageReceivedEvent.getChannel()),
//                                        eve -> {
//                                            if (eve.getMessage().getContentDisplay().equals("")){
//                                                event.reply(":no_entry_sign: Cannot set an empty message, Cancelling setup wizard...");
//                                            } else {
//                                                if (eve.getMessage().getContentRaw().length() > 256) {
//                                                    event.reply(":no_entry_sign: Message too long, Cancelling setup wizard...");
//                                                    return;
//                                                }
//                                                try {
//                                                    provider.setWelcomer(channel, eve.getMessage().getContentRaw(), true);
//
//                                                } catch (SQLException e) {
//                                                    e.printStackTrace();
//                                                }
//                                                event.reply(":ok_hand: All set! you're good to go!");
//                                            }
//                                        });
//                            });
//                } else {
//                    event.reply("Welcomer is already enabled! to reconfigure disable and reenable!");
//                }
//            } else if (event.getArgs().equals("disable")) {
//                try {
//                    provider.setWelcomer(null, "", false);
//
//                } catch (SQLException e) {e.printStackTrace();}
//                event.reply(":ok_hand: Welcomer disabled.");
//            } else {
//                event.reply("Correct use: " + this.usage);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
