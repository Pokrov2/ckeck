package mailserver.main;

import mailserver.Storage.UserStorage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MailServerAddTests {

    private MailServer createServer(String input, ByteArrayOutputStream out) {
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        return new MailServer(new UserStorage(), in, new PrintStream(out));
    }

    @Test
    void testInvalidSendUsage() {
        String input = String.join(System.lineSeparator(),
                "add Petya",
                "send",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();

        String console = out.toString();
        assertTrue(console.contains("Правильно: send <Отправитель> <Получатель> <заголовок> <текст>"), "При пустом send должна выводиться подсказка по правильному использованию");
    }

    @Test
    void testInboxUsageError() {
        String input = String.join(System.lineSeparator(),
                "add u",
                "inbox",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();

        assertTrue(out.toString().contains("Правильно: inbox <Пользователь>"), "При неверном inbox должна быть соответствующая подсказка");
    }

    @Test
    void testOutboxUsageError() {
        String input = String.join(System.lineSeparator(),
                "add u",
                "outbox",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();

        assertTrue(out.toString().contains("Правильно: outbox <Пользователь>"), "При неверном outbox должна быть соответствующая подсказка");
    }

    @Test
    void testSetFilterUsageError() {
        String input = String.join(System.lineSeparator(),
                "add u",
                "setfilter",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();

        assertTrue(out.toString().contains("Правильно: setfilter <Имя пользователя>"), "При неверном setfilter должна быть соответствующая подсказка");
    }

    @Test
    void testDuplicateAdd() {
        String input = String.join(System.lineSeparator(),
                "add Vasya",
                "add Vasya",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();

        String console = out.toString();
        assertTrue(console.contains("Пользователь добавлен: Vasya"));
        assertTrue(console.contains("Такой пользователь уже существует"), "При попытке добавить дубликат должно быть соответствующее сообщение");
    }

    @Test
    void testEmptyList() {
        String input = String.join(System.lineSeparator(),
                "list",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();

        String[] lines = out.toString().split(System.lineSeparator());
        assertEquals("Пользователи:", lines[1].trim(),
                "После «Пользователи:» не должно быть элементов");
    }

    @Test
    void testCaseInsensitivity() {
        String input = String.join(System.lineSeparator(),
                "AdD Carl",
                "LiSt",
                "eXit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();

        String console = out.toString();
        assertTrue(console.contains("Пользователь добавлен: Carl"));
        assertTrue(console.contains("- Carl"), "Команды должны работать вне зависимости от регистра");
    }


    @Test
    void testRepetitionFilterNonSpam() {
        String input = String.join(System.lineSeparator(),
                "add Vasya",
                "add Petya",
                "setfilter Vasya",
                "repetition 3",
                "done",
                "send Petya Vasya subj ok ok ok",
                "inbox Vasya",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();

        String output = out.toString();
        assertTrue(output.contains("Subject: subj") || output.contains("ok ok ok"), "Сообщение должно быть во входящих");
    }


    @Test
    void testSendAndInboxShowsMessage() {
        String input = String.join(System.lineSeparator(),
                "add Petya",
                "add Vasya",
                "send Petya Vasya hi Hello Vasya",
                "inbox Vasya",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();
        String console = out.toString();
        assertTrue(console.contains("From: Petya"),"Должен показывать отправителя");
        assertTrue(console.contains("Subject: hi"),"Должен показывать тему");
        assertTrue(console.contains("Body: Hello Vasya"),"Должен показывать тело сообщения");
    }

    @Test
    void testOutboxShowsSentMessage() {
        String input = String.join(System.lineSeparator(),
                "add Petya",
                "add Vasya",
                "send Petya Vasya subj Body text",
                "outbox Petya",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();
        String console = out.toString();
        assertTrue(console.contains("От: Petya"),"Outbox должен использовать toString() Message");
        assertTrue(console.contains("Кому: Vasya"),"Outbox должен использовать toString() Message");
        assertTrue(console.contains("Тема: subj"),"Outbox должен использовать toString() Message");
        assertTrue(console.contains("Текст: Body text"),"Outbox должен использовать toString() Message");
    }

    @Test
    void testSpamDefaultEmptyThenInboxGivesIt() {
        String input = String.join(System.lineSeparator(),
                "add Petya",
                "add Vasya",
                "send Petya Vasya spam junk",
                "spam Vasya",
                "inbox Vasya",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();
        String console = out.toString();
        assertTrue(console.contains("Сообщения: пусто."),"Spam по-умолчанию пуст");
        assertTrue(console.contains("From: Petya"),"Inbox должен содержать сообщение");
    }

    @Test
    void testKeywordsUsageErrorAndRecovery() {
        String input = String.join(System.lineSeparator(),
                "add Petya",
                "add Vasya",
                "setfilter Vasya",
                "keywords",
                "keywords sale",
                "done",
                "send Petya Vasya sale item",
                "spam Vasya",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();
        String console = out.toString();
        assertTrue(console.contains("Правильно: keywords <слово1> <слово2> ..."), "Должен указывать синтаксис keywords при отсутствии аргументов");
        assertTrue(console.contains("Добавлен фильтр ключевых слов: sale"), "Должен добавить фильтр после правильного ввода");
        assertTrue(console.contains("From: Petya"), "Сообщение с ключевым словом 'sale' должно попасть в spam");
    }

    @Test
    void testRepetitionUsageErrorAndSenderUsageError() {
        String input = String.join(System.lineSeparator(),
                "add Petya",
                "add Vasya",
                "setfilter Vasya",
                "repetition not_a_number",
                "sender",
                "sender Petya",
                "done",
                "send Petya Vasya x x",
                "spam Vasya",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();
        String console = out.toString();
        assertTrue(console.contains("Ошибка: лимит должен быть числом"), "При неверном параметре repetition должны сообщить об ошибке");
        assertTrue(console.contains("Правильно: sender <имя1> <имя2> ..."), "При отсутствии имён sender должен показать подсказку");
        assertTrue(console.contains("Добавлен фильтр отправителей: [Petya]"), "После правильного sender должен добавить фильтр");
        assertTrue(console.contains("From: Petya"), "Письмо от заблокированного отправителя должно попасть в spam");
    }

    @Test
    void testEmptyAndWhitespaceLinesAreIgnored() {
        String input = ""
                + System.lineSeparator()
                + "   "+System.lineSeparator()
                + "add x"+System.lineSeparator()
                + ""+ System.lineSeparator()
                + "inbox x" + System.lineSeparator()
                + "exit";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).run();
        String console = out.toString();
        assertTrue(console.contains("Пользователь добавлен: x"));
        assertTrue(console.contains("Сообщения: пусто."), "Пустые и пробельные строки должны просто игнорироваться");
    }
}



