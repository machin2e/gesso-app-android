package camp.computer.clay.system.old_model;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Date;
import java.util.UUID;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.host.TcpMessageClientHost;

// TODO: Integrate with Frame in new model. Also separate the model (descriptive information that
// TODO: (cont'd) provides a lookup table of sorts, and the simulation (includes virtual
// TODO: (cont'd) communications interfaces such as TCP).

public class Device {

    private Clay clay = null;

    private UUID uuid = null; // The unit's static, unchanging, UUID

    // TODO: Put this into a MessageHost<Internet> class
    private String internetAddress = null; // The unit's IP address
    private TcpMessageClientHost tcpMessageClientHost;

    // TODO: Put this into a MessageHost<Thread> class
    private String meshAddress = null; // The unit's IP address

    private Timeline timeline = null;

    // TODO: Add this to MessageHost<?> classes
    private Date timeOfLastContact = null;

    // TODO: Cache/model the unit's state and behavior

    public Device() {
    }

    public Device(Clay clay, UUID uuid) {
        this.clay = clay;

        this.uuid = uuid;

        this.timeline = new Timeline(this);
    }

    public Clay getClay () {
        return this.clay;
    }

    // TODO: Put this into MessageHost<?> classes
    public void setTimeOfLastContact (Date time) {
        this.timeOfLastContact = time;
        Log.v("Clay_Time", "Changing time from " + this.timeOfLastContact.getTime() + " to " + time.getTime());
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public void setInternetAddress (String address) {
        Log.v("TCP", "setInternetAddress(): " + internetAddress);
        this.internetAddress = address;
    }

    public String getInternetAddress () {
        return this.internetAddress;
    }

    public void setMeshAddress (String address) {
        this.meshAddress = address;
    }

    public String getMeshAddress () {
        return this.meshAddress;
    }

    public Timeline getTimeline() {
        return this.timeline;
    }

    public void setTimeline(Timeline timeline) {
        this.timeline = timeline;
        this.timeline.setDevice(this);
    }

    public void connectTcp() {

        if (tcpMessageClientHost == null) {
            tcpMessageClientHost = new TcpMessageClientHost();
        }

        try {
            if (this.internetAddress != null) {
                Log.v ("TCP", "internetAddress: " + this.internetAddress);
                InetAddress inetAddress = InetAddress.getByName(this.internetAddress);
                Log.v ("TCP", "inetAddress: " + inetAddress);
                tcpMessageClientHost.connect(inetAddress);
            }
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    // TODO: Add addMessageHost to set general-purpose message interface.
    // TODO: (cont'd) Replace with messageInterface.send(Message message)
    public void enqueueMessage(String content) {
        Log.v("TCP_Server", "enqueueMessage");

        // Get sourceMachine and destinationMachine addresses
        String source = null; // TODO: getClay().getCurrentDevice().getInternetAddress()
        String destination = getInternetAddress();

        // Create message
        Message message = new Message("tcp", source, destination, content);
        message.setDeliveryGuaranteed(true);

        tcpMessageClientHost.enqueueMessage(message);
    }
}
