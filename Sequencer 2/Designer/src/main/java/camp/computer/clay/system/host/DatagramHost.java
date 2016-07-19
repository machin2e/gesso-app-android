package camp.computer.clay.system.host;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import camp.computer.clay.application.Application;
import camp.computer.clay.system.old_model.Message;
import camp.computer.clay.system.host.util.CRC16;

public class DatagramHost extends Thread implements MessageHostInterface {

    private static final int MAX_UDP_DATAGRAM_LEN = 1500;

    public static final String BROADCAST_ADDRESS = "255.255.255.255";

    public static final int DISCOVERY_BROADCAST_PORT = 4445;
    public static final int BROADCAST_PORT = 4446;
    public static final int MESSAGE_PORT = BROADCAST_PORT; // or 4446

    // UDP server
    private WifiManager.MulticastLock multicastLock = null;

    private boolean isRunning = true;

    private MessageHost messageHost;

    private String type;

    public DatagramHost(String type) {
        this.type = type;
    }

    public void engage(MessageHost messageHost) {
        this.messageHost = messageHost;
    }

    public MessageHost getMessageHost() {
        return this.messageHost;
    }

    public void disengage(MessageHost messageHost) {
        if (this.messageHost == messageHost) {
            this.messageHost = null;
        }
    }

    public void setType (String type) {
        this.type = type;
    }

    public String getType () {
        return this.type;
    }

    public void process (Message message) {
        if (messageHost != null) {
            if (message.getType().equals(this.getType())) {
                processMessage(message);
            }
        }
    }

    public void run() {
        byte[] messageBytes = new byte[MAX_UDP_DATAGRAM_LEN];
        DatagramSocket serverSocket = null;
        DatagramPacket packet = new DatagramPacket (messageBytes, messageBytes.length);

        try {

            // Open socket for UDP communications.
            Log.v("Clay", "Opening socket on port " + DISCOVERY_BROADCAST_PORT + ".");
            serverSocket = new DatagramSocket(DISCOVERY_BROADCAST_PORT); // "Constructs a UDP datagram socket which is bound to the specific port aPort on the local host using a wildcard address."
            if (serverSocket.isBound()) {
                Log.v("Clay", "Bound socket to local port " + serverSocket.getLocalPort() + ".");
            } else {
                Log.v("Clay", "Error: Could not bind to local port " + serverSocket.getLocalPort() + ".");
            }

            while(isRunning) {

                // Block the thread until a packet is received or a timeout period has expired.
                // Note: "This method blocks until a packet is received or a timeout has expired."
                serverSocket.receive (packet);

                String source = getIpAsString(packet.getAddress());

                // Get the message from the incoming packet...
                String content = new String(messageBytes, 0, packet.getLength());

                // "\f<content_length>\t<content_checksum>\t<content_type>\t<content>"
                // e.g., "\f52	16561	text	announce device 002fffff-ffff-ffff-4e45-3158200a0015"
                if (content.charAt(0) == '\f') {

                    // Remove "start of message" character (i.e., '\f'). // Split message into terms.
                    content = content.substring(1);
                    String[] terms = content.split("\t");

                    // TODO: Extract checksum, compute new one, and compare. If equal, add message.
                    String incomingMessageSize = terms[0];
                    int incomingChecksum = Integer.parseInt(terms[1]);

                    String incomingMessageType = terms[2];
                    String incomingMessageContent = terms[3];

                    // Compute checksum from received message
                    CRC16 CRC16 = new CRC16();
                    int computedChecksum = CRC16.calculate(incomingMessageContent.getBytes("UTF-8"), 0);

                    if (computedChecksum == incomingChecksum) {
                        // String destinationMachine = null;

                        // ...and create a serialized object...
                        Message incomingMessage = new Message("udp", source, null, incomingMessageContent);

                        // ...then pass the message to the message manager running in the main thread.
                        if (messageHost != null) {
                            messageHost.expose(incomingMessage);
                        }
                    }
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
            WifiManager wm = (WifiManager) Application.getContext().getSystemService(Context.WIFI_SERVICE);
            multicastLock = wm.createMulticastLock ("mydebuginfo");
            multicastLock.acquire ();
        }

        Log.v("Clay_Threads", "Starting datagram server.");
        this.start();
        if (getState() == State.TERMINATED) {
            Log.v("Clay_Threads", "Re-starting datagram server.");
            start();
        }
        isRunning = true;
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
        // isRunning = false; // HACK! This should not be commented. It was commented to previous mysterious crashing, which should be debugged! It seems to crash when an Android pop-up (on a different thread than the datagram serer) or UDP expose message (on a different thread as well) is run! It's something related to that, apparently.
    }

    public boolean isActive () {
        return isRunning;
    }

    // Send the message.
    public void processMessage (Message outgoingMessage) {
        exposeAsync(outgoingMessage);
    }

    // formerly "exposeAsync"
    private void exposeAsync(Message message) {
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

            // Get the message to expose.
            Message message = params[0];

            // Send the datagram.
            // formerly "sendDatagram(...)"
            //exposeDatagram (DatagramHost.getIpAsString(message.getTargetAddress()), MESSAGE_PORT, message.getDescriptor());
            exposeDatagram(message.getTargetAddress(), MESSAGE_PORT, message.getContent());
            Log.v("UDP", "from: " + message.getSourceAddress());
            Log.v("UDP", "to: " + message.getTargetAddress());
            Log.v("UDP", "port: " + MESSAGE_PORT);
            Log.v("UDP", "message: " + message.getContent());
            Log.v("UDP", "---");

            // This only happens if there was an error getting or parsing the forecast.
            return null;
        }

        private void exposeDatagram(String ipAddress, int port, String message) {

            try {

                Log.v ("UDP", "ipAddress: " + ipAddress);
                Log.v ("UDP", "port: " + port);
                Log.v ("UDP", "message: " + message);
                Log.v ("UDP", "---");

                message += "\n";

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
