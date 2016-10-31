package camp.computer.clay.engine.system;

import android.util.Log;

import java.util.List;

import camp.computer.clay.Clay;
import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Camera;
import camp.computer.clay.engine.component.Extension;
import camp.computer.clay.engine.component.Host;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.engine.component.Path;
import camp.computer.clay.engine.component.Port;
import camp.computer.clay.engine.component.Portable;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.engine.component.Workspace;
import camp.computer.clay.engine.entity.Entity;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.World;
import camp.computer.clay.util.image.Visibility;

public class ActionHandlerSystem extends System {

    @Override
    public boolean update(World world) {

        // TODO: Dequeue actions and apply them to the targeted Entity (calling one of the below functions)

        return false;
    }

    // TODO: Make World an Entity?
    public static void handleWorldAction(final Entity workspace, Action action) {

        Event event = action.getLastEvent();
        Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

            if (action.isDragging()) {
                camera.getComponent(Camera.class).setOffset(action.getOffset());
            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            /*
            // Previous Action targeted also this ExtensionEntity
            if (action.getPrevious() != null && action.getPrevious().getFirstEvent().getTargetImage().getEntity() == getEntity()) {

                if (action.isTap()) {
                    // Title
                    setTitleText("Project");
                    setTitleVisibility(Visibility.VISIBLE);
                }

            } else {
            */

            // NOT a repeat tap on this Image

            if (action.isTap()) {

                // Title
                // TODO: workspace.setTitleVisibility(Visibility.INVISIBLE);

                // Camera
                camera.getComponent(Camera.class).setFocus(World.getWorld());
            }

        }
    }

