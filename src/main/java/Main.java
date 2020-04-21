// JDA API imports
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.hooks.EventListener;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
// Java-based API imports
import javax.security.auth.login.LoginException;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.Scanner;


/**
 * @author Ajay Seedoo, John Boyd
 * @version beta
 * This class creates a bot for use with the Discord webservice. It is currently able
 * to report and forecast the weather the user's Geolocation
 */
public class Main extends ListenerAdapter implements EventListener {

    public static void main(String[] args) throws LoginException {
        JDA jda = new JDABuilder(getToken()).addEventListeners(new Main()).setActivity(Activity.watching("joe mama")).build();
        jda.addEventListener(new Commands());
    }

    public static String getToken() {//getToken method
        String token;

        try (InputStream input = new FileInputStream("config.properties")) {//start try statement
            Properties prop = new Properties();
            prop.load(input);
            token = prop.getProperty("token");
        }//end try statement
        catch (IOException e) {//start catch statement
            e.printStackTrace();
            System.out.println("Enter the bot token: ");
            Scanner in = new Scanner(System.in);
            token = in.nextLine();
            in.close();
        }//end catch statement

        return token;
    }//end getToken method
}
