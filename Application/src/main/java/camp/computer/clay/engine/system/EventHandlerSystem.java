package camp.computer.clay.engine.system;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.Clay;
import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.Visibility2;
import camp.computer.clay.engine.World;

public class EventHandlerSystem extends System {

    // TODO: Rename to EventManager, ActionManager, or something like that.
    // TODO: Allow Entities to register for specific Events/Actions.

    private List<Event> incomingEvents = new ArrayList<>();

    public EventHandlerSystem() {
    }

    @Override
    public boolean update(World world) {

        // Dequeue actions and apply them to the targeted Entity
        while (incomingEvents.size() > 0) {
            Event event = dequeueEvent();
            dispatchEvent(event);
        }

        return true;
    }

    public void queueEvent(Event event) {
        incomingEvents.add(event);
    }

    private Event dequeueEvent() {
        if (incomingEvents.size() > 0) {
            return incomingEvents.remove(0);
        }
        return null;
    }

    private void dispatchEvent(Event event) {

        // Annotate the Event
        Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());
        Entity targetEntity = null;
        if (targetEntities.size() > 0) {
            targetEntity = targetEntities.get(0);
        } else {
            Group<Entity> cameras = Entity.Manager.filterWithComponent(Camera.class);
            targetEntity = cameras.get(0);
        }
        event.setTarget(targetEntity);

        // Handle special cases for MOVE and UNSELECT actions
        if (event.getType() == Event.Type.MOVE || event.getType() == Event.Type.UNSELECT) {
            targetEntity = event.getFirstEvent().getTarget();
        }

//        if (targetEntity.hasComponent(Label.class)) {
//            Log.v("EventHandlerSystem", "" + event.getType() + ": " + targetEntity.getComponent(Label.class).getLabel());
//        }

        // Dispatch the Event
        if (targetEntity.hasComponent(Camera.class)) {
            EventHandlerSystem.handleCameraEvent(targetEntity, event);
        } else if (targetEntity.hasComponent(Host.class)) {
            EventHandlerSystem.handleHostEvent(targetEntity, event);
        } else if (targetEntity.hasComponent(Extension.class)) {
            EventHandlerSystem.handleExtensionEvent(targetEntity, event);
        } else if (targetEntity.hasComponent(Port.class)) {
            EventHandlerSystem.handlePortEvent(targetEntity, event);
        } else if (targetEntity.hasComponent(Path.class)) {
            EventHandlerSystem.handlePathEvent(targetEntity, event);
        }
//        else if (targetEntity.hasComponent(Image.class)) { // TODO: HACK!
//
//            if (targetEntity.hasComponent(Label.class)) {
//                Log.v("EventHandlerSystem", "targetEntity: " + targetEntity.getComponent(Label.class).getLabel());
//            }
//
//        }
    }

    // TODO: Make World an Entity?
    public static void handleCameraEvent(final Entity workspace, Event event) {

        Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

//            if (action.isDragging()) {
            camera.getComponent(Camera.class).setOffset(event.getOffset());
//            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            /*
            // Previous Action targeted also this ExtensionEntity
            if (action.getPrevious() != null && action.getPrevious().getFirstEvent().getTargetImage().getEntity() == getEntity()) {

                if (action.isTap()) {
                    // Title
                    setTitleText("Project");
                    setTitleVisibility(Visibility2.VISIBLE);
                }

            } else {
            */

            // NOT a repeat tap on this Image

//            if (action.isTap()) {

            // Title
            // TODO: workspace.setTitleVisibility(Visibility2.INVISIBLE);

            // Camera
            camera.getComponent(Camera.class).setFocus(World.getWorld());
//            }

        }
    }

    public static void handleHostEvent(final Entity host, final Event event) {

        final Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

//            if (action.isDragging()) {

            // Update position of prototype Extension
            World.getWorld().portableLayoutSystem.setExtensionPrototypePosition(event.getPosition());

//                    hostEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility2.INVISIBLE);
//                hostEntity.getComponent(Portable.class).setPathVisibility(false);
            host.getComponent(Portable.class).getPaths().setVisibility(false);

//            World.getWorld().setExtensionPrototypeVisibility2(Visibility2.VISIBLE);
            Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
            extensionPrototype.getComponent(Visibility.class).isVisible = true;

//            } else if (action.isHolding()) {
//
//                // Update Position of Host
//                host.getComponent(Transform.class).set(event.getPosition());
//
//                // Camera
//                camera.getComponent(Camera.class).setFocus(host);
//
////            }

        } else if (event.getType() == Event.Type.UNSELECT) {

//            if (action.isTap()) {

            // Focus on touched Host
            host.getComponent(Portable.class).getPaths().setVisibility(true);
            host.getComponent(Portable.class).getPorts().setVisibility(true);

            host.getComponent(Image.class).setTransparency(1.0);

            // Show Ports and Paths of touched Host
            for (int i = 0; i < host.getComponent(Portable.class).getPorts().size(); i++) {
                Group<Entity> pathEntities = host.getComponent(Portable.class).getPort(i).getComponent(Port.class).getPaths();

                for (int j = 0; j < pathEntities.size(); j++) {
                    Entity pathEntity = pathEntities.get(j);

                    // Show source and target Ports in Paths
                    pathEntity.getComponent(Path.class).getSource().getComponent(Visibility.class).isVisible = true;
                    pathEntity.getComponent(Path.class).getTarget().getComponent(Visibility.class).isVisible = true;

                    // Show Path connection
                    pathEntity.getComponent(Visibility.class).isVisible = true;
                }
            }

            // Camera
            camera.getComponent(Camera.class).setFocus(host);

            if (host.getComponent(Portable.class).getExtensions().size() > 0) {
                /*
                host.getComponent(Portable.class).getExtensions().setTransparency(0.1);
                */

                // <HACK>
                // TODO: Move this into PortableLayoutSystem
                // TODO: Replace ASAP. This is shit.
                // TODO: Use "rectangle" or "circular" extension layout algorithms
                PortableLayoutSystem.setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);
                // </HACK>
            }

            // Title
            World.getWorld().setTitleText("Host");
            World.getWorld().setTitleVisibility(Visibility2.VISIBLE);

