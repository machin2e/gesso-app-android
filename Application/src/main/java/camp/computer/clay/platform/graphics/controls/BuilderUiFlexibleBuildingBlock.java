package camp.computer.clay.platform.graphics.controls;

import java.util.HashMap;

public class BuilderUiFlexibleBuildingBlock {
    // TODO: 11/14/2016 Give object intelligence for conditional layout, depending on parent type and composition of children.
    // TODO: 11/14/2016 Add UI scripting for events. Use JavaScript.

    public enum AlignmentInParent {
        LEFT,
        RIGHT,
        CENTER
    }

    // TODO: 11/14/2016 Think of this from parent's point of view?
    public enum DimensionsInParent {
        EQUAL_WIDTH,
        FILL,
        WRAP_EACH
    }

    public static long uuidCounter = 0;

    public long uuid;

    public int androidViewId;

    // List of contained/composed elements
    public HashMap<Long, BuilderUiFlexibleBuildingBlock> children = new HashMap<>();
}
