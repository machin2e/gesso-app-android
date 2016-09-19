package camp.computer.clay.system.host;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import camp.computer.clay.application.Launcher;

public class NetworkResource implements NetworkResourceInterface {

    private NetworkHost manager;

    public NetworkResource() {

    }

    /**
     * Display (or store) sever information.
     * @return Internet Protocol (IP) address for the host device.
     */
    public String getInternetAddress () {
        Context context = Launcher.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    @Override
    public void addHost(NetworkHost manager) {
        this.manager = manager;
    }
}
