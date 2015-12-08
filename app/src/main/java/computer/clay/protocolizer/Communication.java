package computer.clay.protocolizer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ArrayAdapter;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Communication {

//    private class Unit {
//        String unitAddress; // i.e., The unit's UUID.
//        String macAddress;
//        String ipAddress;
//        String meshAddress;
//    }

    ArrayAdapter<String> listAdapter; // List adapter!

    private ArrayList<String> units = new ArrayList<String>();

    private ArrayList<String> incomingMessages = new ArrayList<String>(); // Create incoming message queue.
    private ArrayList<String> outgoigMessages = new ArrayList<String>(); // Create outgoing message queue.

    Communication () {
//        startDatagramServer();
    }

    /**
     * Handles incoming messages from other threads.
     */
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.v ("Clay Datagram Server", "handleMessage called");

            // Process the incoming message's data.
            Bundle bundle = msg.getData();
            String string = bundle.getString("myKey");

            Log.v("Clay", "dequeuedMessage = " + string);

            // Insert the message into the incoming message queue.
            queueIncomingMessage(string);
            Log.v("Clay Datagram Server", "myKey = " + incomingMessages.get(incomingMessages.size() - 1));

            // Dequeue and process the next message on the incoming message queue.
            if (hasIncomingMessages()) {
                String dequeuedMessage = dequeueIncomingMessage();
                processIncomingMessage (dequeuedMessage);

            }


            // TODO: Periodically check for last received update from Clay units that are known, to verify that they are still active in the network. If they're not, heal the network.
        }
    };

    public boolean hasIncomingMessages () {
        return incomingMessages.size() > 0;
    }

    public void queueIncomingMessage (String message) {
        incomingMessages.add(message);
    }

    public String dequeueIncomingMessage () {
        return incomingMessages.remove(0);
    }

    private void processIncomingMessage (String message) {

        Log.v("Clay", "dequeuedMessage = " + message);

        if (message.startsWith("connect to ")) {

            String ipAddress = message.split(" ")[2];

            if (!units.contains (ipAddress)) {
                Log.v("Clay", "Adding Clay with address " + ipAddress);
                units.add(ipAddress);

                // Print the current list of Clay units.
                String currentUnits = "";
                for (String clayUnit : units) {
                    currentUnits += clayUnit + " ";
                }
                Log.v ("Clay", "Network: [ " + currentUnits + " ]");

                // HACK: Updates the list of discovered Clay units.
//                this.getUnits().add("N/A");
//                listAdapter.add ("N/A");
                listAdapter.notifyDataSetChanged(); // TODO: Remove this! Make it automatic and abstracted away!
//                listAdapter.clear();
//                for (String clayUnit : units) {
//                    listAdapter.add(clayUnit);
//                }

            } else {
                Log.v("Clay", "Updating Clay with address " + ipAddress);
            }

        } else {
            Log.v ("Clay", "bad command");
//            String ipAddress = message;
//            if (!units.contains(ipAddress)) {
//                Log.v("Clay", "Adding Clay with address " + ipAddress);
//                units.add(ipAddress);
//
//                // Print the current list of Clay units.
//                String currentUnits = "";
//                for (String clayUnit : units) {
//                    currentUnits += clayUnit + " ";
//                }
//                Log.v("Clay", "Network: [ " + currentUnits + " ]");
//            }
        }
    }

    public ArrayList<String> getUnits () {
        return this.units;
    }

    public void startDatagramServer () {
        Log.v("Clay", "Starting datagram server.");
        if (datagramServer == null) {
            datagramServer = new DatagramServer();
        }
        datagramServer.start();

        // Display (or store) sever information
        Context context = MainActivity.getAppContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress (wm.getConnectionInfo ().getIpAddress ());
        Log.v ("Clay", "Internet address: " + ip);
    }

    public void stopDatagramServer() {
        Log.v("Clay", "Stopping datagram server.");
        datagramServer.kill();
    }

    /**
     * UDP Incoming Message Server
     */

    private static final int UDP_SERVER_PORT = 4446;
    private static final int MAX_UDP_DATAGRAM_LEN = 1500;

    private DatagramServer datagramServer = null;

    private class DatagramServer extends Thread {
        private boolean bKeepRunning = true;
//        private String lastMessage = "";

        public void run() {
            String message;
            byte[] messageBytes = new byte[MAX_UDP_DATAGRAM_LEN];
            DatagramPacket packet = new DatagramPacket(messageBytes, messageBytes.length);

            try {

                // Open socket for UDP communications.
                Log.v("Clay", "Opening socket on port " + UDP_SERVER_PORT + ".");
                DatagramSocket socket = new DatagramSocket(UDP_SERVER_PORT); // "Constructs a UDP datagram socket which is bound to the specific port aPort on the local host using a wildcard address."
                if (socket.isBound()) {
                    Log.v("Clay", "Bound socket to local port " + socket.getLocalPort() + ".");
                }

                while(bKeepRunning) {
                    socket.receive (packet); // "This method blocks until a packet is received or a timeout has expired."
                    message = new String (messageBytes, 0, packet.getLength());
//                    lastMessage = message;
//                    runOnUiThread(updateTextMessage);

                    Log.v("Clay Datagram Server", "lastMessage = " + message);

                    // Get IP Address
//                    Context context = MainActivity.getAppContext();
//                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//                    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());

//                    Log.v("Clay", "Received datagram from " + packet.getAddress() + " on port " + packet.getPort());
//                    Log.v("Clay", "Datagram: " + message);

                    /*
                    // Send response on the same socket.
                    String responseStr = ip;
                    int msg_length = responseStr.length();
                    byte[] messageBytes = responseStr.getBytes();
                    DatagramPacket p = new DatagramPacket(messageBytes, messageBytes.length, packet.getAddress(), packet.getPort());
                    socket.send(p);
                    */


                    // Send the received data to the main communication thread.
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("myKey", message);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }

                if (socket != null) {
                    Log.v("Clay", "Closing local socket on port " + socket.getLocalPort() + ".");
                    socket.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public void kill() {
            Log.v("Clay", "Killing datagram server.");
            bKeepRunning = false;
        }

//        public String getLastMessage() {
//            return lastMessage;
//        }

        // Protocaller
        // Transmitter
        // Protocolist
        // Relay
        // Router
        // Networking
        // Socializer
        // Synchronizer
    }

    /*
    private Runnable updateTextMessage = new Runnable() {
        public void run() {
            if (datagramServer == null) return;
//            textMessage.setText(myDatagramReceiver.getLastMessage());

//            String httpRequestText = httpRequestAdapter.getItem(position); //CharSequence text = "Hello toast!";
//                int duration = Toast.LENGTH_SHORT;
//            Toast toast = Toast.makeText(getParent(), myDatagramReceiver.getLastMessage(), Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
//            toast.show();
            Log.v("Clay UDP Server", datagramServer.getLastMessage());
        }
    };
    */

    /**
     * UDP Outbound Messaging
     */

    private static final int UDP_LOCAL_PORT = 4445;
    private static final int UDP_MESSAGE_PORT = 4445;

    public void sendDatagram (String ipAddress, String message) {
        try {
            // Send UDP packet to the specified address.
            String messageStr = message; // "turn light 1 on";
            int local_port = UDP_LOCAL_PORT;
            int server_port = UDP_MESSAGE_PORT;
            DatagramSocket s = new DatagramSocket(local_port);
            InetAddress local = InetAddress.getByName(ipAddress); // ("192.168.43.235");
//                InetAddress local = InetAddress.getByName("255.255.255.255");
            int msg_length = messageStr.length();
            byte[] messageBytes = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(messageBytes, msg_length, local, server_port);
            s.send(p);
            s.close();
        } catch (IOException e) {
            Log.e("Clay", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

//            return null;
        }
    }

    public void sendDatagram (String message) {
        try {



//            // Broadcast UDP packet to the specified address.
//            String messageStr = params[0]; // "turn light 1 on";
//            int local_port = 4445;
//            int server_port = 4445;
//            DatagramSocket s = new DatagramSocket(local_port);
////                InetAddress local = InetAddress.getByName("192.168.43.235");
//            InetAddress local = InetAddress.getByName("255.255.255.255");
//            int msg_length = messageStr.length();
//            byte[] message = messageStr.getBytes();
//            DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
//            s.send(p);
//            s.close();



            // Send UDP packet to the specified address.
            String messageStr = message; // "turn light 1 on";
            int local_port = UDP_LOCAL_PORT;
            int server_port = UDP_MESSAGE_PORT;
            DatagramSocket s = new DatagramSocket(local_port);
            InetAddress local = InetAddress.getByName("255.255.255.255"); // ("192.168.43.235");
//                InetAddress local = InetAddress.getByName("255.255.255.255");
            int msg_length = messageStr.length();
            byte[] messageBytes = messageStr.getBytes();
            DatagramPacket p = new DatagramPacket(messageBytes, msg_length, local, server_port);
            s.send(p);
            s.close();
        } catch (IOException e) {
            Log.e("Clay", "Error ", e);
            // If the code didn't successfully get the weather data, there's no point in attemping
            // to parse it.

//            return null;
        }
    }

    // TODO: startHttpServer

    // TODO: addRequestHandler

    // TODO: handleRequest

    // TODO: sendHttpRequest (TODO: Add paramters for different callbacks)
}
