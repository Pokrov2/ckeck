package mailserver.Filter;

import mailserver.Model.Message;

import java.util.HashMap;
import java.util.Map;

public class RepetitionsSpamFilter implements SpamFilter {
    private final int maxRepetitions;

    public RepetitionsSpamFilter(int maxRepetitions) {
        this.maxRepetitions = maxRepetitions;
    }

    @Override
    public boolean isSpam(Message message) {
        if (message == null || message.getText() == null || message.getText().isEmpty()) {
            return false;
        }

        String text = message.getText().toLowerCase();
        String[] words = text.split("[^\\p{L}]+");


        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            if (word.isEmpty()) continue;

            int count = wordCount.getOrDefault(word, 0) + 1;
            wordCount.put(word, count);

            if (count > maxRepetitions) {
                return true;
            }
        }

        return false;
    }
}
