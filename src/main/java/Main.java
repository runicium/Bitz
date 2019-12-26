// Local-based API imports
// Net-based API imports
import com.github.dvdme.ForecastIOLib.ForecastIO;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
// Java-based API imports
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * @author Ajay Seedoo, John Boyd
 * @version beta
 * This class creates a bot for use with the Discord webservice. It is currently able
 * to report and forecast the weather at a Geolocation set in Southern Florida.
 */
public class Main extends ListenerAdapter implements EventListener {
    static final private String BESTPREFIX = "."; //The prefix at the beginning of of message that is by default used to call the bot
    static private List<String[][]> prefix = new ArrayList<>(); //A list of Strings that the prefix has been changed to
    static private String lastMessage = ""; //The most recent message sent to the server

    public static void main(String[] args) throws LoginException {
        JDA jda = new JDABuilder(getToken()).addEventListeners(new Main()).setActivity(Activity.watching("my master create me")).build();
        jda.addEventListener(new BasicCommands());
    }

    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        Guild guild = msg.getGuild();

        if (msg.getContentRaw().contains(BESTPREFIX + "prefix") || msg.getContentRaw().contains(getPrefix(guild) + "prefix")) {
            changePrefix(guild, msg.getContentRaw().substring(msg.getContentRaw().indexOf("prefix") + 7) + " ");
            System.out.println(msg.getContentRaw().substring(msg.getContentRaw().indexOf("prefix") + 7));
            msg.getChannel().sendMessage("The prefix has been changed to **" + msg.getContentRaw().substring(msg.getContentRaw().lastIndexOf("prefix") + 7) + "**").queue();
        }

        if (msg.getContentRaw().contains(BESTPREFIX + "getprefix") || msg.getContentRaw().contains(getPrefix(guild) + "getprefix")) {
            System.out.println("This is the current prefix assigned to the server: " + getPrefix(guild));
            msg.getChannel().sendMessage("Your current prefix is: **" + getPrefix(guild) + "**").queue();
        }

        if (msg.getContentRaw().contains(BESTPREFIX + "weather") || msg.getContentRaw().contains(getPrefix(guild) + "weather")) {
            getWeather();
            msg.getChannel().sendMessage(getWeather().build()).queue();
        }
        if (lastMessage.contains(BESTPREFIX + "notify") && msg.getContentRaw().contains(":") && !event.getAuthor().isBot() && !lastMessage.contains(":")) {
            lastMessage = msg.getContentRaw();
            String hour = msg.getContentRaw().substring(0, 2);
            int nhour = Integer.parseInt(hour);
            String minute = msg.getContentRaw().substring(3, 5);
            int nminute = Integer.parseInt(minute);
            createTimer(event.getGuild(), nhour, nminute, msg);
            msg.getChannel().sendMessage("The time you input is: " + msg.getContentRaw()).queue();
        }
        if (msg.getContentRaw().contains(BESTPREFIX + "notify") || msg.getContentRaw().contains(getPrefix(guild) + "notify")) {
            lastMessage = msg.getContentRaw();
            if (msg.getContentRaw().contains(":")) {
                String hour = msg.getContentRaw().substring(msg.getContentRaw().indexOf(":") - 2, msg.getContentRaw().lastIndexOf(":"));
                int nhour = Integer.parseInt(hour);
                System.out.println(nhour);
                String minute = msg.getContentRaw().substring(msg.getContentRaw().indexOf(":") + 1, msg.getContentRaw().indexOf(":") + 3);
                int nminute = Integer.parseInt(minute);
                System.out.println(nminute);

                createTimer(event.getGuild(), nhour, nminute, msg);
                msg.getChannel().sendMessage("The time you input is: " + msg.getContentRaw().substring(msg.getContentRaw().indexOf(":") - 2)).queue();
            } else {
                msg.getChannel().sendMessage("Please input a time you would like to be notified (use military time; Ex: 21:30 or 01:20)").queue();

            }
        }

    }

    private static void createTimer(Guild guild, int hour, int minute, Message msg) {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, hour);
        today.set(Calendar.MINUTE, minute);
        today.set(Calendar.SECOND, 0);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                msg.getChannel().sendMessage(getWeather().build()).queue();
            }
        };

        timer.scheduleAtFixedRate(task, today.getTime(), TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
    }

    private static MessageBuilder getWeather() {
        ForecastIO fio = new ForecastIO("432f74725dc976dbf0c43574a6d874bb");
        fio.setUnits(ForecastIO.UNITS_US);
        fio.setLang(ForecastIO.LANG_ENGLISH);
        fio.getForecast("26.27166558", "-80.18416593");

        String information = fio.getCurrently().toString();
        String strHumidity = information.substring(information.lastIndexOf("humidity") + 10, information.lastIndexOf("pressure") - 2);
        double humidity = Double.parseDouble(strHumidity);
        String temperature = information.substring(information.lastIndexOf("temperature") + 13, information.lastIndexOf("apparentTemperature") - 2);
        String wind = information.substring(information.lastIndexOf("windSpeed") + 11, information.lastIndexOf("windGust") - 2);
        String rain = information.substring(information.lastIndexOf("precipProbability") + 19, information.indexOf("temperature") - 2);
        MessageBuilder messagebuild = new MessageBuilder(
                "**The current weather information is:** " +
                        "\nHumidity: " + humidity * 100 + "%" +
                        "\r\nTemperature: " + temperature + "F" +
                        "\r\nWind: " + wind + "mph" +
                        "\r\nChance of Rain: " + rain + "%"
        );

        System.out.println(fio.getCurrently().toString());
        System.out.println("Latitude: " + fio.getLatitude());
        System.out.println("Longitude: " + fio.getLongitude());
        System.out.println("Timezone: " + fio.getTimezone());
        System.out.println("Humidity: " + fio.getCurrently().toString().substring(fio.getCurrently().toString().lastIndexOf("humidity") + 10, fio.getCurrently().toString().lastIndexOf("humidity") + 14));
        System.out.println("Temperature: " + fio.getCurrently().toString().substring(fio.getCurrently().toString().lastIndexOf("temperature") + 13, fio.getCurrently().toString().lastIndexOf("temperature") + 18));

        return messagebuild;

    }

    private static boolean changePrefix(Guild guild, String prefixId) {

        for (int i = 0; i < prefix.size(); i++) {
            if (prefix.get(i)[0][0].equals(guild.getId())) {
                String[][] string = new String[2][1];
                string[0][0] = guild.getId();
                string[1][0] = prefixId;
                prefix.set(i, string);
                return (true);
            }
        }

        String[][] goodstring = new String[2][1];
        goodstring[0][0] = guild.getId();
        goodstring[1][0] = prefixId;
        prefix.add(goodstring);
        return (true);
    }

    private static String getPrefix(Guild guild) {
        for (String[][] strings : prefix) {
            if (strings[0][0].equals(guild.getId()))
                return (strings[1][0]);
        }
        return "This should never happen wtf";
    }

    private static String getToken() {
        String token = "";
        try (InputStream input = new FileInputStream("src/main/resources/config.properties")) {
            Properties prop = new Properties();

            //loads properties file
            prop.load(input);

            //pulls token
            token = prop.getProperty("token");

            //prints token
            //System.out.println(token);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return token;
    }
}
