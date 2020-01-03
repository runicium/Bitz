//Weather API imports

import com.github.dvdme.ForecastIOLib.ForecastIO;
//JDA API imports
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Invite;
import net.dv8tion.jda.api.entities.Message;
/* import net.dv8tion.jda.api.entities.User; unused*/
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

//Java-based API imports
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class Commands extends ListenerAdapter {

    private String DEFAULTPREFIX = "."; //The prefix at the beginning of of message that is by default used to call the bot
    /* private ArrayList<GuildPrefixes> prefix = new ArrayList<>(); */ //A list of Strings that the prefix has been changed to (unused)

    private ArrayList<Invite.Channel> archivedChannels = new ArrayList<>();
    private Guild guild;

    public void onMessageReceived(MessageReceivedEvent event) {

        Message msg = event.getMessage();
        String rawMsg = msg.getContentRaw();
        Guild guild = msg.getGuild();

        if (rawMsg.contains(".help")) {
            try {
                System.out.print("Displaying help... ");
                displayHelp(msg);
                System.out.println("Success!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        /*unused
        else if (rawMsg.contains(DEFAULTPREFIX + "sban")) {
            try {
                System.out.print("Soft banning ");
                softBan();
                System.out.println("Success!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else if (msg.getContentRaw().contains(DEFAULTPREFIX + "prefix")) {
            changePrefix(guild, msg.getContentRaw().substring(msg.getContentRaw().indexOf("prefix") + 7) + " ");
            System.out.println(msg.getContentRaw().substring(msg.getContentRaw().indexOf("prefix") + 7));
            msg.getChannel().sendMessage("The prefix has been changed to **" + msg.getContentRaw().substring(msg.getContentRaw().lastIndexOf("prefix") + 7) + "**").queue();
        }
        else if (msg.getContentRaw().contains(DEFAULTPREFIX + "getprefix") || msg.getContentRaw().contains(getPrefix(guild) + "getprefix")) {
            System.out.println("This is the current prefix assigned to the server: " + getPrefix(guild));
            msg.getChannel().sendMessage("Your current prefix is: **" + prefix + "**").queue();
        }*/
        else if (msg.getContentRaw().contains(DEFAULTPREFIX + "weather")) {
            try {
                System.out.print("Sending weather information... ");
                msg.getChannel().sendMessage(getWeather().build()).queue();
                System.out.println("Success!");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (rawMsg.contains(DEFAULTPREFIX + "notify") && !event.getAuthor().isBot()) {
            try {
                System.out.print("Parsing time... ");
                int hour = Integer.parseInt(msg.getContentRaw().substring(8, 10));
                int minute = Integer.parseInt(msg.getContentRaw().substring(11, 13));
                System.out.println("Success!");
                System.out.println("Setting weather notification... ");
                createTimer(event.getGuild(), hour, minute, msg);
                System.out.println("Success!");
                msg.getChannel().sendMessage("The time you input is: " + msg.getContentRaw().substring(8)).queue();
            } catch (IndexOutOfBoundsException e) {
                msg.getChannel().sendMessage("Please input a time you would like to be notified (use military time; Ex: 21:30 is 9:30 PM)").queue();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (rawMsg.contains(DEFAULTPREFIX + "archive") && !event.getAuthor().isBot()) {
            try {
                System.out.print("Retrieving channel... ");
                TextChannel channel = msg.getMentionedChannels().get(0);
                System.out.println("Success!");
                System.out.println("Writing messages from " + channel.getName() + " to file... ");
                archiveChannel(channel, msg);
                System.out.println("Success!");
            } catch (IndexOutOfBoundsException e) {

            } catch (Exception e) {

            }
        }
    }

    private void displayHelp(Message msg) {
        msg.getChannel().sendMessage("figure it out nerd").queue();
    }

    /*unused
    private void softBan() {
        try {
            User user = msg.getMentionedUsers().get(0);
            String username = user.getName();
            System.out.println(username + "... ");
            guild.ban(user, 1);
            msg.getChannel().sendMessage("*Banned " + username + "*").queue();
            guild.unban(user);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }*/

    private void createTimer(Guild guild, int hour, int minute, Message msg) {
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

    private MessageBuilder getWeather() {
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
        MessageBuilder msgBuild = new MessageBuilder(
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

        return msgBuild;

    }
    /* unused
    private boolean changePrefix(Guild guild, String prefixId) {
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
    } */

    private void archiveChannel(TextChannel channel, Message requestMsg) {
        String channelMessages = "";
        List<Message> messageHistory = channel.getIterableHistory().complete();
        File archiveFile = new File(channel.getName() + "-archive.txt");
        for (Message message : messageHistory) {
            channelMessages += message.getTimeCreated().toLocalDateTime().format(DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM)) + ": ";
            channelMessages += message.getAuthor().getName() + ":\n";
            channelMessages += message.getContentDisplay() + "\n\n";
        }
        System.out.println(channelMessages);
        try {
            FileWriter historyWrite = new FileWriter(archiveFile);
            historyWrite.write(channelMessages);
        } catch (IOException e) {
            System.err.println("Failed to create file.");
        } catch (Exception e) {
            e.printStackTrace();
        }

        requestMsg.getChannel().sendMessage("Archives of " + channel.getName()).addFile(new File(channel.getName() + "-archive.txt")).queue();
    }
}