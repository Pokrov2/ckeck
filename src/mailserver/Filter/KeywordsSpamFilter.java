package mailserver.Filter;

import mailserver.Model.Message;

import java.util.*;

public class KeywordsSpamFilter implements SpamFilter {
    private final List<String> keywords;

    public KeywordsSpamFilter(String get_list) {

        this.keywords = Arrays.stream(get_list.trim().split("\\s+")).map(String::toLowerCase).toList();
    }

    @Override
    public boolean isSpam(Message message) {
        List<String> messageWords = new ArrayList<>();
        String[] tempMsgWords = (message.GetCaption() + " " + message.GetText())
                .toLowerCase()
                .split("[^а-яёa-z0-9]");
        for (String word : tempMsgWords) {
            if (!word.isEmpty()) {
                messageWords.add(word);
            }
        }

        return messageWords.stream().anyMatch(keywords::contains);
    }
}

