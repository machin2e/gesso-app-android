package computer.clay.protocolizer;

import android.graphics.Point;
import android.util.Log;

import java.util.ArrayList;

public class LoopConstruct {

    private Perspective perspective = null;
    private Unit unit = null;
//    private Loop loop = null;

    private ArrayList<BehaviorConstruct> behaviorConstructs = new ArrayList<BehaviorConstruct> ();

    LoopConstruct (Perspective perspective) {

        this.perspective = perspective;
        this.unit = null;

    }

    LoopConstruct (Perspective perspective, Unit unit) {

        this.perspective = perspective;
        this.unit = unit;

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

    // TODO: Point position

    public Unit getUnit () {
        return this.unit;
    }

    public Loop getLoop () {
        return this.getUnit ().getLoop ();
    }

    public ArrayList<BehaviorConstruct> getBehaviorConstructs () {
        return this.behaviorConstructs;
    }
}
