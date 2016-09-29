package camp.computer.clay.host;

import camp.computer.clay.old_model.NetworkHost;

public interface NetworkResourceInterface {

    String getInternetAddress();

    void addHost(NetworkHost manager);
}
