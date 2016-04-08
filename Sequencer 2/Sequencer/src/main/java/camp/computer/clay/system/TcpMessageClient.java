package camp.computer.clay.system;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class TcpMessageClient {

    private ArrayList<Message> incomingMessages = new ArrayList<Message>(); // Create incoming message queue.

    private ArrayList<Message> outgoingMessages = new ArrayList<Message>(); // Create outgoing message queue.

    private TcpClient mTcpClient = null;

    public TcpMessageClient() {

    }

    public void queueMessage (Message message) {
        // Queue message
        outgoingMessages.add(message);
    }

    private void sendMessage () {
        // TODO: Add delay between sends!

        // <HACK>
        if (outgoingMessages.size() > 0) {
            // Send messages on queue
            // TODO: Put this into a separate thread and call it periodically.
            //mTcpClient.sendMessage("connected to " + getUuid().toString());
            Message outgoingMessage = outgoingMessages.remove(0);

            // Format message for transmission (according to messaging protocol)
            String formattedMessage = outgoingMessage.getContent() + "\n";
            mTcpClient.sendMessage(formattedMessage);
            // </HACK>
        }
    }

    public void connect (String internetAddress) {
        Log.v("TCP_Server", "Connecting to " + internetAddress);

        // Start task to send messages only if ConnectTask successful
        //new SendMessageTask().execute();
        startMyTask(new SendMessageTask());

        // new ConnectTask().execute(internetAddress);
        startMyTask (new ConnectTask(), internetAddress);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    void startMyTask(AsyncTask asyncTask, String... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }

    public void disonnect () {
        mTcpClient.stopClient();
    }

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
                    Log.v("TCP_Server_Receive", "Received: " + message);

//                    sendMessageTcp("echoing received message: " + message);
                }
            });
            // </HACK>

            mTcpClient.run();

            return null;
        }
    }

    private class SendMessageTask extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            Log.v("TCP_Server", "SendMessageTask");

            boolean run = true;

            Calendar currentTime = Calendar.getInstance();
            Date previousSendTime = currentTime.getTime();

            while (run) {

                currentTime = Calendar.getInstance();
                long timeSinceSend = currentTime.getTime().getTime() - previousSendTime.getTime();
//                Log.v("TCP_Server", "time since send:  " + timeSinceSend);

                if (outgoingMessages.size() > 0) {
                    if (timeSinceSend > 2000) {
                        Message outgoingMessage = outgoingMessages.remove(0);
                        Log.v("TCP_Server_Send", "Sending message: " + outgoingMessage.getContent());
                        mTcpClient.sendMessage(outgoingMessage.getContent());
                        previousSendTime = currentTime.getTime();
                    }
                }
            }

            return null;
        }
    }
}
