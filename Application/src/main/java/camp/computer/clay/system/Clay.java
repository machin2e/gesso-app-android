package camp.computer.clay.system;

import android.content.Context;
import android.net.wifi.WifiManager;
import android.text.format.Formatter;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import camp.computer.clay.app.Application;
import camp.computer.clay.model.data.ImageSet;
import camp.computer.clay.model.interaction.OnTouchActionListener;
import camp.computer.clay.model.interaction.Perspective;
import camp.computer.clay.model.sim.Body;
import camp.computer.clay.model.interaction.TouchInteraction;
import camp.computer.clay.model.sim.Frame;
import camp.computer.clay.model.sim.Path;
import camp.computer.clay.model.sim.Port;
import camp.computer.clay.model.sim.Simulation;
import camp.computer.clay.system.host.CacheHost;
import camp.computer.clay.system.host.DisplayHostInterface;
import camp.computer.clay.system.host.MessageHost;
import camp.computer.clay.system.host.MessageHostInterface;
import camp.computer.clay.system.host.NetworkHost;
import camp.computer.clay.system.host.NetworkResourceInterface;
import camp.computer.clay.system.host.SQLiteStoreHost;
import camp.computer.clay.system.old_model.Descriptor;
import camp.computer.clay.system.old_model.Device;
import camp.computer.clay.system.old_model.Event;
import camp.computer.clay.viz.arch.Image;
import camp.computer.clay.viz.arch.OnDrawListener;
import camp.computer.clay.viz.arch.Visibility;
import camp.computer.clay.viz.arch.Viz;
import camp.computer.clay.viz.util.Circle;
import camp.computer.clay.viz.util.Color;
import camp.computer.clay.viz.util.Line;
import camp.computer.clay.viz.util.Point;
import camp.computer.clay.viz.util.Rectangle;
import camp.computer.clay.viz.arch.Shape;

public class Clay {

    private Descriptor descriptor;

    private MessageHost messageHost = null;

    private NetworkHost networkHost = null;

    private CacheHost cache = null;

    private SQLiteStoreHost storeHost = null;

    private Simulation simulation;

    private Viz viz;

    /**
     * List of the discovered touchscreen devices
     */
    private List<DisplayHostInterface> displays;

    // Group of discovered devices
    private List<Device> devices = new ArrayList<>();

    public Clay() {

        this.displays = new ArrayList<>(); // Create list to storeHost displays

        this.messageHost = new MessageHost(this); // Start the communications systems

        this.networkHost = new NetworkHost(this); // Start the networking systems

        this.cache = new CacheHost(this); // Set up behavior repository

        // Descriptor
        // TODO: Stream this in from the Internet and devices.
        descriptor = new Descriptor("clay", "");
        descriptor.list("devices");

        // Simulation
        this.simulation = new Simulation();

        // Viz
        this.viz = new Viz(simulation);

        // Create body and set perspective
        Body body = new Body();
        Perspective perspective = new Perspective(viz);
        body.setPerspective(perspective);

        // Add body to simulation
        simulation.addBody(body);

        Application.getDisplay().getSurface().setViz(viz);
    }

    public Simulation getSimulation() {
        return this.simulation;
    }

    public Viz getViz() {
        return this.viz;
    }

