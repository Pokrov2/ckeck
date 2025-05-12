package mailserver.main;


import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void testMainInvokesMailServerRun() {
        ByteArrayInputStream in = new ByteArrayInputStream("exit\n".getBytes());
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        System.setIn(in);
        System.setOut(new PrintStream(out));

        Main.main(new String[0]);

        String console = out.toString();
        assertTrue(console.contains("An impudent copy mail.ru launched:"),
                "Main должен запустить MailServer и вывести приветствие");
    }
}
