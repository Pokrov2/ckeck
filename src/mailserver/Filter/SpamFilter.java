package mailserver.Filter;

import mailserver.Model.Message;

public interface SpamFilter {
    boolean isSpam(Message message);
}
