package camp.computer.clay.platform.communication;

import camp.computer.clay.platform.Message;
import camp.computer.clay.platform.Messenger;

public interface MessengerInterface {

    public void addMessenger(Messenger messenger); // formerly, addHost

    public void removeMessenger(Messenger messenger); // formerly, removeMessageManager

    public Messenger getMessenger();

    public String getType();

    public void setType(String type);

    void process(Message message);
}
