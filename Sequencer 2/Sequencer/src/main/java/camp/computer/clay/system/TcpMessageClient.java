package camp.computer.clay.system;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TcpMessageClient {

    public static int MESSAGE_SEND_FREQUENCY = 100;

    private ArrayList<Message> incomingMessages = new ArrayList<Message>(); // Create incoming message queue.

    private ArrayList<Message> outgoingMessages = new ArrayList<Message>(); // Create outgoing message queue.

    private String internetAddress;

    private TcpClient mTcpClient = null;

    public TcpMessageClient() {

    }

    public void setInternetAddress (String internetAddress) {
        this.internetAddress = internetAddress;
    }

    public void enqueueMessage(Message message) {
        Log.v("TCP_Server", "enqueueMessage");

        // Queue message
        outgoingMessages.add(message);
    }

//    private void sendMessage () {
//
//        // <HACK>
//        if (outgoingMessages.size() > 0) {
//            // Send messages on queue
//            // TODO: Put this into a separate thread and call it periodically.
//            //mTcpClient.sendMessage("connected to " + getUuid().toString());
//            Message outgoingMessage = outgoingMessages.remove(0);
//
//            // Format message for transmission (according to messaging protocol)
//            String formattedMessage = outgoingMessage.getContent() + "\n";
//            mTcpClient.sendMessage(formattedMessage);
//            // </HACK>
//        }
//    }

    public void connect (String internetAddress) {
        Log.v("TCP_Client", "Connecting to " + internetAddress);

        // Start task to send messages only if TcpConnectTask successful
        //new SendMessageTask().execute();
        executeAsyncTask(new SendMessageTask());

        // new TcpConnectTask().execute(internetAddress);
//        this.internetAddress = internetAddress;
//        executeAsyncTask (new TcpConnectTask(), internetAddress);
    }

//    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
//    void startMyTask(AsyncTask asyncTask, String... params) {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
//            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
//        else
//            asyncTask.execute(params);
//    }

    /**
     * Used to start multiple AsyncTasks
     *
     * @param asyncTask
     * @param params
     * @param <T>
     */
    // TODO: Update the above to the below (from http://stackoverflow.com/questions/4068984/running-multiple-asynctasks-at-the-same-time-not-possible)
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    public static <T> void executeAsyncTask(AsyncTask<T, ?, ?> asyncTask, T... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        } else {
            asyncTask.execute(params);
        }
    }

    public void disconnect () {
        if (mTcpClient != null) {
            Log.v ("TCP_Client", "disconnect");
            mTcpClient.stop();
            mTcpClient = null;
        }
    }

    TcpConnectTask tcpConnectTask = null;

    private class SendMessageTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.v("TCP_Client", "Started AsyncTask: SendMessageTask");

            boolean runTask = true;

            // Record the time that the current and previous messages were sent.
            Calendar currentTime = Calendar.getInstance();
            Date previousSendTime = currentTime.getTime();

            while (runTask) {

                // Update time since last message was sent
                currentTime = Calendar.getInstance();
                long timeSinceSend = currentTime.getTime().getTime() - previousSendTime.getTime();

                if (outgoingMessages.size() > 0) {
//                    Log.v("TCP_Client", "Outgoing message count: " + outgoingMessages.size());
                    if (mTcpClient == null || !mTcpClient.isRunning()) {
                        Log.v("TCP_Client", "No client to " + internetAddress + ". Creating.");
                        if (internetAddress != null) {
                            if (tcpConnectTask == null) {
                                tcpConnectTask = new TcpConnectTask();
                                executeAsyncTask(tcpConnectTask, internetAddress);
                            }
                        }
                    } else if (mTcpClient != null && mTcpClient.isRunning()) { // Only send messages if the client is connected
//                    if (outgoingMessages.size() > 0) {
                        if (timeSinceSend > MESSAGE_SEND_FREQUENCY) {
                            Message outgoingMessage = outgoingMessages.get(0);
                            Log.v("TCP_Client_Send", "Sending message: " + outgoingMessage.getContent());
                            boolean result = mTcpClient.sendMessage(outgoingMessage.getContent());
                            if (result) {
                                outgoingMessages.remove(0);
                            }
                            previousSendTime = currentTime.getTime();
                        }
//                    }
                    } else {
                        Log.e("TCP_Client_Send", "Could not send message. No connection.");
                    }
                } else {
                    // Wait until all messages are sent!
                    disconnect();
                }
            }

            return null;
        }
    }

    private class TcpConnectTask extends AsyncTask<String, String, TcpClient> {

        @Override
        protected TcpClient doInBackground(String... params) {
            try {

                Log.v("TCP_Client", "Started AsyncTask: TcpConnectTask");

                if (params.length == 0 || params[0] == null) {
                    return null;
                }

                String internetAddress = (String) params[0];
                Log.v ("TCP_Client", "IP: " + internetAddress);

                //we create a TCPClient object and
                InetAddress remoteServerAddress = remoteServerAddress = InetAddress.getByName(internetAddress);
                int remoteServerPort = 3000;
                mTcpClient = new TcpClient(remoteServerAddress, remoteServerPort);

                // <HACK>
                // TODO: Put this in an intermediate "DeviceTcpConnection" class, that contains "TcpClient" and has methods for registering callbacks?
                mTcpClient.setOnMessageReceived(new TcpClient.OnMessageReceived() {
                    @Override
                    //here the messageReceived method is implemented
                    public void messageReceived(String messageString) {
                        //this method calls the onProgressUpdate
                        //publishProgress(message);
                        Log.v("TCP_Client_Receive", "Received: " + messageString);

                        //                    enqueueMessage("echoing received message: " + message);

                        /*
                        // Create message
                        Message message = new Message("tcp", source, destination, content);
                        message.setDeliveryGuaranteed(true);

                        // Enqueue message
                        incomingMessages.add (message);
                        */
                    }
                });
                // </HACK>

                // if Disconnects, try reconnecting!
//                boolean dorun = true;
//                while (dorun) {
                    mTcpClient.run();
//                }

            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

//            Log.v("TCP_Client", "WELL IT STOPPED!");
            mTcpClient = null; // Remove the client connection
            tcpConnectTask = null;

            return null;
        }
    }
}
