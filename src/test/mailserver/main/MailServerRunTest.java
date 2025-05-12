package mailserver.main;



import mailserver.Storage.UserStorage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MailServerRunTest {

    @Test
    void testRun_addAndListAndExit() {
        // готовим эмуляцию ввода: add alice → list → exit
        String input = String.join(System.lineSeparator(),
                "add alice",
                "list",
                "exit");
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MailServer server = new MailServer(new UserStorage(), in, new PrintStream(out));

        server.Run();

        String console = out.toString();
        assertTrue(console.contains("Пользователь добавлен: alice"),
                "должны видеть подтверждение добавления");
        assertTrue(console.contains("- alice"),
                "должны видеть в списке пользователя alice");
    }

    @Test
    void testRun_unknownCommandShowsError() {
        String input = String.join(System.lineSeparator(),
                "foobar",
                "exit");
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MailServer server = new MailServer(new UserStorage(), in, new PrintStream(out));

        server.Run();

        String console = out.toString();
        assertTrue(console.contains("Неизвестная команда."),
                "неизвестная команда должна вывести сообщение об ошибке");
    }
}