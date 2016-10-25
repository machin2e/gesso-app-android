package camp.computer.clay.engine.entity;

import android.util.Log;

import java.util.List;
import java.util.UUID;

import camp.computer.clay.Clay;
import camp.computer.clay.application.Application;
import camp.computer.clay.application.graphics.controls.Prompt;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.space.image.ExtensionImage;
import camp.computer.clay.space.image.HostImage;
import camp.computer.clay.space.image.PortableImage;
import camp.computer.clay.util.geometry.Geometry;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;

public class Host extends Portable {

    public Host() {
        super();
        setup();
    }

    private void setup() {
    }

    // has Script/is Scriptable/ScriptableComponent (i.e., Host runs a Script)

    public ActionListener getActionListener() {

        final Host host = this;

        final HostImage hostImage = (HostImage) getComponent(Image.class);

        return new ActionListener() {
            @Override
            public void onAction(Action action) {

                final Event event = action.getLastEvent();

                final Camera camera = event.getActor().getCamera();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                } else if (event.getType() == Event.Type.MOVE) {

                    if (action.getFirstEvent().getTargetShape() == null) {
                        return;
                    }

                    if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                        if (action.isDragging()) {

                            // Update position of prototype Extension
                            Space.getSpace().setExtensionPrototypePosition(event.getPosition());

                            hostImage.getPortShapes().setVisibility(Visibility.INVISIBLE);
                            hostImage.setPathVisibility(Visibility.INVISIBLE);

                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.VISIBLE);

                        } else if (action.isHolding()) {

                            // Update position of Host image
                            getComponent(Transform.class).set(event.getPosition());

                            // Camera
                            camera.setFocus(host);

                        }

                    } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                        if (action.isDragging()) {

                            // Prototype Path Visibility
                            Space.getSpace().setPathPrototypeSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                            Space.getSpace().setPathPrototypeDestinationPosition(event.getPosition());
                            Space.getSpace().setPathPrototypeVisibility(Visibility.VISIBLE);

                            // Prototype Extension Visibility
                            boolean isCreateExtensionAction = true;
                            //Group<Image> imageGroup = Space.getSpace().getImages(Host.class, Extension.class);
                            Group<Image> imageGroup = Entity.Manager.filterType2(Host.class, Extension.class).getImages();
                            for (int i = 0; i < imageGroup.size(); i++) {
                                Image otherImage = imageGroup.get(i);

                                // Update style of nearby Hosts
                                double distanceToHostImage = Geometry.distance(
                                        event.getPosition(),
                                        otherImage.getEntity().getComponent(Transform.class)
                                );

                                if (distanceToHostImage < 375) { // 500
                                    isCreateExtensionAction = false;
                                    break;
                                }

                                // TODO: if distance > 800: connect to cloud service and show "cloud portable" image
                            }

                            if (isCreateExtensionAction) {
                                Space.getSpace().setExtensionPrototypeVisibility(Visibility.VISIBLE);
                                Space.getSpace().setPathPrototypeSourcePosition(action.getFirstEvent().getTargetShape().getPosition());
                                Space.getSpace().setExtensionPrototypePosition(event.getPosition());
                            } else {
                                Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);
                            }

                            // Show Ports of nearby Hosts and Extensions
                            Port sourcePort = (Port) action.getFirstEvent().getTargetShape().getEntity();
                            Event lastEvent = action.getLastEvent();

                            // Show Ports of nearby Hosts and Extensions
                            double nearbyRadiusThreshold = 200 + 60;
                            Group<Image> nearbyPortableImages = imageGroup.filterArea(lastEvent.getPosition(), nearbyRadiusThreshold);

                            for (int i = 0; i < imageGroup.size(); i++) {
                                PortableImage portableImage = (PortableImage) imageGroup.get(i);

                                if (portableImage.getPortable() == sourcePort.getPortable() || nearbyPortableImages.contains(portableImage)) {

//                                                        // <HACK>
                                    PortableImage nearbyImage = portableImage;
                                    nearbyImage.setTransparency(1.0f);
                                    nearbyImage.getPortShapes().setVisibility(Visibility.VISIBLE);

                                    // Add additional Port to Extension if it has no more available Ports
                                    if (portableImage.getPortable().getProfile() == null) {
                                        if (portableImage instanceof ExtensionImage) {
                                            Portable extensionPortable = portableImage.getPortable();

                                            boolean addPrototypePort = true;
                                            for (int j = 0; j < extensionPortable.getPorts().size(); j++) {
                                                Port existingPort = extensionPortable.getPorts().get(j);
                                                if (existingPort.getType() == Port.Type.NONE) {
                                                    addPrototypePort = false;
                                                    break;
                                                }
                                            }

                                            if (addPrototypePort) {
                                                Port port = new Port();
                                                port.setIndex(extensionPortable.getPorts().size());
                                                extensionPortable.addPort(port);
                                            }
                                        }
                                    }

                                    // </HACK>

                                } else {

                                    PortableImage nearbyFigure = portableImage;
                                    nearbyFigure.setTransparency(0.1f);
                                    nearbyFigure.getPortShapes().setVisibility(Visibility.INVISIBLE);

                                }
                            }

                            // Camera
                            camera.setFocus(sourcePort, event.getPosition());

                        } else if (action.isHolding()) {

//                                                // Holding and dragging

                        }

                    }

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // <HACK>
                    // TODO: Refactor so this doesn't have to be here! It's messy this way... standardize the way "null shapes" are handled
                    if (action.getFirstEvent().getTargetShape() == null) {
                        return;
                    }
                    // </HACK>

                    if (action.getFirstEvent().getTargetShape().getLabel().equals("Board")) {

                        if (action.isTap()) {

                            // Focus on touched form
                            hostImage.setPathVisibility(Visibility.VISIBLE);
                            hostImage.getPortShapes().setVisibility(Visibility.VISIBLE);

                            hostImage.setTransparency(1.0);

                            // Show ports and paths of touched form
                            for (int i = 0; i < host.getPorts().size(); i++) {
                                Group<Path> paths = host.getPort(i).getPaths();

                                for (int j = 0; j < paths.size(); j++) {
                                    Path path = paths.get(j);

                                    // Show source and target ports in path
                                    Space.getSpace().getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                    Space.getSpace().getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                    // Show Path connection
                                    path.getComponent(Image.class).setVisibility(Visibility.VISIBLE);
                                }
                            }

                            // Camera
                            camera.setFocus(host);

                            if (host.getExtensions().size() > 0) {
//                                                    Space.getSpace().getImages(getHost().getExtensions()).setTransparency(1.0);
                                host.getExtensions().setTransparency(0.1);

                                // <HACK>
                                // TODO: Replace ASAP. This is shit.
                                // TODO: Use "rectangle" or "circular" extension layout algorithms
                                hostImage.setExtensionDistance(500);
                                // </HACK>
                            }

                            // Title
                            Space.getSpace().setTitleText("Host");
                            Space.getSpace().setTitleVisibility(Visibility.VISIBLE);

                        } else {

                            // TODO: Release longer than tap!

                            if (event.getTargetImage() instanceof HostImage) {

                                // If getFirstEvent queueEvent was on the same form, then respond
                                if (action.getFirstEvent().isPointing() && action.getFirstEvent().getTargetImage() instanceof HostImage) {

                                    // Host
//                                                        event.getTargetImage().queueEvent(action);

                                    // Camera
//                                                        camera.setFocus();
                                }

                            } else if (event.getTargetImage() instanceof Space) {

                                // Host
//                                                        action.getFirstEvent().getTargetImage().queueEvent(action);

                            }

                        }

                        // Check if connecting to a extension
                        if (Space.getSpace().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);

                            // Get cached extension profiles (and retrieve additional from Internet store)
                            List<Profile> profiles = Application.getView().getClay().getProfiles();


                            if (profiles.size() == 0) {

                                // Show "default" DIY extension builder (or info about there being no headerExtensions)

                            } else if (profiles.size() > 0) {

                                // Prompt User to select an Extension from the Store
                                // i.e., Prompt to select extension to use! Then use that profile to create and configure ports for the extension.
                                Application.getView().getActionPrompts().promptSelection(profiles, new Prompt.OnActionListener<Profile>() {
                                    @Override
                                    public void onComplete(Profile profile) {

                                        // Add Extension from Profile
                                        Extension extension = hostImage.restoreExtension(profile, event.getPosition());

                                        // Update Camera
                                        camera.setFocus(extension);
                                    }
                                });
                                // Application.getView().promptTasks();
                            }
                        }

                    } else if (action.getFirstEvent().getTargetShape().getLabel().startsWith("Port")) {

                        if (action.getLastEvent().getTargetShape() != null && action.getLastEvent().getTargetShape().getLabel().startsWith("Port")) {

                            // (Host.Port, ..., Host.Port) Action Pattern

                            if (action.getFirstEvent().getTargetShape() == action.getLastEvent().getTargetShape() && action.isTap()) { // if (action.isTap()) {

                                // (Host.Port A, ..., Host.Port A) Action Pattern
                                // i.e., The action's first and last events address the same port. Therefore, it must be either a tap or a hold.

                                // Get port associated with the touched port shape
                                Port port = (Port) action.getFirstEvent().getTargetShape().getEntity();
                                int portIndex = host.getPorts().indexOf(port);

                                if (port.getExtension() == null || port.getExtension().getProfile() == null) {

                                    if (port.getType() == Port.Type.NONE) {

                                        // Set initial Port Type

                                        Log.v("TouchPort", "-A");

                                        port.setDirection(Port.Direction.INPUT);
                                        port.setType(Port.Type.next(port.getType()));

                                    } else if (!port.hasPath()) {

                                        // Change Port Type

                                        Log.v("TouchPort", "-B");

                                        Port.Type nextType = port.getType();
                                        while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                            nextType = Port.Type.next(nextType);
                                        }
                                        port.setType(nextType);

                                    } else if (hostImage.hasVisiblePaths(portIndex)) {

                                        // Change Path Type. Updates each Port in the Path.

                                        Log.v("TouchPort", "-D");

                                        // Paths are being shown. Touching a port changes the port type. This will also
                                        // updates the corresponding path requirement.

                                        Port.Type nextType = port.getType();
                                        while ((nextType == Port.Type.NONE) || (nextType == port.getType())) {
                                            nextType = Port.Type.next(nextType);
                                        }

                                        // <FILTER>
                                        // TODO: Make Filter/Editor to pass to Group.filter(Filter) or Group.filter(Editor)
                                        Group<Path> paths = port.getPaths();
                                        for (int i = 0; i < paths.size(); i++) {
                                            Path path = paths.get(i);

                                            // <FILTER>
                                            // TODO: Make Filter/Editor
                                            Group<Port> ports = path.getPorts();
                                            for (int j = 0; j < ports.size(); j++) {
                                                ports.get(j).setType(nextType);
                                            }
                                            // </FILTER>
                                        }
                                        // </FILTER>

                                    }

                                    Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);
                                }

                            } else if (action.getFirstEvent().getTargetShape() != action.getLastEvent().getTargetShape()) {

                                // (Host.Port A, ..., Host.Port B) Action Pattern
                                // i.e., The Action's first and last Events address different Ports.

                                Shape sourcePortShape = event.getAction().getFirstEvent().getTargetShape();

                                if (action.isDragging()) {

                                    Log.v("Events", "B.1");

                                    Port sourcePort = (Port) sourcePortShape.getEntity();
                                    Port targetPort = null;

                                    Shape targetPortShape = Space.getSpace().getShapes(Port.class).remove(sourcePortShape).filterContains(event.getPosition()).get(0);
                                    targetPort = (Port) targetPortShape.getEntity();

                                    Log.v("Events", "D.1");

                                    // Create and configure new Path
                                    UUID pathUuid = Clay.createEntity(Path.class);
                                    Path path = (Path) Entity.getEntity(pathUuid);
                                    path.set(sourcePort, targetPort);

                                    event.getActor().getCamera().setFocus(path.getExtension());

                                    Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);

                                }

                            }

                        } else if (action.getLastEvent().getTargetShape() == null
                                // TODO: && action.getLastEvent().getTargetImage().getLabel().startsWith("Space")) {
                                && action.getLastEvent().getTargetImage() == Space.getSpace()) {

                            // (Host.Port, ..., Space) Action Pattern

                            if (Space.getSpace().getExtensionPrototypeVisibility() == Visibility.VISIBLE) {

                                Shape hostPortShape = event.getAction().getFirstEvent().getTargetShape();
                                Port hostPort = (Port) hostPortShape.getEntity();

                                // Create new Extension from scratch (for manual configuration/construction)
                                Extension extension = hostImage.createExtension(hostPort, event.getPosition());

                                // Update Camera
                                camera.setFocus(extension);
                            }

                            // Update Image
                            Space.getSpace().setPathPrototypeVisibility(Visibility.INVISIBLE);
                            Space.getSpace().setExtensionPrototypeVisibility(Visibility.INVISIBLE);

                        }
                    }
                }
            }
        };
    }
}
