package camp.computer.clay.engine.system;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camp.computer.clay.engine.Event;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.World;
import camp.computer.clay.engine.component.Boundary;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Component;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Label;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Prototype;
import camp.computer.clay.engine.component.RelativeLayoutConstraint;
import camp.computer.clay.engine.component.ShapeComponent;
import camp.computer.clay.engine.component.Style;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Visibility;
import camp.computer.clay.engine.component.util.Visible;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.lib.ImageBuilder.Segment;
import camp.computer.clay.model.configuration.Configuration;
import camp.computer.clay.platform.Application;
import camp.computer.clay.platform.graphics.controls.NativeUi;
import camp.computer.clay.util.Geometry;

public class EventHandlerSystem extends System {

    // TODO: Rename to EventManager, ActionManager, or something like that.
    // TODO: Allow Entities to register for specific Events/Actions.

    private List<Event> incomingEvents = new ArrayList<>();

    public Entity previousTarget = null;

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

    private void dispatchEvent(Event event) {


        Log.v("EventType", "event.type: " + event.getType()); // + ", target: " + primaryTarget + ", hasPrototype: " + primaryTarget.hasComponent(Prototype.class));
        Entity primaryTarget = null;

        // Handle special cases for MOVE and UNSELECT actions
        if (event.getType() != Event.Type.SELECT) {
//            primaryTarget = event.getFirstEvent().getTarget();
            event.setTarget(event.getFirstEvent().getTarget());
            // TODO: 11/6/2016 event.setTarget(targetEntity);
//            event.setSecondaryTarget(primaryTarget);
//            if (secondaryTargets.size() > 0) {
            event.setSecondaryTarget(event.getFirstEvent().getSecondaryTarget());
//            }
        } else {

            // Annotate the Event
            Group<Entity> primaryTargets = world.Manager.getEntities().filterVisibility(true).filterWithComponents(Image.class, Boundary.class).sortByLayer().filterContains(event.getPosition());
            Group<Entity> secondaryTargets = world.Manager.getEntities().filterVisibility(true).filterWithComponents(ShapeComponent.class, Boundary.class).filterContains(event.getPosition());

            if (primaryTargets.size() > 0) {
                //targetEntity = targetEntities.get(0);
                primaryTarget = primaryTargets.get(primaryTargets.size() - 1);
//            Log.v("handlePathEvent", "targetEntities.size: " + topLevelTargets.size());
            } else {
                Group<Entity> cameras = world.Manager.getEntities().filterWithComponent(Camera.class);
                primaryTarget = cameras.get(0);
            }
            event.setTarget(primaryTarget);


//            event.setSecondaryTarget(event.getFirstEvent().getSecondaryTarget());
//            event.setSecondaryTarget(boundaryTarget.get(0));
            Log.v("SecondaryTarget", "secondaryTargets: " + secondaryTargets.size());
//            event.setTarget(primaryTarget);
            if (secondaryTargets.size() > 0) {
//                event.setSecondaryTarget(secondaryTargets.get(0));
                for (int i = 0; i < secondaryTargets.size(); i++) {
                    if (Image.getShapes(primaryTarget).contains(secondaryTargets.get(i))) {
                        event.setSecondaryTarget(secondaryTargets.get(i));
                    }
                }
            }
        }

        // <HACK>
        // Get boundary entity (if any)

        // </HACK>

        // Dispatch the Event
        Entity eventTarget = event.getTarget();
        if (eventTarget != null) {
            if (eventTarget.hasComponent(Host.class)) {
                world.eventHandlerSystem.handleHostEvent(eventTarget, event);
            } else if (eventTarget.hasComponent(Extension.class)) {
                world.eventHandlerSystem.handleExtensionEvent(eventTarget, event);
            } else if (eventTarget.hasComponent(Port.class)) {
                world.eventHandlerSystem.handlePortEvent(eventTarget, event);
            } else if (eventTarget.hasComponent(Path.class)) {
                world.eventHandlerSystem.handlePathEvent(eventTarget, event);
            } else if (eventTarget.hasComponent(Camera.class)) {
                world.eventHandlerSystem.handleCameraEvent(eventTarget, event);
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

    public void handleCameraEvent(final Entity camera, Event event) {

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

//            if (action.isDragging()) {
            // TODO: Make sure there's no inconsistency "information access sequence" between this EventHandlerSystem, InputSystem, and PlatformRenderSurface.onTouch. Should only access info from previously dispatched? event
            world.cameraSystem.setOffset(camera, event.xOffset, event.yOffset);
            Log.v("CameraEvent", "offset.x: " + event.getOffset().y + ", y: " + event.getOffset().y);
//            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            // TODO: 11/13/2016 Set Title

            // Camera
            if (event.isTap()) {
                world.cameraSystem.setFocus(camera, null);
            }

        }
    }

    public void handleHostEvent(final Entity host, final Event event) {

        final Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

            // Show prototype Extension if any are saved and available in the repository
            if (Application.getView().getClay().getConfigurations().size() > 0) {

                Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.

                // Update position of prototype Extension
                // world.portableLayoutSystem.setPathPrototypeSourcePosition(host.getComponent(Transform.class));

                // Set Event Angle (angle from first Event to current Event)
                double eventAngle = Geometry.getAngle(
                        host.getComponent(Transform.class),
                        event.getPosition()
                );

                extensionPrototype.getComponent(Transform.class).set(event.getPosition());
                extensionPrototype.getComponent(Transform.class).setRotation(eventAngle);

                // Show the prototype Extension
                extensionPrototype.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            // Focus on touched Host
            Portable.getPaths(host).setVisibility(Visible.VISIBLE);
            Portable.getPorts(host).setVisibility(Visible.VISIBLE);

            // Update transparency
            host.getComponent(Style.class).setTransparency(host, 1.0);

            // Show Ports and Paths of touched Host
            for (int i = 0; i < Portable.getPorts(host).size(); i++) {
                Entity port = Portable.getPort(host, i);
                Group<Entity> paths = Port.getPaths(port);

                for (int j = 0; j < paths.size(); j++) {
                    Entity path = paths.get(j);

                    // Show source and target Ports in Paths
                    Path.getPorts(path).setVisibility(Visible.VISIBLE);

                    // Show Path connection
                    path.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                }
            }

            // Camera
            world.cameraSystem.setFocus(camera, host);

            if (Portable.getExtensions(host).size() > 0) {

                // <HACK>
                // TODO: Move this into PortableLayoutSystem
                // TODO: Replace ASAP. This is shit.
                // TODO: Use "rectangle" or "circular" extension layout algorithms
                world.portableLayoutSystem.setExtensionDistance(host, World.HOST_TO_EXTENSION_LONG_DISTANCE);
                // </HACK>
            }

            // TODO: 11/13/2016 Set Title

            // Check if connecting to a Extension
            Entity prototypeExtension = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0);
            if (prototypeExtension.getComponent(Visibility.class).getVisibile() == Visible.VISIBLE) {

                Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

                // Get cached extension configurations (and retrieve additional from Internet store)
                List<Configuration> configurations = Application.getView().getClay().getConfigurations();

                if (configurations.size() == 0) {

                    // TODO: Show "default" DIY extension builder (or info about there being no headerExtensions)

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
        }
    }

    public void handleExtensionEvent(final Entity extension, Event event) {

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

            world.createExtensionProfile(extension);

        } else if (event.getType() == Event.Type.MOVE) {

        } else if (event.getType() == Event.Type.UNSELECT) {

            if (world.eventHandlerSystem.previousTarget == extension) {

                boolean openImageEditor = false;

                /*
                // TODO:
                Shape board = extension.getComponent(Image.class).getImage().getShape("Board");
                List<Transform> vertices = board.getVertices();
                Log.v("ExtPos", "ex: " + event.getPosition().x + ", y: " + event.getPosition().y);
                for (int i = 0; i < vertices.size(); i++) {
                    Log.v("ExtPos", "x: " + vertices.get(i).x + ", y: " + vertices.get(i).y);
                    if (Geometry.distance(vertices.get(i), event.getPosition()) < 20) {
                        openImageEditor = true;
                    }
                }
                */

                // <HACK>
                if (Geometry.distance(event.getPosition(), extension.getComponent(Transform.class)) > 75) {
                    openImageEditor = true;
                }
                // </HACK>

                if (openImageEditor) {
                    Application.getView().getNativeUi().createImageEditor(extension);
                } else {
                    Application.getView().getNativeUi().openActionEditor(extension);
                }
            }

            // Focus on selected Host
            Group<Entity> extensionPaths = Portable.getPaths(extension);
            Group<Entity> extensionPorts = Portable.getPorts(extension);
            extensionPaths.setVisibility(Visible.VISIBLE);
            extensionPorts.setVisibility(Visible.VISIBLE);
            extension.getComponent(Style.class).setTransparency(extension, 1.0);

            // Show Ports and Paths for selected Host
            for (int i = 0; i < extensionPorts.size(); i++) {
                Entity port = extensionPorts.get(i);

                Group<Entity> paths = Port.getPaths(port);
                for (int j = 0; j < paths.size(); j++) {
                    Entity path = paths.get(j);

                    // Show Ports
                    Entity sourcePort = Path.getSource(path);
                    Entity targetPort = Path.getTarget(path);
                    sourcePort.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                    targetPort.getComponent(Visibility.class).setVisible(Visible.VISIBLE);


                    // Show Path
                    path.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
                }
            }
            // TODO: Replace above with?: portEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visible.VISIBLE);

            // TODO: 11/13/2016 Set Title

            // Camera
            Entity camera = world.Manager.getEntities().filterWithComponent(Camera.class).get(0);
            world.cameraSystem.setFocus(camera, extension);
        }
    }

    public void handlePortEvent(final Entity port, Event event) {

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

        } else if (event.getType() == Event.Type.UNSELECT) {

            // (Host.Port, ..., Host.Port) Action Pattern

            if (event.isTap() && event.getTarget() == port) {

                // (Host.Port A, ..., Host.Port A) Action Pattern
                // i.e., The action's first and last events address the same Port. Therefore, it must be either a tap or a hold.

                // Get Port associated with the touched Port
                Entity sourcePort = event.getFirstEvent().getTarget();

                // Check if the target Port is contained in any Path.
                boolean portHasPath = false;
                Entity firstPort = event.getFirstEvent().getTarget();
                if (Port.getPaths(firstPort).size() > 0) {
                    portHasPath = true;
                }

                // Create new singleton Path, enabling the Port to be connected to other Ports.
                if (!portHasPath) {
                    Entity singletonPath = world.createEntity(Path.class);
                    Path.setSource(singletonPath, sourcePort);
                    Path.setType(singletonPath, Path.Type.SWITCH);
                }

            }
        }
    }

    public void handlePathEvent(final Entity path, Event event) {

        boolean isSingletonPath = (Path.getTarget(path) == null);

        // Check if source or target in Path was moved, and reassign it
        Group<Entity> touchedPorts = world.Manager.getEntities().filterWithComponent(Port.class).filterContains(event.getPosition());

        /*
        Entity touchedPort = null;
        boolean isSourceTouched = false;
        boolean isTargetTouched = false;
        if (touchedPorts.size() > 0) {
            touchedPort = touchedPorts.get(0); // NOTE: This gets the first port in the list, no matter how many there are or which Ports they are. Maybe not always work...
        }
        */

//        Entity sourcePortShape = Image.getShape(path, "Source Port"); // path.getComponent(Image.class).getImage().getShape("Source Port");
//        Entity targetPortShape = Image.getShape(path, "Target Port"); // path.getComponent(Image.class).getImage().getShape("Target Port");

        /*
        if (Geometry.contains(world.boundarySystem.getBoundary(sourcePortShape), event.getPosition())) {
//            path.getComponent(Path.class).setSource(touchedPort);
            isSourceTouched = true;
        } else if (Geometry.contains(world.boundarySystem.getBoundary(targetPortShape), event.getPosition())) {
//            path.getComponent(Path.class).setTarget(touchedPort);
            isTargetTouched = true;
        }
        */

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

//            Log.v("handlePathEvent", "Target Entity: " + event.getTarget());
//            Log.v("handlePathEvent", "Specific (Intent) Entity: " + event.getSecondaryTarget() + " (hasShapeComponent: " + event.getSecondaryTarget().hasComponent(ShapeComponent.class) + ")");

            if (!isSingletonPath) {

                // Multi-Port Path (non-singleton)

                Entity sourcePortShape = Image.getShape(path, "Source Port"); // path.getComponent(Image.class).getImage().getShape("Source Port");
                Entity targetPortShape = Image.getShape(path, "Target Port"); // path.getComponent(Image.class).getImage().getShape("Target Port");

//                Shape sourcePortShape2 = path.getComponent(Image.class).getImage().getShape("Source Port");
//                if (Geometry.contains(Boundary.getBoundary(sourcePortShape), event.getPosition())) {
                if (event.getSecondaryTarget() == sourcePortShape) {
                    Log.v("handlePathEvent", "Touched Source");
                    sourcePortShape.getComponent(ShapeComponent.class).shape.setPosition(event.getPosition()); // TODO: Change TRANSFORM
//                    sourcePortShape.getComponent(Transform.class).set(event.getPosition()); // TODO: Change TRANSFORM
//                    sourcePortShape.getComponent(Physics.class).targetTransform.set(event.getPosition());

                    Path.setState(path, Component.State.EDITING);

                    // <HACK>
//                    world.boundarySystem.updateShapeBoundary(sourcePortShape);
                    // </HACK>
                }

                //if (Geometry.contains(Boundary.getBoundary(targetPortShape), event.getPosition())) {
                if (event.getSecondaryTarget() == targetPortShape) {
                    Log.v("handlePathEvent", "Touched Target");
                    targetPortShape.getComponent(ShapeComponent.class).shape.setPosition(event.getPosition()); // TODO: Change TRANSFORM
//                    targetPortShape.getComponent(Transform.class).set(event.getPosition()); // TODO: Change TRANSFORM
//                    targetPortShape.getComponent(Physics.class).targetTransform.set(event.getPosition()); // TODO: Change TRANSFORM

                    Path.setState(path, Component.State.EDITING);

                    // <HACK>
//                    world.boundarySystem.updateShapeBoundary(targetPortShape);
                    // </HACK>
                }

            } else {

                // Singleton Path

                Log.v("handlePathEvent", "Moving on singleton Path.");

                // Show Prototype Path
//                Transform position = sourcePortShape.getComponent(Transform.class);
//                Transform position = Path.getSource(path).getComponent(Transform.class);
//                world.portableLayoutSystem.setPathPrototypeSourcePosition(position);
//                world.portableLayoutSystem.setPathPrototypeDestinationPosition(event.getPosition());
//                world.portableLayoutSystem.setPathPrototypeVisibility(Visible.VISIBLE);

                Path.setState(path, Component.State.EDITING);

                Entity pathShape = Image.getShape(path, "Path");
                Segment pathSegment = (Segment) pathShape.getComponent(ShapeComponent.class).shape;
                pathSegment.setTarget(event.getPosition());
                pathShape.getComponent(Visibility.class).visible = Visible.VISIBLE;

//                Path.

                // Determine if taking "create new Extension" action. This is determined to be true
                // if at least one Extension is "near enough" to the Event's target position.
                boolean isCreateExtensionAction = true; // TODO: Convert into Event to send to World?

                Group<Entity> extensions = world.Manager.getEntities().filterWithComponent(Extension.class);

                for (int i = 0; i < extensions.size(); i++) {

                    double distanceToExtension = Geometry.distance(
                            event.getPosition(),
                            extensions.get(i).getComponent(Transform.class)
                    );

                    if (distanceToExtension < World.NEARBY_EXTENSION_DISTANCE_THRESHOLD) {
                        isCreateExtensionAction = false;
                        break;
                    }

                    // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
                }

                // Update position of prototype Path and Extension
                if (isCreateExtensionAction) {
                    Entity extensionPrototype = world.Manager.getEntities().filterWithComponents(Prototype.class, Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    extensionPrototype.getComponent(Visibility.class).setVisible(Visible.VISIBLE);
//                    world.portableLayoutSystem.setPathPrototypeSourcePosition(Image.getShape(event.getFirstEvent().getTarget(), "Source Port").getComponent(Transform.class));

                    // Set Event Angle (angle from first Event to current Event)
                    double eventAngle = Geometry.getAngle(
                            event.getSecondaryTarget().getComponent(Transform.class), // event.getFirstEvent().getTarget().getComponent(Transform.class),
                            event.getPosition()
                    );

                    //world.portableLayoutSystem.setExtensionPrototypePosition(extensionPrototype, event.getPosition());
//                    world.portableLayoutSystem.setExtensionPrototypePosition(extensionPrototype, event.getPosition(), eventAngle);
                    extensionPrototype.getComponent(Transform.class).set(event.getPosition());
                    extensionPrototype.getComponent(Transform.class).setRotation(eventAngle);
                } else {
                    Entity extensionPrototype = world.Manager.getEntities().filterWithComponents(Prototype.class, Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);
                }

                // Ports of nearby Hosts and Extensions
                Group<Entity> nearbyExtensions = extensions.filterArea(event.getPosition(), World.NEARBY_EXTENSION_RADIUS_THRESHOLD);

                for (int i = 0; i < extensions.size(); i++) {
                    Entity extension = extensions.get(i);

                    if (nearbyExtensions.contains(extension)) {

                        Group<Entity> nearbyExtensionPorts = Portable.getPorts(extension);

                        // Style
                        nearbyExtensionPorts.setVisibility(Visible.VISIBLE);

                        // Add new Port (if needed)
                        if (!extension.getComponent(Extension.class).isPersistent()) {

                            // Determine if a new Port is required on the custom Extension
                            boolean addNewPort = true;
                            for (int j = 0; j < nearbyExtensionPorts.size(); j++) {
                                Entity existingPort = nearbyExtensionPorts.get(j);
                                if (Port.getType(existingPort) == Port.Type.NONE) {
                                    addNewPort = false;
                                    break;
                                }
                            }
                            Log.v("handlePathEvent", "addNewPort: " + addNewPort);

                            // Add new Port to the Extension (if determined necessary)
                            if (addNewPort) {
                                Entity newPort = world.createEntity(Port.class);

                                // <HACK>
                                newPort.addComponent(new RelativeLayoutConstraint());
                                newPort.getComponent(RelativeLayoutConstraint.class).setReferenceEntity(extension);
                                newPort.getComponent(RelativeLayoutConstraint.class).relativeTransform.set(0, 25.0 * 6.0);
                                // </HACK>

                                int newPortIndex = nearbyExtensionPorts.size();
                                Port.setIndex(newPort, newPortIndex);
                                Portable.addPort(extension, newPort);
                            }
                        }
                    }
                }
            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            Entity sourcePortShape = Image.getShape(path, "Source Port"); // path.getComponent(Image.class).getImage().getShape("Source Port");
            Entity targetPortShape = Image.getShape(path, "Target Port"); // path.getComponent(Image.class).getImage().getShape("Target Port");

            Log.v("handlePathEvent", "UNSELECT PATH.");

            if (Path.getTarget(path) != null) {

                Log.v("handlePathEvent", "NON SINGLETON.");

                // Full Path (non-singleton Path)

                if (Path.getState(path) != Component.State.EDITING) {

                    // <PATH>
                    // Set next Path type
                    Path.Type nextType = Path.Type.getNext(Path.getType(path));
                    while ((nextType == Path.Type.NONE) || (nextType == Path.getType(path))) {
                        nextType = Path.Type.getNext(nextType);
                    }
                    Path.setType(path, nextType);
                    // <PATH>

                    // Notification
                    world.createAndConfigureNotification("" + nextType, event.getPosition(), 800);

                } else if (Path.getState(path) == Component.State.EDITING) {

                    Group<Entity> dropTargetPorts = world.Manager.getEntities().filterWithComponent(Port.class).filterContains(event.getPosition());

                    // Moved the Path to another Port
                    if (dropTargetPorts.size() > 0) {

                        Entity dropTargetPort = dropTargetPorts.get(0); // NOTE: This gets the first port in the list, no matter how many there are or which Ports they are. Maybe not always work...
                        Log.v("TargetAreaPort", "targetAreaPort: " + dropTargetPort);

                        // Remap the Path's Port if the touched Port doesn't already have a Path
                        Group<Entity> targetPaths = Port.getPaths(dropTargetPort);
                        if (targetPaths.size() > 0 && targetPaths.get(0) == path) {

                            // Swap the Path's Ports in the SAME path (swap Ports/flip direction)
                            Log.v("handlePathEvent", "flipping the path");
                            Entity sourcePort = Path.getSource(path);
                            Path.setSource(path, Path.getTarget(path));
                            Path.setTarget(path, sourcePort);

                            // TODO: path.getComponent(Path.class).setDirection();

                            // Notification
                            world.createAndConfigureNotification("flipped path", event.getPosition(), 1000);

                        } else if (targetPaths.size() > 0 && targetPaths.get(0) != path) {

                            // TODO: Make sure both Ports are connected between both a common Host and Extension

                            // Swap ports ACROSS different paths (swap Paths)
                            if (Geometry.contains(Boundary.getBoundary(sourcePortShape), event.getPosition())) {
                                // Swapping path A source port shape...
                                if (dropTargetPort == Path.getSource(targetPaths.get(0))) {
                                    Entity sourcePort = Path.getSource(path);
                                    Path.setSource(path, Path.getSource(targetPaths.get(0))); // Path.getTarget(path));
                                    Path.setSource(targetPaths.get(0), sourcePort);
                                } else if (dropTargetPort == Path.getTarget(targetPaths.get(0))) {
                                    Entity sourcePort = Path.getSource(path);
                                    Path.setSource(path, Path.getTarget(targetPaths.get(0))); // Path.getTarget(path));
                                    Path.setTarget(targetPaths.get(0), sourcePort);
                                }
                            } else if (Geometry.contains(Boundary.getBoundary(targetPortShape), event.getPosition())) {
                                // Swapping path A target port shape...
                                if (dropTargetPort == Path.getSource(targetPaths.get(0))) {
                                    Entity targetPath = Path.getTarget(path);
                                    Path.setTarget(path, Path.getSource(targetPaths.get(0))); // Path.getTarget(path));
                                    Path.setSource(targetPaths.get(0), targetPath);
                                } else if (dropTargetPort == Path.getTarget(targetPaths.get(0))) {
                                    Entity targetPath = Path.getTarget(path);
                                    Path.setTarget(path, Path.getTarget(targetPaths.get(0))); // Path.getTarget(path));
                                    Path.setTarget(targetPaths.get(0), targetPath);
                                }
                            }

                            // TODO: path.getComponent(Path.class).setDirection();

                            // Notification
                            world.createAndConfigureNotification("swapped paths", event.getPosition(), 1000);

                        } else if (Port.getPaths(dropTargetPort).size() == 0) {

                            // Remap the Path's Ports

                            // Check if source or target in Path was moved, and reassign it
                            Entity sourcePortShape2 = Image.getShape(path, "Source Port"); // path.getComponent(Image.class).getImage().getShape("Source Port");
                            Entity targetPortShape2 = Image.getShape(path, "Target Port"); // path.getComponent(Image.class).getImage().getShape("Target Port");
                            if (Geometry.contains(Boundary.getBoundary(sourcePortShape2), event.getPosition())) {

                                // Check if the new Path's Port's would be on the same Portable
                                if (Path.getTarget(path).getParent() == dropTargetPort.getParent()) {
                                    // Prevent the Path from moving onto the Extension with both Ports
                                    if (!Path.getTarget(path).getParent().hasComponent(Extension.class)) {
                                        Path.setSource(path, dropTargetPort);
                                    }
                                } else {
                                    Path.setSource(path, dropTargetPort);
                                }

                            } else if (Geometry.contains(Boundary.getBoundary(targetPortShape2), event.getPosition())) {

                                // Check if the new Path's Port's would be on the same Portable
                                if (Path.getSource(path).getParent() == dropTargetPort.getParent()) {
                                    // Prevent the Path from moving onto the Extension with both Ports
                                    if (!Path.getSource(path).getParent().hasComponent(Extension.class)) {
                                        Path.setTarget(path, dropTargetPort);
                                    }
                                } else {
                                    Path.setTarget(path, dropTargetPort);
                                }
                            }

                            // TODO: Configure new Port, clear configuration from old port

                            // Notification
                            world.createAndConfigureNotification("moved path", event.getPosition(), 1000);
                        }
                    }

                    // Moved the Path OFF of Ports (dropped onto the background)
                    else if (touchedPorts.size() == 0) {

                        // Remove the Path (and the Extension if the removed Path was the only one)
                        path.isActive = false;
                        world.Manager.getEntities().remove(path);

//                    Group<Entity> extensionPorts1 = extension.getComponent(Portable.class).getPorts();
//                    extensionPorts1.remove(extensionPort); // Remove from Portable

                        // Notification
                        world.createAndConfigureNotification("removed path", event.getPosition(), 1000);

                        // Reset Ports that were in removed Path
                        Entity sourcePort = Path.getSource(path);
                        Port.setType(sourcePort, Port.Type.NONE);
                        Entity targetPort = Path.getTarget(path);
                        Port.setType(targetPort, Port.Type.NONE);

                        // Update the Path
                        Entity extension = Path.getExtension(path);
                        Group<Entity> extensionPaths = Portable.getPaths(extension);
                        Log.v("handlePathEvent", "paths.size(): " + extensionPaths.size());

                        // Delete Extension if no Paths exist to it
                        if (extensionPaths.size() == 0) {

                            // Deactivate Entities
                            Group<Entity> extensionPorts = Portable.getPorts(extension);
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
                                world.Manager.getEntities().remove(extensionPort);
                                extensionPorts.remove(extensionPort); // Remove from Portable
                            }

                            world.Manager.getEntities().remove(extension);

                            // Notification
                            world.createAndConfigureNotification("removed extension", extension.getComponent(Transform.class), 1000);
                        }

                    }

                }

                Path.setState(path, Component.State.NONE);

            } else {

                // Singleton Path

                // (Host.Port, ..., World) Action Pattern

                Group<Entity> targetAreaPorts = world.Manager.getEntities().filterWithComponent(Port.class).filterContains(event.getPosition());

                Log.v("handlePathEvent", "creating extension");

                // If prototype Extension is visible, create Extension
//                if (world.getExtensionPrototypeVisibility2() == Visible.VISIBLE) {
                Entity prototypeExtension = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                if (prototypeExtension.getComponent(Visibility.class).getVisibile() == Visible.VISIBLE) {

                    Log.v("handlePathEvent", "creating extension");

//                    // Hide prototype Path and prototype Extension
//                    world.setPathPrototypeVisibility(Visible.INVISIBLE);
//                    world.setExtensionPrototypeVisibility2(Visible.INVISIBLE);
                    Entity extensionPrototype = world.Manager.getEntities().filterWithComponent(Label.class).filterLabel("prototypeExtension").get(0); // TODO: This is a crazy expensive operation. Optimize the shit out of this.
                    extensionPrototype.getComponent(Visibility.class).setVisible(Visible.INVISIBLE);

//                    Entity hostPort = event.getFirstEvent().getTarget();
                    Entity hostPort = Path.getSource(path);

                    Log.v("handlePathEvent", "hostPort: " + hostPort);

                    // Create new custom Extension. Custom Extension can be configured manually.
                    Entity extension = world.portableLayoutSystem.createCustomExtension(hostPort, event.getPosition());

                    // Notification
                    world.createAndConfigureNotification("added extension", extension.getComponent(Transform.class), 1000);

                    // Get all Ports in all Paths from the Host
                    Group<Entity> hostPaths = Port.getPaths(hostPort);
                    Group<Entity> hostPorts = new Group<>();
                    for (int i = 0; i < hostPaths.size(); i++) {
                        Group<Entity> pathPorts = Path.getPorts(hostPaths.get(i));
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
                } else if (event.isTap()) { // } else if (event.getFirstEvent().getTarget() == event.getTarget()) {

                    // Change Singleton Path Type

                    // <PATH>
                    // Set next Path type
                    Path pathComponent = path.getComponent(Path.class);
                    Path.Type nextType = Path.Type.getNext(Path.getType(path));
                    while ((nextType == Path.Type.NONE) || (nextType == Path.getType(path))) {
                        nextType = Path.Type.getNext(nextType);
                    }
                    Path.setType(path, nextType);
//                Log.v("EventHandlerSystem", "Setting path type to: " + nextType);
                    // <PATH>

                } else { //} else if (event.getFirstEvent().getTarget() != event.getTarget()) {

                    Entity dropTargetEntity = targetAreaPorts.get(0);

                    // Adding Path. Stretches singleton path to a target port.

                    Log.v("handlePathEvent", "creating paaaaatthhh???");

                    // Handle drop on Path (as opposed to drop on a Port). "Merge" the Paths by
                    // removing the Path onto which the target Path was dropped and then update the
                    // target Path's source and target Ports as usual (below).
                    Entity dropTargetPath = dropTargetEntity;
                    if (dropTargetPath.hasComponent(Path.class) && Path.getPorts(dropTargetPath).size() == 1) {
                        Log.v("handlePathEvent", "target is singleton PATH");
                        if (dropTargetPath != path) {
                            Log.v("handlePathEvent", "target is DIFFERENT path");

                            // Combine the Paths into one, deleting one of them!
                            // TODO: Delete path on target
                            // <CLEANUP_ENTITY_DELETE_CODE>
                            dropTargetPath.isActive = false;
                            Path.setState(dropTargetPath, Component.State.EDITING);
                            Entity tempSourcePort = Path.getSource(dropTargetPath);
                            Path.setSource(dropTargetPath, null); // Reset path
                            Path.setTarget(dropTargetPath, null); // Reset path
                            world.Manager.getEntities().remove(dropTargetPath); // Delete path!
                            // </CLEANUP_ENTITY_DELETE_CODE>

                            // Update the Path from the source Port
                            Entity targetPort = tempSourcePort; // new target is source port from other path
                            Path.setTarget(path, targetPort);
                        }
                    }

                    // Update the Path's target Port
                    if (!dropTargetEntity.hasComponent(Port.class)) { // if (!event.getTarget().hasComponent(Port.class)) {
                        return;
                    }

                    Entity dropTargetPort = dropTargetEntity; // event.getTarget();

                    Path.setTarget(path, dropTargetPort);

                    world.createAndConfigureNotification("added path", event.getPosition(), 1000);

                }

                Path.setState(path, Component.State.NONE);
            }
        }
    }
}
