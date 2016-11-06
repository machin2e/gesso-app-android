package camp.computer.clay.platform;

import java.util.ArrayList;

import camp.computer.clay.Clay;
import camp.computer.clay.platform.communication.InternetInterface;

public class Internet {

    private Clay clay;

    private ArrayList<InternetInterface> networkResources;

    // TODO: Combine incoming and outgoing message queues into a single queue.
    private ArrayList<Message> incomingMessages = new ArrayList<>(); // Create incoming message queue.
    private ArrayList<Message> outgoingMessages = new ArrayList<>(); // Create outgoing message queue.

    public Internet(Clay clay) {
        this.networkResources = new ArrayList<>();
        this.clay = clay;
    }

    public void addHost(InternetInterface resource) {
        if (!this.networkResources.contains(resource)) {
            this.networkResources.add(resource);
            resource.addHost(this);
        }
    }

    /**
     * Retrieves Internet address using one of the available network resources. If there are no
     * network resources available, returns null.
     *
     * @return
     */
    String getInternetAddress() {
        if (networkResources.size() > 0) {
            return networkResources.get(0).getInternetAddress();
        }
        return null;
    }
}