//            } else {
//
//                // TODO: Release longer than tap!
//
//                if (event.getTarget().hasComponent(Host.class)) {
//
//                    // If getFirstEvent queueEvent was on the same form, then respond
////                                if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage().getEntity() instanceof HostEntity) {
//                    if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTarget().hasComponent(Host.class)) {
//
//                        // HostEntity
////                                    event.getTargetImage().queueEvent(action);
//
//                        // CameraEntity
////                                    cameraEntity.setFocus();
//                    }
//
//                }
////                else if (event.getTargetImage() instanceof World) {
////
////                    // HostEntity
//////                                action.getFirstEvent().getTargetImage().queueEvent(action);
////
////                }
//            }

            // Check if connecting to a Extension
//            if (World.getWorld().getExtensionPrototypeVisibility2() == Visibility2.VISIBLE) {
            Entity prototypeExtension = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
            if (prototypeExtension.getComponent(Visibility.class).isVisible) {

//                World.getWorld().setExtensionPrototypeVisibility2(Visibility2.INVISIBLE);
                Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                extensionPrototype.getComponent(Visibility.class).isVisible = false;

                // Get cached extension profiles (and retrieve additional from Internet store)
                List<Profile> profiles = Application.getView().getClay().getProfiles();

                if (profiles.size() == 0) {

                    // Show "default" DIY extension builder (or info about there being no headerExtensions)

                } else if (profiles.size() > 0) {

                    // Prompt User to select an ExtensionEntity from the Store
                    // i.e., Prompt to select extension to use! Then use that profile to create and configure ports for the extension.
                    Application.getView().getActionPrompts().promptSelection(profiles, new Prompt.OnActionListener<Profile>() {
                        @Override
                        public void onComplete(Profile profile) {

                            // Add Extension from Profile
                            Entity extension = PortableLayoutSystem.restoreExtension(host, profile, event.getPosition());

                            // Camera
                            camera.getComponent(Camera.class).setFocus(extension);
                        }
                    });

                    // Application.getPlatform().promptTasks();
                }
            }

