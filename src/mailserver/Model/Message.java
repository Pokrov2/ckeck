package mailserver.Model;

public class Message {
    private final String caption;
    private final String text;
    private final String sender;
    private final String receiver;

    public Message(String caption, String text, String sender, String receiver) {
        this.caption = caption;
        this.text = text;
        this.sender = sender;
        this.receiver = receiver;
    }

    public String GetCaption() {
        return caption;
    }

    public String GetText() {
        return text;
    }

    public String GetSender() {
        return sender;
    }

    public String GetReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return "От: " + sender + "\n" + "Кому: " + receiver + "\n" + "Тема: " + caption + "\n"+ "Текст: " + text;
    }
}
