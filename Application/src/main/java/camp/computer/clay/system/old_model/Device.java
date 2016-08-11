package camp.computer.clay.system.old_model;

import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

import camp.computer.clay.system.Clay;
import camp.computer.clay.system.host.TcpMessageClientHost;

// TODO: Integrate with Frame in new construct. Also separate the construct (descriptive information that
// TODO: (cont'd) provides a lookup table of sorts, and the simulation (includes virtual
// TODO: (cont'd) communications interfaces such as TCP).

public class Device {

    private Clay clay = null;

    private UUID uuid = null; // The unit's static, unchanging, UUID

    private String internetAddress = null; // The unit's IP address

    private String meshAddress = null; // The unit's IP address

    private Timeline timeline = null;

    private Date timeOfLastContact = null;

    // TODO: Cache/construct the unit's state and behavior

    private TcpMessageClientHost tcpMessageClientHost;

    private ArrayList<String> tags;

    public Device() {
    }

    public Device(Clay clay, UUID uuid) {
        this.clay = clay;

        this.uuid = uuid;

        this.timeline = new Timeline(this);

        tags = new ArrayList<String>();
    }

    public Clay getClay () {
        return this.clay;
    }

//    public long getTimeSinceLastMessage () {
////        Log.v ("Clay_Time", "Time since last message: " + this.timeOfLastContact);
//        Date currentTime = Calendar.getInstance().getTime ();
//
//        if (timeOfLastContact != null) {
//            long timeDifferenceInMilliseconds = currentTime.getTime () - timeOfLastContact.getTime ();
//            // long seconds = timeDifferenceInMilliseconds / 1000;
//            // long minutes = seconds / 60;
//            // long hours = minutes / 60;
//            // long days = hours / 24;
//            return timeDifferenceInMilliseconds;
//        } else {
//            return Long.MAX_VALUE;
//        }
//    }

    public void setTag (String tag) {
        if (this.tags != null && this.tags.size() > 0) {
            this.tags.add(0, tag);
        } else {
            this.tags.add(tag);
        }
    }

    public void addTag (String tag) {
        this.tags.add(tag);
    }

    public String getTag () {
        if (this.tags != null && this.tags.size() > 0) {
            return this.tags.get(0);
        }
        return null;
    }

    public ArrayList<String> getTags () {
        return this.tags;
    }

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

    // TODO: public void queueMessageTcp (String content, callbackToCallWhenReceiveResponse) // Adding a callback indicates that the message should be acknowledged and bookkeeping should track when it is received and invoke this callback when response is received.
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
