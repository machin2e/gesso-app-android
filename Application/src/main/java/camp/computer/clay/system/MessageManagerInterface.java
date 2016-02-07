package camp.computer.clay.system;

public interface MessageManagerInterface {

    public void engage (MessageManager messageManager); // formerly, addMessageManager
    public MessageManager getMessageManager ();
    public void disengage (MessageManager messageManager); // formerly, removeMessageManager

    public String getType();
    public void setType(String type);

    void process(Message message);
}
