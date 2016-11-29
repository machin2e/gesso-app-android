package camp.computer.clay.platform.communication;

import android.os.AsyncTask;
import android.util.Log;

import java.net.InetAddress;
import java.util.Calendar;
import java.util.Date;

import camp.computer.clay.platform.Message;
import camp.computer.clay.platform.util.CRC16;

public class TcpTasks {

    // TODO: Change parameter to object so this can be a one-off task.
    public static class SendMessageTask extends AsyncTask<TCPMessageClientHost, String, String> {

        @Override
        protected String doInBackground(TCPMessageClientHost... params) {
            Log.v("TCP_Client", "Started AsyncTask: SendMessageTask");

            TCPMessageClientHost tcpMessageClientHost = params[0];

            boolean runTask = true;

            // Record the time that the current and previous messages were sent.
            Calendar currentTime = Calendar.getInstance();
            Date previousSendTime = currentTime.getTime();

            while (runTask) {

                // Update time since getLastEvent message was sent
                currentTime = Calendar.getInstance();
                long timeSinceSend = currentTime.getTime().getTime() - previousSendTime.getTime();

                if (tcpMessageClientHost.outgoingMessages.size() > 0) {
//                    Log.v("TCP_Client", "Outgoing message count: " + outgoingMessages.size());

                    if (tcpMessageClientHost.mTCPClientHost == null || !tcpMessageClientHost.mTCPClientHost.isRunning()) {

//                        Log.v("TCP_Client", "No client to " + inetAddress + ". Creating.");
                        if (tcpMessageClientHost.inetAddress != null) {
                            if (tcpMessageClientHost.tcpConnectTask == null) {
                                tcpMessageClientHost.tcpConnectTask = new TcpConnectTask();
                                ThreadHelper.executeAsyncTask(tcpMessageClientHost.tcpConnectTask, tcpMessageClientHost);
                            }
                        }

                    } else if (tcpMessageClientHost.mTCPClientHost != null && tcpMessageClientHost.mTCPClientHost.isRunning()) { // Only send messages if the client is connected

//                    if (outgoingMessages.size() > 0) {
                        if (timeSinceSend > TCPMessageClientHost.MESSAGE_SEND_FREQUENCY) {
                            Message outgoingMessage = tcpMessageClientHost.outgoingMessages.get(0);
                            Log.v("TCP_Client_Send", "Sending message: " + outgoingMessage.getContent());
                            if (tcpMessageClientHost.mTCPClientHost != null) {


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

                                Log.v("TCP_Send", "outmsg: " + outmsg);


                                boolean result = tcpMessageClientHost.mTCPClientHost.sendMessage(outmsg);
                                if (result) {
                                    tcpMessageClientHost.outgoingMessages.remove(0);
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

    public static class TcpConnectTask extends AsyncTask<TCPMessageClientHost, String, TCPClientHost> {

        @Override
        protected TCPClientHost doInBackground(TCPMessageClientHost... params) {

            Log.v("TCP_Client", "Started AsyncTask: TcpConnectTask");

            if (params.length == 0 || params[0] == null) {
                return null;
            }

            TCPMessageClientHost tcpMessageClientHost = params[0];

//                String inetAddress = (String) params[0];
            InetAddress remoteServerAddress = (InetAddress) tcpMessageClientHost.inetAddress;
            Log.v("TCP_Client", "IP: " + remoteServerAddress);

            //we create a TCPClient object and
//                InetAddress remoteServerAddress = remoteServerAddress = InetAddress.getByName(inetAddress);
            int remoteServerPort = 3000;
            tcpMessageClientHost.mTCPClientHost = new TCPClientHost(remoteServerAddress, remoteServerPort);

            // <HACK>
            // TODO: Put this in an intermediate "DeviceTcpConnection" class, that contains "TCPClientHost" and has methods for registering callbacks?
            tcpMessageClientHost.mTCPClientHost.setOnMessageReceived(new TCPClientHost.OnMessageReceived() {
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
                    incomingMessages.addEvent (message);
                    */
                }
            });
            // </HACK>

            // if Disconnects, try reconnecting!
//                boolean dorun = true;
//                while (dorun) {
            tcpMessageClientHost.mTCPClientHost.run();
//                }

            //            Log.v("TCP_Client", "WELL IT STOPPED!");
            tcpMessageClientHost.mTCPClientHost = null; // Remove the client connection
            tcpMessageClientHost.tcpConnectTask = null;

            return null;
        }
    }
}
