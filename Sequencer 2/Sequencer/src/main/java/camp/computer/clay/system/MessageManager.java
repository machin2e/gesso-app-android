package camp.computer.clay.system;

import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class MessageManager {

    private Clay clay;

    private ArrayList<MessageManagerInterface> messageManagers;

    // TODO: Combine incoming and outgoing message queues into a single queue.
    private ArrayList<Message> incomingMessages = new ArrayList<Message>(); // Create incoming message queue.
    private ArrayList<Message> outgoingMessages = new ArrayList<Message>(); // Create outgoing message queue.

    MessageManager(Clay clay) {
        this.messageManagers = new ArrayList<MessageManagerInterface>();
        this.clay = clay;
    }

    public void addManager(MessageManagerInterface messageManager) {
        if (!this.messageManagers.contains(messageManager)) {
            this.messageManagers.add(messageManager);
            messageManager.engage(this);
        }
    }

    /**
     * Handles incoming messages from other threads.
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage (android.os.Message msg) {
//            Log.v("Clay_Time", "addUnit called");

            // Process the incoming message's data.
            Bundle bundle = msg.getData();
            String serializedMessageObject = bundle.getString("serializedMessageObject");

            String[] tokens = serializedMessageObject.split (":");
            String senderAddress = null;
            senderAddress = tokens[0]; // InetAddress.getByName(tokens[0]);

//            Log.v("Clay_Queue", "dequeuedMessage = " + serializedMessageObject);
//            Log.v("Clay_Queue", "IP = " + senderAddress);

            // Create the message
            Message message = new Message ("udp", senderAddress, null, tokens[1]);

            // Update the unit construct associated with the message
            if (getClay ().hasUnitByAddress (tokens[0])) {

                // Get the unit associated with the received message
                Unit unit = getClay ().getUnitByAddress (tokens[0]);

                // Set time that this message was added to the message queue.
                // NOTE: This is NOT the time that the message was received! It is probably shortly thereafter, though!
                Calendar currentTime = Calendar.getInstance();
                unit.setTimeOfLastContact(currentTime.getTime());
            }

            // Insert the message into the incoming message queue.
            queueIncomingMessage (message);
            if (incomingMessages.size () > 0) {
                Log.v("Clay Datagram Server", "myKey = " + incomingMessages.get(incomingMessages.size() - 1));
            }
        }
    };

    public Clay getClay() {
        return this.clay;
    }

    /**
     * Send the received data to the main communication thread.
     */
    public void addMessage (Message message) {

        // TODO: Make this handle both incoming and outgoing messages... right now it only does incoming!

        // Serialize the message so it can be passed to the main thread...
        //String serializedMessage = getIpAsString (message.getFromAddress()) + ":" + message.getContent();
        String serializedMessage = message.getFromAddress () + ":" + message.getContent ();

        // ...prepare the serialized message to be passed to the main thread...
        android.os.Message msg = handler.obtainMessage ();
        Bundle bundle = new Bundle ();
        bundle.putString ("serializedMessageObject", serializedMessage);
        msg.setData (bundle);

        // ...and finally, sendMessage the message to the main thread.
        handler.sendMessage (msg);
    }

    public boolean hasIncomingMessages () {
        return incomingMessages.size() > 0;
    }

    public void queueIncomingMessage (Message message) {
        incomingMessages.add(message);
    }

    public Message dequeueIncomingMessage () {
        return incomingMessages.remove(0);
    }

    public void processIncomingMessages () {
//        Log.v ("UDP_Processing", "<<< processIncomingQueue");
        // Dequeue and process the next message on the incoming message queue.
        if (hasIncomingMessages()) {
            Log.v("Clay_Time", "Processing incoming message");
            while (hasIncomingMessages ()) {
                Message dequeuedMessage = dequeueIncomingMessage ();
                processIncomingMessage (dequeuedMessage);
            }
        }
    }

    private void processIncomingMessage (Message message) {

        Log.v("UDP", "Processing message \"" + message.getContent() + "\"");

        Log.v ("UDP_Time", "Processing incoming message: " + message.getContent());

        if (message.getContent().startsWith(Message.VERIFY_PREFIX)) {

            Log.v("UDP", "\tReceived verification message \"" + message.getContent() + "\"");

            if (hasOutgoingMessages ()) {
                Message outgoingMessage = peekOutgoingMessage ();

                Log.v("UDP", "\tHas outgoing message");

                // Check if the outgoing message at the front of the queue should be verified.
                //if (outgoingMessage.isDelivered() == true) {
                if (outgoingMessage.isDeliveryGuaranteed() == true) {

                    // Compute the checksum for the incoming message.
                    // Note that this incoming message does not INCLUDE the checksum in the received content.
//                    message.checksum = generateChecksum (message.content);
//                    String rechecksum = generateChecksum (Message.VERIFY_PREFIX + outgoingMessage.content);

                    // TODO: Get the behaviorConstruct by UUID which sent this message originally.
                    // TODO: behaviorConstruct.setSynchronized (true);

                    Log.v ("UDP", "\t----");
                    Log.v ("UDP", "\tChecksum expected: " + outgoingMessage.getChecksum() + "\t" + Message.VERIFY_PREFIX + outgoingMessage.getContent());
                    Log.v ("UDP", "\tChecksum received: " + message.getChecksum() + "\t" + message.getContent());
                    Log.v ("UDP", "---");

                    try {
                        byte[] outgoingBytes = outgoingMessage.getContent().getBytes("UTF-8");
                        byte[] incomingBytes = message.getContent().getBytes("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace ();
                    }

                    // Check if the checksum matches the expected one for the outgoing message at the front of the queue.
                    if (message.getChecksum().compareTo(outgoingMessage.getChecksum()) == 0) {

                        Log.v ("UDP_Time", "Got ack for front message.");

                        // Flag the outgoing message as verified
                        outgoingMessage.setDelivered(true);

                        // TODO: Remove the outgoing message from the outgoing queue here? Or leave it so another thread can take care of it without worrying.
                        dequeueOutgoingMessage ();

                        // TODO: Notify the behavior representation that has been verified, so it can be drawn to indicate it has been verified by the unit!

                    }

                }

            }

        } else if (message.getContent().startsWith("announce device ")) {

            Log.v ("UDP", "Trying to add a device.");

            // e.g., "announce device <uuid>"

            String[] messageTokens = message.getContent().split(" ");

            if (messageTokens.length == 3) {
                String unitUuid = new String (messageTokens[2]);
                String unitAddress = message.getFromAddress(); // messageTokens[5];

                if (!getClay ().hasUnitByAddress (unitAddress)) {

//                    Log.v("UDP", "Adding Clay " + unitUuid + " with address " + unitAddress);
                    UUID deviceUuid = UUID.fromString(unitUuid);
                    getClay().addUnit(deviceUuid, unitAddress);

                } else {
//                    Log.v("Clay", "Updating state of existing Unit with address " + unitAddress);

                    UUID deviceUuid = UUID.fromString(unitUuid);
                    Unit unit = getClay().getUnitByUuid(deviceUuid);
                }
            }

//            Log.v ("UDP", "---");

        } else if (message.getContent().startsWith("say ")) {

//            Log.v("Clay_Verbalizer", message.getContent());

            String phrase = message.getContent().split(" ")[1];

//            ((AppActivity) getClay ().getContext()).speakPhrase(phrase);

        } else {
//            Log.v("Clay", "Error: Unrecognized message.");
            // TODO: Add the unrecognized message the FileContentManager in a category for unrecognized messages, and allow it to be defined.
        }

//        Log.v ("UDP", "---");
    }

    public boolean hasOutgoingMessages () {
        return outgoingMessages.size() > 0;
    }

    /**
     * Adds a message to the message manager.
     *
     * Searches for a message with duplicate UUID. If such a message is found, then the new message
     * will not be added.
     */
    public void queueOutgoingMessage (Message message) {
        if (!outgoingMessages.contains(message)) {
            outgoingMessages.add(message);
        }
    }

    /**
     * Returns the next outgoing message but does not remove it.
     * @return The next outgoing message.
     */
    public Message peekOutgoingMessage () {
        return outgoingMessages.get (0);
    }

    public Message dequeueOutgoingMessage () {
        return outgoingMessages.remove (0);
    }

//    Calendar currentCalendar = Calendar.getInstance();
//    Date currentTime = currentCalendar.getTime ();
    Date timeLastSentMessage = new Date(0);
    long outgoingMessagePeriod = 200;
    public void processOutgoingMessages () {
//        Log.v ("UDP_Processing", "processOutgoingMessage (count: " + outgoingMessages.size() + ")");

        if (hasOutgoingMessages ()) {

            Calendar currentCalendar = Calendar.getInstance();
            Date currentTime = currentCalendar.getTime ();

//            Log.v ("Clay_Messaging", "Monitoring outgoing message queue.");

            // Dequeue any messages that have been delivered
            for (Message queuedOutgoingMessage : outgoingMessages) {
                if (queuedOutgoingMessage.isDelivered()) {
                    outgoingMessages.remove(queuedOutgoingMessage);
                }
            }

            // Get the next outgoing message.
            Message outgoingMessage = peekOutgoingMessage ();

//            Log.v("UDP", "                 Message: " + outgoingMessage.getContent());
//            Log.v("UDP", "Time since last dispatch: " + (currentTime.getTime() - timeLastSentMessage.getTime()));
//            Log.v("UDP", " Time since last message: " + (currentTime.getTime() - outgoingMessage.getTimeLastSent().getTime()));
//            Log.v("UDP", "                 Retries: " + outgoingMessage.getRetryCount());
//            Log.v("UDP", "-----");

//            if ((currentTime.getTime () - timeLastSentMessage.getTime ()) > outgoingMessagePeriod) {
            long currentTimeMillis = currentTime.getTime();

//            Log.v ("UDP_Time", "Try count: " + outgoingMessage.getRetryCount());
//            Log.v ("UDP_Time", "Time since retry (must be > " + Message.RETRY_SEND_PERIOD + " ms): " + (currentTimeMillis - outgoingMessage.getTimeLastSent().getTime ()));
//            Log.v ("UDP_Time", "retry time (must be > 5 s): " + ((currentTimeMillis - outgoingMessage.getTimeLastSent().getTime ()) > Message.RETRY_SEND_PERIOD));

            //if (((currentTimeMillis - timeLastSentMessage.getTime ()) > outgoingMessagePeriod && outgoingMessage.getRetryCount() == 0) || (outgoingMessage.getRetryCount() == 0 || (outgoingMessage.getRetryCount() > 0 && ((currentTimeMillis - outgoingMessage.getTimeLastSent().getTime ()) > Message.RETRY_SEND_PERIOD)))) {
//            if (outgoingMessage.getRetryCount() == 0 || (outgoingMessage.getRetryCount() > 0 && ((currentTimeMillis - outgoingMessage.getTimeLastSent().getTime ()) > Message.RETRY_SEND_PERIOD))) {
            if (((currentTimeMillis - timeLastSentMessage.getTime ()) > outgoingMessagePeriod)) {

//                Log.v ("UDP_Time", "Sending message (of " + outgoingMessages.size() + ")");

//                Log.v ("UDP_Processing", ">>> processOutgoingQueue");

//                Log.v("UDP", "\tProcessing outgoing message queue (" + outgoingMessages.size() + " messages)");
//                for (Message queuedOutgoingMessage : outgoingMessages) {
//                    Log.v("UDP", "\t\t" + queuedOutgoingMessage.getContent());
//                }
//                Log.v("UDP", "\tSending outgoing message \"" + outgoingMessage.getContent() + "\" to " + outgoingMessage.getToAddress());

                outgoingMessage.setTimeLastSent(currentCalendar.getTime());
                outgoingMessage.increaseRetryCount();
//                timeLastSentMessage = outgoingMessage.getTimeLastSent();

                processOutgoingMessage (outgoingMessage);

            }

            timeLastSentMessage = outgoingMessage.getTimeLastSent();
//            }

        }

    }

    private void processOutgoingMessage (Message message) {

        for (MessageManagerInterface messageManager : messageManagers) {
            if (messageManager.getType().equals(message.getType())) {

                // Dequeue the message if its delivery is not guaranteed
                if (message.isDeliveryGuaranteed() == false) {
                    message = dequeueOutgoingMessage();
                }

                // Process the message
                messageManager.process (message);
            }
        }

//        if (this.datagramServer != null) {
//            datagramServer.processMessage (outgoingMessage);
//        }

    }

    public void sendMessage (String address, String content) {
//        Log.v("Clay", "sendMessageAsync");
        //Message message = new Message (null, InetAddress.getByName(address), content);
        Message message = new Message("udp", null, address, content);
        message.setDeliveryGuaranteed(true);
        queueOutgoingMessage (message);
    }

    public void processMessage() {

        // Process incoming messages
        processIncomingMessages();

        // Process outgoing messages
        processOutgoingMessages();
    }
}
