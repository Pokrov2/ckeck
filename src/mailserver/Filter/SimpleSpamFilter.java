package mailserver.Filter;

import mailserver.Model.Message;
public class SimpleSpamFilter implements SpamFilter {
    private final KeywordsSpamFilter keywordsFilter;

    public SimpleSpamFilter() {
        this.keywordsFilter = new KeywordsSpamFilter("spam");
    }

    @Override
    public boolean isSpam(Message message) {
        return keywordsFilter.isSpam(message);
    }
}




