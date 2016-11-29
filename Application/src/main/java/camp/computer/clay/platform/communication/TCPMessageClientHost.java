package camp.computer.clay.platform.communication;

import android.util.Log;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.platform.Message;

// This object is created for each PhoneHost.
public class TCPMessageClientHost {

    public static int MESSAGE_SEND_FREQUENCY = 100;

    private List<Message> incomingMessages = new ArrayList<>(); // Create incoming message queue.

    public List<Message> outgoingMessages = new ArrayList<>(); // Create outgoing message queue.

    public InetAddress inetAddress;

    public TcpTasks.TcpConnectTask tcpConnectTask = null;

    public TCPClientHost mTCPClientHost = null;

    public TCPMessageClientHost() {

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

    public void connect(InetAddress inetAddress) {
        Log.v("TCP_Client", "Connecting to " + inetAddress);

        this.inetAddress = inetAddress;

        // Start task to send messages only if TcpConnectTask successful
        ThreadHelper.executeAsyncTask(new TcpTasks.SendMessageTask());
    }

    public void disconnect() {
        if (mTCPClientHost != null) {
            Log.v("TCP_Client", "disconnect");
            mTCPClientHost.stop();
            mTCPClientHost = null;
        }
    }

//    private class SendMessageTask extends AsyncTask<String, String, String> {
//
//        @Override
//        protected String doInBackground(String... params) {
//            Log.v("TCP_Client", "Started AsyncTask: SendMessageTask");
//
//            boolean runTask = true;
//
//            // Record the time that the current and previous messages were sent.
//            Calendar currentTime = Calendar.getInstance();
//            Date previousSendTime = currentTime.getTime();
//
//            while (runTask) {
//
//                // Update time since getLastEvent message was sent
//                currentTime = Calendar.getInstance();
//                long timeSinceSend = currentTime.getTime().getTime() - previousSendTime.getTime();
//
//                if (outgoingMessages.size() > 0) {
////                    Log.v("TCP_Client", "Outgoing message count: " + outgoingMessages.size());
//                    if (mTCPClientHost == null || !mTCPClientHost.isRunning()) {
////                        Log.v("TCP_Client", "No client to " + inetAddress + ". Creating.");
//                        if (inetAddress != null) {
//                            if (tcpConnectTask == null) {
//                                tcpConnectTask = new TcpConnectTask();
//                                executeAsyncTask(tcpConnectTask, inetAddress);
//                            }
//                        }
//                    } else if (mTCPClientHost != null && mTCPClientHost.isRunning()) { // Only send messages if the client is connected
////                    if (outgoingMessages.size() > 0) {
//                        if (timeSinceSend > MESSAGE_SEND_FREQUENCY) {
//                            Message outgoingMessage = outgoingMessages.get(0);
//                            Log.v("TCP_Client_Send", "Sending message: " + outgoingMessage.getContent());
//                            if (mTCPClientHost != null) {
//
//
//
////                                CRC16 CRC16 = new CRC16(CRC16.DEFAULT_POLYNOMIAL);
//                                CRC16 CRC16 = new CRC16();
//                                int seed = 0;
//                                byte[] outgoingMessageBytes = outgoingMessage.getContent().getBytes();
//                                int check = CRC16.calculate(outgoingMessageBytes, seed);
//                                String outmsg =
//                                        "\f" +
//                                        String.valueOf(outgoingMessage.getContent().length()) + "\t" +
//                                        String.valueOf(check) + "\t" +
//                                        "text" + "\t" +
//                                        outgoingMessage.getContent();
//
//                                Log.v ("TCP_Send", "outmsg: " + outmsg);
//
//
//
//                                boolean result = mTCPClientHost.sendMessage(outmsg);
//                                if (result) {
//                                    outgoingMessages.remove(0);
//                                }
//                            }
//                            previousSendTime = currentTime.getTime();
//                        }
////                    }
//                    } else {
//                        Log.e("TCP_Client_Send", "Could not send message. No connection.");
//                    }
//                } else {
//                    // Wait until all messages are sent!
//                    //disconnect();
//                }
//            }
//
//            return null;
//        }
//    }
//
//    private class TcpConnectTask extends AsyncTask<InetAddress, String, TCPClientHost> {
//
//        @Override
//        protected TCPClientHost doInBackground(InetAddress... params) {
//
//            Log.v("TCP_Client", "Started AsyncTask: TcpConnectTask");
//
//            if (params.length == 0 || params[0] == null) {
//                return null;
//            }
//
////                String inetAddress = (String) params[0];
//            InetAddress remoteServerAddress = (InetAddress) params[0];
//                Log.v ("TCP_Client", "IP: " + inetAddress);
//
//            //we create a TCPClient object and
////                InetAddress remoteServerAddress = remoteServerAddress = InetAddress.getByName(inetAddress);
//            int remoteServerPort = 3000;
//            mTCPClientHost = new TCPClientHost(remoteServerAddress, remoteServerPort);
//
//            // <HACK>
//            // TODO: Put this in an intermediate "DeviceTcpConnection" class, that contains "TCPClientHost" and has methods for registering callbacks?
//            mTCPClientHost.setOnMessageReceived(new TCPClientHost.OnMessageReceived() {
//                @Override
//                //here the messageReceived method is implemented
//                public void messageReceived(String messageString) {
//                    //this method calls the onProgressUpdate
//                    //publishProgress(message);
//                    Log.v("TCP_Client_Receive", "Received: " + messageString);
//
//                    //                    enqueueMessage("echoing received message: " + message);
//
//                    /*
//                    // Create message
//                    Message message = new Message("tcp", sourceMachine, destinationMachine, content);
//                    message.setDeliveryGuaranteed(true);
//
//                    // Enqueue message
//                    incomingMessages.addEvent (message);
//                    */
//                }
//            });
//            // </HACK>
//
//            // if Disconnects, try reconnecting!
////                boolean dorun = true;
////                while (dorun) {
//            mTCPClientHost.run();
////                }
//
//            //            Log.v("TCP_Client", "WELL IT STOPPED!");
//            mTCPClientHost = null; // Remove the client connection
//            tcpConnectTask = null;
//
//            return null;
//        }
//    }
}