    public void generateFrame(UUID uuid) {

        // Description
        // TODO: Read this from the physical frame description and look up configuration in store.
        final int PORT_COUNT = 12;

        // Model
        final Frame frame = new Frame();

        frame.setUuid(uuid);

        for (int j = 0; j < PORT_COUNT; j++) {
            Port port = new Port();
            frame.addPort(port);
        }

        // Simulation
        simulation.addFrame(frame);

        // Image
        Image frameImage = generateFrameImage(frame);

        if (viz.getImages().filterType(Frame.class).getList().size() == 0) {
            frameImage.setPosition(new Point(0, 0));
        } else if (viz.getImages().filterType(Frame.class).getList().size() == 1) {
            frameImage.setPosition(new Point(300, -300));
        } else if (viz.getImages().filterType(Frame.class).getList().size() == 2) {
            frameImage.setPosition(new Point(-300, 300));
        }

        viz.addImage(frameImage, "frames");

        // Ports
        Image portImage = null;
        portImage = generatePortImage(frame.getPort(0), new Point(-90, 160));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(1), new Point(0, 160));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(2), new Point(90, 160));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(3), new Point(160, 90));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(4), new Point(160, 0));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(5), new Point(160, -90));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(6), new Point(90, -160));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(7), new Point(0, -160));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(8), new Point(-90, -160));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(9), new Point(-160, -90));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(10), new Point(-160, 0));
        viz.addImage(portImage, "ports");

        portImage = generatePortImage(frame.getPort(11), new Point(-160, 90));
        viz.addImage(portImage, "ports");
    }

    private static double CENTIMETERS_PER_PIXEL = 2.54;

    private Image generateFrameImage(final Frame frame) {

        final Image<Frame> frameImage = new Image<>(frame);

        // Port Groups (i.e., Headers)
        // Dimensions: (2.41 mm, 8.13 mm)
        // Width equation: 2.54 mm * <Number> + 0.51 mm
        Shape portGroupShape = null;
        portGroupShape = new Rectangle(new Point(0, 103), 81.3 / CENTIMETERS_PER_PIXEL, 24.1 / CENTIMETERS_PER_PIXEL);
        portGroupShape.setRotation(0);
        portGroupShape.setStyle("color", "#ff3b3b3b");
        portGroupShape.setStyle("outlineColor", "#ff414141");
        portGroupShape.setStyle("outlineThickness", "0");
        frameImage.addShape(portGroupShape);

        portGroupShape = new Rectangle(new Point(103, 0), 24.1 / CENTIMETERS_PER_PIXEL, 81.3 / CENTIMETERS_PER_PIXEL);
        portGroupShape.setRotation(0);
        portGroupShape.setStyle("color", "#ff3b3b3b");
        portGroupShape.setStyle("outlineColor", "#ff414141");
        portGroupShape.setStyle("outlineThickness", "0");
        frameImage.addShape(portGroupShape);

        portGroupShape = new Rectangle(new Point(0, -103), 81.3 / CENTIMETERS_PER_PIXEL, 24.1 / CENTIMETERS_PER_PIXEL);
        portGroupShape.setRotation(0);
        portGroupShape.setStyle("color", "#ff3b3b3b");
        portGroupShape.setStyle("outlineColor", "#ff414141");
        portGroupShape.setStyle("outlineThickness", "0");
        frameImage.addShape(portGroupShape);

        portGroupShape = new Rectangle(new Point(-103, 0), 24.1 / CENTIMETERS_PER_PIXEL, 81.3 / CENTIMETERS_PER_PIXEL);
        portGroupShape.setRotation(0);
        portGroupShape.setStyle("color", "#ff3b3b3b");
        portGroupShape.setStyle("outlineColor", "#ff414141");
        portGroupShape.setStyle("outlineThickness", "0");
        frameImage.addShape(portGroupShape);

        // Board
        // Dimensions: (5.08 cm, 5.08 cm)
        final Shape boardShape = new Rectangle(new Point(0, 0), 508 / CENTIMETERS_PER_PIXEL, 508 / CENTIMETERS_PER_PIXEL);
        boardShape.setRotation(0);
        boardShape.setStyle("color", "#fff7f7f7");
        boardShape.setStyle("outlineColor", "#ff414141");
        boardShape.setStyle("outlineThickness", "1");
        frameImage.addShape(boardShape);

        boardShape.setOnTouchActionListener(new OnTouchActionListener() {
            @Override
            public void onAction(TouchInteraction touchInteraction) {

                switch (touchInteraction.getType()) {
                    case TOUCH:
                        Log.v("Touch", "boardShape.onAction");
                        break;

                    case HOLD:
                        Log.v("Touch", "boardShape.onHold");
                        break;

                    case DRAG:
                        Log.v("Touch", "boardShape.onDrag");
                        frameImage.setPosition(touchInteraction.getPosition());
//                        boardShape.setPosition(touchInteraction.getPosition());
                        break;

                    case RELEASE:
                        Log.v("Touch", "boardShape.onRelease");
                        break;

                    case TAP:
                        Log.v("Touch", "boardShape.onTap");

                        // Show Ports
                        for (Port port : frame.getPorts()) {
                            viz.getImage(port).setVisibility(Visibility.VISIBLE);
                        }

                        break;
                }
            }
        });

        // Lights
        // Dimensions: (1.60 mm, 2.10 mm)
        Shape lightShape = null;
        lightShape = new Rectangle(new Point(-11, 89), 16.0 / CENTIMETERS_PER_PIXEL, 25.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(0, 89), 16.0 / CENTIMETERS_PER_PIXEL, 25.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(11, 89), 16.0 / CENTIMETERS_PER_PIXEL, 25.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(89, 11), 25.0 / CENTIMETERS_PER_PIXEL, 16.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(89, 0), 25.0 / CENTIMETERS_PER_PIXEL, 16.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(89, -11), 25.0 / CENTIMETERS_PER_PIXEL, 16.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(11, -89), 16.0 / CENTIMETERS_PER_PIXEL, 25.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(0, -89), 16.0 / CENTIMETERS_PER_PIXEL, 25.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(-11, -89), 16.0 / CENTIMETERS_PER_PIXEL, 25.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(-89, -11), 25.0 / CENTIMETERS_PER_PIXEL, 16.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(-89, 0), 25.0 / CENTIMETERS_PER_PIXEL, 16.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        lightShape = new Rectangle(new Point(-89, 11), 25.0 / CENTIMETERS_PER_PIXEL, 16.0 / CENTIMETERS_PER_PIXEL);
        lightShape.setRotation(0);
        lightShape.setStyle("color", "#ffe7e7e7");
        lightShape.setStyle("outlineColor", "#ff414141");
        lightShape.setStyle("outlineThickness", "1");
        frameImage.addShape(lightShape);

        frameImage.setOnDrawListener(new OnDrawListener() {
            @Override
            public void onUpdate(Viz viz) {

            }

            @Override
            public void onDraw(Viz viz) {
                for (int i = 0; i < frameImage.getShapes().size(); i++) {
                    frameImage.getShape(i).draw(viz);
                }
            }
        });

        return frameImage;
    }

    private Image generatePortImage(final Port port, Point position) {

        final Image<Port> portImage = new Image<>(port);

        // Offset the image relative to the parent frame
        final Image<Frame> frameImage = viz.getImage(port.getFrame());
        portImage.getPosition().setReferencePoint(frameImage.getPosition());
        portImage.getPosition().setRelative(position);

        // Port shape
        final Shape portShape = new Circle(new Point(0, 0), 40);
        portShape.setRotation(0);
        portShape.setStyle("color", "#ffefefef");
        portShape.setStyle("outlineColor", "#ff000000");
        portShape.setStyle("outlineThickness", "0");
        portImage.addShape(portShape);

        // Shapes
        final Shape candidateTargetPortShape = new Circle(new Point(0, 0), 40);
        candidateTargetPortShape.setRotation(0);
        candidateTargetPortShape.setStyle("color", "#ffefefef");
        candidateTargetPortShape.setStyle("outlineColor", "#ff000000");
        candidateTargetPortShape.setStyle("outlineThickness", "0");
        portImage.addShape(candidateTargetPortShape);

        final Line candidatePathLine = new Line(portImage.getPosition(), candidateTargetPortShape.getPosition());

        portImage.setVisibility(Visibility.INVISIBLE);

        // Interaction
        candidateTargetPortShape.setOnTouchActionListener(new OnTouchActionListener() {
            @Override
            public void onAction(TouchInteraction touchInteraction) {

                switch (touchInteraction.getType()) {
                    case TOUCH:
                        Log.v("Touch", "portShape.onAction");
                        break;

                    case HOLD:
                        Log.v("Touch", "portShape.onHold");
                        break;

                    case DRAG:
                        Log.v("Touch", "portShape.onDrag");

                        candidateTargetPortShape.setPosition(touchInteraction.getPosition());

                        // Hide all ports
                        ImageSet frameImagesToHide = viz.getImages()
                                .filterType(Frame.class)
                                .remove(frameImage);

                        for (Frame frame : frameImagesToHide.getModels(Frame.class)) {
                            viz.getImages(frame.getPorts()).setVisibility(Visibility.INVISIBLE);
                        }

                        // Show ports of nearby frames
                        ImageSet nearbyFrameImages = viz.getImages()
                                .filterType(Frame.class)
                                .filterDistance(candidateTargetPortShape.getPosition(), 200);

                        for (Frame frame : nearbyFrameImages.getModels(Frame.class)) {
                            viz.getImages(frame.getPorts()).setVisibility(Visibility.VISIBLE);
                        }

                        break;

                    case RELEASE:
                        Log.v("Touch", "portShape.onRelease");

                        candidateTargetPortShape.setPosition(portShape.getPosition());

                        // Get target port (if any)
                        Image<Port> nearestPortImage = viz.getImages()
                                .filterType(Port.class)
                                .getNearest(touchInteraction.getPosition());
                                //.getAt(touchInteraction.getPosition());
                        Log.v("Touching", "nearestPortImage: " + nearestPortImage);

                        // Add path
                        if (nearestPortImage != null) {
                            Path path = new Path(port, nearestPortImage.getModel());
                            port.addPath(path);
                        }

                        break;

                    case TAP:
                        Log.v("Touch", "portShape.onTap");

                        candidateTargetPortShape.setPosition(portShape.getPosition());

                        portImage.setVisibility(Visibility.INVISIBLE);

                        break;
                }
            }
        });

        // Draw
        portImage.setOnDrawListener(new OnDrawListener() {
            @Override
            public void onUpdate(Viz viz) {

            }

            @Override
            public void onDraw(Viz viz) {
                for (Shape shape : portImage.getShapes()) {
                    shape.draw(viz);
                }
            }
        });

        return portImage;
    }

    public Descriptor getDescriptor() {
        return this.descriptor;
    }

    /*
     * Clay's essential operating system functions.
     */

    public void addHost(MessageHostInterface messageManager) {
        this.messageHost.addHost(messageManager);
    }

    public void addHost(NetworkResourceInterface networkResource) {
        this.networkHost.addHost(networkResource);
    }

    /**
     * Adds a descriptor manager for use by Clay. Retrieves the basic actions provided by the
     * descriptor manager and makes them available in Clay.
     */
    public void setStore(SQLiteStoreHost contentManager) {
        this.storeHost = contentManager;
    }

    /*
     * Clay's infrastructure management functions.
     */

    /**
     * Adds a view to Clay. This makes the view available for use in systems built with Clay.
     *
     * @param view The view to make available to Clay.
     */
    public void addDisplay(DisplayHostInterface view) {
        this.displays.add(view);
    }

    /**
     * Returns the view manager the specified index.
     *
     * @param i The index of the view to return.
     * @return The view at the specified index.
     */
    public DisplayHostInterface getView(int i) {
        return this.displays.get(i);
    }

    public CacheHost getCache() {
        return this.cache;
    }

    public SQLiteStoreHost getStore() {
        return this.storeHost;
    }

    public List<Device> getDevices() {
        return this.devices;
    }

    public boolean hasNetworkManager() {
        return this.networkHost != null;
    }

    // TODO: Create device profile. Add this to device profile. Change to getClay().getProfile().getInternetAddress()
    public String getInternetAddress() {
        Context context = Application.getContext();
        WifiManager wm = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        String ip = Formatter.formatIpAddress(wm.getConnectionInfo().getIpAddress());
        Log.v("Clay", "Internet address: " + ip);
        return ip;
    }

    public String getInternetBroadcastAddress() {
        String broadcastAddressString = getInternetAddress();
        Log.v("Clay", "Broadcast: " + broadcastAddressString);
        broadcastAddressString = broadcastAddressString.substring(0, broadcastAddressString.lastIndexOf("."));
        broadcastAddressString += ".255";
        return broadcastAddressString;
    }

    public Device getDeviceByAddress(String address) {
        for (Device device : getDevices()) {
            if (device.getInternetAddress().compareTo(address) == 0) {
                return device;
            }
        }
        return null;
    }

    private Clay getClay() {
        return this;
    }

    /**
     * Adds the specified unit to Clay's operating environment.
     */
    public Device addFrame(final UUID uuid, final String internetAddress) {

//        Log.v("UDP", "found device");

        // Search for the device in the store
        if (hasFrame(uuid)) {
            return null;
        }

        generateFrame(uuid);

        // Try to restore the device profile from the storeHost.
        // TODO: Replace this with regenerateFrame(UUID)
        Device device = getStore().restoreDevice(uuid);

        // If unable to restore the device's profile, then create a profile for the device.
        if (device == null) {
            device = new Device(getClay(), uuid);
        }

        // Update the device's profile based on information received from device itself.
        if (device != null) {

            // Data.
            Descriptor deviceDescriptor = getClay().getDescriptor().get("devices").put(uuid.toString());

            // <HACK>
            // TODO: Update this from a list of the observables received from the boards.
            Descriptor channelsDescriptor = deviceDescriptor.list("channels");
            for (int i = 0; i < 12; i++) {

                // device/<uuid>/channels/<number>
                Descriptor channelDescriptor = channelsDescriptor.put(String.valueOf(i + 1));

                // device/<uuid>/channels/<number>/number
                channelDescriptor.put("number", String.valueOf(i + 1));

                // device/<uuid>/channels/<number>/direction
                channelDescriptor.put("direction").from("input", "output").set("input");

                // device/<uuid>/channels/<number>/type
                channelDescriptor.put("type").from("toggle", "waveform", "pulse").set("toggle"); // TODO: switch

                // device/<uuid>/channels/<number>/descriptor
                Descriptor channelContentDescriptor = channelDescriptor.put("descriptor");

                // device/<uuid>/channels/<number>/descriptor/<observable>
                // TODO: Retreive the "from" values and the "default" value from the exposed observables on the actual hardware (or the hardware profile)
                channelContentDescriptor.put("toggle_value").from("on", "off").set("off");
                channelContentDescriptor.put("waveform_sample_value", "none");
                channelContentDescriptor.put("pulse_period_seconds", "0");
                channelContentDescriptor.put("pulse_duty_cycle", "0");
            }
            // </HACK>

            // Update restored device with information from device
            device.setInternetAddress(internetAddress);

            Log.v("TCP", "device.internetAddress: " + internetAddress);

            // Store the updated device profile.
            getStore().storeDevice(device);
            getStore().storeTimeline(device.getTimeline());

            Log.v("TCP", "device.internetAddress (2): " + internetAddress);

            // Add device to ClayaddMessage
            if (!this.devices.contains(device)) {

                // Add device to present (i.e., local cache).
                this.devices.add(device);
                Log.v("Content_Manager", "Successfully added timeline.");

//                ApplicationView.getDisplay().mapView.getSimulation().simulateFrame(new Frame());

                // Add timelines to attached displays
                for (DisplayHostInterface view : this.displays) {
                    view.addDeviceView(device);
                }
            }

            Log.v("TCP", "device.internetAddress (3): " + internetAddress);

            // Establish TCP connection
            device.connectTcp();

            Log.v("TCP", "device.internetAddress (4): " + internetAddress);

            /*
            // Reset the device
            if (isNew) {

                // <HACK>
                device.enqueueMessage("request reset");
                // getClay().getDeviceByUuid(UUID.fromString(sourceDeviceUuid)).enqueueMessage(propagatorMessage);
                // </HACK>

                isNew = false;
            }
            */

//            // Show the action button
//            ApplicationView.getDisplay().getCursorView().show(true);

            // Populate the device's timeline
            // TODO: Populate from scratch only if no timeline has been programmed for the device
            for (Event event : device.getTimeline().getEvents()) {
                // <HACK>
                device.enqueueMessage("start event " + event.getUuid());
                device.enqueueMessage("set event " + event.getUuid() + " action " + event.getAction().getScript().getUuid()); // <HACK />
                device.enqueueMessage("set event " + event.getUuid() + " descriptor \"" + event.getState().get(0).getState().toString() + "\"");
                // </HACK>
            }
        }

        return device;
    }

    public boolean hasFrame(UUID uuid) {
        for (Device device : getDevices()) {
            if (device.getUuid().compareTo(uuid) == 0) {
                return true;
            }
        }
        return false;
    }

    public Device getDeviceByUuid(UUID uuid) {
        for (Device device : getDevices()) {
            if (device.getUuid().compareTo(uuid) == 0) {
                return device;
            }
        }
        return null;
    }

    public boolean hasDeviceByAddress(String address) {
        /*
        for (Device device : getDevices()) {
            if (device.getInternetAddress().equals(address)) {
                return true;
            }
        }
        */
        return false;
    }

//    public void simulateSession (boolean addBehaviorToTimeline, int behaviorCount, boolean addAbstractBehaviorToTimeline) {
//        Log.v("Content_Manager", "simulateSession");
//
//        // Discover first device
//        UUID unitUuidA = UUID.fromString("403d4bd4-71b0-4c6b-acab-bd30c6548c71");
//        getClay().addFrame(unitUuidA, "10.1.10.29");
//        Device foundUnit = getDeviceByUuid(unitUuidA);
//
//        // Discover second device
//        UUID unitUuidB = UUID.fromString("903d4bd4-71b0-4c6b-acab-bd30c6548c78");
//        getClay().addFrame(unitUuidB, "192.168.1.123");
//
//        if (addBehaviorToTimeline) {
//            for (int i = 0; i < behaviorCount; i++) {
//                // Create action based on action script
//                Log.v("Content_Manager", "> Creating action");
//                Random r = new Random();
//                int selectedBehaviorIndex = r.nextInt(getClay().getCache().getActions().size());
////                Script selectedBehaviorScript = getClay().getCache().getScripts().get(selectedBehaviorIndex);
////                Action action = new Action(selectedBehaviorScript);
//                Action action = getClay().getCache().getActions().get(selectedBehaviorIndex);
//                getClay().getStore().storeAction(action);
//
//                // Create event for the action and add it to the unit's timeline
//                Log.v("Content_Manager", "> Device (UUID: " + foundUnit.getUuid() + ")");
//                Event event = new Event(foundUnit.getTimeline(), action);
//                getClay().getDeviceByUuid(unitUuidA).getTimeline().addEvent(event);
//                getClay().getStore().storeEvent(event);
//                // TODO: Update unit
//            }
//        }
//
//        if (addAbstractBehaviorToTimeline) {
//            // Create action based on action script
//            Log.v("Content_Manager", "> Creating action");
////            Action action = new Action("so high");
////            action.setDescription("oh yeah!");
////            action.addAction(foundUnit.getTimeline().getEvents().get(0).getAction());
////            action.addAction(foundUnit.getTimeline().getEvents().get(1).getAction());
////            getClay().getStore().storeAction(action);
//            ArrayList<Action> children = new ArrayList<Action>();
//            ArrayList<State> states = new ArrayList<State>();
//            children.add(foundUnit.getTimeline().getEvents().get(0).getAction());
//            states.addAll(foundUnit.getTimeline().getEvents().get(0).getState());
//            children.add(foundUnit.getTimeline().getEvents().get(1).getAction());
//            states.addAll(foundUnit.getTimeline().getEvents().get(1).getState());
//            Action action = getClay().getStore().getActionComposition(children);
//
//            // remove events for abstracted actions
//            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0));
//            foundUnit.getTimeline().getEvents().remove(0); // if storeHost action successful
//            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1));
//            foundUnit.getTimeline().getEvents().remove(1); // if storeHost action successful
//
//            // Create event for the action and add it to the unit's timeline
//            Log.v("Content_Manager", "> Device (UUID: " + foundUnit.getUuid() + ")");
//            Event event = new Event(foundUnit.getTimeline(), action);
//            // insert new event for abstract action
//            //            foundUnit.getTimeline().addEvent(event);
//            event.getState().erase();
//            event.getState().addAll(states);
//            Log.v("New_Behavior_Parent", "Added " + states.size() + " states to new event.");
//            for (State descriptor : event.getState()) {
//                Log.v("New_Behavior_Parent", "\t" + descriptor.getState());
//            }
//            foundUnit.getTimeline().getEvents().add(0, event); // if storeHost event was successful
//            getClay().getStore().storeEvent(event);
//            // TODO: Update unit
//        }
//
////        if (addAbstractBehaviorToTimeline) {
////            // Create behavior based on behavior script
////            Log.v("Content_Manager", "> Creating behavior");
////            Action behavior = new Action("so so high");
////            behavior.setDescription("oh yeah!");
////            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(0), null);
////            behavior.cacheAction(foundUnit.getTimeline().getEvents().get(0).getAction());
////            getClay().getStore().removeEvent(foundUnit.getTimeline().getEvents().get(1), null);
////            behavior.cacheAction(foundUnit.getTimeline().getEvents().get(1).getAction());
////            getClay().getStore().storeAction(behavior);
////            // remove events for abstracted actions
////            foundUnit.getTimeline().getEvents().remove(0); // if storeHost behavior successful
////            foundUnit.getTimeline().getEvents().remove(1); // if storeHost behavior successful
////
////            // Create event for the behavior and add it to the unit's timeline
////            Log.v("Content_Manager", "> Device (UUID: " + foundUnit.getUuid() + ")");
////            Event event = new Event(foundUnit.getTimeline(), behavior);
////            // insert new event for abstract behavior
////            //            foundUnit.getTimeline().addEvent(event);
////            foundUnit.getTimeline().getEvents().add(0, event); // if storeHost event was successful
////            getClay().getStore().storeEvent(event);
////            // TODO: Update unit
////        }
//
////        getClay().notifyChange(event);
//
//        getClay().getStore().writeDatabase();
//
//        for (Device unit : getClay().getDevices()) {
//            Log.v ("Content_Manager", "Device (UUID: " + unit.getUuid() + ")");
//            Log.v ("Content_Manager", "\tTimeline (UUID: " + unit.getTimeline().getUuid() + ")");
//
//            int tabCount = 3;
//            for (Event e : unit.getTimeline().getEvents()) {
//                Log.v ("Content_Manager", "\t\tEvent (UUID: " + e.getUuid() + ")");
//                // TODO: Recursively print out the behavior tree
//                printBehavior (e.getAction(), tabCount);
//            }
//        }
//    }

    /**
     * Returns true if Clay has a descriptor manager.
     *
     * @return True if Clay has a descriptor manager. False otherwise.
     */
    public boolean hasStore() {
        return this.storeHost != null;
    }

    private boolean hasCache() {
        return this.cache != null;
    }

    /**
     * Cycle through routine operations.
     */
    public void step() {
        messageHost.processMessage();
    }
}
