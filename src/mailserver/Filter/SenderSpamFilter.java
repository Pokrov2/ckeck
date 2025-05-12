package mailserver.Filter;

import mailserver.Model.Message;
import java.util.Set;

public class SenderSpamFilter implements SpamFilter {
    private final Set<String> blocked;
    public SenderSpamFilter(Set<String> blocked) {
        this.blocked = blocked;
    }
    @Override
    public boolean isSpam(Message message) {
        return blocked.contains(message.getSender());
    }
}


