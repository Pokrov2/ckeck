package mailserver.Model;

import mailserver.Filter.SpamFilter;

import java.util.ArrayList;
import java.util.List;

public class User {
    private final String username;
    private final List<Message> inbox = new ArrayList<>();
    private final List<Message> outbox = new ArrayList<>();
    private final List<Message> spam = new ArrayList<>();
    private SpamFilter spamFilter;

    public User(String username) {
        this.username = username;
    }

    public String GetUsername() {
        return username;
    }

    public List<Message> GetInbox() {
        return new ArrayList<>(inbox);
    }

    public List<Message> GetOutbox() {
        return new ArrayList<>(outbox);
    }

    public List<Message> GetSpam() {
        return new ArrayList<>(spam);
    }

    public void SetSpamFilter(SpamFilter filter) {
        this.spamFilter = filter; // Сеттер
    }

    public void SendMessage(User receiver, String caption, String text) {
        Message msg = new Message(caption, text, this.username, receiver.GetUsername());
        boolean isSpam = (receiver.spamFilter != null) && receiver.spamFilter.isSpam(msg);
        if (isSpam) {
            receiver.spam.add(msg);
        }
        else {
            receiver.inbox.add(msg);
        }
        this.outbox.add(msg);
    }
}