//            }
        }
    }

    public static void handleExtensionEvent(final Entity extension, Event event) {

        final Image extensionImage = extension.getComponent(Image.class);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

            Clay.createExtensionProfile(extension);

        } else if (event.getType() == Event.Type.MOVE) {

        } else if (event.getType() == Event.Type.UNSELECT) {

            // Previous Action targeted also this Extension
            // TODO: Refactor
            /*
            if (action.getPrevious().getFirstEvent().getTarget() == extensionImage.getEntity()) {

                if (action.isTap()) {
                    // TODO: Replace with script editor/timeline
                    Application.getView().openActionEditor(extensionImage.getEntity());
                }

            } else {
            */

//            if (action.isTap()) {

            // Focus on selected Host
            extension.getComponent(Portable.class).getPaths().setVisibility(true);
            extension.getComponent(Portable.class).getPorts().setVisibility(true);
            extensionImage.setTransparency(1.0);

            // Show Ports and Paths for selected Host
            for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {
                Entity portEntity = extension.getComponent(Portable.class).getPorts().get(i);

                Group<Entity> paths = portEntity.getComponent(Port.class).getPaths();
                for (int j = 0; j < paths.size(); j++) {
                    Entity path = paths.get(j);

                    // Show Ports
                    Entity sourcePort = path.getComponent(Path.class).getSource();
                    Entity targetPort = path.getComponent(Path.class).getTarget();
                    sourcePort.getComponent(Visibility.class).isVisible = true;
                    targetPort.getComponent(Visibility.class).isVisible = true;


                    // Show Path
                    path.getComponent(Visibility.class).isVisible = true;
                }
            }
            // TODO: Replace above with?: portEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility2.VISIBLE);

            // Title
            World.getWorld().setTitleText("Extension");
            World.getWorld().setTitleVisibility(Visibility2.VISIBLE);

            // Camera
            Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);
            camera.getComponent(Camera.class).setFocus(extensionImage.getEntity());
        }
