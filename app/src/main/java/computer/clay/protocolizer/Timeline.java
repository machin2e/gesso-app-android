package computer.clay.protocolizer;

public class Timeline {

    private Clay clay = null;

    Timeline (Clay clay) {
        this.clay = clay;
    }

    public Clay getClay () {
        return this.clay;
    }

//    public void addEvent (Unit unit, String event) {
//
//        String unitUuid = unit.getUuid ().toString ();
//
//        // Units/<uuid>
//        Firebase unitRef = rootRef.child ("Units").child (unitUuid);
//
//        // Units/<uuid>/Events
//        Firebase unitEvent = unitRef.child ("Events").push ();
//        unitEvent.child("Title").setValue (event);
//
//        SimpleDateFormat timeFormat = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
//        String currentTimestamp = timeFormat.format (getClay ().getDate ());
//        unitEvent.child("Time").setValue (currentTimestamp);
//
//        /*
//        // Units/<uuid>/Interactions
//        Firebase unitInteractions = unitRef.child("Interactions");
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
//        */
//    }
}
