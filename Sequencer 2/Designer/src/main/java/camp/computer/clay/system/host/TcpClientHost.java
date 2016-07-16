package camp.computer.clay.system.host;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.Socket;

public class TcpClientHost {

    private InetAddress remoteAddress;
    private int remotePort;

    // message to send to the server
    private String mServerMessage;
    // sends message received notifications
    private OnMessageReceived mMessageListener = null;
    // while this is true, the server will continue running
    private boolean mRun = false;
    // used to send messages
    private PrintWriter mBufferOut;
    // used to read messages from the server
    private BufferedReader mBufferIn;

    /**
     * Constructor of the class. OnMessagedReceived listens for the messages received from server
     */
    public TcpClientHost(InetAddress inetAddress, int port) {

        this.remoteAddress = inetAddress;
        this.remotePort = port;
    }

    /**
     * Callback to call when a message is received from the remote device.
     * @param onMessageReceived Callback
     */
    public void setOnMessageReceived (OnMessageReceived onMessageReceived) {
        mMessageListener = onMessageReceived;
    }

    /**
     * Sends the message entered by client to the server
     *
     * @param message text entered by client
     */
    public boolean expose(String message) {
        Log.v ("TCP_Client", "\tSending: " + message);
        if (mBufferOut != null && !mBufferOut.checkError()) {
            mBufferOut.println(message);
            mBufferOut.flush();
            return true;
        } else {
            Log.v ("TCP_Client", "\tERROR Sending: " + message);
            return false;
        }
    }

    /**
     * Close the connection and release the members
     */
    public void stop () {
        Log.i("TCP_Client", "stop");

        // send mesage that we are closing the connection
        //expose(Constants.CLOSED_CONNECTION + "Kazy");

        mRun = false;

        if (mBufferOut != null) {
            mBufferOut.flush();
            mBufferOut.close();
        }

        mMessageListener = null;
        mBufferIn = null;
        mBufferOut = null;
        mServerMessage = null;
    }

    public void run() {

        mRun = true;

        try {
            //here you must put your computer's IP address.
//            InetAddress remoteAddress = InetAddress.getByName(SERVER_IP);

//            Log.e("TCP_Client", "C: Connecting...");

            Log.v ("TCP_Client_Connect", "Connecting to " + this.remoteAddress.toString());

            //create a socket to make the connection with the server
            Socket socket = new Socket(remoteAddress, remotePort);

            try {
                Log.i("TCP_Client", "inside try catch");
                //sends the message to the server
                mBufferOut = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);

                //receives the message which the server sends back
                mBufferIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                // send login name
                //expose(Constants.LOGIN_NAME + PreferencesManager.getInstance().getUserName());
                // TODO: Call callback (if any) for "connection established"
                //in this while the client listens for the messages sent by the server
                while (mRun) {
                    int firstCharacter = mBufferIn.read();
                    if (firstCharacter == -1) {
                        Log.v("TCP_Client", "disconnected");
                        stop();
                    } else {

                        if (String.valueOf(firstCharacter).equals("\n")) { // TODO: Update "\n" to look for regex
                            // call the method messageReceived from MyActivity class
                            String messageCopy = new String (mServerMessage);
                            mMessageListener.messageReceived(messageCopy);
                            mServerMessage = ""; // Reset the string
                        }

                        // Append the character
                        mServerMessage += String.valueOf((char) firstCharacter);

                        /*
                        mServerMessage = mBufferIn.readLine();
                        if (mServerMessage != null && mMessageListener != null) {
                            //call the method messageReceived from MyActivity class
                            mMessageListener.messageReceived(mServerMessage);
                        }
                        */
                    }

//                    if (socket.isClosed()) {
//                        Log.v("TCP_Client", "closed");
//                    }
//
//                    if (!socket.isConnected()) {
//                        Log.v ("TCP_Client", "disconnected");
//                    }

                    /*
                    // Check if connection is valid
                    mBufferOut.print(" ");
                    mBufferOut.flush();
                    if (mBufferOut.checkError()) {
                        Log.v ("TCP_Client", "checkError");
                    }
                    */

                    Log.v ("TCP_Client", "running");

                }
                Log.e("RESPONSE FROM SERVER", "S: Received Message: '" + mServerMessage + "'");

            } catch (Exception e) {

                // Log.e("TCP_Client", "S: Error", e);
                Log.e("TCP_Client", "S: Error");

            } finally {

                stop();

                //the socket must be closed. It is not possible to reconnect to this socket
                // after it is closed, which means a new socket instance has to be created.
                socket.close();
            }

        } catch (Exception e) {

            //Log.e("TCP_Client", "C: Error", e);
//            Log.e("TCP_Client", "C: Error");

            // e.g., ECONNREFUSED (Connection refused)

            stop();

        }

    }

    public boolean isRunning () {
        return mRun;
    }

    //Declare the interface. The method messageReceived(String message) will must be implemented in the MyActivity
    //class at on asynckTask doInBackground
    public interface OnMessageReceived {
        public void messageReceived(String message);
    }
}