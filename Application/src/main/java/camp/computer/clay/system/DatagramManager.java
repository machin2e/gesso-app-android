package camp.computer.clay.system;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import camp.computer.clay.sequencer.ApplicationView;

public class DatagramManager extends Thread implements MessageManagerInterface {

    private static final int MAX_UDP_DATAGRAM_LEN = 1500;

    private static final String BROADCAST_ADDRESS = "255.255.255.255";

    private static final int DISCOVERY_BROADCAST_PORT = 4445;
    private static final int BROADCAST_PORT = 4446;
    private static final int MESSAGE_PORT = BROADCAST_PORT; // or 4446

    // UDP server
    private WifiManager.MulticastLock multicastLock = null;

    private boolean bKeepRunning = true;

    private MessageManager messageManager;

    private String type;

    public DatagramManager(String type) {
        this.type = type;
    }

    public void addMessageManager (MessageManager messageManager) {
        this.messageManager = messageManager;
    }

    public MessageManager getMessageManager () {
        return this.messageManager;
    }

    public void removeMessageManager (MessageManager messageManager) {
        if (this.messageManager == messageManager) {
            this.messageManager = null;
        }
    }

    public void setType (String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }

    public void process (Message message) {
        if (messageManager != null) {
            if (message.getType().equals(this.getType())) {
                processMessage(message);
            }
        }
    }





    public void run() {
        byte[] messageBytes = new byte[MAX_UDP_DATAGRAM_LEN];
        DatagramSocket serverSocket = null;
        DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);

        try {

            // Open socket for UDP communications.
            Log.v("Clay", "Opening socket on port " + DISCOVERY_BROADCAST_PORT + ".");
            serverSocket = new DatagramSocket(DISCOVERY_BROADCAST_PORT); // "Constructs a UDP datagram socket which is bound to the specific port aPort on the local host using a wildcard address."
            if (serverSocket.isBound()) {
                Log.v("Clay", "Bound socket to local port " + serverSocket.getLocalPort() + ".");
            } else {
                Log.v("Clay", "Error: Could not bind to local port " + serverSocket.getLocalPort() + ".");
            }

            while(bKeepRunning) {
//                Log.v("Clay_Traffic", "Looking for incoming messages");

//                if (serverSocket.isBound()) {
////                        Log.v("Clay", "Bound socket to local port " + serverSocket.getLocalPort() + ".");
//                } else {
//                    Log.v("Clay_Datagram_Server", "Error: Could not bind to local port " + serverSocket.getLocalPort() + ".");
//                }

                // Block the thread until a packet is received or a timeout period has expired.
                // Note: "This method blocks until a packet is received or a timeout has expired."
                serverSocket.receive (packet);

                // Get the message from the incoming packet...
                String content = new String(messageBytes, 0, packet.getLength());
                String source = getIpAsString(packet.getAddress ());
                String destination = null;

                // ...and create a serialized object...
                //Message incomingMessage = new Message(fromAddress, toAddress, packetData);
                Message incomingMessage = new Message("udp", source, destination, content);

//                Log.v("Clay_Datagram_Server", "Received packet data \"" + packetData + "\" from " + packet.getAddress().getHostAddress());

//                    Log.v ("Clay Datagram Server", "Received packet data: " + packetData);

                // ...then pass the message to the message manager running in the main thread.
                if (messageManager != null) {
                    messageManager.addMessage(incomingMessage);
                }
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }

        if (serverSocket != null) {
            Log.v("Clay_Time", "Closing local socket on port " + serverSocket.getLocalPort() + ".");
            serverSocket.close ();
        }
    }

    public static String getIpAsString(InetAddress address) {
        if (address == null) {
            return null;
        }
        byte[] ipAddress = address.getAddress();
        StringBuffer str = new StringBuffer();
        for(int i=0; i<ipAddress.length; i++) {
            if(i > 0) str.append('.');
            str.append(ipAddress[i] & 0xFF);
        }
        return str.toString();
    }

