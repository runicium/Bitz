import discord4j.core.DiscordClient;
import discord4j.core.DiscordClientBuilder;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class Main {
    public static void main(String[] args) {
        //stores token from config file
        String token = getToken();

        //builds client with token
        DiscordClient client = new DiscordClientBuilder(token).build();

        //establishes a connection to server
        client.login().block();
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
