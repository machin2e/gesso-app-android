package computer.clay.protocolizer;

import java.util.ArrayList;

public class BehaviorRepository {

    private Clay clay;

    private ArrayList<String> repositoryUris = new ArrayList<String>();
    private ArrayList<Behavior> cachedBehaviors = new ArrayList<Behavior>();

    BehaviorRepository (Clay clay) {

        // Associate this behavior repository with Clay.
        this.clay = clay;

        // HACK: Set up some sample behaviors
        // TODO: Load these from a server! Or recover them from the local cache.
        this.setupTestRepository();

        // TODO: Populate repository with basic behavior transforms.
    }

    private void setupTestRepository () {

        Behavior behavior = null;

        behavior = new Behavior ("control");
        this.cachedBehaviors.add (behavior);

        behavior = new Behavior ("time");
        this.cachedBehaviors.add (behavior);

        behavior = new Behavior ("cause/effect");
        this.cachedBehaviors.add (behavior);

        behavior = new Behavior ("message");
        this.cachedBehaviors.add (behavior);

        behavior = new Behavior ("say");
        this.cachedBehaviors.add (behavior);

        /*
        final CharSequence[] items = {
                "channel",
                "time",
                "cause/effect",
                "message",
                "say",

                "reset",
                "condition",
                "connect component",
                "request",
                "memory",

//                "turn light 1 on",
//                "turn light 1 off",
//                "turn light 2 on",
//                "turn light 2 off",
//                "turn lights on",
//                "turn lights off",
//                "wait 200 ms",
//                "wait 1000",
//                "say \"i sense a soul in search of answers\"",
//                "slowly say it's done",
//                "quickly say it's done",
//                "request plug the sensor's signal wire into channel 6. i am blinking it for you.",
//                "request connect ground",
//                "request connect power"
        };
        */

//        Behavior lightBehavior = new Behavior("light"); // e.g., "turn on lights  3 8 9 10 12"
//        this.cachedBehaviors.add(lightBehavior);
//        Behavior motionBehavior = new Behavior("motion");
//        this.cachedBehaviors.add(motionBehavior);
//        Behavior gestureBehavior = new Behavior("gesture");
//        this.cachedBehaviors.add(gestureBehavior);
//        Behavior timeBehavior = new Behavior("time"); // e.g., "delay 1 second"
//        this.cachedBehaviors.add(timeBehavior);
//        Behavior communicationBehavior = new Behavior("communication");
//        this.cachedBehaviors.add(communicationBehavior);
//        Behavior controlBehavior = new Behavior("control"); // e.g., "turn on 3 9 12"
//        this.cachedBehaviors.add(controlBehavior);
//        Behavior soundBehavior = new Behavior("sound");
//        this.cachedBehaviors.add(soundBehavior);
//        Behavior speechBehavior = new Behavior("speech");
//        this.cachedBehaviors.add(speechBehavior);
//        Behavior serviceBehavior = new Behavior("service");
//        this.cachedBehaviors.add(serviceBehavior);
    }

    public void addRepositoryUri (String repositoryUri) {
        this.repositoryUris.add (repositoryUri);
    }

    public void hasBehavior (String behaviorUri) {
        // TODO: Search current set of repositories for the specified behavior
    }

    public Behavior getBehavior (String behaviorUri) {
        // TODO: Return the behavior with the specified URI or null.
        return null;
    }

    public ArrayList<Behavior> getCachedBehaviors () {
        return this.cachedBehaviors;
    }
}
