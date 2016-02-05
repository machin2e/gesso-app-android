package camp.computer.clay.system;

public interface MessageManagerInterface {

    public void addMessageManager (MessageManager messageManager);
    public MessageManager getMessageManager ();
    public void removeMessageManager (MessageManager messageManager);

    public String getType();
    public void setType(String type);

    void process(Message message);
}
