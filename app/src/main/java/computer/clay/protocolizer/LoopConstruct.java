package computer.clay.protocolizer;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

// TODO: loopConcept/loopBody/loopSubject/loopFrame, loopPerspective, loopBehavior/loopOperation

public class LoopConstruct { // TODO: Possibly renamed to LoopScaffold, LoopScaffold, LoopStructure, LoopMachine, LoopEngine, LoopUnit, LoopOperator

    public static int DEFAULT_RADIUS = 350;
    public static int DEFAULT_START_ANGLE = 15; // i.e., -75
    public static int DEFAULT_ANGLE_SPAN = 330;

    private Point position = new Point ();

    private int radius = DEFAULT_RADIUS;

    private int startAngle = DEFAULT_START_ANGLE;
    private int angleSpan = DEFAULT_ANGLE_SPAN;

    private Perspective perspective = null;
    private Unit unit = null;
//    private Loop loop = null;

    private ArrayList<LoopPerspective> candidateLoopPerspectives = new ArrayList<LoopPerspective>();
    private ArrayList<LoopPerspective> loopPerspectives = new ArrayList<LoopPerspective>();

    private ArrayList<BehaviorConstruct> behaviorConstructs = new ArrayList<BehaviorConstruct> ();

    LoopConstruct (Perspective perspective, Unit unit) {



        this.perspective = perspective;

        this.unit = unit;

        // TODO: Create a default loop and perspective for the placeholder.
//        Loop defaultLoop = new Loop(this.system);
//        LoopPerspective defaultLoopPerspective = new LoopPerspective(defaultLoop);

    }

    public Perspective getPerspective () {
        return this.perspective;
    }

    public boolean hasBehaviorConstructs () {
        return (this.behaviorConstructs.size () > 0);
    }

    public boolean hasBehaviorConstruct (BehaviorConstruct behaviorConstruct) {
        return this.behaviorConstructs.contains (behaviorConstruct);
    }

    public void addBehaviorConstruct (BehaviorConstruct behaviorConstruct) {

//        Log.v ("Behavior_Construct", "Adding behavior construct " + behaviorConstruct.getUuid ());

        // TODO: Make sure the behavior construct is in the perspective

        // Check if the behavior construct is already in the loop...
        if (!this.hasBehaviorConstruct (behaviorConstruct)) {

            // ...and update state of this behavior construct.
            behaviorConstruct.setLoopConstruct (this);
            behaviorConstruct.state = BehaviorConstruct.State.SEQUENCED;

            // ...if not, then add the behavior construct to the loop construct...
            this.behaviorConstructs.add (behaviorConstruct);

            // Associate the specified loop construct with this behavior construct...
//            this.loopConstruct = loopConstruct;

            // ...then add this behavior construct to the loop...
//            this.loopConstruct.addBehaviorConstruct (this);

            // ...and add the behavior to the loop.
            this.getLoop().addBehavior (behaviorConstruct.getBehavior ());
        }

        // Update the sequence order of behaviors based on the orientation of the behavior constructs on the loop construct.
        this.reorderBehaviors ();

        // <HACK>
        // Queue behavior transformation in the outgoing message queue.
        // e.g., create behavior <uuid> "turn light <number> on" --> Response: got <message>
        // e.g., (shorthand) "add behavior <uuid> to loop (<uuid>)"
        // e.g., "focus perspective on behavior <uuid>" (Changes perspective so implicit language refers to it.)
        String behaviorConstructUuid = behaviorConstruct.getUuid ().toString (); // HACK: BehaviorConstruct and Behavior should have separate UUIDs.

        // Get the UUID of the behavior prior to the one being added to the loop, or null if there's no previous behavior (i.e., the behavior is the first one in the loop).
        BehaviorConstruct nextBehaviorConstruct = null;
        for (int i = 0; i < this.getBehaviorConstructs ().size (); i++) {
            BehaviorConstruct currentBehaviorConstruct = this.getBehaviorConstructs ().get (i);
            if (currentBehaviorConstruct.getUuid ().compareTo (behaviorConstruct.getUuid ()) == 0) {
                if (i < (this.getBehaviorConstructs ().size () - 1)) {
                    nextBehaviorConstruct = this.getBehaviorConstructs ().get (i + 1);
                    break;
                }
            }
        }
        if (nextBehaviorConstruct != null) {
            Log.v ("Clay_Language", "Adding behavior prior to " + nextBehaviorConstruct.getUuid ().toString ());
        } else {
            Log.v ("Clay_Language", "Adding behavior prior to end of the list.");
        }

        getPerspective ().getClay ().getCommunication ().sendMessage (this.getUnit ().getInternetAddress (), "create behavior " + behaviorConstructUuid + " \"" + behaviorConstruct.getBehavior ().getTransform () + "\"");
        if (nextBehaviorConstruct != null) {
            // Add the behavior to the front of the loop
            getPerspective ().getClay ().getCommunication ().sendMessage (this.getUnit ().getInternetAddress (), "add behavior " + behaviorConstructUuid + " before " + nextBehaviorConstruct.getUuid ().toString ()); // TODO: "add behavior <behavior-uuid> to loop <loop-uuid> before <behavior-uuid>
        } else {
            // Add the behavior to the end of the loop
            getPerspective ().getClay ().getCommunication ().sendMessage (this.getUnit ().getInternetAddress (), "add behavior " + behaviorConstructUuid); // TODO: "add behavior <behavior-uuid> to loop <loop-uuid>
        }
        // </HACK>
    }

