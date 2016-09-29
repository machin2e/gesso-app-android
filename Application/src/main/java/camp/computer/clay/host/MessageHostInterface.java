package camp.computer.clay.host;

import camp.computer.clay.old_model.MessageHost;
import camp.computer.clay.old_model.Message;

public interface MessageHostInterface {

    public void engage (MessageHost messageHost); // formerly, addHost
    public MessageHost getMessageHost();
    public void disengage (MessageHost messageHost); // formerly, removeMessageManager

    public String getType();
    public void setType(String type);

    void process(Message message);
}
