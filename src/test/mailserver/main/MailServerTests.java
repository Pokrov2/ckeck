package mailserver.main;
import java.util.HashSet;
import mailserver.Filter.*;
import mailserver.Model.Message;
import mailserver.Model.User;
import mailserver.Storage.UserStorage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class MailServerTests {


    private UserStorage storage;
    private User Petya;
    private User Vasya;

    @BeforeEach
    public void SetUp() {
        storage = new UserStorage();
        Petya = new User("Petya");
        Vasya = new User("Vasya");
        storage.addUser(Petya);
        storage.addUser(Vasya);
    }

    @Test
    public void TestSendMessageToInbox() {
        Petya.sendMessage(Vasya, "Ку", "Время зарегать катку");

        List<Message> inbox = Vasya.getInbox();
        List<Message> outbox = Petya.getOutbox();

        assertEquals(1, inbox.size());
        assertEquals(1, outbox.size());
        assertEquals("Petya", inbox.get(0).getSender());
        assertEquals("Vasya", outbox.get(0).getReceiver());
    }

    @Test
    public void TestSimpleSpamFilter() {
        Vasya.setSpamFilter(new SimpleSpamFilter());
        Petya.sendMessage(Vasya, "spam", "This is a spam message");

        assertEquals(0, Vasya.getInbox().size());
        assertEquals(1, Vasya.getSpam().size());
    }

    @Test
    public void TestKeywordsSpamFilter() {
        Vasya.setSpamFilter(new KeywordsSpamFilter(("купить дешево")));
        Petya.sendMessage(Vasya, "акция", "Вы можете купить новый телевизор уже прямо сейчас!");

        assertEquals(1, Vasya.getSpam().size());
        assertEquals(0, Vasya.getInbox().size());
    }

    @Test
    public void TestRepetitionsSpamFilter() {
        Vasya.setSpamFilter(new RepetitionsSpamFilter(2));
        Petya.sendMessage(Vasya, "Привет!", "Вы выиграли большой большой большой приз");

        assertEquals(1, Vasya.getSpam().size());
    }

    @Test
    public void TestSenderSpamFilter() {
        Vasya.setSpamFilter(new SenderSpamFilter(new HashSet<>(List.of("Petya"))));
        Petya.sendMessage(Vasya, "test", "обязан быть спамом");

        assertEquals(1, Vasya.getSpam().size());
    }


    @Test
    public void TestCompositeSpamFilter() {
        SpamFilter composite = new CompositeSpamFilter(Arrays.asList(
                new SimpleSpamFilter(),
                new KeywordsSpamFilter("lottery")
        ));
        Vasya.setSpamFilter(composite);
        Petya.sendMessage(Vasya, "Победа!", "You won the lottery!");
                                                    //тут просто склонять и спрягать на русском тяжко конечно)
        assertEquals(1, Vasya.getSpam().size());
    }

    @Test
    public void TestUserStorage() {
        assertTrue(storage.userExists("Petya"));
        assertNotNull(storage.getUser("Vasya"));
    }

}
