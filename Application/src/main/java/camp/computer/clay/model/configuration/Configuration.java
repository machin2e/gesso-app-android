package camp.computer.clay.model.configuration;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.entity.Entity;

public class Configuration {
    private UUID uuid = null;

    private String label = "";

    private List<PortConfiguration> portConfigurations = new ArrayList<>();

    // TODO: Add geometry endpoints or UUID to Image

    public Configuration() {
    }

    public Configuration(Entity portableEntity) {
        Group<Entity> ports = Portable.getPorts(portableEntity);
        for (int i = 0; i < ports.size(); i++) {
            Entity port = ports.get(i);
            PortConfiguration portConfiguration = new PortConfiguration(port);
            addPort(portConfiguration);
        }
        Log.v("Configuration", "created Extension Configuration with # ports: " + ports.size());
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

    public List<PortConfiguration> getPorts() {
        return portConfigurations;
    }

    public void addPort(PortConfiguration portConfiguration) {
        this.portConfigurations.add(portConfiguration);
    }
}
