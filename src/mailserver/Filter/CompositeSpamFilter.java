package mailserver.Filter;

import mailserver.Model.Message;

import java.util.List;

public class CompositeSpamFilter implements SpamFilter {
    private final List<SpamFilter> filters;

    public CompositeSpamFilter(List<SpamFilter> filters) {
        this.filters = filters;
    }

    @Override
    public boolean isSpam(Message message) {
        return filters.stream().anyMatch(filter -> filter.isSpam(message));
    }
}
