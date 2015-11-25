package computer.clay.protocolizer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends ActionBarActivity {

//    private static final int UDP_SERVER_PORT = 4446;
//    private static final int MAX_UDP_DATAGRAM_LEN = 1500;
////    private TextView textMessage;
//    private MyDatagramReceiver myDatagramReceiver = null;

    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivity.context = getApplicationContext();
        setContentView(R.layout.activity_main);
    }

    public static Context getAppContext() {
        return MainActivity.context;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
//            startActivity(new Intent(this, SettingsActivity.class));

            Intent settingsIntent = new Intent(this, SettingsActivity.class);
            startActivity(settingsIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onPause() {
        super.onPause();
//        myDatagramReceiver.kill();
    }

    @Override
    protected void onResume() {
        super.onResume();
//        myDatagramReceiver = new MyDatagramReceiver();
//        myDatagramReceiver.start();
    }

    /**
     * UDP Server
//     */
//    private class MyDatagramReceiver extends Thread {
//        private boolean bKeepRunning = true;
//        private String lastMessage = "";
//
//        public void run() {
//            String message;
//            byte[] lmessage = new byte[MAX_UDP_DATAGRAM_LEN];
//            DatagramPacket packet = new DatagramPacket(lmessage, lmessage.length);
//
//            try {
//                DatagramSocket socket = new DatagramSocket(UDP_SERVER_PORT);
//
//                while(bKeepRunning) {
//                    socket.receive(packet);
//                    message = new String(lmessage, 0, packet.getLength());
//                    lastMessage = message;
//                    runOnUiThread(updateTextMessage);
//
////                    // Send response datagram
////                    DatagramPacket p = new DatagramPacket(message, msg_length, local, server_port);
////                    s.send(p);
//                }
//
//                if (socket != null) {
//                    socket.close();
//                }
//            } catch (Throwable e) {
//                e.printStackTrace();
//            }
//        }
//
//        public void kill() {
//            bKeepRunning = false;
//        }
//
//        public String getLastMessage() {
//            return lastMessage;
//        }
//
//        // Protocaller
//        // Transmitter
//        // Protocolist
//        // Relay
//        // Router
//        // Networking
//        // Socializer
//        // Synchronizer
//    }

//    private Runnable updateTextMessage = new Runnable() {
//        public void run() {
//            if (myDatagramReceiver == null) return;
////            textMessage.setText(myDatagramReceiver.getLastMessage());
//
////            String httpRequestText = httpRequestAdapter.getItem(position); //CharSequence text = "Hello toast!";
////                int duration = Toast.LENGTH_SHORT;
////            Toast toast = Toast.makeText(getParent(), myDatagramReceiver.getLastMessage(), Toast.LENGTH_SHORT); //Toast toast = Toast.makeText(context, text, duration);
////            toast.show();
//            Log.v("UDP", myDatagramReceiver.getLastMessage());
//        }
//    };
}
