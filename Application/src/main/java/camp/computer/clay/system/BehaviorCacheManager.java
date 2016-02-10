package camp.computer.clay.system;

import android.util.Log;

import java.util.ArrayList;

public class BehaviorCacheManager {

    private Clay clay;

    private ArrayList<String> repositoryUris = new ArrayList<String>();
    private ArrayList<Behavior> cachedBehaviors = new ArrayList<Behavior>();

    BehaviorCacheManager(Clay clay) {

        // Associate this behavior repository with Clay.
        this.clay = clay;

        // HACK: Set up some sample behaviors
        // TODO: Load these from a server! Or recover them from the local cache.
        initializeRepository ();

        // TODO: Populate repository with basic behavior transforms.
    }

    public Clay getClay () {
        return this.clay;
    }

    private void initializeRepository () {
        // TODO: First check if a content manager exists that can access the remote repository URI. If so, use it.
        getClay ().getContentManager().getBehaviors ();
    }

    public void addBehavior (Behavior behavior) {
        Log.v("Clay_Behavior_Repo", "Adding behavior to repository.");
        this.cachedBehaviors.add(behavior);
    }

    public void verifyBasicBehaviors () {

        Behavior behavior = null;

//        if (!hasBehaviorByTitle ("control")) {
//            Log.v("Clay_Behavior_Repo", "\"control\" behavior not found in the repository. Adding it.");
//            behavior = new Behavior("control");
//            this.cachedBehaviors.add(behavior);
//            getClay().getContentManager().addBehavior(behavior);
//        }
//
//        if (!hasBehaviorByTitle ("time")) {
//            Log.v("Clay_Behavior_Repo", "\"time\" behavior not found in the repository. Adding it.");
//            behavior = new Behavior("time");
//            this.cachedBehaviors.add(behavior);
//            getClay().getContentManager().addBehavior(behavior);
//        }
//
//        if (!hasBehaviorByTitle ("cause/effect")) {
//            Log.v("Clay_Behavior_Repo", "\"cause/effect\" behavior not found in the repository. Adding it.");
//            behavior = new Behavior("cause/effect");
//            this.cachedBehaviors.add(behavior);
//            getClay().getContentManager().addBehavior(behavior);
//        }

        if (!hasBehaviorByTitle ("lights")) {
            Log.v("Clay_Behavior_Repo", "\"lights\" behavior not found in the repository. Adding it.");
            createBehavior("lights", "F F F F F F F F F F F F");
        }

        if (!hasBehaviorByTitle ("io")) {
            Log.v("Clay_Behavior_Repo", "\"lights\" behavior not found in the repository. Adding it.");
            createBehavior("io", "FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL");
        }

        if (!hasBehaviorByTitle ("message")) {
            Log.v("Clay_Behavior_Repo", "\"message\" behavior not found in the repository. Adding it.");
            createBehavior("message", "hello");
        }

        if (!hasBehaviorByTitle ("wait")) {
            Log.v("Clay_Behavior_Repo", "\"wait\" behavior not found in the repository. Adding it.");
            createBehavior("wait", "250");
        }

        if (!hasBehaviorByTitle ("say")) {
            Log.v("Clay_Behavior_Repo", "\"say\" behavior not found in the repository. Adding it.");
            createBehavior("say", "oh, that's great");
        }

        // TODO:
        // - Add "choose behavior" behavior which tells Clay to ask to "select one of the following menu of curated behaviors and I will perform it."

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

    private void createBehavior (String tag, String state) {

        // Create behavior
        Behavior behavior = new Behavior(tag, state);

        // Cache the behavior locally
        this.addBehavior(behavior);

        // Store the behavior remotely
        getClay().getContentManager().addBehaviorState(behavior.getState());
        getClay().getContentManager().addBehavior(behavior);
    }

    private boolean hasBehaviorByTitle (String title) {
        for (Behavior cachedBehavior : this.cachedBehaviors) {
            if (cachedBehavior.getTag().equals(title)) {
                return true;
            }
        }
        return false;
    }

    public void addRepositoryUri (String repositoryUri) {
        this.repositoryUris.add (repositoryUri);
    }

    public boolean hasBehavior (String behaviorUuid) {

        // Search cached behaviors.
        for (Behavior cachedBehavior : this.cachedBehaviors) {
            if (cachedBehavior.getUuid().equals(behaviorUuid)) {
                return true;
            }
        }

        // TODO: Search online behavior repository.

        return false;
    }

    public Behavior getBehavior (String behaviorUuid) {

        // Search cached behaviors.
        for (Behavior cachedBehavior : this.cachedBehaviors) {
            if (cachedBehavior.getUuid().toString().equals(behaviorUuid)) {
                return cachedBehavior;
            }
        }

        // TODO: Search online behavior repository.

        return null;
    }

    public ArrayList<Behavior> getCachedBehaviors () {
        return this.cachedBehaviors;
    }
}
