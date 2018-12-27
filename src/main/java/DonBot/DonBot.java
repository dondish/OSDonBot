package DonBot;

import DonBot.api.*;
import DonBot.audio.MusicBotSetup;
import DonBot.commands.currency.*;
import DonBot.commands.fun.*;
import DonBot.commands.general.*;
import DonBot.commands.image.*;
import DonBot.commands.moderation.*;
import DonBot.commands.music.*;
import DonBot.commands.other.*;
import DonBot.features.DonGuildSettingsManager;
import DonBot.utils.StatsUtils;
import DonBot.utils.TextUtils;
import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
import com.jagrosh.jdautilities.commons.waiter.EventWaiter;
import com.sedmelluq.discord.lavaplayer.player.AudioPlayerManager;
import com.sedmelluq.discord.lavaplayer.player.DefaultAudioPlayerManager;
import net.dv8tion.jda.bot.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.bot.sharding.ShardManager;
import net.dv8tion.jda.core.entities.Game;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;
import java.time.Instant;
import java.util.Calendar;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * DonBot Launcher
 * Launches the bot here
 */
public class DonBot {

    private static void updateServerCount(ShardManager jda) {
        DiscordBotListApi.update_stats(jda);
        BotsForDiscordApi.update_stats(jda);
        DiscordBotsGroupAPI.update_stats(jda);
        DiscordBotsPWAPI.update_stats(jda);
        DiscordListAppAPI.update_stats(jda);
        DiscordBoatsAPI.update_stats(jda);
        BotlistSpaceAPI.update_stats(jda);
    }


    public static void main(String[] args) {
        Instant before = Calendar.getInstance().toInstant();
        // Setting up log
        Logger logger = LoggerFactory.getLogger(DonBot.class);
        logger.info("Booting...");
        // Setting up statistic manager
        StatsUtils.init(System.currentTimeMillis());
        logger.info("Done setting up statistics manager");
        // Setting up audio
        logger.info("Setting up audio...");
        AudioPlayerManager manager = new DefaultAudioPlayerManager();
        final MusicBotSetup musicBot = new MusicBotSetup(manager);
        EventWaiter waiter = new EventWaiter();
        logger.info("done.");
        // Setting up commands
        logger.info("Setting up commands...");
        DonGuildSettingsManager settingsManager = new DonGuildSettingsManager(musicBot);
        CommandClientBuilder builder = new CommandClientBuilder();
        builder.setPrefix("<@"+Config.BOTID+"> ").setAlternativePrefix("don ").setOwnerId("239790360728043520").setServerInvite("https://discord.gg/pEKkg9r");
        builder.setGuildSettingsManager(settingsManager);
        builder.setCoOwnerIds("254685342924275712");
        builder.setGame(Game.playing("with my dashboard @ donbot.space"));
        builder.addCommands(
                            // ----- General Commands -----
                            new InviteCommand(),
                            new InfoCommand(),
                            new PingCommand(),
                            new TagCommand(),
                            new ServerInfoCommand(),
                            new MathCommand(),
                            new PubgCommand(),
                            new WorldCupCommand(),
                            new StatsCommand(),
                            // ----- Moderation Commands -----
                            new SettingsCommand(),
                            new WelcomerCommand(waiter),
                            new GoodbyerCommand(waiter),
                            new PurgeCommand(),
                            new MuteCommand(),
                            new UnmuteCommand(),
                            new SoftbanCommand(),
                            new WarnCommand(),
                            new BanCommand(),
                            new UnbanCommand(),
                            new KickCommand(),
                            new ReasonCommand(),
                            // ----- Other Commands -----
                            new ShutdownCommand(),
                            new CommandsCommand(),
                            new GuildsCommand(waiter),
                            new CreateKeyCommand(),
                            new EvalCommand(musicBot),
                            // ----- Music Commands -----
                            new JoinCommand(),
                            new PlayCommand(waiter, musicBot),
                            new PlaylistCommand(musicBot),
                            new StopCommand(musicBot),
                            new PauseCommand(musicBot),
                            new ResumeCommand(musicBot),
                            new QueueCommand(waiter, musicBot),
                            new ClearCommand(musicBot),
                            new SeekCommand(musicBot),
                            new ExportCommand(musicBot),
                            new ShuffleCommand(musicBot),
                            new SkipCommand(musicBot),
                            new NowPlayingCommand(musicBot),
                            new RepeatCommand(musicBot),
                            new ReverseCommand(musicBot),
                            new HistoryCommand(waiter, musicBot),
                            new RemoveCommand(musicBot),
                            new BackCommand(musicBot),
                            new NoDupesCommand(musicBot),
                            new PlayTopCommand(waiter, musicBot),
                            // ----- Fun Commands -----
                            new RandomCommand(),
                            new ChooseCommand(),
                            new RollCommand(),
                            new GuessCommand(waiter),
                            new XKCDCommand(),
                            new SayCommand(),
                            new SimpleCommand("oof", "I don't know why this exists", "https://www.urbandictionary.com/define.php?term=Oof", "fun", true),
                            new CookieCommand(),
                            // new PandaCommand(),
                            // ------ Image Commands -----
                            new AutistCommand(),
                            new GayCommand(),
                            new SovietCommand(),
                            new BotCommand(),
                            new SlavCommand(),
                            new BlurpleCommand(),
                            new MonochromeCommand(),
                            new InvertCommand(),
                            new AvatarCommand(),
                            // ----- Currency Commands -----
                            new DailyCommand(),
                            new FlipCommand(),
                            new CoinsCommand(),
                            new BetCommand(),
                            new TransferCommand(),
                            new AddMoneyCommand(),
                            new SetMoneyCommand()
                            );
        builder.setHelpConsumer(new HelpCommand());
        logger.info("done.");
        logger.info("Building client...");
        CommandClient client = builder.build();
        logger.info("done.");
        logger.info("Starting jda...");
        try {

            ShardManager sm = new DefaultShardManagerBuilder().setToken(Config.BETA)
                    .setGame(Game.playing("booting up..."))
                    .addEventListeners(waiter, client, new DonEventListener(client, waiter))
                    .setAutoReconnect(true)
                    .setBulkDeleteSplittingEnabled(false)
                    .build();
//            ScheduledExecutorService service = Executors.newSingleThreadScheduledExecutor();
//             service.scheduleAtFixedRate(() -> updateServerCount(sm),
//                     1, 20, TimeUnit.MINUTES);
            logger.info("done.");
            logger.info("Loading Backend API...");
            WebAPI.loadServer(client, sm);
        } catch (LoginException  e) {
            System.out.println(e.getMessage());
        }
        logger.info("done.");
        Instant after = Calendar.getInstance().toInstant();
        logger.info("Successful boot! Boot time: " + TextUtils.durationToString(after.toEpochMilli() - before.toEpochMilli()));
    }

}
