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

    public String getCaption() {
        return caption;
    }

    public String getText() {
        return text;
    }

    public String getSender() {
        return sender;
    }

    public String getReceiver() {
        return receiver;
    }

    @Override
    public String toString() {
        return "От: " + sender + "\n" + "Кому: " + receiver + "\n" + "Тема: " + caption + "\n"+ "Текст: " + text;
    }
}
