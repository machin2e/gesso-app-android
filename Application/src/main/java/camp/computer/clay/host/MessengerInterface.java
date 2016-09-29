package camp.computer.clay.host;

import camp.computer.clay.old_model.Message;
import camp.computer.clay.old_model.Messenger;

public interface MessengerInterface {

    public void addMessenger(Messenger messenger); // formerly, addHost

    public void removeMessenger(Messenger messenger); // formerly, removeMessageManager

    public Messenger getMessenger();

    public String getType();

    public void setType(String type);

    void process(Message message);
}
