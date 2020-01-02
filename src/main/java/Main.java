// Local-based API imports
// Net-based API imports

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

// Java-based API imports

/**
 * @author Ajay Seedoo, John Boyd
 * @version beta
 * This class creates a bot for use with the Discord webservice. It is currently able
 * to report and forecast the weather the user's Geolocation
 */
public class Main extends ListenerAdapter implements EventListener {

    public static void main(String[] args) throws LoginException {
        JDA jda = new JDABuilder(getToken()).addEventListeners(new Main()).setActivity(Activity.watching("my masters create me")).build();
        jda.addEventListener(new Commands());
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
