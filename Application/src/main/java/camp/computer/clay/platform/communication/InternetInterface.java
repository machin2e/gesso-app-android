package camp.computer.clay.platform.communication;

import camp.computer.clay.platform.Internet;

public interface InternetInterface {

    String getInternetAddress();

    void addHost(Internet manager);
}
