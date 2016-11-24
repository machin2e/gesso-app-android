package camp.computer.clay.platform.communication;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;

import camp.computer.clay.platform.Application;

public class Internet implements InternetInterface {

    private camp.computer.clay.platform.Internet manager;

    public Internet() {

    }

    /**
     * Display (or store) sever information.
     *
     * @return Internet Protocol (IP) address for the host device.
     */
    public String getInternetAddress() {
        Context context = Application.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        return ip;
    }

    @Override
    public void addHost(camp.computer.clay.platform.Internet manager) {
        this.manager = manager;
    }
}
