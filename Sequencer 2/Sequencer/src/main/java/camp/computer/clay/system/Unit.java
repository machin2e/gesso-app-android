package camp.computer.clay.system;

import android.os.AsyncTask;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

//import com.fasterxml.jackson.annotation.JsonIgnore;

public class Unit {

    private Clay clay = null;

    private UUID uuid = null; // The unit's static, unchanging, UUID

    private String internetAddress = null; // The unit's IP address

    private TcpClient mTcpClient = null;

    private String meshAddress = null; // The unit's IP address

    private UUID timelineUuid;
    // TODO: Replace timeline with timelineUuid so it can be easily moved between multiple devices or simulators.
    private Timeline timeline = null;

    private Date timeOfLastContact = null;

    // TODO: Cache/model the unit's state and behavior

    public Unit() {
        // This empty default constructor is necessary for Firebase to be able to deserialize objects.
    }

    Unit(Clay clay, UUID uuid) {
        this.clay = clay;

        this.uuid = uuid;

        this.timeline = new Timeline(this);
        this.timelineUuid = this.timeline.getUuid();
    }

    public Clay getClay () {
        return this.clay;
    }

    public UUID getTimelineUuid () {
        return timelineUuid;
    }

    public void setTimelineUuid (UUID uuid) {
        this.timelineUuid = uuid;
        this.timeline = null;
    }

    public long getTimeSinceLastMessage () {
//        Log.v ("Clay_Time", "Time since last message: " + this.timeOfLastContact);
        Date currentTime = Calendar.getInstance().getTime ();

        if (timeOfLastContact != null) {
            long timeDifferenceInMilliseconds = currentTime.getTime () - timeOfLastContact.getTime ();
            // long seconds = timeDifferenceInMilliseconds / 1000;
            // long minutes = seconds / 60;
            // long hours = minutes / 60;
            // long days = hours / 24;
            return timeDifferenceInMilliseconds;
        } else {
            return Long.MAX_VALUE;
        }
    }

    public void setTimeOfLastContact (Date time) {
        this.timeOfLastContact = time;
        Log.v("Clay_Time", "Changing time from " + this.timeOfLastContact.getTime() + " to " + time.getTime());
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public void setInternetAddress (String address) {
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
        this.timelineUuid = timeline.getUuid();

        this.timeline.setUnit(this);
    }

//    public void notifyChange (Event event) {
//        getClay().notifyChange (event);
//
//        this.sendMessage("create behavior " + event.getUuid() + " \"" + event.getAction().getTag() + " " + event.getAction().getState().getState() + "\"");
//        this.sendMessage("addUnit behavior " + event.getUuid());
//    }

    /*
    // TODO:
    public void notifyRemove (Event event) {
        getClay().notifyChange (event);

        // Remove the previous behavior from the timeline
        this.sendMessage("remove behavior " + event.getAction().getUuid());
    }
    */

    public void sendMessage(String content) {
        getClay().sendMessage(this, content);
    }

    private ArrayList<Message> incomingMessages = new ArrayList<Message>(); // Create incoming message queue.
    private ArrayList<Message> outgoingMessages = new ArrayList<Message>(); // Create outgoing message queue.

    public void connectTcp() {
        Log.v("TCP_Server", "Adding device");
        new ConnectTask().execute(this.internetAddress);
    }

    // TODO: public void queueMessageTcp (String content, callbackToCallWhenReceiveResponse) // Adding a callback indicates that the message should be acknowledged and bookkeeping should track when it is received and invoke this callback when response is received.
    public void sendMessageTcp (String content) {
        Log.v("TCP_Server", "sendMessageTcp");

        // Get source and destination addresses
        String source = null; // TODO: getClay().getCurrentDevice().getInternetAddress()
        String destination = getInternetAddress();

        // Create message
        Message message = new Message("tcp", source, destination, content);
        message.setDeliveryGuaranteed(true);

        // Queue message
        outgoingMessages.add(message);

        // <HACK>
        // Send messages on queue
        // TODO: Put this into a separate thread and call it periodically.
        //mTcpClient.sendMessage("connected to " + getUuid().toString());
        Message outgoingMessage = outgoingMessages.remove(0);

        // Format message for transmission (according to messaging protocol)
        String formattedMessage = outgoingMessage.getContent() + "\n";
        mTcpClient.sendMessage (formattedMessage);
        // </HACK>
    }

    public void disconnectTcp() {
        mTcpClient.stopClient();
    }

//    public void cacheBehavior(UUID behaviorUuid) {
//
//        // Get the behavior with the specified UUID
//        Action behavior = getClay().getAction(behaviorUuid);
//
//        // Generate the behavior's initial state
//        State behaviorState = new State(behavior, behavior.getDefaultState());
//
//        // Create an event for the behavior so it can be added to the timeline
//        Event event = new Event(timeline, behavior, behaviorState);
//
//        // Add the event to the timeline
//        this.getTimeline().addEvent(event);
//    }

//    public void cacheBehavior(Action behavior) {
//
//        // Create an event for the behavior so it can be added to the timeline
//        Event event = new Event(timeline, behavior);
//
//        // Add the event to the timeline
//        this.getTimeline().addEvent(event);
//    }

    private class ConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... params) {
            Log.v("TCP_Server", "ConnectTask");

            if (params.length == 0) {
                return null;
            }

            String internetAddress = (String) params[0];
            Log.v ("TCP_Server", "IP: " + internetAddress);

            //we create a TCPClient object and
            mTcpClient = new TcpClient(internetAddress);

            // <HACK>
            // TODO: Put this in an intermediate "DeviceTcpConnection" class, that contains "TcpClient" and has methods for registering callbacks?
            mTcpClient.setOnMessageReceived(new TcpClient.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String message) {
                    //this method calls the onProgressUpdate
                    //publishProgress(message);
                    Log.v("TCP_Server", "Input message: " + message);

//                    sendMessageTcp("echoing received message: " + message);
                }
            });
            // </HACK>

            mTcpClient.run();

            return null;
        }
    }
}
