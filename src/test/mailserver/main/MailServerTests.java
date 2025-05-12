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
        storage.AddUser(Petya);
        storage.AddUser(Vasya);
    }

    @Test
    public void TestSendMessageToInbox() {
        Petya.SendMessage(Vasya, "Ку", "Время зарегать катку");

        List<Message> inbox = Vasya.GetInbox();
        List<Message> outbox = Petya.GetOutbox();

        assertEquals(1, inbox.size());
        assertEquals(1, outbox.size());
        assertEquals("Petya", inbox.get(0).GetSender());
        assertEquals("Vasya", outbox.get(0).GetReceiver());
    }

    @Test
    public void TestSimpleSpamFilter() {
        Vasya.SetSpamFilter(new SimpleSpamFilter());
        Petya.SendMessage(Vasya, "spam", "This is a spam message");

        assertEquals(0, Vasya.GetInbox().size());
        assertEquals(1, Vasya.GetSpam().size());
    }

    @Test
    public void TestKeywordsSpamFilter() {
        Vasya.SetSpamFilter(new KeywordsSpamFilter(("купить дешево")));
        Petya.SendMessage(Vasya, "акция", "Вы можете купить новый телевизор уже прямо сейчас!");

        assertEquals(1, Vasya.GetSpam().size());
        assertEquals(0, Vasya.GetInbox().size());
    }

    @Test
    public void TestRepetitionsSpamFilter() {
        Vasya.SetSpamFilter(new RepetitionsSpamFilter(2));
        Petya.SendMessage(Vasya, "Привет!", "Вы выиграли большой большой большой приз");

        assertEquals(1, Vasya.GetSpam().size());
    }

    @Test
    public void TestSenderSpamFilter() {
        Vasya.SetSpamFilter(new SenderSpamFilter(new HashSet<>(List.of("Petya"))));
        Petya.SendMessage(Vasya, "test", "обязан быть спамом");

        assertEquals(1, Vasya.GetSpam().size());
    }


    @Test
    public void TestCompositeSpamFilter() {
        SpamFilter composite = new CompositeSpamFilter(Arrays.asList(
                new SimpleSpamFilter(),
                new KeywordsSpamFilter("lottery")
        ));
        Vasya.SetSpamFilter(composite);
        Petya.SendMessage(Vasya, "Победа!", "You won the lottery!");
                                                    //тут просто склонять и спрягать на русском тяжко конечно)
        assertEquals(1, Vasya.GetSpam().size());
    }

    @Test
    public void TestUserStorage() {
        assertTrue(storage.UserExists("Petya"));
        assertNotNull(storage.GetUser("Vasya"));
    }

}
