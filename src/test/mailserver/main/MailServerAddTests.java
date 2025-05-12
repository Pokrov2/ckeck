package mailserver.main;

import mailserver.Storage.UserStorage;
import mailserver.Model.Message;
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
                "add alice",
                "send",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        String console = out.toString();
        assertTrue(console.contains("Правильно: send <Отправитель> <Получатель> <заголовок> <текст>"),
                "При пустом send должна выводиться подсказка по правильному использованию");
    }

    @Test
    void testInboxUsageError() {
        String input = String.join(System.lineSeparator(),
                "add u",
                "inbox",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        assertTrue(out.toString().contains("Правильно: inbox <Пользователь>"),
                "При неверном inbox должна быть соответствующая подсказка");
    }

    @Test
    void testOutboxUsageError() {
        String input = String.join(System.lineSeparator(),
                "add u",
                "outbox",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        assertTrue(out.toString().contains("Правильно: outbox <Пользователь>"),
                "При неверном outbox должна быть соответствующая подсказка");
    }

    @Test
    void testSetFilterUsageError() {
        String input = String.join(System.lineSeparator(),
                "add u",
                "setfilter",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        assertTrue(out.toString().contains("Правильно: setfilter <Имя пользователя>"),
                "При неверном setfilter должна быть соответствующая подсказка");
    }

    @Test
    void testDuplicateAdd() {
        String input = String.join(System.lineSeparator(),
                "add bob",
                "add bob",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        String console = out.toString();
        assertTrue(console.contains("Пользователь добавлен: bob"));
        assertTrue(console.contains("Такой пользователь уже существует"),
                "При попытке добавить дубликат должно быть соответствующее сообщение");
    }

    @Test
    void testEmptyList() {
        String input = String.join(System.lineSeparator(),
                "list",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

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
        createServer(input, out).Run();

        String console = out.toString();
        assertTrue(console.contains("Пользователь добавлен: Carl"));
        assertTrue(console.contains("- Carl"),
                "Команды должны работать вне зависимости от регистра");
    }

    @Test
    void testSimpleSpamFilter() {
        String input = String.join(System.lineSeparator(),
                "add bob",
                "add alice",
                "setfilter bob",
                "simple",
                "done",
                "send alice bob imp smap spam offer",
                "spam bob",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();
        assertTrue(out.toString().contains("-----"),
                "Сообщение со словом spam должно попасть в спам");
    }

    @Test
    void testKeywordsSpamFilter() {
        String input = String.join(System.lineSeparator(),
                "add bob",
                "add alice",
                "setfilter bob",
                "keywords buy cheap",
                "done",
                "send alice bob hello buy now",
                "spam bob",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        assertTrue(out.toString().contains("-----"),
                "Сообщение с ключевым словом должно попасть в спам");
    }


    @Test
    void testRepetitionFilterNonSpam() {
        String input = String.join(System.lineSeparator(),
                "add bob",
                "add alice",
                "setfilter bob",
                "repetition 3",
                "done",
                "send alice bob subj ok ok ok",
                "inbox bob",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        String output = out.toString();
        assertTrue(output.contains("Subject: subj") || output.contains("ok ok ok"),
                "Сообщение должно быть во входящих");
    }

    @Test
    void testSenderFilter() {
        String input = String.join(System.lineSeparator(),
                "add bob",
                "add mallory",
                "setfilter bob",
                "sender mallory",
                "done",
                "send mallory bob subj hey",
                "spam bob",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        assertTrue(out.toString().contains("-----"),
                "Сообщение от заблокированного отправителя должно попасть в спам");
    }

    @Test
    void testCompositeFilter() {
        String input = String.join(System.lineSeparator(),
                "add bob",
                "add eve",
                "setfilter bob",
                "simple",
                "keywords secret",
                "done",
                "send eve bob spam news",
                "spam bob",
                "exit");
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        createServer(input, out).Run();

        assertTrue(out.toString().contains("-----"),
                "Composite должен поймать по любому из фильтров");
    }
}
