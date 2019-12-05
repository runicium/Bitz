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

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Main extends ListenerAdapter implements EventListener {
    static final private String  BESTPREFIX = ".";
    static private List<String[][]> prefix = new ArrayList<>();

    public static void main(String[] args) throws LoginException {
        createTimer();
        JDA jda = new JDABuilder(getToken()).addEventListeners(new Main()).setActivity(Activity.watching("my master create me")).build();
    }
    public void onMessageReceived(MessageReceivedEvent event) {

        Message msg = event.getMessage();
        Guild guild = msg.getGuild();

        if(msg.getContentRaw().contains(BESTPREFIX + "prefix") || msg.getContentRaw().contains(getPrefix(guild) + "prefix")) {
            changePrefix(guild, msg.getContentRaw().substring(msg.getContentRaw().indexOf("prefix") + 7) +" ");
            System.out.println(msg.getContentRaw().substring(msg.getContentRaw().indexOf("prefix") + 7));
            msg.getChannel().sendMessage("The prefix has been changed to **" + msg.getContentRaw().substring(msg.getContentRaw().lastIndexOf("prefix") + 7) + "**").queue();
        }

        if(msg.getContentRaw().contains(BESTPREFIX + "getprefix") || msg.getContentRaw().contains(getPrefix(guild) + "getprefix")) {
            System.out.println("This is the current prefix assigned to the server: " + getPrefix(guild));
            msg.getChannel().sendMessage("Your current prefix is: **" + getPrefix(guild) + "**").queue();
        }

        if(msg.getContentRaw().contains(BESTPREFIX + "weather") || msg.getContentRaw().contains(getPrefix(guild) + "weather")) {
            getWeather();
            msg.getChannel().sendMessage(getWeather().build()).queue();
        }

    }
    private static void createTimer() {
        Calendar today = Calendar.getInstance();
        today.set(Calendar.HOUR_OF_DAY, 1);
        today.set(Calendar.MINUTE, 35);
        today.set(Calendar.SECOND, 0);

        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Main.getWeather();
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
        String humidity = information.substring(information.lastIndexOf("humidity") + 10, information.lastIndexOf("pressure") - 2);
        String temperature = information.substring(information.lastIndexOf("temperature") + 13, information.lastIndexOf("apparentTemperature") - 2);
        String wind = information.substring(information.lastIndexOf("windSpeed") + 11, information.lastIndexOf("windGust") - 2);
        String rain = information.substring(information.lastIndexOf("precipProbability") + 19, information.indexOf("temperature") - 2);
        MessageBuilder messagebuild = new MessageBuilder(
                "**The current weather information is:** \r\n" +
                "Humidity: " + humidity +
                "\r\nTemperature: " + temperature + "F" +
                "\r\nWind: " + wind + "mph" +
                "\r\nChance of Rain: " + rain + "%"
        );

        System.out.println(fio.getCurrently().toString());
        System.out.println("Latitude: "+fio.getLatitude());
        System.out.println("Longitude: "+fio.getLongitude());
        System.out.println("Timezone: "+fio.getTimezone());
        System.out.println("Humidity: "+fio.getCurrently().toString().substring(fio.getCurrently().toString().lastIndexOf("humidity") + 10, fio.getCurrently().toString().lastIndexOf("humidity") + 14));
        System.out.println("Temperature: "+fio.getCurrently().toString().substring(fio.getCurrently().toString().lastIndexOf("temperature") + 13, fio.getCurrently().toString().lastIndexOf("temperature") + 18));

        return messagebuild;

    }
    private static boolean changePrefix(Guild guild, String prefixId) {

        for(int i = 0; i < prefix.size(); i++) {
            if(prefix.get(i)[0][0].equals(guild.getId())) {
                String[][] string = new String[2][1];
                string[0][0] = guild.getId();
                string[1][0] = prefixId;
                prefix.set(i, string);
                return(true);
            }
        }

        String[][] goodstring = new String[2][1];
        goodstring[0][0] = guild.getId();
        goodstring[1][0] = prefixId;
        prefix.add(goodstring);
        return(true);
    }

    private static String getPrefix(Guild guild) {
        for(int i = 0; i < prefix.size(); i++) {
            if(prefix.get(i)[0][0].equals(guild.getId()))
                return(prefix.get(i)[1][0]);
        }
        return "This should never happen wtf";
    }

    private static String getToken() {
        String token = "";
        try(InputStream input = new FileInputStream("src/main/resources/config.properties")) {
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
