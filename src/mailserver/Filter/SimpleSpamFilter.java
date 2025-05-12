package mailserver.Filter;

import mailserver.Model.Message;
import java.util.*;
public class SimpleSpamFilter implements SpamFilter {
    private final KeywordsSpamFilter simpleSpamFilter;

    public SimpleSpamFilter() {
        this.simpleSpamFilter = new KeywordsSpamFilter("spam");
    }

    @Override
    public boolean isSpam(Message message) {
        return this.simpleSpamFilter.isSpam(message);
    }
}




