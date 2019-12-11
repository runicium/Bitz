package john.ajay.alt;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

public class BasicCommands extends ListenerAdapter {

    private Message msg;
    private String rawMsg;

    public void onMessageReceived(MessageReceivedEvent event) {

        msg = event.getMessage();
        rawMsg = event.getMessage().getContentRaw();

        if(rawMsg.contains(".help")) {
            displayHelp();
        }
        if(rawMsg.contains(".sban")) {
            softBan();
        }
    }

    private void displayHelp() {
        msg.getChannel().sendMessage("figure it out nerd").queue();
    }

    private void softBan() {
        String user = rawMsg.substring(rawMsg.indexOf("ban") + 4);
    }



}
