package camp.computer.clay.system;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import java.util.UUID;

public class ContentManager implements ContentManagerInterface {

    private static String firebaseUri = "https://clay.firebaseio.com/";

    private Firebase rootRef = null;

    private Clay clay;

    public ContentManager(Clay clay) {
        this.clay = clay;

        this.enableFirebase ();
        this.startFirebase();

//        getBehaviors();

//        testAddUnit();
    }

    private void enableFirebase () {
        Firebase.setAndroidContext(Clay.getContext());
    }

    private void startFirebase () {
        this.rootRef = new Firebase (firebaseUri);
    }

    public Clay getClay () {
        return this.clay;
    }

    public void addUnit (Unit unit) {
        Firebase unitRef = rootRef.child ("units");
        unitRef.push ().setValue (unit);
    }

    public void getUnit (final UUID unitUuid) {

        final String unitUuidString = unitUuid.toString();

        Firebase unitsRef = rootRef.child("units");
//        Query unitQueryRef = unitsRef.orderByChild ("uuid").equalTo (unitUuidString);

        unitsRef.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getChildrenCount() == 0) {
                    Log.v("Clay_Database", "There is no unit with UUID equal to " + unitUuidString + ".");

                    // Save unit to database.
                    if (getClay().hasUnitByUuid(unitUuid)) {
                        Log.v("Clay_Database", "Saving unit to database.");
                        Unit unit = getClay().getUnitByUuid(unitUuid);
                        addUnit(unit);
                    } else {
                        Log.v("Clay_Database", "Failed to save unit to database. This entails undefined problems.");
                    }

                } else if (dataSnapshot.getChildrenCount() > 0) {

                    // Store unit in the local cache.
                    for (DataSnapshot unitSnapshot : dataSnapshot.getChildren()) {
                        // Create behavior object from database.
                        Unit retrievedUnit = unitSnapshot.getValue(Unit.class);
                        Log.v("Clay_Database", "Retrieved unit (UUID: " + retrievedUnit.getUuid() + ").");

                        // Update cached unit from database.
                        if (getClay().hasUnitByUuid(unitUuid)) {
                            Unit unit = getClay().getUnitByUuid(unitUuid);

                            // Updated the cached unit with information from the unit retrieved from the database.
                            // TODO: unit.setLoop () <-- retrievedUnit.getTimeline()

                            Log.v("Clay_Database", "Saving unit to database.");
                        } else {
                            Log.v("Clay_Database", "Failed to save unit to database. This entails undefined problems.");
                        }


                        // TODO: Update the state of unit in the cache. This includes (1) streaming in the current behavior.
                    }

                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("Clay_Database", "The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void addBehavior(Behavior behavior) {

        Firebase behaviorRef = rootRef.child("behaviors");
        behaviorRef.push ().setValue (behavior);

        /*
        // Behaviors/<uuid>
        String behaviorUuid = behavior.getUuid ().toString ();
        Firebase behaviorRef = rootRef.child ("Behaviors").child (behaviorUuid);

        // Behaviors/<uuid>/Transform
        String behaviorTransform = behavior.getDefaultState ();
        behaviorRef.child ("Transform").setValue  (behaviorTransform);
        */

        // Behaviors/<uuid>/Timeline
        // TODO: Create a list of the UUIDs that are part of the unit's behavior.
//        String behaviorLoop = behavior.getDefaultState ();
//        behaviorRef.child ("Transform").setValue  (behaviorTransform);

    }

    public void getBehaviorState (final Behavior behavior, UUID stateUuid) {
        Firebase behaviorStateRef = rootRef.child ("behaviorStates");
        Query queryRef = behaviorStateRef.orderByChild("uuid").equalTo(stateUuid.toString()).limitToFirst(1);
        queryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Store behaviors in the local cache.
                for (DataSnapshot behaviorSnapshot : dataSnapshot.getChildren()) {
                    // Create behavior object from database.
                    BehaviorState behaviorState = behaviorSnapshot.getValue (BehaviorState.class);
                    Log.v ("Clay_Behavior_Repo", "got behavior state " + behaviorState);
                    behavior.setState(behaviorState);

//                    Log.v("Clay_Behavior_Repo", "Adding behavior " + behavior.getTag() + " (UUID: " + behavior.getUuid() + ")");
//                    getClay ().getBehaviorCacheManager().addBehavior (behavior);
                }

                // Add the basic behaviors if they do not exist.
//                getClay ().getBehaviorCacheManager().verifyBasicBehaviors ();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("Clay_Behavior_Repo", "The read failed: " + firebaseError.getMessage());
            }
        });
    }

    public void getBehaviors () {

        Firebase behaviorRef = rootRef.child ("behaviors");
        behaviorRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                // Store behaviors in the local cache.
                for (DataSnapshot behaviorSnapshot : dataSnapshot.getChildren()) {
                    // Create behavior object from database.
                    Behavior behavior = behaviorSnapshot.getValue (Behavior.class);

//                    getClay().getContentManager().getBehaviorState(behavior, behavior.);

                    Log.v("Clay_Behavior_Repo", "Adding behavior " + behavior.getTag() + " (UUID: " + behavior.getUuid() + ")");
                    Log.v("Clay_Behavior_Repo", "Adding behavior state " + behavior.getState().getState() + " (UUID: " + behavior.getState().getUuid() + ")");
                    getClay ().getBehaviorCacheManager().addBehavior (behavior);
                }

                // Add the basic behaviors if they do not exist.
                getClay ().getBehaviorCacheManager().verifyBasicBehaviors ();
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("Clay_Behavior_Repo", "The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void addBehaviorState (BehaviorState behaviorState) {

        Firebase behaviorStateRef = rootRef.child("behaviorStates");
        behaviorStateRef.push ().setValue(behaviorState);

        /*
        // Behaviors/<uuid>
        String behaviorUuid = behavior.getUuid ().toString ();
        Firebase behaviorRef = rootRef.child ("Behaviors").child (behaviorUuid);

        // Behaviors/<uuid>/Transform
        String behaviorTransform = behavior.getDefaultState ();
        behaviorRef.child ("Transform").setValue  (behaviorTransform);
        */

        // Behaviors/<uuid>/Timeline
        // TODO: Create a list of the UUIDs that are part of the unit's behavior.
//        String behaviorLoop = behavior.getDefaultState ();
//        behaviorRef.child ("Transform").setValue  (behaviorTransform);

    }

//    public void setBehavior (Unit unit, Behavior behavior) {
//
//        String unitUuid = unit.getUuid ().toString ();
//
//        // Units/<uuid>
//        Firebase unitRef = rootRef.child ("Units").child (unitUuid);
//
//        // Units/<uuid>/Behavior
//        String behaviorUuid = behavior.getUuid ().toString ();
//        unitRef.child ("Behavior").setValue  (behaviorUuid);
//
//    }

//    public void testAddUnit () {
//        // TODO: String unitUuid = clay.getUnits().get(0).getUuid();
//        String unitUuid = UUID.randomUUID().toString();
//        Firebase unitRef = rootRef.child("units").child(unitUuid);
//
//        String behaviorUuid = UUID.randomUUID ().toString ();
//        unitRef.child ("behaviorUuid").setValue  (behaviorUuid);
//
//        Firebase unitInteractions = unitRef.child("interactions");
//        unitRef.child ("behaviorUuid").setValue  (behaviorUuid);
//
//        // Simulate interactions
//        ArrayList<String> interactions = new ArrayList<>();
//        interactions.add ("create loop");
//        interactions.add ("create behavior");
//        interactions.add ("remove behavior");
//        interactions.add ("focus on behavior");
//        interactions.add ("stop focus on behavior");
//
//        unitInteractions.push().setValue("power on");
//        Random random = new Random ();
//        for (int i = 0; i < 10; i++) {
//            int randomInteractionIndex = random.nextInt(interactions.size());
//            unitInteractions.push().setValue(interactions.get(randomInteractionIndex));
//        }
//        unitInteractions.push().setValue ("power off");
//    }
//
//    public void testDeleteUnit (UUID uuid) {
//        Firebase unitRef = rootRef.child ("units").child (uuid.toString());
//        unitRef.setValue(null);
//    }
}
