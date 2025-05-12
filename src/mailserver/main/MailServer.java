package mailserver.main;

import mailserver.Filter.*;
import mailserver.Model.User;
import mailserver.Storage.UserStorage;
import mailserver.Model.Message;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

public class MailServer {
    private final Scanner scanner;
    private final UserStorage userStorage;
    private final PrintStream output;
    private boolean isSettingFilter = false;
    private String filteringUser = null;
    private final List<SpamFilter> currentFilters = new ArrayList<>();

    public MailServer(UserStorage userStorage, InputStream input, PrintStream output) {
        this.userStorage = userStorage;
        this.scanner = new Scanner(input);
        this.output = output;
    }

    public MailServer() {
        this(new UserStorage(), System.in, System.out);
    }

    public void Run() {
        output.println("An impudent copy mail.ru launched:");
        while (true) {
            String input = scanner.nextLine().trim();
            if (input.equalsIgnoreCase("exit")) break;

            if (isSettingFilter) {
                HandleFilterInput(input);
            } else {
                HandleCommand(input);
            }
        }
    }

    void HandleCommand(String input) {
        String[] tokens = input.split(" ");
        if (tokens.length == 0) return;
        String command = tokens[0].toLowerCase();
        switch (command) {
            case "add": HandleAdd(tokens); break;
            case "list": HandleList(); break;
            case "send": HandleSend(tokens); break;
            case "inbox": HandleInbox(tokens, false); break;
            case "spam": HandleInbox(tokens, true); break;
            case "outbox": HandleOutbox(tokens); break;
            case "setfilter": HandleSetFilter(tokens); break;
            default: output.println("Неизвестная команда.");
        }
    }

    void HandleAdd(String[] tokens) {
        if (tokens.length != 2) {
            output.println("Правильно: add <Пользователь>");
            return;
        }
        String username = tokens[1];
        if (userStorage.UserExists(username)) {
            output.println("Такой пользователь уже существует");
        } else {
            userStorage.AddUser(new User(username));
            output.println("Пользователь добавлен: " + username);
        }
    }

    void HandleList() {
        output.println("Пользователи:");
        for (User user : userStorage.GetAllUsers()) {
            output.println("- " + user.GetUsername());
        }
        output.println("Пользователи:");

        List<User> all = userStorage.GetAllUsers();

        if (all.isEmpty()) {
            return;
        }
        for (User user : all) {
            output.println("- " + user.GetUsername());
        }
    }


    void HandleSend(String[] tokens) {
        if (tokens.length < 5) {
            output.println("Правильно: send <Отправитель> <Получатель> <заголовок> <текст>");
            return;
        }
        String senderName = tokens[1];
        String receiverName = tokens[2];
        if (!userStorage.UserExists(senderName) || !userStorage.UserExists(receiverName)) {
            output.println("Отправитель или получатель отсутствует");
            return;
        }
        String caption = tokens[3];
        String text = String.join(" ", Arrays.copyOfRange(tokens, 4, tokens.length));
        User sender = userStorage.GetUser(senderName);
        User receiver = userStorage.GetUser(receiverName);
        sender.SendMessage(receiver, caption, text);
        output.println("Сообщение отправлено!.");
    }

    void HandleInbox(String[] tokens, boolean spam) {
        if (tokens.length != 2) {
            output.println("Правильно: " + (spam ? "spam" : "inbox") + " <Пользователь>");
            return;
        }
        String username = tokens[1];
        if (!userStorage.UserExists(username)) {
            output.println("Пользователь отсутствует");
            return;
        }
        User user = userStorage.GetUser(username);
        List<Message> messages = spam ? user.GetSpam() : user.GetInbox();
        if (messages.isEmpty()) {
            output.println("Сообщения: пусто.");
            return;
        }
        for (Message msg : messages) {
            output.println("-----");
            output.println("Subject: " + msg.GetCaption()); // Явный вывод заголовка
            output.println(msg.GetText());
        }
    }

    void HandleOutbox(String[] tokens) {
        if (tokens.length != 2) {
            output.println("Правильно: outbox <Пользователь>");
            return;
        }
        String username = tokens[1];
        if (!userStorage.UserExists(username)) {
            output.println("Пользователь отсутствует");
            return;
        }
        User user = userStorage.GetUser(username);
        List<Message> outbox = user.GetOutbox();
        if (outbox.isEmpty()) {
            output.println("Отправленные сообщения: пусто.");
            return;
        }
        for (Message msg : outbox) {
            output.println("-----");
            output.println(msg);
        }
    }

    void HandleSetFilter(String[] tokens) {
        if (tokens.length != 2) {
            output.println("Правильно: setfilter <Имя пользователя>");
            return;
        }
        String username = tokens[1];
        if (!userStorage.UserExists(username)) {
            output.println("Пользователь отсутствует");
            return;
        }
        isSettingFilter = true;
        filteringUser = username;
        currentFilters.clear();
        output.println("Примеры фильтров: simple, keywords, repetition, sender. Напишите 'done' чтобы закончить вводить фильтры:");
    }

    void HandleFilterInput(String input) {
        if (input.equalsIgnoreCase("done")) {
            User user = userStorage.GetUser(filteringUser);
            user.SetSpamFilter(new CompositeSpamFilter(currentFilters));
            output.println("Спам фильтр установлен для " + filteringUser);
            isSettingFilter = false;
            filteringUser = null;
            currentFilters.clear();
            return;
        }

        String[] tokens = input.split(" ");
        switch (tokens[0]) {
            case "simple":
                currentFilters.add(new SimpleSpamFilter());
                output.println("Добавлен простой фильтр");
                break;
            case "keywords":
                if (tokens.length < 2) {
                    output.println("Правильно: keywords <слово1> <слово2> ...");
                    break;
                }
                String kw = "";
                for(int i = 1;i<tokens.length;i++){
                    kw = kw+tokens[i]+"";
                }
                List<String> keywords = Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length));
                currentFilters.add(new KeywordsSpamFilter(kw));
                output.println("Добавлен фильтр ключевых слов: " + kw);
                break;
            case "repetition":
                if (tokens.length != 2) {
                    output.println("Правильно: repetition <число>");
                    break;
                }
                try {
                    int limit = Integer.parseInt(tokens[1]);
                    currentFilters.add(new RepetitionsSpamFilter(limit));
                    output.println("Добавлен фильтр повторений с лимитом " + limit);
                } catch (NumberFormatException e) {
                    output.println("Ошибка: лимит должен быть числом");
                }
                break;
            case "sender":
                if (tokens.length < 2) {
                    output.println("Правильно: sender <имя1> <имя2> ...");
                    break;
                }
                Set<String> blocked = new HashSet<>(Arrays.asList(Arrays.copyOfRange(tokens, 1, tokens.length)));
                currentFilters.add(new SenderSpamFilter(blocked));
                output.println("Добавлен фильтр отправителей: " + blocked);
                break;
            default:
                output.println("Неизвестный фильтр: " + tokens[0]);
        }
    }
}