    public void startServer () {

        // Acquire a multicast lock to enable receiving broadcast packets
        if (multicastLock == null) {
            WifiManager wm = (WifiManager) ApplicationView.getContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wm.createMulticastLock ("mydebuginfo");
//            if (!multicastLock.isHeld ()) {
            multicastLock.acquire ();
//            }
        }

//        Log.v ("Clay_Time", "Starting datagram server.");
//        if (datagramServer == null) {
            Log.v("Clay_Threads", "Starting datagram server.");
//            datagramServer = new DatagramManager ();
            this.start();
//        }
        if (getState() == Thread.State.TERMINATED) {
            Log.v("Clay_Threads", "Re-starting datagram server.");
            start();
        }
        bKeepRunning = true;

        // Display (or store) sever information
//        Context context = ApplicationView.getAppContext();
//        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        String ip = Formatter.formatIpAddress (wm.getConnectionInfo ().getIpAddress ());
//        Log.v ("Clay", "Internet address: " + ip);
    }

    public void stopServer () {
        Log.v("Clay_Time", "Stopping datagram server.");
        _kill();

        if (multicastLock != null) {
            if (multicastLock.isHeld ()) {
                multicastLock.release ();
            }
        }
    }

    private void _kill() {
        Log.v("Clay_Messaging", "Killing datagram server.");
        // bKeepRunning = false; // HACK! This should not be commented. It was commented to previous mysterious crashing, which should be debugged! It seems to crash when an Android pop-up (on a different thread than the datagram serer) or UDP send message (on a different thread as well) is run! It's something related to that, apparently.
    }

    public void processMessage (Message outgoingMessage) {

        // Send the message.
        sendMessageAsync (outgoingMessage);

//        // If the message should be verified but hasn't yet been verified...
//        if (outgoingMessage.isDeliveryGuaranteed() == true && outgoingMessage.isDelivered() == false) {
//
//            // Send the message.
//            sendMessageAsync (outgoingMessage);
//
//        }
//
//        /*
//        // If the message should be verified and has been verified successfully... dequeue it. It doesn't need to be resent, since it has already been sent.
//        else if (outgoingMessage.verify == true && outgoingMessage.isVerified == true) {
//
//            // Dequeue the message
//            outgoingMessage = dequeueOutgoingMessage ();
//
//        }
//        */
//
//        // If the message doesn't need to be verified... dequeue it and send it.
//        else if (outgoingMessage.isDeliveryGuaranteed() == false) {
//
//            // Dequeue the message
//            outgoingMessage = dequeueOutgoingMessage();
//
//            // Send the message.
//            sendMessageAsync(outgoingMessage);
//
//        }

        // Dequeue the message if it has been verified or if no verifiaction is requested.\
    }

    private void sendMessageAsync (Message message) {
        Log.v("Clay", "sendMessageAsync");
        UdpDatagramTask udpDatagramTask = new UdpDatagramTask();
        udpDatagramTask.execute(message);
    }

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
            //sendDatagram (DatagramManager.getIpAsString(message.getToAddress()), MESSAGE_PORT, message.getContent());
            sendDatagram (message.getToAddress(), MESSAGE_PORT, message.getContent());

            // This only happens if there was an error getting or parsing the forecast.
            return null;
        }

        private void sendDatagram (String ipAddress, int port, String message) {
            Log.v("Clay_Messaging", "\tSending datagram to " + ipAddress + ": " + message);
            try {
                // Send UDP packet to the specified address.
                DatagramSocket socket = new DatagramSocket(port);
                DatagramPacket packet = new DatagramPacket(message.getBytes(), message.length(), InetAddress.getByName(ipAddress), port);
                socket.send(packet);
                socket.close();
            } catch (IOException e) {
                Log.e("Clay", "Error ", e);
            }
        }
    }
}
