package camp.computer.clay.engine.system;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.Clay;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.controls.NativeUi;
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
import camp.computer.clay.engine.Event;
import camp.computer.clay.util.BuilderImage.Geometry;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.World;
import camp.computer.clay.util.BuilderImage.Shape;

public class EventHandlerSystem extends System {

    // TODO: Rename to EventManager, ActionManager, or something like that.
    // TODO: Allow Entities to register for specific Events/Actions.

    private List<Event> incomingEvents = new ArrayList<>();

    public EventHandlerSystem(World world) {
        super(world);
    }

    @Override
    public void update() {

        // Dequeue actions and apply them to the targeted Entity
        while (incomingEvents.size() > 0) {
            Event event = dequeueEvent();
            dispatchEvent(event);
        }
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

    public Entity previousTarget = null;

    private void dispatchEvent(Event event) {

        // Annotate the Event
//        Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());
        Group<Entity> targetEntities = Entity.Manager.filterVisibility(true).filterWithComponent(Image.class).sortByLayer().filterContains(event.getPosition());
        Entity targetEntity = null;
        if (targetEntities.size() > 0) {
            //targetEntity = targetEntities.get(0);
            targetEntity = targetEntities.get(targetEntities.size() - 1);
            Log.v("handlePathEvent", "targetEntities.size: " + targetEntities.size());
        } else {
            Group<Entity> cameras = Entity.Manager.filterWithComponent(Camera.class);
            targetEntity = cameras.get(0);
        }
        event.setTarget(targetEntity);

        // Handle special cases for MOVE and UNSELECT actions
//        if (event.getType() == Event.Type.MOVE || event.getType() == Event.Type.UNSELECT) {
        if (event.getType() != Event.Type.SELECT) {
            targetEntity = event.getFirstEvent().getTarget();
            // TODO: 11/6/2016 event.setTarget(targetEntity);
            event.setIntentTarget(targetEntity);
        } else {
            event.setIntentTarget(event.getFirstEvent().getIntentTarget());
        }

//        if (event.getFirstEvent() == null) {
//            event.setTarget(targetEntities.get(0));
//            targetEntity = targetEntities.get(0);
//        } else {
//            event.setTarget(event.getFirstEvent().getTarget());
//            targetEntity = event.getFirstEvent().getTarget();
//        }

        // Dispatch the Event
        Entity targetIntentEntity = event.getIntentTarget();
        if (targetIntentEntity != null) {
            if (targetIntentEntity.hasComponent(Camera.class)) {
                world.eventHandlerSystem.handleCameraEvent(targetIntentEntity, event);
            } else if (targetIntentEntity.hasComponent(Host.class)) {
                world.eventHandlerSystem.handleHostEvent(targetIntentEntity, event);
            } else if (targetIntentEntity.hasComponent(Extension.class)) {
                world.eventHandlerSystem.handleExtensionEvent(targetIntentEntity, event);
            } else if (targetIntentEntity.hasComponent(Port.class)) {
                world.eventHandlerSystem.handlePortEvent(targetIntentEntity, event);
            } else if (targetIntentEntity.hasComponent(Path.class)) {
                world.eventHandlerSystem.handlePathEvent(targetIntentEntity, event);
            }
//        else if (targetEntity.hasComponent(Image.class)) { // TODO: HACK!
//
//            if (targetEntity.hasComponent(Label.class)) {
//                Log.v("EventHandlerSystem", "targetEntity: " + targetEntity.getComponent(Label.class).getLabel());
//            }
//
//        }
        }

        // Handle special bookkeeping storing previous target Entity
        if (event.getType() == Event.Type.UNSELECT) {
            previousTarget = event.getTarget();
        }
    }

    // TODO: Make World an Entity?
    public void handleCameraEvent(final Entity workspace, Event event) {

        Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

//            if (action.isDragging()) {
//            camera.getComponent(Camera.class).setOffset(event.getOffset());
            world.cameraSystem.setOffset(camera, event.getOffset());
//            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            /*
            // Previous Action targeted also this ExtensionEntity
            if (action.getPrevious() != null && action.getPrevious().getFirstEvent().getTargetImage().getEntity() == getEntity()) {

                if (action.isTap()) {
                    // Title
                    setTitleText("Project");
                    setTitleVisibility(Visible.VISIBLE);
                }

            } else {
            */

            // NOT a repeat tap on this Image

//            if (action.isTap()) {

            // Title
            // TODO: workspace.setTitleVisibility(Visible.INVISIBLE);

            // Camera
            world.cameraSystem.setFocus(camera, world);
//            }

        }
    }

    public void handleHostEvent(final Entity host, final Event event) {

        final Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

//            if (action.isDragging()) {

            // Update position of prototype Extension
            world.portableLayoutSystem.setExtensionPrototypePosition(event.getPosition());

//                    hostEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visible.INVISIBLE);
//                hostEntity.getComponent(Portable.class).setPathVisibility(false);
            host.getComponent(Portable.class).getPaths().setVisibility(Visible.INVISIBLE);

//            world.setExtensionPrototypeVisibility2(Visible.VISIBLE);
            Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
            extensionPrototype.getComponent(Visibility.class).setVisible(Visible.VISIBLE);

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
            host.getComponent(Portable.class).getPaths().setVisibility(Visible.VISIBLE);
            host.getComponent(Portable.class).getPorts().setVisibility(Visible.VISIBLE);

            world.imageSystem.setTransparency(host.getComponent(Image.class), 1.0);

            // Show Ports and Paths of touched Host
            for (int i = 0; i < host.getComponent(Portable.class).getPorts().size(); i++) {
                Group<Entity> pathEntities = host.getComponent(Portable.class).getPort(i).getComponent(Port.class).getPaths();

                for (int j = 0; j < pathEntities.size(); j++) {
                    Entity pathEntity = pathEntities.get(j);

                    // Show source and target Ports in Paths
//                    pathEntity.getComponent(Path.class).getSource().getComponent(Visibility.class).setVisible(Visible.VISIBLE);
//                    pathEntity.getComponent(Path.class).getTarget().getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                    pathEntity.getComponent(Path.class).getPorts().setVisibility(Visible.VISIBLE);

                    // Show Path connection
                    pathEntity.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                }
            }

            // Camera
//            camera.getComponent(Camera.class).setFocus(host);
            world.cameraSystem.setFocus(camera, host);

            if (host.getComponent(Portable.class).getExtensions().size() > 0) {
                /*
                host.getComponent(Portable.class).getExtensions().setTransparency(0.1);
                */

                // <HACK>
                // TODO: Move this into PortableLayoutSystem
                // TODO: Replace ASAP. This is shit.
                // TODO: Use "rectangle" or "circular" extension layout algorithms
                world.portableLayoutSystem.setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);
                // </HACK>
            }

            // Title
            world.setTitleText("Host");
            world.setTitleVisibility(Visible.VISIBLE);

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
//            if (world.getExtensionPrototypeVisibility2() == Visible.VISIBLE) {
            Entity prototypeExtension = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
            if (prototypeExtension.getComponent(Visibility.class).getVisibile() == Visible.VISIBLE) {

//                world.setExtensionPrototypeVisibility2(Visible.INVISIBLE);
                Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

                // Get cached extension configurations (and retrieve additional from Internet store)
                List<Configuration> configurations = Application.getView().getClay().getConfigurations();

                if (configurations.size() == 0) {

                    // Show "default" DIY extension builder (or info about there being no headerExtensions)

                } else if (configurations.size() > 0) {

                    // NativeUi Player to select an ExtensionEntity from the Store
                    // i.e., NativeUi to select extension to use! Then use that profile to create and configure ports for the extension.
                    Application.getView().getNativeUi().promptSelection(configurations, new NativeUi.OnActionListener<Configuration>() {
                        @Override
                        public void onComplete(Configuration configuration) {

                            // Add Extension from Configuration
                            Entity extension = world.portableLayoutSystem.createExtensionFromProfile(host, configuration, event.getPosition());

                            // Camera
//                            camera.getComponent(Camera.class).setFocus(extension);
                            world.cameraSystem.setFocus(camera, extension);
                        }
                    });

                    // Application.getPlatform().promptTasks();
                }
            }

//            }
        }
    }

    public void handleExtensionEvent(final Entity extension, Event event) {

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

            Clay.createExtensionProfile(extension);

        } else if (event.getType() == Event.Type.MOVE) {

        } else if (event.getType() == Event.Type.UNSELECT) {

            // Previous Action targeted also this Extension
            // TODO: Refactor

            Log.v("EventHandlerSystem", "target: " + event.getTarget());
            Log.v("EventHandlerSystem", "previousTarget: " + world.eventHandlerSystem.previousTarget);
            Log.v("EventHandlerSystem", "---");
            if (world.eventHandlerSystem.previousTarget == extension) {
//                Application.getView().getNativeUi().OLD_openActionEditor(extension);
                Application.getView().getNativeUi().openActionEditor(extension);
            }

//            if (action.isTap()) {

            // Focus on selected Host
            extension.getComponent(Portable.class).getPaths().setVisibility(Visible.VISIBLE);
            extension.getComponent(Portable.class).getPorts().setVisibility(Visible.VISIBLE);
            world.imageSystem.setTransparency(extension.getComponent(Image.class), 1.0);

            // Show Ports and Paths for selected Host
            for (int i = 0; i < extension.getComponent(Portable.class).getPorts().size(); i++) {
                Entity portEntity = extension.getComponent(Portable.class).getPorts().get(i);

                Group<Entity> paths = portEntity.getComponent(Port.class).getPaths();
                for (int j = 0; j < paths.size(); j++) {
                    Entity path = paths.get(j);

                    // Show Ports
                    Entity sourcePort = path.getComponent(Path.class).getSource();
                    Entity targetPort = path.getComponent(Path.class).getTarget();
                    sourcePort.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                    targetPort.getComponent(Visibility.class).setVisible(Visible.VISIBLE);


                    // Show Path
                    path.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                }
            }
            // TODO: Replace above with?: portEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visible.VISIBLE);

            // Title
            world.setTitleText("Extension");
            world.setTitleVisibility(Visible.VISIBLE);

            // Camera
            Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);
