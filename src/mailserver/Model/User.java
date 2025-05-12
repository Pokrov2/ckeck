package mailserver.Model;

import mailserver.Filter.CompositeSpamFilter;
import mailserver.Filter.SpamFilter;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final List<Message> inbox;
    private final List<Message> outbox;
    private final List<Message> spam;
    private SpamFilter spamFilter = new CompositeSpamFilter(List.of());

    public User(String username) {
        this.username = username;
        this.inbox = new ArrayList<>();
        this.outbox = new ArrayList<>();
        this.spam = new ArrayList<>();

    }

    public String getUsername() {
        return username;
    }

    public List<Message> getInbox() {
        return new ArrayList<>(inbox);
    }

    public List<Message> getOutbox() {
        return new ArrayList<>(outbox);
    }

    public List<Message> getSpam() {
        return new ArrayList<>(spam);
    }

    public void setSpamFilter(SpamFilter filter) {
        this.spamFilter = filter;
    }


    public void sendMessage(User receiver, String caption, String text) {
        Message msg = new Message(caption, text, this.username, receiver.getUsername());
        boolean isSpam = receiver.spamFilter.isSpam(msg);
        if (isSpam) {
            receiver.spam.add(msg);
        }
        else {
            receiver.inbox.add(msg);
        }
        this.outbox.add(msg);
    }

}
