package camp.computer.clay.system.host;

import android.annotation.TargetApi;
import android.os.AsyncTask;
import android.os.Build;
import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import camp.computer.clay.system.old_model.Message;
import camp.computer.clay.system.host.util.CRC16;

public class TcpMessageClientHost {

    public static int MESSAGE_SEND_FREQUENCY = 100;

    private ArrayList<Message> incomingMessages = new ArrayList<Message>(); // Create incoming message queue.

    private ArrayList<Message> outgoingMessages = new ArrayList<Message>(); // Create outgoing message queue.

    private InetAddress inetAddress;

    private TcpConnectTask tcpConnectTask = null;

    private TcpClientHost mTcpClientHost = null;

    public TcpMessageClientHost() {

    }

    public void setInetAddress(InetAddress inetAddress) {
        this.inetAddress = this.inetAddress;
    }

    public void enqueueMessage(Message message) {
        Log.v("TCP_Server", "enqueueMessage (size: " + outgoingMessages.size() + ")");
        if (outgoingMessages != null) {
            outgoingMessages.add(message);
        }
    }

    public void connect (InetAddress inetAddress) {
        Log.v("TCP_Client", "Connecting to " + inetAddress);

        this.inetAddress = inetAddress;

        // Start task to send messages only if TcpConnectTask successful
        executeAsyncTask(new SendMessageTask());
    }

    /**
     * Used to start multiple AsyncTasks concurrently.
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

    /*
    // Note: This is a less capable implementation of the above method by the same name.
    @TargetApi(Build.VERSION_CODES.HONEYCOMB) // API 11
    void executeAsyncTask(AsyncTask asyncTask, String... params) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
            asyncTask.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, params);
        else
            asyncTask.execute(params);
    }
    */

    public void disconnect () {
        if (mTcpClientHost != null) {
            Log.v ("TCP_Client", "disconnect");
            mTcpClientHost.stop();
            mTcpClientHost = null;
        }
    }

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
                    if (mTcpClientHost == null || !mTcpClientHost.isRunning()) {
//                        Log.v("TCP_Client", "No client to " + inetAddress + ". Creating.");
                        if (inetAddress != null) {
                            if (tcpConnectTask == null) {
                                tcpConnectTask = new TcpConnectTask();
                                executeAsyncTask(tcpConnectTask, inetAddress);
                            }
                        }
                    } else if (mTcpClientHost != null && mTcpClientHost.isRunning()) { // Only send messages if the client is connected
//                    if (outgoingMessages.size() > 0) {
                        if (timeSinceSend > MESSAGE_SEND_FREQUENCY) {
                            Message outgoingMessage = outgoingMessages.get(0);
                            Log.v("TCP_Client_Send", "Sending message: " + outgoingMessage.getContent());
                            if (mTcpClientHost != null) {



//                                CRC16 CRC16 = new CRC16(CRC16.DEFAULT_POLYNOMIAL);
                                CRC16 CRC16 = new CRC16();
                                int seed = 0;
                                byte[] outgoingMessageBytes = outgoingMessage.getContent().getBytes();
                                int check = CRC16.calculate(outgoingMessageBytes, seed);
                                String outmsg =
                                        "\f" +
                                        String.valueOf(outgoingMessage.getContent().length()) + "\t" +
                                        String.valueOf(check) + "\t" +
                                        "text" + "\t" +
                                        outgoingMessage.getContent();

                                Log.v ("TCP_Send", "outmsg: " + outmsg);



                                boolean result = mTcpClientHost.expose(outmsg);
                                if (result) {
                                    outgoingMessages.remove(0);
                                }
                            }
                            previousSendTime = currentTime.getTime();
                        }
//                    }
                    } else {
                        Log.e("TCP_Client_Send", "Could not send message. No connection.");
                    }
                } else {
                    // Wait until all messages are sent!
                    //disconnect();
                }
            }

            return null;
        }
    }

    private class TcpConnectTask extends AsyncTask<InetAddress, String, TcpClientHost> {

        @Override
        protected TcpClientHost doInBackground(InetAddress... params) {

            Log.v("TCP_Client", "Started AsyncTask: TcpConnectTask");

            if (params.length == 0 || params[0] == null) {
                return null;
            }

//                String inetAddress = (String) params[0];
            InetAddress remoteServerAddress = (InetAddress) params[0];
                Log.v ("TCP_Client", "IP: " + inetAddress);

            //we create a TCPClient object and
//                InetAddress remoteServerAddress = remoteServerAddress = InetAddress.getByName(inetAddress);
            int remoteServerPort = 3000;
            mTcpClientHost = new TcpClientHost(remoteServerAddress, remoteServerPort);

            // <HACK>
            // TODO: Put this in an intermediate "DeviceTcpConnection" class, that contains "TcpClientHost" and has methods for registering callbacks?
            mTcpClientHost.setOnMessageReceived(new TcpClientHost.OnMessageReceived() {
                @Override
                //here the messageReceived method is implemented
                public void messageReceived(String messageString) {
                    //this method calls the onProgressUpdate
                    //publishProgress(message);
                    Log.v("TCP_Client_Receive", "Received: " + messageString);

                    //                    enqueueMessage("echoing received message: " + message);

                    /*
                    // Create message
                    Message message = new Message("tcp", sourceMachine, destinationMachine, content);
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
            mTcpClientHost.run();
//                }

            //            Log.v("TCP_Client", "WELL IT STOPPED!");
            mTcpClientHost = null; // Remove the client connection
            tcpConnectTask = null;

            return null;
        }
    }
}
