package computer.clay.protocolizer;

import android.util.Log;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

public class Database {

    private static String firebaseUri = "https://clay.firebaseio.com/";

    private Firebase rootRef = null;

    private Clay clay;

    public Database(Clay clay) {
        this.clay = clay;

        this.enableFirebase ();
        this.startFirebase();

        getBehaviors();

//        testAddUnit();
    }

    private void enableFirebase () {
        Firebase.setAndroidContext(Clay.getPlatformContext());
    }

    private void startFirebase () {
        this.rootRef = new Firebase (firebaseUri);
    }

    public Clay getClay () {
        return this.clay;
    }

    public void addBehaviorToRepository (Behavior behavior) {

        // Behaviors/<uuid>
        String behaviorUuid = behavior.getUuid ().toString ();
        Firebase behaviorRef = rootRef.child ("Behaviors").child (behaviorUuid);

        // Behaviors/<uuid>/Transform
        String behaviorTransform = behavior.getTransform ();
        behaviorRef.child ("Transform").setValue  (behaviorTransform);

        // Behaviors/<uuid>/Loop
        // TODO: Create a list of the UUIDs that are part of the unit's behavior.
//        String behaviorLoop = behavior.getTransform ();
//        behaviorRef.child ("Transform").setValue  (behaviorTransform);

    }

    public void getBehaviors () {

        // Firebase unitRef = rootRef.child ("Units").child (unitUuid);
        Firebase unitRef = new Firebase ("https://clay.firebaseio.com/Units");

        unitRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Log.v("Firebase", child.getValue().toString());
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.v("Firebase", "The read failed: " + firebaseError.getMessage());
            }
        });

    }

    public void setBehavior (Unit unit, Behavior behavior) {

        String unitUuid = unit.getUuid ().toString ();

        // Units/<uuid>
        Firebase unitRef = rootRef.child ("Units").child (unitUuid);

        // Units/<uuid>/Behavior
        String behaviorUuid = behavior.getUuid ().toString ();
        unitRef.child ("Behavior").setValue  (behaviorUuid);

    }

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
