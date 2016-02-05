package camp.computer.clay.system;

import android.util.Log;

import java.util.UUID;

public class Behavior {

    private UUID uuid;

    private String title;
    private String description;
    private String transform;

    public Behavior() {
        // This empty default constructor is necessary for Firebase to be able to deserialize objects.
    }

    Behavior(String title) {
        this.uuid = UUID.randomUUID();

        this.title = title;
        this.description = "";
        this.transform = "";
    }

    public UUID getUuid () {
        return this.uuid;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public String getTitle () {
        return this.title;
    }

    public void setDescription (String description) {
        this.description = description;
    }

    public String getDescription () {
        return this.description;
    }

    public void setTransform (String transform) {
        this.transform = transform;
    }

    public String getTransform () {
        return this.transform;
    }

    public void perform () {
        // TODO: Perform the action, whatever it is!

        Log.v("Clay", "Performing behavior " + this.getTitle() + ".");
    }
}
