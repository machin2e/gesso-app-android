package camp.computer.clay.system;

import java.util.ArrayList;

public class NetworkManager {

    private Clay clay;

    private ArrayList<NetworkResourceInterface> networkResources;

    // TODO: Combine incoming and outgoing message queues into a single queue.
    private ArrayList<Message> incomingMessages = new ArrayList<Message>(); // Create incoming message queue.
    private ArrayList<Message> outgoingMessages = new ArrayList<Message>(); // Create outgoing message queue.

    NetworkManager(Clay clay) {
        this.networkResources = new ArrayList<NetworkResourceInterface>();
        this.clay = clay;
    }

    public void addResource(NetworkResourceInterface resource) {
        if (!this.networkResources.contains(resource)) {
            this.networkResources.add(resource);
            resource.addManager(this);
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