    public static void handleHostAction(final Entity hostEntity, Action action) {

        final Image hostImage = hostEntity.getComponent(Image.class);

        final Event event = action.getLastEvent();

        final Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

            if (action.isDragging()) {

                // Update position of prototype ExtensionEntity
                World.getWorld().setExtensionPrototypePosition(event.getPosition());

//                    hostEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
                hostEntity.getComponent(Portable.class).setPathVisibility(false);

                World.getWorld().setExtensionPrototypeVisibility(Visibility.VISIBLE);

            } else if (action.isHolding()) {

                // Update position of HostEntity image
                hostEntity.getComponent(Transform.class).set(event.getPosition());

                // CameraEntity
                camera.getComponent(Camera.class).setFocus(hostEntity);

            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            if (action.isTap()) {

                // Focus on touched form
                hostEntity.getComponent(Portable.class).setPathVisibility(true);
                hostEntity.getComponent(Portable.class).getPorts().setVisibility(true);

                hostImage.setTransparency(1.0);

                // Show Ports and Paths of touched Host
                for (int i = 0; i < hostEntity.getComponent(Portable.class).getPorts().size(); i++) {
                    Group<Entity> pathEntities = hostEntity.getComponent(Portable.class).getPort(i).getComponent(Port.class).getPaths();

                    for (int j = 0; j < pathEntities.size(); j++) {
                        Entity pathEntity = pathEntities.get(j);

                        // Show source and target Ports in Paths
                        pathEntity.getComponent(Path.class).getSource().getComponent(camp.computer.clay.engine.component.Visibility.class).isVisible = true;
                        pathEntity.getComponent(Path.class).getTarget().getComponent(camp.computer.clay.engine.component.Visibility.class).isVisible = true;

                        // Show Path connection
                        pathEntity.getComponent(camp.computer.clay.engine.component.Visibility.class).isVisible = true;
                    }
                }

                // Camera
                camera.getComponent(Camera.class).setFocus(hostEntity);

                if (hostEntity.getComponent(Portable.class).getExtensions().size() > 0) {
//                                World.getWorld().getImages(getHost().getExtensions()).setTransparency(1.0);
                    hostEntity.getComponent(Portable.class).getExtensions().setTransparency(0.1);

                    // <HACK>
                    // TODO: Replace ASAP. This is shit.
                    // TODO: Use "rectangle" or "circular" extension layout algorithms
                    hostEntity.getComponent(Host.class).setExtensionDistance(World.HOST_TO_EXTENSION_LONG_DISTANCE);
                    // </HACK>
                }

                // Title
                World.getWorld().setTitleText("Host");
                World.getWorld().setTitleVisibility(Visibility.VISIBLE);

            } else {

                // TODO: Release longer than tap!

                if (event.getTarget().hasComponent(Host.class)) {

                    // If getFirstEvent queueEvent was on the same form, then respond
//                                if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage().getEntity() instanceof HostEntity) {
                    if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTarget().hasComponent(Host.class)) {

                        // HostEntity
//                                    event.getTargetImage().queueEvent(action);

                        // CameraEntity
//                                    cameraEntity.setFocus();
                    }

                }
//                else if (event.getTargetImage() instanceof World) {
//
//                    // HostEntity
////                                action.getFirstEvent().getTargetImage().queueEvent(action);
//
//                }
            }

            // Check if connecting to a Extension
            if (World.getWorld().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                World.getWorld().setExtensionPrototypeVisibility(Visibility.INVISIBLE);

                // Get cached extension profiles (and retrieve additional from Internet store)
                List<Profile> profiles = Application.getView().getClay().getProfiles();


                if (profiles.size() == 0) {

                    // Show "default" DIY extension builder (or info about there being no headerExtensions)

                } else if (profiles.size() > 0) {

                    // Prompt User to select an ExtensionEntity from the Store
                    // i.e., Prompt to select extension to use! Then use that profile to create and configure portEntities for the extension.
                    Application.getView().getActionPrompts().promptSelection(profiles, new Prompt.OnActionListener<Profile>() {
                        @Override
                        public void onComplete(Profile profile) {

                            // Add ExtensionEntity from Profile
                            Entity extensionEntity = hostEntity.getComponent(Host.class).restoreExtension(profile, event.getPosition());

                            // Update CameraEntity
                            camera.getComponent(Camera.class).setFocus(extensionEntity);
                        }
                    });
                    // Application.getView().promptTasks();
                }
            }

//            }
        }
    }

    public static void handleExtensionAction(final Entity extensionEntity, Action action) {

        final Image extensionImage = extensionEntity.getComponent(Image.class);

        Log.v("ExtensionImage", "onAction " + action.getLastEvent().getType());

        Event event = action.getLastEvent();

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

            Log.v("ExtensionImage", "ExtensionImage.HOLD / createProfile()");
            extensionEntity.getComponent(Portable.class).createProfile(extensionEntity);

        } else if (event.getType() == Event.Type.MOVE) {

        } else if (event.getType() == Event.Type.UNSELECT) {

            // Previous Action targeted also this ExtensionEntity
            // TODO: Refactor
            if (action.getPrevious().getFirstEvent().getTarget() == extensionImage.getEntity()) {

                if (action.isTap()) {
                    // TODO: Replace with script editor/timeline
                    Application.getView().openActionEditor(extensionImage.getEntity());
                }

            } else {

                if (action.isTap()) {

                    // Focus on touched base
                    extensionEntity.getComponent(Portable.class).setPathVisibility(true);
//                            extensionEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);
                    extensionEntity.getComponent(Portable.class).getPorts().setVisibility(true);
                    extensionImage.setTransparency(1.0);

                    // Show Ports and Paths for selected Host
//                            Group<Shape> portShapes = extensionEntity.getComponent(Portable.class).getPortShapes();
                    for (int i = 0; i < extensionEntity.getComponent(Portable.class).getPorts().size(); i++) {
//                                Shape portShape = portShapes.get(i);
                        Entity portEntity = extensionEntity.getComponent(Portable.class).getPorts().get(i);

                        Group<Entity> paths = portEntity.getComponent(Port.class).getPaths();
                        for (int j = 0; j < paths.size(); j++) {
                            Entity path = paths.get(j);

                            // Show Ports
//                                    World.getWorld().getShape(path.getComponent(Path.class).getSource()).setVisibility(Visibility.VISIBLE);
//                                    World.getWorld().getShape(path.getComponent(Path.class).getTarget()).setVisibility(Visibility.VISIBLE);
                            Entity sourcePort = path.getComponent(Path.class).getSource();
                            Entity targetPort = path.getComponent(Path.class).getTarget();
                            sourcePort.getComponent(camp.computer.clay.engine.component.Visibility.class).isVisible = true;
                            targetPort.getComponent(camp.computer.clay.engine.component.Visibility.class).isVisible = true;


                            // Show Path
                            path.getComponent(camp.computer.clay.engine.component.Visibility.class).isVisible = true;
                        }
                    }
                    // TODO: Replace above with?: portEntity.getComponent(Portable.class).getPorts().getImages().setVisibility(Visibility.VISIBLE);

                    // CameraEntity
                    Entity camera = Entity.Manager.filterWithComponent(Camera.class).get(0);
                    camera.getComponent(Camera.class).setFocus(extensionImage.getEntity());

                    // Title
                    World.getWorld().setTitleText("Extension");
                    World.getWorld().setTitleVisibility(Visibility.VISIBLE);
                }
            }
        }
    }

    public static void handlePortAction(final Entity portEntity2, Action action) {

        final Event event = action.getLastEvent();

        final Entity cameraEntity = Entity.Manager.filterWithComponent(Camera.class).get(0);

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

            if (action.isDragging()) {

                // Prototype Path Visibility
                // TODO: World.getWorld().setPathPrototypeSourcePosition(action.getFirstEvent().getTarget().getComponent(Transform.class));
                World.getWorld().setPathPrototypeSourcePosition(action.getFirstEvent().getTarget().getComponent(Image.class).getShape("Port").getPosition());
                World.getWorld().setPathPrototypeDestinationPosition(event.getPosition());
                World.getWorld().setPathPrototypeVisibility(Visibility.VISIBLE);

                // Prototype Extension Visibility
                boolean isCreateExtensionAction = true;

                // <HACK>
                Group<Image> imageGroup = Entity.Manager.filterWithComponent(Host.class, Extension.class).getImages();

                for (int i = 0; i < imageGroup.size(); i++) {
                    Image otherImage = imageGroup.get(i);

                    // Update style of nearby Hosts
                    double distanceToHostImage = Geometry.distance(
                            event.getPosition(),
                            otherImage.getEntity().getComponent(Transform.class)
                    );

                    if (distanceToHostImage < 375) { // 375, 500
                        isCreateExtensionAction = false;
                        break;
                    }

                    // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
                }

                if (isCreateExtensionAction) {
                    World.getWorld().setExtensionPrototypeVisibility(Visibility.VISIBLE);
                    // TODO: World.getWorld().setPathPrototypeSourcePosition(action.getFirstEvent().getTarget().getComponent(Transform.class));
                    World.getWorld().setPathPrototypeSourcePosition(action.getFirstEvent().getTarget().getComponent(Image.class).getShape("Port").getPosition());
                    World.getWorld().setExtensionPrototypePosition(event.getPosition());
                } else {
                    World.getWorld().setExtensionPrototypeVisibility(Visibility.INVISIBLE);
                }

                // Show Ports of nearby Hosts and Extensions
                Entity sourcePortEntity = action.getFirstEvent().getTarget();
                Event lastEvent = action.getLastEvent();

                // Show Ports of nearby Hosts and Extensions
                double nearbyRadiusThreshold = 200 + 60;
                Group<Image> nearbyPortableImages = imageGroup.filterArea(lastEvent.getPosition(), nearbyRadiusThreshold);

                for (int i = 0; i < imageGroup.size(); i++) {
                    Image portableImage = imageGroup.get(i);

                    //if (portableImage.getEntity() == sourcePortEntity.getComponent(Port.class).getPortable() || nearbyPortableImages.contains(portableImage)) {
                    if (portableImage.getEntity() == sourcePortEntity.getParent() || nearbyPortableImages.contains(portableImage)) {

//                                                        // <HACK>
                        Image nearbyImage = portableImage;
                        Entity nearbyPortableEntity = nearbyImage.getEntity();
                        nearbyImage.setTransparency(1.0f);
//                                    nearbyPortableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.VISIBLE);
                        nearbyPortableEntity.getComponent(Portable.class).getPorts().setVisibility(true);

                        // Add additional PortEntity to ExtensionEntity if it has no more available Ports
                        Entity portableEntity = portableImage.getEntity();

                        if (portableEntity.hasComponent(Extension.class)) { // HACK
                            if (portableEntity.getComponent(Extension.class).getProfile() == null) {
                                Entity extensionPortableEntity = portableImage.getEntity();

                                boolean addPrototypePort = true;
                                for (int j = 0; j < extensionPortableEntity.getComponent(Portable.class).getPorts().size(); j++) {
                                    Entity existingPortEntity = extensionPortableEntity.getComponent(Portable.class).getPorts().get(j);
                                    if (existingPortEntity.getComponent(Port.class).getType() == Port.Type.NONE) {
                                        addPrototypePort = false;
                                        break;
                                    }
                                }

                                if (addPrototypePort) {

                                    Entity portEntity = Clay.createEntity(Port.class);

                                    portEntity.getComponent(Port.class).setIndex(extensionPortableEntity.getComponent(Portable.class).getPorts().size());

                                    extensionPortableEntity.getComponent(Portable.class).addPort(portEntity);
                                }
                            }
                        }

                        // </HACK>

                    } else {

                        Image nearbyImage = portableImage;
                        Entity nearbyPortableEntity = portableImage.getEntity();
                        nearbyImage.setTransparency(0.1f);
                        //nearbyPortableEntity.getComponent(Portable.class).getPortShapes().setVisibility(Visibility.INVISIBLE);
                        nearbyPortableEntity.getComponent(Portable.class).getPorts().setVisibility(false);

                    }
                }

                // CameraEntity
                cameraEntity.getComponent(Camera.class).setFocus(sourcePortEntity, event.getPosition());

            } else if (action.isHolding()) {

//                                                // Holding and dragging

            }

        } else if (event.getType() == Event.Type.UNSELECT) {

            if (action.getLastEvent().getTarget() != null && action.getLastEvent().getTarget().hasComponent(Port.class)) {

                // (Host.Port, ..., Host.Port) Action Pattern

                if (action.getFirstEvent().getTarget() == action.getLastEvent().getTarget() && action.isTap()) { // if (action.isTap()) {

                    // (Host.Port A, ..., Host.Port A) Action Pattern
                    // i.e., The action's first and last events address the same portEntity. Therefore, it must be either a tap or a hold.

                    // Get Port associated with the touched Port
                    Entity portEntity = action.getFirstEvent().getTarget();

                    Port portComponent = portEntity.getComponent(Port.class);

                    if (portComponent.getExtension() == null || portComponent.getExtension().getComponent(Extension.class).getProfile() == null) {

                        if (portComponent.getType() == Port.Type.NONE) {

                            // Set initial PortEntity Type

                            Log.v("TouchPort", "-A");

                            portComponent.setDirection(Port.Direction.INPUT);
                            portComponent.setType(Port.Type.next(portComponent.getType()));

                        } else if (!portComponent.hasPath()) {

                            // Change PortEntity Type

                            Log.v("TouchPort", "-B");

                            Port.Type nextType = portComponent.getType();
                            while ((nextType == Port.Type.NONE) || (nextType == portComponent.getType())) {
                                nextType = Port.Type.next(nextType);
                            }
                            portComponent.setType(nextType);

//                                    } else if (portEntity.getComponent(Portable.class).hasVisiblePaths(portIndex)) {
                        } else if (portEntity.getComponent(Port.class).hasVisiblePaths()) {

                            // Change PathEntity Type. Updates each PortEntity in the PathEntity.

                            Log.v("TouchPort", "-D");

                            // Paths are being shown. Touching a portEntity changes the portEntity type. This will also
                            // updates the corresponding path requirement.

                            Port.Type nextType = portComponent.getType();
                            while ((nextType == Port.Type.NONE) || (nextType == portComponent.getType())) {
                                nextType = Port.Type.next(nextType);
                            }

                            // <FILTER>
                            // TODO: Make Filter/Editor to pass to Group.filter(Filter) or Group.filter(Editor)
                            Group<Entity> pathEntities = portComponent.getPaths();
                            for (int i = 0; i < pathEntities.size(); i++) {
                                Entity pathEntity = pathEntities.get(i);

                                // <FILTER>
                                // TODO: Make Filter/Editor
                                Group<Entity> portEntities = pathEntity.getComponent(Path.class).getPorts();
                                for (int j = 0; j < portEntities.size(); j++) {
                                    portEntities.get(j).getComponent(Port.class).setType(nextType);
                                }
                                // </FILTER>
                            }
                            // </FILTER>

                        }

                        World.getWorld().setPathPrototypeVisibility(Visibility.INVISIBLE);
                    }

                } else if (action.getFirstEvent().getTarget() != action.getLastEvent().getTarget()) {

                    // (Host.Port A, ..., Host.Port B) Action Pattern
                    // i.e., The Action's first and last Events address different Ports.

                    if (action.isDragging()) {

                        Log.v("Events", "B.1");

                        Entity sourcePortEntity = event.getAction().getFirstEvent().getTarget();
                        Entity targetPortEntity = event.getTarget();

                        Log.v("Events", "D.1");

                        // Create and configure new PathEntity
                        Entity pathEntity = Clay.createEntity(Path.class);
                        pathEntity.getComponent(Path.class).set(sourcePortEntity, targetPortEntity);

                        Entity extensionEntity = pathEntity.getComponent(Path.class).getExtension();
                        cameraEntity.getComponent(Camera.class).setFocus(extensionEntity);

                        World.getWorld().setPathPrototypeVisibility(Visibility.INVISIBLE);

                    }

                }

            } else if (action.getLastEvent().getTarget().hasComponent(Workspace.class)) {

                // (Host.Port, ..., World) Action Pattern

                if (World.getWorld().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                    Entity hostPortEntity = event.getAction().getFirstEvent().getTarget();

                    // Create new ExtensionEntity from scratch (for manual configuration/construction)
                    portEntity2.getParent().getComponent(Host.class).createExtension(hostPortEntity, event.getPosition());

                    // Update CameraEntity
//                    cameraEntity.setFocus(extensionEntity);
                }

                // Update Image
                World.getWorld().setPathPrototypeVisibility(Visibility.INVISIBLE);
                World.getWorld().setExtensionPrototypeVisibility(Visibility.INVISIBLE);

            }
        }
    }

    public static void handlePathAction(final Entity pathEntity, Action action) {
        Event event = action.getLastEvent();

        if (event.getType() == Event.Type.NONE) {

        } else if (event.getType() == Event.Type.SELECT) {

        } else if (event.getType() == Event.Type.HOLD) {

        } else if (event.getType() == Event.Type.MOVE) {

        } else if (event.getType() == Event.Type.UNSELECT) {

        }
    }
}