//            camera.getComponent(Camera.class).setFocus(extension);
            world.cameraSystem.setFocus(camera, extension);
        }
//        }
//        }
    }

    public void handlePortEvent(final Entity port, Event event) {

        Log.v("EventHandlerSystem", "handlePortEvent");

        final Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

        } else if (event.getType() == Event.Type.UNSELECT) {

            // TODO? Group<Entity> entitiesAtEventPosition = Entity.Manager.filterVisibility(true).filterContains(event.getPosition());

//            if (event.getTarget() != null && event.getTarget().hasComponent(Port.class)) {

            // (Host.Port, ..., Host.Port) Action Pattern

            if (event.isTap() && event.getTarget() == port) {

                // (Host.Port A, ..., Host.Port A) Action Pattern
                // i.e., The action's first and last events address the same Port. Therefore, it must be either a tap or a hold.

                // Get Port associated with the touched Port
                Entity sourcePort = event.getFirstEvent().getTarget();

                // Check if the target Port is contained in any Path.
                boolean portHasPath = false;
                Entity firstPort = event.getFirstEvent().getTarget();
                if (firstPort.getComponent(Port.class).getPaths().size() > 0) {
                    portHasPath = true;
                }

                // Create new singleton Path, enabling the Port to be connected to other Ports.
                if (!portHasPath) {
                    Entity singletonPath = world.createEntity(Path.class);
                    singletonPath.getComponent(Path.class).setSource(sourcePort);
                    singletonPath.getComponent(Path.class).setType(Path.Type.SWITCH);
                }

            }
//            }
        }
    }

    public void handlePathEvent(final Entity path, Event event) {

//        Log.v("handlePathEvent", "handlePathEvent");

        boolean isSingletonPath = path.getComponent(Path.class).getTarget() == null;

        // Check if source or target in Path was moved, and reassign it
        Group<Entity> touchedPorts = Entity.Manager.filterWithComponent(Port.class).filterContains(event.getPosition());

        Entity touchedPort = null;
        boolean isSourceTouched = false;
        boolean isTargetTouched = false;
        if (touchedPorts.size() > 0) {
            touchedPort = touchedPorts.get(0); // NOTE: This gets the first port in the list, no matter how many there are or which Ports they are. Maybe not always work...
        }

        Shape sourcePortShape = path.getComponent(Image.class).getImage().getShape("Source Port");
        Shape targetPortShape = path.getComponent(Image.class).getImage().getShape("Target Port");
        if (Geometry.contains(sourcePortShape.getBoundary(), event.getPosition())) {
//            path.getComponent(Path.class).setSource(touchedPort);
            isSourceTouched = true;
        } else if (Geometry.contains(targetPortShape.getBoundary(), event.getPosition())) {
//            path.getComponent(Path.class).setTarget(touchedPort);
            isTargetTouched = true;
        }

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

            if (!isSingletonPath) {

                // Multi-Port Path (non-singleton)

                Log.v("PathEvent", "Moving path.");

                path.getComponent(Image.class).getImage().getShapes();

//                Shape sourcePortShape2 = path.getComponent(Image.class).getImage().getShape("Source Port");
                if (Geometry.contains(sourcePortShape.getBoundary(), event.getPosition())) {
                    Log.v("PathEvent", "Touched Source");
                    sourcePortShape.setPosition(event.getPosition());

                    path.getComponent(Path.class).state = Path.State.EDITING;

                    // <HACK>
                    BoundarySystem.updateShapeBoundary(sourcePortShape);
                    // </HACK>
                }

//                Shape targetPortShape2 = path.getComponent(Image.class).getImage().getShape("Target Port");
                if (Geometry.contains(targetPortShape.getBoundary(), event.getPosition())) {
                    Log.v("PathEvent", "Touched Target");
                    targetPortShape.setPosition(event.getPosition());

                    path.getComponent(Path.class).state = Path.State.EDITING;

                    // <HACK>
                    BoundarySystem.updateShapeBoundary(targetPortShape);
                    // </HACK>
                }
//            world.boundarySystem.updateImage(path);

            } else {

                // Singleton Path

                Log.v("PathEventHandler", "Moving on singleton Path.");

                // Prototype Path Visible
                Transform position = event.getFirstEvent().getTarget().getComponent(Image.class).getImage().getShape("Source Port").getPosition();
                world.portableLayoutSystem.setPathPrototypeSourcePosition(position);
                world.portableLayoutSystem.setPathPrototypeDestinationPosition(event.getPosition());
                world.portableLayoutSystem.setPathPrototypeVisibility(Visible.VISIBLE);

                // Prototype Extension Visible
                boolean isCreateExtensionAction = true; // TODO: Convert into Event to send to World?

                // <HACK>
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
//                world.setExtensionPrototypeVisibility2(Visible.VISIBLE);
                    Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    extensionPrototype.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                    // TODO: world.setPathPrototypeSourcePosition(action.getFirstEvent().getTarget().getComponent(Transform.class));
                    world.portableLayoutSystem.setPathPrototypeSourcePosition(event.getFirstEvent().getTarget().getComponent(Image.class).getImage().getShape("Source Port").getPosition());
                    world.portableLayoutSystem.setExtensionPrototypePosition(event.getPosition());
                } else {
//                world.setExtensionPrototypeVisibility2(Visible.INVISIBLE);
                    Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);
                }

                // Ports of nearby Hosts and Extensions
                Entity sourcePort = event.getFirstEvent().getTarget();
                Group<Entity> nearbyExtensions = extensions.filterArea(event.getPosition(), World.NEARBY_RADIUS_THRESHOLD);

                for (int i = 0; i < extensions.size(); i++) {
                    Entity extension = extensions.get(i);

                    if (extension == sourcePort.getParent() || nearbyExtensions.contains(extension)) {

                        // <STYLE>
                        // world.imageSystem.setTransparency(extension.getComponent(Image.class), 1.0);
                        extension.getComponent(Portable.class).getPorts().setVisibility(Visible.VISIBLE);
                        // </STYLE>

                        if (!extension.getComponent(Extension.class).isPersistent()) {

                            // Determine if a new Port is required on the custom Extension
                            boolean addNewPort = true;
                            for (int j = 0; j < extension.getComponent(Portable.class).getPorts().size(); j++) {
                                Entity existingPort = extension.getComponent(Portable.class).getPorts().get(j);
                                if (existingPort.getComponent(Port.class).getType() == Port.Type.NONE) {
                                    addNewPort = false;
                                    break;
                                }
                            }
                            Log.v("PathEventHandler", "addNewPort: " + addNewPort);

                            // Add new Port to the Extension (if determined necessary)
                            if (addNewPort) {
                                Entity newPort = World.createEntity(Port.class);

                                // <HACK>
                                newPort.addComponent(new RelativeLayoutConstraint());
                                newPort.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(extension);
                                // </HACK>

                                int newPortIndex = extension.getComponent(Portable.class).getPorts().size();
                                newPort.getComponent(Port.class).setIndex(newPortIndex);
                                extension.getComponent(Portable.class).addPort(newPort);
                            }
                        }

                    }
                }

                // Camera
