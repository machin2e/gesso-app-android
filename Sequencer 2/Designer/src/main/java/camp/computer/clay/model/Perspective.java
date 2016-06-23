package camp.computer.clay.model;

import android.graphics.PointF;

public class Perspective {
    // TODO: Move position into Body, so can share Perspective among different bodies
    // ^ actually NO, because then a Body couldn't adopt a different Perspective
    PointF position; // Center position --- interactions (e.g., touches) are interpreted relative to this point

    float width; // Width of perspective --- interactions (e.g., touches) are interpreted relative to this point
    float height; // Height of perspective
}
