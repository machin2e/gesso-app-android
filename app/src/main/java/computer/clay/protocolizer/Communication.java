package computer.clay.protocolizer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Communication {

    public class Message {

        public final static String VERIFY_PREFIX = "got ";
        public final static int MAXIMUM_RETRY_COUNT = 10;
        public static final int RETRY_SEND_PERIOD = 5000;

        private UUID uuid = null;

        InetAddress address;
        String content;

        boolean verify; // if true, then verify that a response was received
        boolean isVerified; // if true, then this message has been verified by the unit
        // TODO: timeout
        private String checksum; // i.e., the expected response to compare against

        Date timeLastSent;
        int retryCount;

        Message (InetAddress address, String content) {

            this.uuid = UUID.randomUUID ();

            this.address = address;
            this.content = content;

            timeLastSent = new Date (0);
            retryCount = 0;

            // TODO: Only do this when "verify" is true.
            if (content.startsWith (Message.VERIFY_PREFIX)) {
                this.checksum = Communication.generateChecksum (this.content);
            } else {
                this.checksum = Communication.generateChecksum (Message.VERIFY_PREFIX + this.content);
            }

        }

        public String getChecksum () {
            return this.checksum;
        }

//        public boolean isGuaranteed () {
//            return this.verify;
//        }
//
//        public boolean hasArrived () {
//            return this.isVerified == true ;
//        }
//
//        public void verify () {
//
//        }
    }

    private ArrayList<Message> incomingMessages = new ArrayList<Message> (); // Create incoming message queue.
    private ArrayList<Message> outgoingMessages = new ArrayList<Message> (); // Create outgoing message queue.

    // UDP server
    WifiManager.MulticastLock multicastLock = null;

    private Clay clay = null;

    Communication (Clay clay) {
        this.clay = clay;
    }

    public Clay getClay () {
        return this.clay;
    }

    /**
     * Handles incoming messages from other threads.
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage (android.os.Message msg) {
            Log.v ("Clay_Time", "handleMessage called");

            // Process the incoming message's data.
            Bundle bundle = msg.getData ();
            String serializedMessageObject = bundle.getString ("serializedMessageObject");

            Log.v ("Clay", "dequeuedMessage = " + serializedMessageObject);

            String[] tokens = serializedMessageObject.split (":");
            InetAddress senderAddress = null;
            Message message = null;
            try {
                senderAddress = InetAddress.getByName (tokens[0]);

                // Create the message
                message = new Message (senderAddress, tokens[1]);

                // Update the unit construct associated with the message
//                Log.v ("Clay_Messaging", "Looking for unit with address " + tokens[0]);
                if (getClay ().hasUnitByAddress (tokens[0])) {

                    Log.v ("Clay_Time", "Found unit.");

                    // Get the unit associated with the received message
                    Unit unit = getClay ().getUnitByAddress (tokens[0]);

                    Log.v ("Clay_Time", "The unit is " + unit);

                    // Set time that this message was added to the message queue.
                    // NOTE: This is NOT the time that the message was received! It is probably shortly thereafter, though!
                    Calendar currentTime = Calendar.getInstance ();
                    unit.setTimeOfLastContact (currentTime.getTime ());
                }

                // Insert the message into the incoming message queue.
                queueIncomingMessage (message);
                if (incomingMessages.size () > 0) {
                    Log.v ("Clay Datagram Server", "myKey = " + incomingMessages.get (incomingMessages.size () - 1));
                }
            } catch (UnknownHostException e) {
                e.printStackTrace ();
            }

//            // Dequeue and process the next message on the incoming message queue.
//            Log.v ("Clay_Time", "Checking for incoming messages");
//            if (hasIncomingMessages()) {
//                Log.v ("Clay_Time", "Processing incoming message");
//                while (hasIncomingMessages ()) {
//                    Message dequeuedMessage = dequeueIncomingMessage();
//                    processIncomingMessage (dequeuedMessage);
//                }
//            }
//            Log.v ("Clay_Time", "Done processing messages");


            // TODO: Periodically check for last received update from Clay units that are known, to verify that they are still active in the network. If they're not, heal the network.
        }
    };

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
        // Dequeue and process the next message on the incoming message queue.
//        Log.v ("Clay_Time", "Checking for incoming messages");
        if (hasIncomingMessages ()) {
            Log.v ("Clay_Time", "Processing incoming message");
            while (hasIncomingMessages ()) {
                Message dequeuedMessage = dequeueIncomingMessage ();
                processIncomingMessage (dequeuedMessage);
            }
        }
//        Log.v ("Clay_Time", "Done processing messages");
    }

    private void processIncomingMessage (Message message) {

        Log.v ("Clay_Messaging", "Processing message \"" + message.content + "\"");

        if (message.content.startsWith (Message.VERIFY_PREFIX)) {

            Log.v ("Clay_Messaging", "\tReceived verification message \"" + message.content + "\"");

            if (hasOutgoingMessages ()) {
                Message outgoingMessage = peekOutgoingMessage ();

                Log.v ("Clay_Messaging", "\tHas outgoing message");

                // Check if the outgoing message at the front of the queue should be verified.
                if (outgoingMessage.verify == true) {

                    // Compute the checksum for the incoming message.
                    // Note that this incoming message does not INCLUDE the checksum in the received content.
//                    message.checksum = generateChecksum (message.content);
//                    String rechecksum = generateChecksum (Message.VERIFY_PREFIX + outgoingMessage.content);

                    // TODO: Get the behaviorConstruct by UUID which sent this message originally.
                    // TODO: behaviorConstruct.setSynchronized (true);

                    Log.v ("Clay_Messaging", "\t----");
                    Log.v ("Clay_Messaging", "\tChecksum expected: " + outgoingMessage.getChecksum () + "\t" + Message.VERIFY_PREFIX + outgoingMessage.content);
                    Log.v ("Clay_Messaging", "\tChecksum received: " + message.getChecksum () + "\t" + message.content);

                    try {
                        byte[] outgoingBytes = outgoingMessage.content.getBytes ("UTF-8");
                        byte[] incomingBytes = message.content.getBytes ("UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace ();
                    }

                    // Check if the checksum matches the expected one for the outgoing message at the front of the queue.
                    if (message.checksum.compareTo (outgoingMessage.checksum) == 0) {

                        // Flag the outgoing message as verified
                        outgoingMessage.isVerified = true;

                        // TODO: Remove the outgoing message from the outgoing queue here? Or leave it so another thread can take care of it without worrying.
                        dequeueOutgoingMessage ();

                        // TODO: Notify the behavior representation that has been verified, so it can be drawn to indicate it has been verified by the unit!

                    }

                }

            }

        } else if (message.content.startsWith("set unit ")) {

            // e.g., "set unit <uuid> address to <ip-address>"

            String[] messageTokens = message.content.split (" ");

            if (messageTokens.length > 5) {
                String unitUuid = messageTokens[2];
                String unitAddress = messageTokens[5];

                if (!getClay ().hasUnitByAddress (unitAddress)) {

                    Log.v ("Clay_Time", "Adding Clay " + unitUuid + " with address " + unitAddress);
                    Unit unit = new Unit (clay, UUID.fromString (unitUuid));
                    unit.setInternetAddress (unitAddress);
                    getClay ().addUnit (unit);
//                    getClay ().getDatabase().addEvent (unit, "discovered");

                    // HACK: This should not be here always. It should eventually be replaced with a command to load a specific loop from the Internet.
//                    sendMessage (unit.getInternetAddress (), "reset");

                    /*
                    // Print the current list of Clay units.
                    String currentUnits = "";
                    for (Unit clayUnit : getClay ().getUnits ()) {
                        currentUnits += clayUnit.getInternetAddress () + " ";
                    }
                    Log.v ("Clay_Time", "Network: [ " + currentUnits + " ]");
                    */

                } else {
                    Log.v ("Clay", "Updating state of existing Unit with address " + unitAddress);
                }
            }

        } else if (message.content.startsWith("say ")) {

            Log.v ("Clay_Verbalizer", message.content);

            String phrase = message.content.split (" ")[1];

            ((MainActivity) getClay ().getPlatformContext()).Hack_Speak(phrase);

        } else {
            Log.v ("Clay", "Error: Unrecognized message.");
            // TODO: Add the unrecognized message the Database in a category for unrecognized messages, and allow it to be defined.
        }
    }

    public boolean hasOutgoingMessages () {
        return outgoingMessages.size() > 0;
    }

    public void queueOutgoingMessage (Message message) {
        // TODO: Search outgoing queue for message with duplicate UUID, in which case, compare the content, to verify that it's not a duplicate before sending! (Or just drop it.)
        outgoingMessages.add (message);
    }

    public Message peekOutgoingMessage () {
        return outgoingMessages.get (0);
    }

    public Message dequeueOutgoingMessage () {
        return outgoingMessages.remove (0);
    }

    Date timeLastSentMessage = new Date (0);
    long outgoingMessagePeriod = 500;
    public void processOutgoingMessages () {

        if (hasOutgoingMessages ()) {

            Calendar currentCalendar = Calendar.getInstance ();
            Date currentTime = currentCalendar.getTime ();

//            Log.v ("Clay_Messaging", "Monitoring outgoing message queue.");

            // Get the next outgoing message.
            Message outgoingMessage = peekOutgoingMessage ();

            Log.v ("Verbose_Clay_Messaging", "                 Message: " + outgoingMessage.content);
            Log.v ("Verbose_Clay_Messaging", "Time since last dispatch: " + (currentTime.getTime () - timeLastSentMessage.getTime ()));
            Log.v ("Verbose_Clay_Messaging", " Time since last message: " + (currentTime.getTime () - outgoingMessage.timeLastSent.getTime ()));
            Log.v ("Verbose_Clay_Messaging", "                 Retries: " + outgoingMessage.retryCount);
            Log.v ("Verbose_Clay_Messaging", "-----");

//            if ((currentTime.getTime () - timeLastSentMessage.getTime ()) > outgoingMessagePeriod) {
                long currentTimeMillis = currentTime.getTime ();

                if (((currentTimeMillis - timeLastSentMessage.getTime ()) > outgoingMessagePeriod) && ((currentTimeMillis - outgoingMessage.timeLastSent.getTime ()) > Message.RETRY_SEND_PERIOD)) {

                    Log.v ("Clay_Messaging", "\tProcessing outgoing message queue (" + outgoingMessages.size () + " messages)");
                    for (Message queuedOutgoingMessage : outgoingMessages) {
                        Log.v ("Clay_Messaging", "\t\t" + queuedOutgoingMessage.content);
                    }
                    Log.v ("Clay_Messaging", "\tSending outgoing message \"" + outgoingMessage.content + "\" to " + outgoingMessage.address);

                    outgoingMessage.timeLastSent = currentCalendar.getTime ();
                    outgoingMessage.retryCount++;
                    timeLastSentMessage = outgoingMessage.timeLastSent;

                    processOutgoingMessage (outgoingMessage);

                }
//            }

        }

    }

    private void processOutgoingMessage (Message outgoingMessage) {

        Log.v ("Clay_Messaging", "\t\t    verify = " + outgoingMessage.verify);
        Log.v ("Clay_Messaging", "\t\tisVerified = " + outgoingMessage.isVerified);

        // If the message should be verified but hasn't yet been verified...
        if (outgoingMessage.verify == true && outgoingMessage.isVerified == false) {

            // Send the message.
            sendMessageAsync (outgoingMessage);

        }

        /*
        // If the message should be verified and has been verified successfully... dequeue it. It doesn't need to be resent, since it has already been sent.
        else if (outgoingMessage.verify == true && outgoingMessage.isVerified == true) {

            // Dequeue the message
            outgoingMessage = dequeueOutgoingMessage ();

        }
        */

        // If the message doesn't need to be verified... dequeue it and send it.
        else if (outgoingMessage.verify == false) {

            // Dequeue the message
            outgoingMessage = dequeueOutgoingMessage ();

            // Send the message.
            sendMessageAsync (outgoingMessage);

        }

        // Dequeue the message if it has been verified or if no verifiaction is requested.

    }

    public static String generateChecksum (String message) {
        return generateSha1Hash (message);
    }

    public static String generateSha1Hash (String string) {
        String hash = null;
        try {
            MessageDigest digest = MessageDigest.getInstance ("SHA-1");
            byte[] bytes = string.getBytes ("UTF-8");
            digest.update (bytes, 0, bytes.length);
            bytes = digest.digest ();

            // This is ~55x faster than looping and String.formating()
            hash = bytesToHex (bytes);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace ();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace ();
        }
        return hash;
    }

    // http://stackoverflow.com/questions/9655181/convert-from-byte-array-to-hex-string-in-java
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    public static String bytesToHex( byte[] bytes )
    {
        char[] hexChars = new char[ bytes.length * 2 ];
        for( int j = 0; j < bytes.length; j++ )
        {
            int v = bytes[ j ] & 0xFF;
            hexChars[ j * 2 ] = hexArray[ v >>> 4 ];
            hexChars[ j * 2 + 1 ] = hexArray[ v & 0x0F ];
        }
        return new String( hexChars );
    }

    public void startDatagramServer () {

        // Acquire a multicast lock to enable receiving broadcast packets
        if (multicastLock == null) {
            Context context = MainActivity.getAppContext ();
            WifiManager wm = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
            multicastLock = wm.createMulticastLock ("mydebuginfo");
//            if (!multicastLock.isHeld ()) {
                multicastLock.acquire ();
//            }
        }

//        Log.v ("Clay_Time", "Starting datagram server.");
        if (datagramServer == null) {
            Log.v ("Clay_Threads", "Starting datagram server.");
            datagramServer = new DatagramServer ();
            datagramServer.start();
        }
        if (datagramServer.getState () == Thread.State.TERMINATED) {
            Log.v ("Clay_Threads", "Re-starting datagram server.");
            datagramServer.start();
        }
        datagramServer.bKeepRunning = true;

        // Display (or store) sever information
//        Context context = MainActivity.getAppContext();
//        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        String ip = Formatter.formatIpAddress (wm.getConnectionInfo ().getIpAddress ());
//        Log.v ("Clay", "Internet address: " + ip);
    }

    public void stopDatagramServer () {
        Log.v ("Clay_Time", "Stopping datagram server.");
        if (datagramServer != null) {
            datagramServer.kill();

            if (multicastLock != null) {
                if (multicastLock.isHeld()) {
                    multicastLock.release();
                }
            }
        }
    }

    private static String getIpAsString(InetAddress address) {
        byte[] ipAddress = address.getAddress();
        StringBuffer str = new StringBuffer();
        for(int i=0; i<ipAddress.length; i++) {
            if(i > 0) str.append('.');
            str.append(ipAddress[i] & 0xFF);
        }
        return str.toString();
    }

    /**
     * UDP Incoming Message Server
     */

    private static final int MAX_UDP_DATAGRAM_LEN = 1500;

    private DatagramServer datagramServer = null;

    private class DatagramServer extends Thread {

        private boolean bKeepRunning = true;

        public void run() {
            String packetData;
            byte[] messageBytes = new byte[MAX_UDP_DATAGRAM_LEN];
            DatagramSocket serverSocket = null;
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);

            try {

                // Open socket for UDP communications.
                Log.v("Clay", "Opening socket on port " + DISCOVERY_BROADCAST_PORT + ".");
                serverSocket = new DatagramSocket (DISCOVERY_BROADCAST_PORT); // "Constructs a UDP datagram socket which is bound to the specific port aPort on the local host using a wildcard address."
                if (serverSocket.isBound()) {
                    Log.v("Clay", "Bound socket to local port " + serverSocket.getLocalPort() + ".");
                } else {
                    Log.v ("Clay", "Error: Could not bind to local port " + serverSocket.getLocalPort () + ".");
                }

                while(bKeepRunning) {
                    Log.v ("Clay_Traffic", "Looking for incoming messages");

                    if (serverSocket.isBound()) {
//                        Log.v("Clay", "Bound socket to local port " + serverSocket.getLocalPort() + ".");
                    } else {
                        Log.v("Clay_Datagram_Server", "Error: Could not bind to local port " + serverSocket.getLocalPort() + ".");
                    }

                    // NOTE: "This method blocks until a packet is received or a timeout has expired."
                    serverSocket.receive (packet);

                    // Get the message from the incoming packet and create a serialized object to pass to the main thread.
                    packetData = new String (messageBytes, 0, packet.getLength());
                    InetAddress senderAddress = packet.getAddress ();
                    String serializedMessageObject = getIpAsString (senderAddress) + ":" + packetData;

                    Log.v ("Clay_Datagram_Server", "Received packet data \"" + packetData + "\" from " + packet.getAddress ().getHostAddress ());

//                    Log.v ("Clay Datagram Server", "Received packet data: " + packetData);

                    // Send the received data to the main communication thread.
                    android.os.Message msg = handler.obtainMessage ();
                    Bundle bundle = new Bundle ();
                    bundle.putString ("serializedMessageObject", serializedMessageObject);
                    msg.setData (bundle);
                    handler.sendMessage (msg);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }

            if (serverSocket != null) {
                Log.v("Clay_Time", "Closing local socket on port " + serverSocket.getLocalPort() + ".");
                serverSocket.close ();
            }
        }

        public void kill() {
            Log.v("Clay_Messaging", "Killing datagram server.");
            // bKeepRunning = false; // HACK! This should not be commented. It was commented to previous mysterious crashing, which should be debugged! It seems to crash when an Android pop-up (on a different thread than the datagram serer) or UDP send message (on a different thread as well) is run! It's something related to that, apparently.
        }
    }

    /**
     * UDP Outbound Messaging
     */

    private static final String BROADCAST_ADDRESS = "255.255.255.255";

    private static final int DISCOVERY_BROADCAST_PORT = 4445;
    private static final int BROADCAST_PORT = 4446;
    private static final int MESSAGE_PORT = BROADCAST_PORT; // or 4446

    public void sendMessage (String address, String content) {
        Log.v("Clay", "sendMessageAsync");
        try {
            Message message = null;
            message = new Message (InetAddress.getByName (address), content);
            message.verify = true; // TODO: message.verify
            queueOutgoingMessage (message);
        } catch (UnknownHostException e) {
            e.printStackTrace ();
        }
    }

    private void sendMessageAsync (Message message) {
        Log.v("Clay", "sendMessageAsync");
        UdpDatagramTask udpDatagramTask = new UdpDatagramTask();
        udpDatagramTask.execute (message);
    }

    Communication that = this; // Store a reference to this class for use by inner classes.

    private class UdpDatagramTask extends AsyncTask<Message, Void, Void> {

        @Override
        protected Void doInBackground (Message... params) {
            // Send the message as a UDP datagram to the specified address.

            if (params.length == 0) {
                return null;
            }

            // Get the message to send.
            Message message = (Message) params[0];

            // Send the datagram.
            sendDatagram (getIpAsString (message.address), MESSAGE_PORT, message.content);

            // This only happens if there was an error getting or parsing the forecast.
            return null;
        }

        private void sendDatagram (String ipAddress, int port, String message) {
            Log.v ("Clay_Messaging", "\tSending datagram to " + ipAddress + ": " + message);
            try {
                // Send UDP packet to the specified address.
                DatagramSocket socket = new DatagramSocket(port);
                DatagramPacket packet = new DatagramPacket (message.getBytes(), message.length(), InetAddress.getByName (ipAddress), port);
                socket.send(packet);
                socket.close();
            } catch (IOException e) {
                Log.e("Clay", "Error ", e);
            }
        }
    }

    // TODO: startTcpServer
    // TODO: startHttpServer
    // TODO: addRequestHandler
    // TODO: handleRequest
    // TODO: sendHttpRequest (TODO: Add paramters for different callbacks)
}
