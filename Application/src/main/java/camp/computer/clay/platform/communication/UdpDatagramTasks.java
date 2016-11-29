package camp.computer.clay.platform.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

import camp.computer.clay.platform.Message;

public class UdpDatagramTasks {

    public static final int BROADCAST_PORT = 4446;
    public static final int MESSAGE_PORT = BROADCAST_PORT; // or 4446

    public static class UdpDatagramTask extends AsyncTask<Message, Void, Void> {

        @Override
        protected Void doInBackground(Message... params) {
            // Send the message as a UDP datagram to the specified address.

            if (params.length == 0) {
                return null;
            }

            // Get the message to send.
            Message message = params[0];

            // Send the datagram.
            // formerly "sendDatagram(...)"
            //sendDatagram (UdpServer.getIpAsString(message.getTargetAddress()), MESSAGE_PORT, message.getDescriptor());
            sendDatagram(message.getTargetAddress(), MESSAGE_PORT, message.getContent());
//            Log.v("UDP", "from: " + message.getSourceAddress());
//            Log.v("UDP", "to: " + message.getTargetAddress());
//            Log.v("UDP", "port: " + MESSAGE_PORT);
//            Log.v("UDP", "message: " + message.getContent());
//            Log.v("UDP", "---");

            // This only happens if there was an error getting or parsing the forecast.
            return null;
        }

        private void sendDatagram(String ipAddress, int port, String message) {

            try {

//                Log.v ("UDP", "ipAddress: " + ipAddress);
//                Log.v ("UDP", "port: " + port);
//                Log.v ("UDP", "message: " + message);
//                Log.v ("UDP", "---");

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
