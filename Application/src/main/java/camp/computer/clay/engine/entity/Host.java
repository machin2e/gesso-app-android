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
}