    public void removeBehaviorConstruct (BehaviorConstruct behaviorConstruct) {
        Log.v ("Clay_Remove_Behavior", "Removing behavior construct.");

        // ...then remove the behavior construct from the loop construct.
        if (this.behaviorConstructs.contains (behaviorConstruct)) {

            // Update state of the this behavior construct
            behaviorConstruct.setLoopConstruct (null);
            behaviorConstruct.state = BehaviorConstruct.State.FREE;

//            if (behaviorConstruct.hasLoopConstruct ()) {
//                behaviorConstruct.removeLoopConstruct ();
//            }

            this.behaviorConstructs.remove (behaviorConstruct);

            // Remove the behavior from the loop...
            this.getLoop ().removeBehavior (behaviorConstruct.getBehavior ());
        }

        // Update the sequence order of behaviors based on the orientation of the behavior constructs on the loop construct.
        this.reorderBehaviors ();

        Log.v ("Clay_Language", "Removing behavior " + behaviorConstruct.getUuid ().toString () + " from loop.");

        // <HACK>
        // Queue behavior transformation in the outgoing message queue.
        // e.g., "add behavior \"turn light 1 on\" to loop"
        // e.g., "remove behavior 1"
        String behaviorConstructUuid = behaviorConstruct.getUuid ().toString (); // HACK: BehaviorConstruct and Behavior should have separate UUIDs.
        String removeBehaviorMessage = "remove behavior " + behaviorConstructUuid + " from loop";
        Log.v ("Clay_Remove_Behavior", "Removing behavior construct " + behaviorConstructUuid + ".");
//        getPerspective ().getClay ().getCommunication ().sendMessage (this.getUnit ().getInternetAddress (), behaviorConstruct.getBehavior ().getTitle ());
//        getPerspective ().getClay ().getCommunication ().sendMessage (this.getUnit ().getInternetAddress (), "create behavior " + behaviorUuid + " \"" + behaviorConstruct.getBehavior ().getTitle () + "\"");
//        getPerspective ().getClay ().getCommunication ().sendMessage (this.getUnit ().getInternetAddress (), "add behavior " + behaviorUuid + " to loop");
//        getPerspective ().getClay ().getCommunication ().sendMessage (this.getUnit ().getInternetAddress (), "create behavior " + behaviorConstructUuid + " \"" + behaviorConstruct.getBehavior ().getTitle () + "\"");
        behaviorConstruct.setSynchronized (false);
        getPerspective ().getClay ().getCommunication ().sendMessage (this.getUnit ().getInternetAddress (), removeBehaviorMessage);
        // </HACK>
    }

    public ArrayList<LoopPerspective> getLoopPerspectives() {
        return loopPerspectives;
    }

    // TODO: Point position

    public Unit getUnit () {
        return this.unit;
    }

    public Loop getLoop () {
        return this.getUnit ().getLoop ();
    }

    // TODO: double getRadius (double angle);

