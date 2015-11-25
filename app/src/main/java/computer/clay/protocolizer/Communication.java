package computer.clay.protocolizer;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.format.Formatter;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;

public class Communication {

    ArrayList<String> incomingMessages = new ArrayList<String>(); // Create incoming message queue.
    ArrayList<String> outgoigMessages = new ArrayList<String>(); // Create outgoing message queue.

    Communication () {
//        startDatagramServer();
    }


    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            Log.v ("Clay Datagram Server", "handleMessage called");

            // Process the incoming message's data.
            Bundle bundle = msg.getData();
            String string = bundle.getString("myKey");

            // Insert the message into the incoming message queue.
            incomingMessages.add(string);
            Log.v ("Clay Datagram Server", "myKey = " + incomingMessages.get(incomingMessages.size() - 1));
            incomingMessages.remove(0);
        }
    };

    public void startDatagramServer () {
        Log.v("Clay Datagram Server", "startDatagramServer");
        if (datagramServer == null) {
            datagramServer = new DatagramServer();
        }
        datagramServer.start();
    }

    public void stopDatagramServer() {
        Log.v("Clay Datagram Server", "stopDatagramServer");
        datagramServer.kill();
    }

    /**
     * UDP Incoming Message Server
     */

    private static final int UDP_SERVER_PORT = 4446;
    private static final int MAX_UDP_DATAGRAM_LEN = 1500;

    //    private TextView textMessage;
    private DatagramServer datagramServer = null;

    private class DatagramServer extends Thread {
        private boolean bKeepRunning = true;
        private String lastMessage = "";

        public void run() {
            String message;
            byte[] lmessage = new byte[MAX_UDP_DATAGRAM_LEN];
            DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);

            try {
                Log.v("Clay Datagram Server", "Opening socket " + UDP_SERVER_PORT);
                DatagramSocket socket = new DatagramSocket(UDP_SERVER_PORT);

                while(bKeepRunning) {
                    socket.receive(packet);
                    message = new String(lmessage, 0, packet.getLength());
                    lastMessage = message;
//                    runOnUiThread(updateTextMessage);

                    Log.v("Clay Datagram Server", "lastMessage = " + lastMessage);

                    // Get IP Address
                    Context context = MainActivity.getAppContext();
                    WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
                    String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());


                    // Send response on the same socket.
                    String responseStr = ip;
                    int msg_length = responseStr.length();
                    byte[] messageBytes = responseStr.getBytes();
                    DatagramPacket p = new DatagramPacket(messageBytes, messageBytes.length, packet.getAddress(), packet.getPort());
                    socket.send(p);


                    // Send the received data to the main communication thread.
                    Message msg = handler.obtainMessage();
                    Bundle bundle = new Bundle();
                    bundle.putString("myKey", lastMessage);
                    msg.setData(bundle);
                    handler.sendMessage(msg);
                }

                if (socket != null) {
                    Log.v("Clay Datagram Server", "Closing socket " + socket.getPort());
                    socket.close();
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        public void kill() {
            Log.v("Clay Datagram Server", "Killing server");
            bKeepRunning = false;
        }

        public String getLastMessage() {
            return lastMessage;
        }

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
