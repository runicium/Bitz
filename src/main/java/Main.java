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
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class Main extends ListenerAdapter implements EventListener {
    static final private String  BESTPREFIX = ".";
    static private List<String[][]> prefix = new ArrayList<>();

    public static void main(String[] args) throws LoginException {
        JDA jda = new JDABuilder(getToken()).addEventListeners(new Main()).setActivity(Activity.watching("my master create me")).build();
    }
    public void onMessageReceived(MessageReceivedEvent event) {
        Message msg = event.getMessage();
        Guild guild = msg.getGuild();
        if(msg.getContentRaw().contains(BESTPREFIX + "prefix") || msg.getContentRaw().contains(getPrefix(guild) + "prefix")) {
            changePrefix(guild, msg.getContentRaw().substring(msg.getContentRaw().lastIndexOf("prefix") + 7));
            msg.getChannel().sendMessage("The prefix has been changed to *" + msg.getContentRaw().substring(msg.getContentRaw().lastIndexOf("prefix") + 7) + "*").queue();
            //System.out.println("we got here");
        }
    }

    private static void changePrefix(Guild guild, String prefixId) {
        for(int i = 0; i < prefix.size(); i++) {
            if(prefix.get(i)[i][0].equals(guild.getId())) {
                String[][] string = new String[3][3];
                string[1][0] = guild.getId();
                string[0][1] = prefixId;
                System.out.println(string[1][0]);
                prefix.set(i, string);
            } else {
                String[][] string = new String[3][3];
                string[1][0] = guild.getId();
                string[0][1] = prefixId;
                System.out.println("here");
                System.out.println(string[1][0]);
                prefix.add(string);
            }
        }
    }

    private static String getPrefix(Guild guild) {
        for(int i = 0; i < prefix.size(); i++) {
            if(prefix.get(i)[i][0].equals(guild.getId()))
                return(prefix.get(i)[i][1]);
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