    public boolean hasCandidatePerspective (Loop loop) {
        for (LoopPerspective loopPerspective : this.candidateLoopPerspectives) {
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                return true;
            }
        }
        return false;
    }

    public void setCandidatePerspective(LoopPerspective loopPerspective) {
        if (!this.candidateLoopPerspectives.contains(loopPerspective)) {
            this.candidateLoopPerspectives.add(loopPerspective);
        }
    }

    public LoopPerspective getCandidatePerspective (Loop loop) {
        for (LoopPerspective loopPerspective : this.candidateLoopPerspectives) {
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                return loopPerspective;
            }
        }
        return null;
    }

    public void removeCandidatePerspective(Loop loop) {

        for (int i = 0; i < this.candidateLoopPerspectives.size(); i++) {
            LoopPerspective loopPerspective = this.candidateLoopPerspectives.get(i);
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                this.candidateLoopPerspectives.remove(i);
            }
        }
    }

    /**
     * Checks if there's a perspective for the specified loop. Returns true if so, and returns
     * false if not.
     *
     * @param loop The loop for which to search for a perspective.
     * @return True if there is a perspective on the specified loop and false otherwise.
     */
//    public boolean hasPerspectives (Loop loop) {
//        for (LoopPerspective loopPerspective : this.loopPerspectives) {
//            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
//                return true;
//            }
//        }
//        return false;
//    }
    public boolean hasPerspectives () {
        return this.loopPerspectives.size () > 0;
    }

    public boolean hasPerspective (double angle) {
        // TODO: Check if there is a perspective that starts before and ends after the specified angle

//        LoopPerspective nearestLoopPerspective = null;
//        Log.v ("Clay_Loop_Perspective", "# PERSPECTIVES FOR NEAREST LOOP = " + this.perspective.getLoopConstruct (nearestLoop).getPerspectives (nearestLoop).size ());
        for (LoopPerspective loopPerspective : this.loopPerspectives) {
            double startAngle = loopPerspective.startAngle;
            double stopAngle = loopPerspective.startAngle + loopPerspective.span;
            Log.v("Clay_Loop_Perspective", "startAngle = " + startAngle);
            Log.v("Clay_Loop_Perspective", "stopAngle = " + stopAngle);

            // Check which perspective the behavior is in range of.
            if (startAngle < angle && angle < stopAngle) {
//                Log.v("Clay_Loop_Perspective", "nearestPerspective FOUND");

                // Select the loop perspective since.
                return true;
//                nearestLoopPerspective = loopPerspective;
//                break;
            }
        }
        return false;
    }

    public LoopPerspective getPerspective (double angle) {
        // TODO: Check if there is a perspective that starts before and ends after the specified angle

//        LoopPerspective nearestLoopPerspective = null;
//        Log.v ("Clay_Loop_Perspective", "# PERSPECTIVES FOR NEAREST LOOP = " + this.perspective.getLoopConstruct (nearestLoop).getPerspectives (nearestLoop).size ());
        for (LoopPerspective loopPerspective : this.loopPerspectives) {
            double startAngle = loopPerspective.startAngle;
            double stopAngle = loopPerspective.startAngle + loopPerspective.span;
//            Log.v("Clay_Loop_Perspective", "startAngle = " + startAngle);
//            Log.v("Clay_Loop_Perspective", "stopAngle = " + stopAngle);

            // Check which perspective the behavior is in range of.
            if (startAngle < angle && angle < stopAngle) {
//                Log.v("Clay_Loop_Perspective", "nearestPerspective FOUND");

                // Select the loop perspective since.
                return loopPerspective;
//                nearestLoopPerspective = loopPerspective;
//                break;
            }
        }
        return null;
    }

    LoopPerspective getPerspective (Point point) {
        double angle = this.getAngle (point);
        return this.getPerspective (angle);
    }

    // TODO: Replace with createPerspective
    public void addPerspective (LoopPerspective loopPerspective) {
        // TODO: Create new perspectives as needed!

        // --- becomes --- + --- + ---

        Log.v ("Clay_New_Perspectives", "loopPerspectives.size() = " + this.loopPerspectives.size ());

        if (this.loopPerspectives.size() == 0) {

            this.loopPerspectives.add (loopPerspective);

        } else if (this.loopPerspectives.size() == 1) {

            LoopPerspective existingLoopPerspective = this.loopPerspectives.get(0);

            // Add "complementary" loop perspective after the newly-created perspective.
            LoopPerspective complementaryLoopPerspective = new LoopPerspective (this, loopPerspective.getStopAngle(), (existingLoopPerspective.getStopAngle() - loopPerspective.getStopAngle()));

            // Update existing loop perspective to span the range before the newly-created perspective.
            existingLoopPerspective.setSpan(loopPerspective.getStartAngle() - existingLoopPerspective.getStartAngle());

            // Link the perspectives together.
            loopPerspective.setPreviousPerspective(existingLoopPerspective); // Previous
            existingLoopPerspective.setNextPerspective(loopPerspective);
            loopPerspective.setNextPerspective (complementaryLoopPerspective); // Next
            complementaryLoopPerspective.setPreviousPerspective(loopPerspective);

            // Add the new loop perspectives to the loop construct.
            this.loopPerspectives.add(loopPerspective);
            this.loopPerspectives.add(complementaryLoopPerspective);

        } else {

            // TODO: Get all the existing perspectives that are partially (at the beginning or end) or entirely enclosed in the new perspective.
            LoopPerspective existingLoopPerspective = this.getPerspective (loopPerspective.getStartAngle());

            // Add "complementary" loop perspective after the newly-created perspective.
            LoopPerspective complementaryLoopPerspective = new LoopPerspective(this, loopPerspective.getStopAngle(), (existingLoopPerspective.getStopAngle() - loopPerspective.getStopAngle()));

            // Update existing loop perspective to span the range before the newly-created perspective.
            existingLoopPerspective.setSpan(loopPerspective.getStartAngle() - existingLoopPerspective.getStartAngle());

            // Link the perspectives together.
            loopPerspective.setPreviousPerspective(existingLoopPerspective); // Previous
            existingLoopPerspective.setNextPerspective(loopPerspective);
            loopPerspective.setNextPerspective (complementaryLoopPerspective); // Next
            complementaryLoopPerspective.setPreviousPerspective(loopPerspective);

            // Add the new loop perspectives to the loop construct.
            this.loopPerspectives.add(loopPerspective);
            this.loopPerspectives.add(complementaryLoopPerspective);

        }
//        else {
//
//            this.loopPerspectives.add(loopPerspective);
//
//        }

        // TODO: Sort the list of perspectives by their order
    }

    public ArrayList<LoopPerspective> getPerspectives () {
        return this.loopPerspectives;
    }

    public void removePerspective(Loop loop) {

        for (int i = 0; i < this.loopPerspectives.size(); i++) {
            LoopPerspective loopPerspective = this.loopPerspectives.get(i);
            if (loop == loopPerspective.getLoopConstruct ().getLoop ()) {
                this.loopPerspectives.remove(i);
            }
        }
    }

    public void removePerspective (LoopPerspective loopPerspective) {
        this.loopPerspectives.remove (loopPerspective);
    }

    public Point getPosition () {
        return this.position;
    }

    public int getRadius () {
        return this.radius;
    }

    public int getStartAngle () {
        return this.startAngle;
    }

    public int getAngleSpan () {
        return this.angleSpan;
    }

    /**
     * Calculates the distance between the center of the loop and the specified point.
     * @param x
     * @param y
     * @return
     */
    public double getDistance (int x, int y) {
        double distanceSquare = Math.pow (x - this.position.x, 2) + Math.pow (y - this.position.y, 2);
        double distance = Math.sqrt(distanceSquare);
        return distance;
    }

    /**
     * Get the angle at which the specified point falls relative to the center point of the loop.
     */
    public double getAngle (int x, int y) {
        Point startAngle = this.getPoint (this.startAngle);
        Point stopAngle = new Point (x, y);
        double angle = this.getAngle (startAngle, stopAngle);
        return angle;
    }

    /**
     * Get the angle at which the specified point falls with respect to the center of the loop.
     *
     * @param point The point that defines the line, along with the point at the center of the loop, for which the angle will be determined.
     * @return The angle of the line formed by the specified point and the center point of the loop.
     */
    public double getAngle (Point point) {
        Point startAngle = this.getPoint (this.startAngle);
        double angle = this.getAngle (startAngle, point);
        return angle;
    }

    /**
     * Calculates and returns the angle (in degrees) between the specified points and the center
     * point of the loop.
     *
     * @param startingPoint The endpoint of the line from which the angle will be measured.
     * @param endingPoint The endpoint of the line forming the stopping angle.
     * @return The angle between the two lines formed by the specified points and the center point of the loop.
     */
    public double getAngle (Point startingPoint, Point endingPoint) {
        Point p1 = this.getPosition (); // The center point is p1.

        double a = startingPoint.x - p1.x;
        double b = startingPoint.y - p1.y;
        double c = endingPoint.x - p1.x;
        double d = endingPoint.y - p1.y;

        double atanA = Math.atan2 (a, b);
        double atanB = Math.atan2 (c, d);

        double result = Math.toDegrees (atanA - atanB);

        return result;
    }

    /**
     * Calculates the point on the circumference of the circle at the specified angle (in degrees).
     */
    public Point getPoint (double angle) {
        Point point = new Point ();
        double angleInRadians = Math.toRadians (this.startAngle + angle); // ((90.0 - angle) + angle);
        double x = this.getPosition ().x + this.getRadius () * Math.cos (angleInRadians);
        double y = this.getPosition ().y + this.getRadius () * Math.sin (angleInRadians);
        point.set ((int) x, (int) y);
        return point;
    }

    /**
     * Calculates the point on the circumference of the circle at the specified angle (in degrees).
     */
    public Point getPoint (double angle, double radius) {
        Point point = new Point ();
        double angleInRadians = Math.toRadians(this.startAngle + angle); // ((90.0 - angle) + angle);
        double x = this.getPosition ().x + radius * Math.cos (angleInRadians);
        double y = this.getPosition ().y + radius * Math.sin (angleInRadians);
        point.set ((int) x, (int) y);
        return point;
    }

    public ArrayList<BehaviorConstruct> getBehaviorConstructs () {
        return this.behaviorConstructs;
    }

    /**
     * Returns the behavior prior to the specified angle. This method assumes that behaviors are
     * stored in ascending order of their angles on the loop.
     */
    public BehaviorConstruct getBehaviorBeforeAngle (double angle) {

        // Calculate angles along the loop for each behavior
//        ArrayList<Double> behaviorAngles = new ArrayList<Double>();
        String behaviorAngles = "";
        for (BehaviorConstruct behaviorConstruct : this.getBehaviorConstructs ()) {
            Point behaviorPosition = behaviorConstruct.getPosition();
            double behaviorAngle = this.getAngle(behaviorPosition);
            behaviorAngles += behaviorAngle + ", ";
        }
//        Log.v ("Condition", "behaviorAngles = " + behaviorAngles);


        BehaviorConstruct previousBehavior = null;
        BehaviorConstruct behaviorBeforeAngle = null;
        for (BehaviorConstruct behaviorConstruct : this.getBehaviorConstructs()) {
            Point behaviorPosition = behaviorConstruct.getPosition ();
            double behaviorAngle = this.getAngle (behaviorPosition);

//            Log.v ("Condition", "angle = " + angle);
//            Log.v ("Condition", "behaviorAngle = " + behaviorAngle);

            if (behaviorAngle < angle) {
//                previousBehavior = behavior;
                behaviorBeforeAngle = behaviorConstruct;
            } else {
//                behaviorBeforeAngle = previousBehavior;
                break;
            }

//            Log.v("Condition", "behaviorBeforeAngle.angle = " + behaviorAngle);
        }

        return behaviorBeforeAngle;
    }

    /**
     * Returns the behavior prior to the specified angle. This method assumes that behaviors are
     * stored in ascending order of their angles on the loop.
     */
    public BehaviorConstruct getBehaviorAfterAngle (double angle) {

        // Calculate angles along the loop for each behavior
//        ArrayList<Double> behaviorAngles = new ArrayList<Double>();
        String behaviorAngles = "";
        for (BehaviorConstruct behaviorConstruct : this.getBehaviorConstructs ()) {
            Point behaviorPosition = behaviorConstruct.getPosition ();
            double behaviorAngle = this.getAngle (behaviorPosition);
            behaviorAngles += behaviorAngle + ", ";
        }
//        Log.v ("Condition", "behaviorAngles = " + behaviorAngles);


        BehaviorConstruct behaviorAfterAngle = null;
        for (BehaviorConstruct behaviorConstruct : this.getBehaviorConstructs()) {
            Point behaviorPosition = behaviorConstruct.getPosition ();
            double behaviorAngle = this.getAngle (behaviorPosition);

//            Log.v ("Condition", "angle = " + angle);
//            Log.v ("Condition", "behaviorAngle = " + behaviorAngle);

            if (behaviorAngle < angle) {
//                previousBehavior = behavior;
//                behaviorBeforeAngle = behavior;
            } else {
                behaviorAfterAngle = behaviorConstruct;
//                behaviorBeforeAngle = previousBehavior;
                break;
            }

//            Log.v("Condition", "behaviorAfterAngle.angle = " + behaviorAngle);
        }

        return behaviorAfterAngle;
    }

    /**
     * Returns the behavior condition at the specified angle. This method assumes that behaviors
     * are stored in ascending order of their angles on the loop.
     */
    public BehaviorTrigger getBehaviorConditionAtAngle (double angle) {

        BehaviorTrigger behaviorTrigger = null;

        BehaviorConstruct behaviorAfterAngle = this.getBehaviorAfterAngle (angle);
        if (behaviorAfterAngle != null) {
            behaviorTrigger = behaviorAfterAngle.getCondition();
        }

        return behaviorTrigger;
    }

    /**
     * Updates the ordering of the behaviors on the loop based on their position along the loop.
     */
    public void reorderBehaviors () {
        // Re-order the behaviors based on their sequence ordering

        // Calculate angles along the loop for each behavior
        ArrayList<Double> behaviorAngles = new ArrayList<Double> ();
        for (BehaviorConstruct behaviorConstruct : this.getBehaviorConstructs ()) {
            Point behaviorPosition = behaviorConstruct.getPosition ();
            double behaviorAngle = this.getAngle (behaviorPosition);
            behaviorAngles.add (behaviorAngle);
            Log.v("Clay", "Behavior " + behaviorAngle + " = " + behaviorAngle);
        }

        // Sort the list of behaviors based on the sort manipulations done to sort the angles in ascending order.
        for (int i = 0; i < this.getBehaviorConstructs ().size (); i++) { // for (int i = 0; i < this.getLoop ().getBehaviors ().size (); i++) {
            for (int j = 0; j < this.getBehaviorConstructs().size () - 1; j++) { // for (int j = 0; j < this.getLoop ().getBehaviors ().size () - 1; j++) {
                if (behaviorAngles.get (j) > behaviorAngles.get (j + 1)) {

                    // Swap angle
                    double angleToSwap = behaviorAngles.get(j);
                    behaviorAngles.set(j, behaviorAngles.get(j + 1));
                    behaviorAngles.set(j + 1, angleToSwap);

                    // Swap behavior
                    BehaviorConstruct behaviorToSwap = this.getBehaviorConstructs ().get(j); // BehaviorConstruct behaviorToSwap = this.getLoop ().getBehaviors ().get (j);
                    this.getBehaviorConstructs ().set(j, this.getBehaviorConstructs().get(j + 1)); // this.getLoop ().getBehaviors ().set (j, getLoop ().getBehaviors ().get (j + 1));
                    this.getBehaviorConstructs ().set(j + 1, behaviorToSwap); // this.getLoop ().getBehaviors ().set (j + 1, behaviorToSwap);
                }
            }
        }

        String loopSequence = "";
        for (BehaviorConstruct behaviorConstruct : this.getBehaviorConstructs ()) { // for (BehaviorConstruct behavior : this.getLoop ().getBehaviors ()) {
            loopSequence += behaviorConstruct.getBehavior ().getTitle () + " "; // loopSequence += behavior.getBehavior().getTitle() + " ";
        }
        Log.v ("Clay_Loop_Construct", loopSequence);
    }

    public LoopPerspective createPerspective (int startAngle, int span) {
        LoopPerspective defaultLoopPerspective = new LoopPerspective (this, startAngle, span);
        this.addPerspective (defaultLoopPerspective);
        return defaultLoopPerspective;
    }
}
