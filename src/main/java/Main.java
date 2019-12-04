import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
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
import java.util.List;
import java.util.Properties;

public class Main extends ListenerAdapter implements EventListener {
    static final String BESTPREFIX = ".";
    static List<String[][]> prefix;

    public static void main(String[] args) throws LoginException {
        JDA jda = new JDABuilder(getToken()).addEventListeners(new Main()).setActivity(Activity.watching("my master create me")).build();
    }
    @Override
    public void onMessageRecieved(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        Guild guild = msg.getGuild();
        if(msg.getContentRaw().equals(BESTPREFIX + "prefix") || msg.getContentRaw().equals(getPrefix(guild) + "prefix")) {
            changePrefix(guild, msg.getContentRaw().substring(msg.getContentRaw().lastIndexOf("prefix") + 1));
            msg.getChannel().sendMessage("The prefix has been changed to *" + msg.getContentRaw().substring(msg.getContentRaw().lastIndexOf("prefix") + 1) + "*").queue();
            System.out.println("we got here");
        }
    }

    private static void changePrefix(Guild guild, String prefixId) {
        for(int i = 0; i < prefix.size(); i++) {
            if(prefix.get(i)[i][0].equals(guild.getId())) {
                String[][] string = new String[1][0];
                string[1][0] = guild.getId();
                string[0][0] = prefixId;
                prefix.set(i, string);
            } else {
                String[][] string = new String[1][0];
                string[1][0] = guild.getId();
                string[0][0] = prefixId;
                prefix.add(string);
            }
        }
    }

    private static String getPrefix(Guild guild) {
        for(int i = 0; i < prefix.size(); i++) {
            if(prefix.get(i)[i][0].equals(guild.getId()))
                return(prefix.get(i)[i][0]);
        }
        return "This should never happen wtf";
    }
    private static void establishConnection() {
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }
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
