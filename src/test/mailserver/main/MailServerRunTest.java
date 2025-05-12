package mailserver.main;



import mailserver.Storage.UserStorage;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MailServerRunTest {

    @Test
    void testRunaddAndListAndExit() {
        String input = String.join(System.lineSeparator(),
                "add Petya",
                "list",
                "exit");
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MailServer server = new MailServer(new UserStorage(), in, new PrintStream(out));

        server.run();

        String console = out.toString();
        assertTrue(console.contains("Пользователь добавлен: Petya"), "должны видеть подтверждение добавления");
        assertTrue(console.contains("- Petya"), "должны видеть в списке пользователя Petya");
    }

    @Test
    void testRununknownCommandShowsError() {
        String input = String.join(System.lineSeparator(),
                "foobar",
                "exit");
        ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        MailServer server = new MailServer(new UserStorage(), in, new PrintStream(out));

        server.run();

        String console = out.toString();
        assertTrue(console.contains("Неизвестная команда."), "неизвестная команда должна вывести сообщение об ошибке");
    }
}