//                world.cameraSystem.setFocus(camera, sourcePort, event.getPosition());
            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            Log.v("PathEvent", "UNSELECT PATH.");

            if (path.getComponent(Path.class).getTarget() != null) {

                // Full Path (non-singleton Path)

                if (path.getComponent(Path.class).state != Path.State.EDITING) {
                    // <PATH>
                    // Set next Path type
                    Path pathComponent = path.getComponent(Path.class);
                    Path.Type nextType = Path.Type.getNext(pathComponent.getType());
                    while ((nextType == Path.Type.NONE) || (nextType == pathComponent.getType())) {
                        nextType = Path.Type.getNext(nextType);
                    }
                    path.getComponent(Path.class).setType(nextType);
//                Log.v("EventHandlerSystem", "Setting path type to: " + nextType);
                    // <PATH>

                    // TODO: Update the Port Type in the Path to Match the Path Type
                    Group<Entity> ports = path.getComponent(Path.class).getPorts();

                    // Notification
                    World.getWorld().renderSystem.addNotification("" + nextType, event.getPosition(), 800);

                } else if (path.getComponent(Path.class).state == Path.State.EDITING) {

                    Group<Entity> touchedPorts2 = Entity.Manager.filterWithComponent(Port.class).filterContains(event.getPosition());

                    // Moved the Path to another Port
                    if (touchedPorts2.size() > 0) {

                        Entity touchedPort2 = touchedPorts2.get(0); // NOTE: This gets the first port in the list, no matter how many there are or which Ports they are. Maybe not always work...

                        // Remap the Path's Port if the touched Port doesn't already have a Path
                        Group<Entity> targetPaths = touchedPort2.getComponent(Port.class).getPaths();
                        if (targetPaths.size() > 0) {

                            if (targetPaths.get(0) == path) {
                                // Swap the Path's Ports (Flip Direction)
                                Log.v("PathEventHandler", "flipping the path");
                                Entity sourcePort = path.getComponent(Path.class).getSource();
                                path.getComponent(Path.class).setSource(path.getComponent(Path.class).getTarget());
                                path.getComponent(Path.class).setSource(sourcePort);

                                // TODO: path.getComponent(Path.class).setDirection();
                            }

                            // Notification
                            World.getWorld().renderSystem.addNotification("flipped path", event.getPosition(), 1000);

                        } else if (touchedPort2.getComponent(Port.class).getPaths().size() == 0) {

                            // Remap the Path's Ports

                            // Check if source or target in Path was moved, and reassign it
                            Shape sourcePortShape2 = path.getComponent(Image.class).getImage().getShape("Source Port");
                            Shape targetPortShape2 = path.getComponent(Image.class).getImage().getShape("Target Port");
                            if (Geometry.contains(sourcePortShape2.getBoundary(), event.getPosition())) {

                                // Check if the new Path's Port's would be on the same Portable
                                if (path.getComponent(Path.class).getTarget().getParent() == touchedPort2.getParent()) {
                                    // Prevent the Path from moving onto the Extension with both Ports
                                    if (!path.getComponent(Path.class).getTarget().getParent().hasComponent(Extension.class)) {
                                        path.getComponent(Path.class).setSource(touchedPort2);
                                    }
                                } else {
                                    path.getComponent(Path.class).setSource(touchedPort2);
                                }

                            } else if (Geometry.contains(targetPortShape2.getBoundary(), event.getPosition())) {
//                                // Check if the new Port is not on the same Portable
//                                if (path.getComponent(Path.class).getSource().getParent() != touchedPort2.getParent()) {
//                                    path.getComponent(Path.class).setTarget(touchedPort2);
//                                }

                                // Check if the new Path's Port's would be on the same Portable
                                if (path.getComponent(Path.class).getSource().getParent() == touchedPort2.getParent()) {
                                    // Prevent the Path from moving onto the Extension with both Ports
                                    if (!path.getComponent(Path.class).getSource().getParent().hasComponent(Extension.class)) {
                                        path.getComponent(Path.class).setTarget(touchedPort2);
                                    }
                                } else {
                                    path.getComponent(Path.class).setTarget(touchedPort2);
                                }
                            }

                            // TODO: Configure new Port, clear configuration from old port

                            // Notification
                            World.getWorld().renderSystem.addNotification("moved path", event.getPosition(), 1000);
                        }

//                    Entity extension = path.getComponent(Path.class).getExtension();
//                    Group<Entity> paths = extension.getComponent(Portable.class).getPaths();
//                    Log.v("PathEvent", "paths.size(): " + paths.size());

                    }

                    // Moved the Path OFF of Ports (dropped onto the background)
                    else if (touchedPorts.size() == 0) {

                        // Remove the Path (and the Extension if the removed Path was the only one)
                        path.isActive = false;
                        Entity.Manager.remove(path);

//                    Group<Entity> extensionPorts1 = extension.getComponent(Portable.class).getPorts();
//                    extensionPorts1.remove(extensionPort); // Remove from Portable

                        // Notification
                        World.getWorld().renderSystem.addNotification("removed path", event.getPosition(), 1000);

                        // Reset Ports that were in removed Path
                        Entity sourcePort = path.getComponent(Path.class).getSource();
                        sourcePort.getComponent(Port.class).setType(Port.Type.NONE);
                        Entity targetPort = path.getComponent(Path.class).getTarget();
                        targetPort.getComponent(Port.class).setType(Port.Type.NONE);

                        // Update the Path
                        Entity extension = path.getComponent(Path.class).getExtension();
                        Group<Entity> extensionPaths = extension.getComponent(Portable.class).getPaths();
                        Log.v("PathEvent", "paths.size(): " + extensionPaths.size());

                        // Delete Extension if no Paths exist to it
                        if (extensionPaths.size() == 0) {

                            // Deactivate Entities
                            Group<Entity> extensionPorts = extension.getComponent(Portable.class).getPorts();
                            for (int i = 0; i < extensionPorts.size(); i++) {
                                Entity extensionPort = extensionPorts.get(i);
                                extensionPort.isActive = false;
                            }
                            extension.isActive = false;

                            // Remove Extension's Ports
//                        Group<Entity> extensionPorts = extension.getComponent(Portable.class).getPorts();
//                        for (int i = 0; i < extensionPorts.size(); i++) {
                            while (extensionPorts.size() > 0) {
                                Entity extensionPort = extensionPorts.get(0);
                                Entity.Manager.remove(extensionPort);
                                extensionPorts.remove(extensionPort); // Remove from Portable
                            }

                            Entity.Manager.remove(extension);

                            // Notification
                            World.getWorld().renderSystem.addNotification("removed extension", extension.getComponent(Transform.class), 1000);
                        }

                    }

                }

                path.getComponent(Path.class).state = Path.State.NONE;

            } else {

                // Singleton Path

                Log.v("EventHandlerSystem", "UNSELECT Port");
                if (event.getTarget().hasComponent(Label.class)) {
                    Log.v("EventHandlerSystem", "targetEntity: " + event.getTarget().getComponent(Label.class).getLabel());
                }

                // (Host.Port, ..., World) Action Pattern

                // Hide prototype Path and prototype Extension
                world.portableLayoutSystem.setPathPrototypeVisibility(Visible.INVISIBLE);

                Log.v("EventHandlerSystem", "creating extension");

                // If prototype Extension is visible, create Extension
//                if (world.getExtensionPrototypeVisibility2() == Visible.VISIBLE) {
                Entity prototypeExtension = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                if (prototypeExtension.getComponent(Visibility.class).getVisibile() == Visible.VISIBLE) {

                    Log.v("EventHandlerSystem", "creating extension");

//                    // Hide prototype Path and prototype Extension
//                    world.setPathPrototypeVisibility(Visible.INVISIBLE);
//                    world.setExtensionPrototypeVisibility2(Visible.INVISIBLE);
                    Entity extensionPrototype = Entity.Manager.filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

//                    Entity hostPort = event.getFirstEvent().getTarget();
                    Entity hostPort = path.getComponent(Path.class).getSource();

                    Log.v("ExtendPath", "hostPort: " + hostPort);

                    // Create new custom Extension. Custom Extension can be configured manually.
                    Entity extension = world.portableLayoutSystem.createCustomExtension(hostPort, event.getPosition());

                    // Notification
                    World.getWorld().renderSystem.addNotification("added extension", extension.getComponent(Transform.class), 1000);

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
                    hostPaths.setVisibility(Visible.VISIBLE);
                    hostPorts.setVisibility(Visible.VISIBLE);

                    // Update layout
                    Entity host = hostPort.getParent(); // HACK

                    world.portableLayoutSystem.setPortableSeparation(World.HOST_TO_EXTENSION_LONG_DISTANCE);

                    world.portableLayoutSystem.updateExtensionLayout(host);
                    // <STYLE_AND_LAYOUT>

                    // Set Camera focus on the Extension
                    // camera.setFocus(extension);

                } else if (event.getFirstEvent().getTarget() == event.getTarget()) {

                    // Change Singleton Path Type

                    // <PATH>
                    // Set next Path type
                    Path pathComponent = path.getComponent(Path.class);
                    Path.Type nextType = Path.Type.getNext(pathComponent.getType());
                    while ((nextType == Path.Type.NONE) || (nextType == pathComponent.getType())) {
                        nextType = Path.Type.getNext(nextType);
                    }
                    path.getComponent(Path.class).setType(nextType);
//                Log.v("EventHandlerSystem", "Setting path type to: " + nextType);
                    // <PATH>

                } else if (event.getFirstEvent().getTarget() != event.getTarget()) {

                    // Adding Path. Stretches singleton path to a target port.

                    Log.v("PathEventHandler", "creating paaaaatthhh???");


                    if (event.getTarget().hasComponent(Path.class) && event.getTarget().getComponent(Path.class).getPorts().size() == 1) {
                        Log.v("PathEventHandler", "target is singleton PATH");
                        if (event.getTarget() != path) {
                            Log.v("PathEventHandler", "target is DIFFERENT path");

                            // Combine the Paths into one, deleting one of them!
                            // TODO: Delete path on target
                            event.getTarget().isActive = false;
                            event.getTarget().getComponent(Path.class).state = Path.State.EDITING;
                            Entity tempSourcePort = event.getTarget().getComponent(Path.class).getSource();
                            event.getTarget().getComponent(Path.class).setSource(null); // Reset path
                            event.getTarget().getComponent(Path.class).setTarget(null); // Reset path
                            Entity.Manager.remove(event.getTarget()); // Delete path!

                            // Update the Path from the source Port
                            Entity targetPort = tempSourcePort; // new target is source port from other path
                            path.getComponent(Path.class).setTarget(targetPort);
                        }
                    }


                    if (!event.getTarget().hasComponent(Port.class)) {
                        return;
                    }

                    Entity targetPort = event.getTarget();

                    path.getComponent(Path.class).setTarget(targetPort);

                    World.getWorld().renderSystem.addNotification("added path", event.getPosition(), 1000);

//                    // (Host.Port A, ..., Host.Port B) Action Pattern
//                    // i.e., The Action's first and last Events address different Ports.
//
////                    if (action.isDragging()) {
//                    // Hide the prototype Path
//                    world.portableLayoutSystem.setPathPrototypeVisibility(Visible.INVISIBLE);
//
//                    // Get the source and target Ports to be used in new Path
//                    Entity sourcePort = event.getFirstEvent().getTarget();
//                    Entity targetPort = event.getTarget();
//
//                    // Create Path and configure it
//                    Entity path = World.createEntity(Path.class);
//                    path.getComponent(Path.class).set(sourcePort, targetPort);
//
//                    // Focus Camera on Extension
//                    Entity extension = path.getComponent(Path.class).getExtension();
////                    world.cameraSystem.setFocus(camera, extension);
//
//                    // Notification
//                    World.getWorld().renderSystem.addNotification("added path", event.getPosition(), 1000);
////                    }

                }

            }
        }
    }
}
