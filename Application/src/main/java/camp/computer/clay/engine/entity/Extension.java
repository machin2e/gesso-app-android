package camp.computer.clay.engine.entity;

import android.util.Log;

import java.util.List;

import camp.computer.clay.application.Application;
import camp.computer.clay.engine.Group;
import camp.computer.clay.engine.component.Transform;
import camp.computer.clay.model.action.Action;
import camp.computer.clay.model.action.ActionListener;
import camp.computer.clay.model.action.Event;
import camp.computer.clay.model.profile.Profile;
import camp.computer.clay.engine.component.Image;
import camp.computer.clay.space.image.ExtensionImage;
import camp.computer.clay.util.image.Shape;
import camp.computer.clay.util.image.Space;
import camp.computer.clay.util.image.Visibility;
import camp.computer.clay.util.image.util.ShapeGroup;

/**
 * {@code Extension} represents a device connected to a {@code Host}.
 */
public class Extension extends Portable {

    public Extension() {
        super();
        setup();
    }

    public Extension(Profile profile) {
        super(profile);
        setup();
    }

    private void setup() {
        setupComponents();
    }

    private void setupComponents() {
        // TODO: InputComponent/ControllerComponent/ActorComponent
//        setComponent(new Transform()); // addComponent(new Transform());
        // addComponent(new Image());
    }

    public Group<Host> getHosts() {
        return getHosts(this);
    }

    private Group<Host> getHosts(Extension extension) {

        List<Host> hosts = Entity.Manager.filterType2(Host.class);

        Group<Host> hostGroup = new Group<>();
        for (int i = 0; i < hosts.size(); i++) {
            if (hosts.get(i).getExtensions().contains(extension)) {
                if (!hostGroup.contains(hosts.get(i))) {
                    hostGroup.add(hosts.get(i));
                }
            }
        }

        return hostGroup;
    }

    public void setupActionListener() {

        final Extension extension = this;

        final ExtensionImage extensionImage = (ExtensionImage) extension.getComponent(Image.class);

        setOnActionListener(new ActionListener() {
            @Override
            public void onAction(Action action) {

                Log.v("ExtensionImage", "onAction " + action.getLastEvent().getType());

                Event event = action.getLastEvent();

                if (event.getType() == Event.Type.NONE) {

                } else if (event.getType() == Event.Type.SELECT) {

                } else if (event.getType() == Event.Type.HOLD) {

                    Log.v("ExtensionImage", "ExtensionImage.HOLD / createProfile()");
                    extensionImage.createProfile();

                } else if (event.getType() == Event.Type.MOVE) {

                } else if (event.getType() == Event.Type.UNSELECT) {

                    // Previous Action targeted also this Extension
                    // TODO: Refactor
                    if (action.getPrevious().getFirstEvent().getTargetImage().getEntity() == extensionImage.getExtension()) {

                        if (action.isTap()) {
                            // TODO: Replace with script editor/timeline
                            Application.getView().openActionEditor(extensionImage.getExtension());
                        }

                    } else {

                        if (action.isTap()) {

                            // Focus on touched base
                            extensionImage.setPathVisibility(Visibility.VISIBLE);
                            extensionImage.getPortShapes().setVisibility(Visibility.VISIBLE);
                            extensionImage.setTransparency(1.0);

                            // Show ports and paths of touched form
                            ShapeGroup portShapes = extensionImage.getPortShapes();
                            for (int i = 0; i < portShapes.size(); i++) {
                                Shape portShape = portShapes.get(i);
                                Port port = (Port) portShape.getEntity();

                                Group<Path> paths = port.getPaths();
                                for (int j = 0; j < paths.size(); j++) {
                                    Path path = paths.get(j);

                                    // Show ports
                                    Space.getSpace().getShape(path.getSource()).setVisibility(Visibility.VISIBLE);
                                    Space.getSpace().getShape(path.getTarget()).setVisibility(Visibility.VISIBLE);

                                    // Show path
                                    path.getComponent(Image.class).setVisibility(Visibility.VISIBLE);
                                }
                            }

                            // Camera
                            event.getActor().getCamera().setFocus(extensionImage.getExtension());

                            // Title
                            Space.getSpace().setTitleText("Extension");
                            Space.getSpace().setTitleVisibility(Visibility.VISIBLE);
                        }
                    }
                }
            }
        });
    }
}
