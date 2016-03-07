package camp.computer.clay.resource;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import camp.computer.clay.sequencer.ApplicationView;
import camp.computer.clay.system.NetworkManager;
import camp.computer.clay.system.NetworkResourceInterface;

public class NetworkResource implements NetworkResourceInterface {

    private NetworkManager manager;

    public NetworkResource() {

    }

    /**
     * Display (or store) sever information.
     * @return Internet Protocol (IP) address for the host device.
     */
    public String getInternetAddress () {
        Context context = ApplicationView.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    @Override
    public void addManager(NetworkManager manager) {
        this.manager = manager;
    }
}