//        }
//        }
    }

    public static void handlePortEvent(final Entity port, Event event) {

        final Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

            // if (action.isDragging()) {

            // Prototype Path Visibility2
            // TODO: World.getWorld().setPathPrototypeSourcePosition(action.getFirstEvent().getTarget().getComponent(Transform.class));
            World.getWorld().portableLayoutSystem.setPathPrototypeSourcePosition(event.getFirstEvent().getTarget().getComponent(Image.class).getShape("Port").getPosition());
            World.getWorld().portableLayoutSystem.setPathPrototypeDestinationPosition(event.getPosition());
            World.getWorld().portableLayoutSystem.setPathPrototypeVisibility2(Visibility2.VISIBLE);

            // Prototype Extension Visibility2
            boolean isCreateExtensionAction = true; // TODO: Convert into Event to send to World?

            // <HACK>
            // Group<Entity> entities = Entity.Manager.filterWithComponent(Host.class, Extension.class);
            Group<Entity> extensions = Entity.Manager.filterWithComponent(Extension.class);

            for (int i = 0; i < extensions.size(); i++) {

                // Update style of nearby Hosts
                double distanceToHost = Geometry.distance(
                        event.getPosition(),
                        extensions.get(i).getComponent(Transform.class)
                );

                if (distanceToHost < 375) { // 375, 500
                    isCreateExtensionAction = false;
                    break;
                }

                // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
            }

            // Update position of prototype Path and Extension
            if (isCreateExtensionAction) {
//                World.getWorld().setExtensionPrototypeVisibility2(Visibility2.VISIBLE);
                Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                extensionPrototype.getComponent(Visibility.class).isVisible = true;
                // TODO: World.getWorld().setPathPrototypeSourcePosition(action.getFirstEvent().getTarget().getComponent(Transform.class));
                World.getWorld().portableLayoutSystem.setPathPrototypeSourcePosition(event.getFirstEvent().getTarget().getComponent(Image.class).getShape("Port").getPosition());
                World.getWorld().portableLayoutSystem.setExtensionPrototypePosition(event.getPosition());
            } else {
//                World.getWorld().setExtensionPrototypeVisibility2(Visibility2.INVISIBLE);
                Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                extensionPrototype.getComponent(Visibility.class).isVisible = false;
            }

            // Show Ports of nearby Hosts and Extensions
            Entity sourcePort = event.getFirstEvent().getTarget();

            // Show Ports of nearby Hosts and Extensions
            Group<Entity> nearbyExtensions = extensions.filterArea(event.getPosition(), World.NEARBY_RADIUS_THRESHOLD);

            for (int i = 0; i < extensions.size(); i++) {
                Entity extension = extensions.get(i);

                if (extension == sourcePort.getParent() || nearbyExtensions.contains(extension)) {

                    // <HACK>
                    // <STYLE>
                    extension.getComponent(Image.class).setTransparency(1.0f);
                    extension.getComponent(Portable.class).getPorts().setVisibility(true);
                    // </STYLE>

                    if (extension.hasComponent(Extension.class)) { // HACK
                        if (!extension.getComponent(Extension.class).isPersistent()) {

                            // Determine if a new Port is required on the custom Extension
                            boolean addNewPort = true;
                            for (int j = 0; j < extension.getComponent(Portable.class).getPorts().size(); j++) {
                                Entity existingPortEntity = extension.getComponent(Portable.class).getPorts().get(j);
                                if (existingPortEntity.getComponent(Port.class).getType() == Port.Type.NONE) {
                                    addNewPort = false;
                                    break;
                                }
                            }

                            // Add new Port to the Extension (if determined necessary)
                            if (addNewPort) {
                                Entity newPort = World.createEntity(Port.class);
                                int newPortIndex = extension.getComponent(Portable.class).getPorts().size();
                                newPort.getComponent(Port.class).setIndex(newPortIndex);
                                extension.getComponent(Portable.class).addPort(newPort);
                            }
                        }
                    }
                    // </HACK>

                } else {

                    /*
                    // TODO: Remove this if it's not needed... probably isn't!
                    // Make Portable transparent and hide its Ports
                    extension.getComponent(Image.class).setTransparency(0.1f);
                    extension.getComponent(Portable.class).getPorts().setVisibility(false);
                    */

                }
            }

            // Camera
            camera.getComponent(Camera.class).setFocus(sourcePort, event.getPosition());

//            } else if (action.isHolding()) {
//
//                // Holding and dragging
//
//            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            if (event.getTarget() != null && event.getTarget().hasComponent(Port.class)) {

                // (Host.Port, ..., Host.Port) Action Pattern

                // TODO: Delete stuff here that was previously used for connecting Hosts' ports

                if (event.getFirstEvent().getTarget() == event.getTarget()) { // if (action.isTap()) {

                    // (Host.Port A, ..., Host.Port A) Action Pattern
                    // i.e., The action's first and last events address the same Port. Therefore, it must be either a tap or a hold.

                    // Get Port associated with the touched Port
                    Entity sourcePort = event.getFirstEvent().getTarget();

                    Port sourcePortComponent = sourcePort.getComponent(Port.class);

                    if (sourcePortComponent.getExtension() == null || !sourcePortComponent.getExtension().getComponent(Extension.class).isPersistent()) {

                        if (sourcePortComponent.getType() == Port.Type.NONE) {

                            // Set initial Port Type
                            sourcePortComponent.setDirection(Port.Direction.INPUT);
                            sourcePortComponent.setType(Port.Type.getNext(sourcePortComponent.getType()));

                        } else if (!sourcePortComponent.hasPath()) {

                            // Change Port Type
                            Port.Type nextType = sourcePortComponent.getType();
                            while ((nextType == Port.Type.NONE) || (nextType == sourcePortComponent.getType())) {
                                nextType = Port.Type.getNext(nextType);
                            }
                            sourcePortComponent.setType(nextType);

                        } else {

                            // Touching a Port changes its type. This will also updates the
                            // corresponding Path's requirement (for Port types).

                            // Cycle to getNext Path Type
                            // (Below: Updates each Port in the Path, to reflect this change.)
                            Port.Type nextType = sourcePortComponent.getType();
                            while ((nextType == Port.Type.NONE) || (nextType == sourcePortComponent.getType())) {
                                nextType = Port.Type.getNext(nextType);
                            }

                            // Update each Port in the Path to reflect the new Port type
                            // <TODO: MAKE FILTER>
                            // TODO: Make Filter/Editor to pass to Group.filter(Filter) or Group.filter(Editor)
                            Group<Entity> paths = sourcePortComponent.getPaths();
                            for (int i = 0; i < paths.size(); i++) {
                                Entity path = paths.get(i);
                                Group<Entity> ports = path.getComponent(Path.class).getPorts();
                                for (int j = 0; j < ports.size(); j++) {
                                    ports.get(j).getComponent(Port.class).setType(nextType);
                                }
                            }
                            // </TODO: MAKE FILTER>

                        }

                        World.getWorld().portableLayoutSystem.setPathPrototypeVisibility2(Visibility2.INVISIBLE);
                    }

                } else if (event.getFirstEvent().getTarget() != event.getTarget()) {

                    // (Host.Port A, ..., Host.Port B) Action Pattern
                    // i.e., The Action's first and last Events address different Ports.

//                    if (action.isDragging()) {
                    // Hide the prototype Path
                    World.getWorld().portableLayoutSystem.setPathPrototypeVisibility2(Visibility2.INVISIBLE);

                    // Get the source and target Ports to be used in new Path
                    Entity sourcePort = event.getFirstEvent().getTarget();
                    Entity targetPort = event.getTarget();

                    // Create Path and configure it
                    Entity path = World.createEntity(Path.class);
                    path.getComponent(Path.class).set(sourcePort, targetPort);

                    // Focus Camera on Extension
                    Entity extension = path.getComponent(Path.class).getExtension();
                    camera.getComponent(Camera.class).setFocus(extension);
//                    }

                }

                //} else if (event.getTarget().hasComponent(Camera.class)) {
            } else {

                Log.v("EventHandlerSystem", "UNSELECT Port");
                if (event.getTarget().hasComponent(Label.class)) {
                    Log.v("EventHandlerSystem", "targetEntity: " + event.getTarget().getComponent(Label.class).getLabel());
                }

                // (Host.Port, ..., World) Action Pattern

                // Hide prototype Path and prototype Extension
                World.getWorld().portableLayoutSystem.setPathPrototypeVisibility2(Visibility2.INVISIBLE);

                Log.v("EventHandlerSystem", "creating extension");

                // If prototype Extension is visible, create Extension
//                if (World.getWorld().getExtensionPrototypeVisibility2() == Visibility2.VISIBLE) {
                Entity prototypeExtension = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                if (prototypeExtension.getComponent(Visibility.class).isVisible) {

                    Log.v("EventHandlerSystem", "creating extension");

//                    // Hide prototype Path and prototype Extension
//                    World.getWorld().setPathPrototypeVisibility2(Visibility2.INVISIBLE);
//                    World.getWorld().setExtensionPrototypeVisibility2(Visibility2.INVISIBLE);
                    Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    extensionPrototype.getComponent(Visibility.class).isVisible = false;

                    Entity hostPort = event.getFirstEvent().getTarget();

                    // Create new custom Extension. Custom Extension can be configured manually.
                    PortableLayoutSystem.createExtension(hostPort, event.getPosition());

                    // <STYLE_AND_LAYOUT>
                    // Remove focus from other Hosts and their Ports
//                    Group<Entity> hosts = Entity.Manager.filterWithComponent(Host.class);
//                    for (int i = 0; i < hosts.size(); i++) {
//                        Entity host = hosts.get(i);
//                        /*
//                        host.getComponent(Image.class).setTransparency(0.05f);
//                        */
//                        host.getComponent(Portable.class).getPorts().setVisibility(false);
//                        host.getComponent(Portable.class).getPaths().setVisibility(false);
//                    }

                    // Get all Ports in all Paths from the Host
                    Group<Entity> hostPaths = hostPort.getComponent(Port.class).getPaths();
                    Group<Entity> hostPorts = new Group<>();
                    for (int i = 0; i < hostPaths.size(); i++) {
                        Group<Entity> pathPorts = hostPaths.get(i).getComponent(Path.class).getPorts();
                        hostPorts.addAll(pathPorts);
                    }

                    // Show all of Host's Paths and all Ports contained in those Paths
                    hostPaths.setVisibility(true);
                    hostPorts.setVisibility(true);

                    // Update layout
                    Entity host = hostPort.getParent(); // HACK

                    PortableLayoutSystem.setPortableSeparation(World.HOST_TO_EXTENSION_LONG_DISTANCE);

                    PortableLayoutSystem.updateExtensionLayout(host);
                    // <STYLE_AND_LAYOUT>

                    // Set Camera focus on the Extension
                    // camera.setFocus(extension);
                }
            }
        }
    }

    public static void handlePathEvent(final Entity path, Event event) {

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

        } else if (event.getType() == Event.Type.UNSELECT) {

        }
    }
}
