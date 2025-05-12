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
        if (message == null || message.GetText() == null || message.GetText().isEmpty()) {
            return false;
        }

        String text = message.GetText().toLowerCase();
        String[] words = text.split("[^\\p{L}]+");


        Map<String, Integer> wordCount = new HashMap<>();

        for (String word : words) {
            if (word.isEmpty()) continue;

            int count = wordCount.getOrDefault(word, 0) + 1;
            wordCount.put(word, count);

            // Проверяем, превышает ли количество повторений лимит
            if (count > maxRepetitions) { // Например, при maxRepetitions=3: 4 > 3 → true
                return true;
            }
        }

        return false;
    }
}
