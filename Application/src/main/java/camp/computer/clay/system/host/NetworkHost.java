package camp.computer.clay.system.host;

import java.util.ArrayList;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.old_model.Message;

public class NetworkHost {

    private Clay clay;

    private ArrayList<NetworkResourceInterface> networkResources;

    // TODO: Combine incoming and outgoing message queues into a single queue.
    private ArrayList<Message> incomingMessages = new ArrayList<Message>(); // Create incoming message queue.
    private ArrayList<Message> outgoingMessages = new ArrayList<Message>(); // Create outgoing message queue.

    public NetworkHost(Clay clay) {
        this.networkResources = new ArrayList<NetworkResourceInterface>();
        this.clay = clay;
    }

    public void addHost(NetworkResourceInterface resource) {
        if (!this.networkResources.contains(resource)) {
            this.networkResources.add(resource);
            resource.addHost(this);
        }
    }

    /**
     * Retrieves Internet address using one of the available network resources. If there are no
     * network resources available, returns null.
     * @return
     */
    String getInternetAddress () {
        if (networkResources.size() > 0) {
            return networkResources.get(0).getInternetAddress();
        }
        return null;
    }
}
