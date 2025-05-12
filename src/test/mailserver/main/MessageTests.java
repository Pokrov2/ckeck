package mailserver.main;

import mailserver.Model.Message;
import org.junit.jupiter.api.Test;


import static org.junit.jupiter.api.Assertions.*;

public class MessageTests {
    @Test
    void testGettersAndToString() {
        Message m = new Message("Hello", "World", "Vasya", "Petya");
        assertEquals("Hello", m.getCaption());
        assertEquals("World", m.getText());
        assertEquals("Vasya", m.getSender());
        assertEquals("Petya", m.getReceiver());

        String repr = m.toString();
        assertTrue(repr.contains("От: Vasya"));
        assertTrue(repr.contains("Кому: Petya"));
        assertTrue(repr.contains("Тема: Hello"));
        assertTrue(repr.contains("Текст: World"));
    }


}
