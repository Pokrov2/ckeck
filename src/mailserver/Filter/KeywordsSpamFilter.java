package mailserver.Filter;

import mailserver.Model.Message;

import java.util.*;
import java.util.stream.Collectors;
import java.util.Objects;

public class KeywordsSpamFilter implements SpamFilter {
    private final Set<String> keywords;

    public KeywordsSpamFilter(String get_list) {
        Objects.requireNonNull(get_list, "Ключевые слова не могут остуствовать");
        this.keywords = Arrays.stream(get_list.toLowerCase().trim().split("\\s+"))
                .filter(word -> word.matches("[а-яёa-z0-9]+"))
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isSpam(Message message) {
        String text = (message.getCaption() == null ? "" : message.getCaption()) + " " +
                (message.getText() == null ? "" : message.getText());
        return Arrays.stream(text.toLowerCase().split("[^а-яёa-z0-9]+"))
                .filter(word -> !word.isEmpty())
                .anyMatch(keywords::contains);
    }
}

