package camp.computer.clay.system;

import android.util.Log;

import java.util.ArrayList;
import java.util.UUID;

public class BehaviorCacheManager {

    private Clay clay;

    private ArrayList<String> repositoryUris = new ArrayList<String>();
    private ArrayList<Behavior> cachedBehaviors = new ArrayList<Behavior>();

    BehaviorCacheManager(Clay clay) {

        // Associate this behavior repository with Clay.
        this.clay = clay;

        // HACK: Set up some sample behaviors
        // TODO: Load these from a server! Or recover them from the local cache.
        populateCache();

        // TODO: Populate repository with basic behavior transforms.
    }

    public Clay getClay () {
        return this.clay;
    }

    private void populateCache() {
        Log.v("CM_Log", "populateCache");
        // TODO: First check if a content manager exists that can access the remote repository URI. If so, use it.
        getClay ().getContentManager().restoreBehaviors();
    }

    // TODO: populate: initial get all behaviors and cache a select subset
    // TODO: cache: add a single behavior to the cache
    // TODO: persist: cache a single behavior and keep it there
    // TODO: free: removes a single behavior from the cache

    /**
     * Caches the specified behavior.
     * @param behavior
     */
    public void cacheBehavior(Behavior behavior) {
        this.add(behavior);
    }

    private void add (Behavior behavior) {
        Log.v("Behavior_DB", "Adding behavior to repository.");
        this.cachedBehaviors.add(behavior);
    }

    public void setupRepository () {

        Behavior behavior = null;

        if (!hasBehaviorByTitle ("lights")) {
            Log.v("Clay_Behavior_Repo", "\"lights\" behavior not found in the repository. Adding it.");
            getClay().createBehavior("lights", "F F F F F F F F F F F F");
        }

        if (!hasBehaviorByTitle ("io")) {
            Log.v("Clay_Behavior_Repo", "\"lights\" behavior not found in the repository. Adding it.");
            getClay().createBehavior("io", "FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL");
        }

        if (!hasBehaviorByTitle ("message")) {
            Log.v("Clay_Behavior_Repo", "\"message\" behavior not found in the repository. Adding it.");
            getClay().createBehavior("message", "hello");
        }

        if (!hasBehaviorByTitle ("wait")) {
            Log.v("Clay_Behavior_Repo", "\"wait\" behavior not found in the repository. Adding it.");
            getClay().createBehavior("wait", "250");
        }

        if (!hasBehaviorByTitle ("say")) {
            Log.v("Clay_Behavior_Repo", "\"say\" behavior not found in the repository. Adding it.");
            getClay().createBehavior("say", "oh, that's great");
        }
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
//            Log.v("Behavior_DB", "1: " + cachedBehavior.getUuid().toString());
//            Log.v("Behavior_DB", "*: " + behaviorUuid);
            if (cachedBehavior.getUuid().toString().equals(behaviorUuid)) {
//                Log.v("Behavior_DB", "Match!");
                return true;
            }
        }

        // TODO: Search online behavior repository.

        return false;
    }

    public Behavior getBehavior (UUID behaviorUuid) {

        // Search cached behaviors.
        for (Behavior cachedBehavior : this.cachedBehaviors) {
            if (cachedBehavior.getUuid().equals(behaviorUuid)) {
                return cachedBehavior;
            }
        }

        // TODO: Search online behavior repository.
//        getClay().getContentManager().restoreBehavior(behaviorUuid);
//        get the behavior
//        getClay().getContentManager().restoreBehavior (behaviorUuid);

        return null;
    }

    public ArrayList<Behavior> getCachedBehaviors () {
        return this.cachedBehaviors;
    }
}
