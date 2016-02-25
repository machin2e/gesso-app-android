package camp.computer.clay.system;

import android.content.Context;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import camp.computer.clay.sequencer.ApplicationView;

public class ContentManager {

    private Clay clay;

    private String type;

    public ContentManager(Clay clay, String type) {
        this.type = type;

        createBasicBehaviors();

//        if (hasBehaviors()) {
//            getBehaviors();
//        } else {
//            createBasicBehaviors();
//        }
    }

    public Clay getClay () {
        return ApplicationView.getApplicationView().getClay();
    }

    private boolean hasBehaviors () {

        String FILENAME_PREFIX = "clay_";
        String BEHAVIOR_FILENAME = FILENAME_PREFIX + "behaviors";

        Context context = ApplicationView.getContext();

        // Check if file exists
        boolean fileExists = false;
        String[] fileList = context.fileList();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].equals(BEHAVIOR_FILENAME)) {
                fileExists = true;
                break;
            }
        }

        return fileExists;
    }

    private void createBasicBehaviors () {

        String FILENAME_PREFIX = "clay_";
        String BEHAVIOR_FILENAME = FILENAME_PREFIX + "behaviors";

        Context context = ApplicationView.getContext();
        FileOutputStream fos = null;
        FileInputStream fis = null;

        // Check if file exists
        boolean fileExists = false;
        String[] fileList = context.fileList();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].equals(BEHAVIOR_FILENAME)) {
                fileExists = true;
                break;
            }
        }

        if (fileExists) {
            Log.v("Content_Manager", "File exists.");

            // Delete file
            Log.v("Content_Manager", "Deleting file.");
            context.deleteFile(BEHAVIOR_FILENAME);

            fileExists = false;
        } else {
            Log.v("Content_Manager", "File DOES NOT exists.");
        }

        // Open file
        try {
            fos = context.openFileOutput(BEHAVIOR_FILENAME, Context.MODE_PRIVATE);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        // Write to file
        try {
//            String[] basicBehaviors = new String[] lights io message wait say

            getClay().createBehavior("lights", "F F F F F F F F F F F F");
            getClay().createBehavior("io", "FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL");
            getClay().createBehavior("message", "hello");
            getClay().createBehavior("wait", "250");
            getClay().createBehavior("say", "oh, that's great");

            for (Behavior behavior : getClay().getBehaviorCacheManager().getCachedBehaviors()) {

                String line = behavior.getUuid() + "\t" + behavior.getTag() + "\t" + behavior.getDefaultState() + "\n";

                fos.write(line.getBytes());

            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Close file
        try {
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    void getBehaviors () {

        String FILENAME_PREFIX = "clay_";
        String BEHAVIOR_FILENAME = FILENAME_PREFIX + "behaviors";

        Context context = ApplicationView.getContext();
        FileInputStream fis = null;

        // Check if file exists
        boolean fileExists = false;
        String[] fileList = context.fileList();
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].equals(BEHAVIOR_FILENAME)) {
                fileExists = true;
                break;
            }
        }

        if (fileExists) {
            Log.v("Content_Manager", "File exists.");

            // Read line character by character
            final int MAXIMUM_LINE_LENGTH = 1024;
            char[] lineBytes = new char[MAXIMUM_LINE_LENGTH];
            int byteCount = 0;

            try {

                // Read behaviors from file
                fis = context.openFileInput(BEHAVIOR_FILENAME);

                int bytesAvailable = fis.available();
                while (bytesAvailable > 0) {
                    // Read character
                    int character = fis.read();

                    // Check if end of line was reached. If so, parse it and create the behavior.
                    if (character == '\n') {
                        Log.v("Content_Manager", "newline");
                        // <tag>\t<defaultState>
                        String line = String.valueOf(lineBytes, 0, byteCount);
                        String[] splitLine = line.split("\t");
                        getClay ().createBehavior(splitLine[0], splitLine[1], splitLine[2]);
                        Log.v("Content_Manager", line);

                        // Reset line buffer
                        byteCount = 0;
                    }

                    // Buffer character
                    lineBytes[byteCount] = (char) character;
                    byteCount++;

                    // Check if there are more bytes available
                    bytesAvailable = fis.available();
                }

                // Close file
                fis.close();

            } catch (IOException e) {
                e.printStackTrace();
            }

        } else {
            Log.v("Content_Manager", "File DOES NOT exists.");
        }
    }
}
