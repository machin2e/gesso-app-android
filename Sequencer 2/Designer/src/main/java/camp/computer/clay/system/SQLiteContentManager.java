package camp.computer.clay.system;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.os.Environment;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.UUID;

import camp.computer.clay.designer.ApplicationView;

public class SQLiteContentManager {

    private Clay clay;

    private String type;

    private SQLiteDatabaseHelper db;



    public void generate () {

        if (getClay().hasStore()) {

            UUID uuid;

            // light
            uuid = UUID.fromString("1470f5c4-eaf1-43fb-8fb3-d96dc4e2bee4");
            if (!getClay().getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"light\" behavior not found in the repository. Adding it.");
                generateBehaviorScript(uuid, "light", "((T|F) ){11}(T|F)", "000000 000000 000000 000000 000000 000000 000000 000000 000000 000000 000000 000000");
            }

            // signal
            uuid = UUID.fromString("bdb49750-9ead-466e-96a0-3aa88e7d246c");
            if (!getClay().getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"signal\" behavior not found in the repository. Adding it.");
                //getClay().generateBehaviorScript(uuid, "signal", "regex", "FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL FITL");
                generateBehaviorScript(uuid, "signal", "regex", "TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none TIT:none");
            }

            // message
            uuid = UUID.fromString("99ff8f6d-a0e7-4b6e-8033-ee3e0dc9a78e");
            if (!getClay().getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"message\" behavior not found in the repository. Adding it.");
                generateBehaviorScript(uuid, "message", "regex", "Device Other \"hello\"");
            }

            // tone
            uuid = UUID.fromString("16626b1e-cf41-413f-bdb4-0188e82803e2");
            if (!getClay().getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"tone\" behavior not found in the repository. Adding it.");
                generateBehaviorScript(uuid, "tone", "regex", "frequency 0 hz 0 ms");
            }

            // pause
            uuid = UUID.fromString("56d0cf7d-ede6-4529-921c-ae9307d1afbc");
            if (!getClay().getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"pause\" behavior not found in the repository. Adding it.");
                generateBehaviorScript(uuid, "pause", "regex", "250");
            }

            // say
            uuid = UUID.fromString("269f2e19-1fc8-40f5-99b2-6ca67e828e70");
            if (!getClay().getCache().hasScript(uuid)) {
                Log.v("Clay_Behavior_Repo", "\"say\" behavior not found in the repository. Adding it.");
                generateBehaviorScript(uuid, "say", "regex", "oh, that's great");
            }
        }
    }

    /**
     * Creates a new behavior with the specified tag and state and stores it.
     * @param tag
     * @param defaultState
     */
    private void generateBehaviorScript (UUID uuid, String tag, String stateSpacePattern, String defaultState) {

        Log.v ("Content_Manager", "Creating script.");

        // Create behavior (and state) for the behavior script
        Script script = new Script (uuid, tag, stateSpacePattern, defaultState);

        // Cache the behavior
//        this.cache(script);

        // Store the behavior
        if (getClay().hasStore()) {
            getClay().getStore().storeScript(script);
        }

        generateBasicBehavior(script);

    }

    private void generateBasicBehavior (Script script) {

        Log.v ("Content_Manager", "Generating basic behavior for script.");

        // Generate basic actions for all behavior scripts
        Action basicAction = new Action(script);
        getClay().getStore().storeAction(basicAction);
    }

    /* Database */

    // If you change the database schema, you must increment the database version.

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Clay.db";

    /* Tables */

    private static final String DEVICE_TABLE_NAME          = "Device";
    private static final String TIMELINE_TABLE_NAME        = "Timeline";
    private static final String EVENT_TABLE_NAME           = "Event";
    private static final String ACTION_TABLE_NAME          = "Action";
    private static final String SCRIPT_TABLE_NAME          = "Script";
    private static final String STATE_TABLE_NAME           = "State";

    /* Table schemas (these inner classes define the table schemas) */

    public static abstract class DeviceEntry implements BaseColumns {

        public static final String TABLE_NAME                = DEVICE_TABLE_NAME;

        public static final String COLUMN_NAME_UUID          = "uuid";
        public static final String COLUMN_NAME_TIMELINE_UUID = "timelineUuid";

        public static final String COLUMN_NAME_TIME_CREATED  = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN        = "hidden";
        public static final String COLUMN_NAME_AVAILABLE     = "available";
    }

    public static abstract class TimelineEntry implements BaseColumns {

        public static final String TABLE_NAME               = TIMELINE_TABLE_NAME;

        public static final String COLUMN_NAME_UUID         = "uuid";

        public static final String COLUMN_NAME_TIME_CREATED = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN       = "hidden";
        public static final String COLUMN_NAME_AVAILABLE    = "available";
    }

    public static abstract class EventEntry implements BaseColumns {

        public static final String TABLE_NAME                       = EVENT_TABLE_NAME;

        public static final String COLUMN_NAME_UUID                 = "uuid";
        public static final String COLUMN_NAME_EVENT_INDEX          = "eventIndex";

        public static final String COLUMN_NAME_TIMELINE_UUID        = "timelineUuid";
        public static final String COLUMN_NAME_ACTION_UUID = "actionUuid";

        public static final String COLUMN_NAME_TIME_CREATED         = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN               = "hidden";
        public static final String COLUMN_NAME_AVAILABLE            = "available";
    }

    public static abstract class ActionEntry implements BaseColumns {

        public static final String TABLE_NAME                       = ACTION_TABLE_NAME;

        public static final String COLUMN_NAME_UUID                 = "uuid";
        public static final String COLUMN_NAME_TAG                  = "tag";
        public static final String COLUMN_NAME_PARENT_UUID          = "parentActionUuid";
        public static final String COLUMN_NAME_SIBLING_INDEX        = "siblingIndex"; // i.e., Index in parent behavior's list of children.
        public static final String COLUMN_NAME_SCRIPT_UUID          = "scriptUuid";

        public static final String COLUMN_NAME_TIME_CREATED         = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN               = "hidden";
        public static final String COLUMN_NAME_AVAILABLE            = "available";
    }

    public static abstract class ScriptEntry implements BaseColumns {

        public static final String TABLE_NAME                = SCRIPT_TABLE_NAME;

        public static final String COLUMN_NAME_UUID          = "uuid";
        public static final String COLUMN_NAME_TAG           = "tag"; // Used by interpretter on devices
        public static final String COLUMN_NAME_STATE_PATTERN = "statePattern"; // Regular expression to validate state encoding
        public static final String COLUMN_NAME_DEFAULT_STATE = "defaultState"; // Used to initialize State for a (Event, Action) pair

        public static final String COLUMN_NAME_TIME_CREATED  = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN        = "hidden";
        public static final String COLUMN_NAME_AVAILABLE     = "available";
    }

    public static abstract class StateEntry implements BaseColumns {

        public static final String TABLE_NAME                       = STATE_TABLE_NAME;

        public static final String COLUMN_NAME_UUID                 = "uuid";
        public static final String COLUMN_NAME_STATE                = "state";
        public static final String COLUMN_NAME_EVENT_UUID           = "eventUuid";
        public static final String COLUMN_NAME_STATE_INDEX          = "stateIndex";

        public static final String COLUMN_NAME_TIME_CREATED         = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN               = "hidden";
        public static final String COLUMN_NAME_AVAILABLE            = "available";
    }

    private static final String TEXT_TYPE                  = " TEXT";
    private static final String INTEGER_TYPE               = " INTEGER";
    private static final String INTEGER_DEFAULT_1          = " DEFAULT 1";
    private static final String INTEGER_DEFAULT_0          = " DEFAULT 0";
    private static final String INTEGER_DEFAULT_NEGATIVE_1 = " DEFAULT -1";
    private static final String DATETIME_TYPE              = " DATETIME";
    private static final String DATETIME_DEFAULT_NOW       = " DEFAULT CURRENT_TIMESTAMP";
    private static final String COMMA_SEP                  = ",";

    private static final String SQL_CREATE_DEVICE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + DeviceEntry.TABLE_NAME + " (" +
                    DeviceEntry._ID + " INTEGER PRIMARY KEY," +

                    DeviceEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    DeviceEntry.COLUMN_NAME_TIMELINE_UUID + TEXT_TYPE + COMMA_SEP +

                    DeviceEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    DeviceEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +
                    DeviceEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 +
                    " )";

    private static final String SQL_CREATE_TIMELINE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TimelineEntry.TABLE_NAME + " (" +
                    TimelineEntry._ID + " INTEGER PRIMARY KEY," +

                    TimelineEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
//                    TimelineEntry.COLUMN_NAME_UNIT_UUID + TEXT_TYPE + COMMA_SEP +

                    TimelineEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    TimelineEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +
                    TimelineEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 +
                    " )";

    private static final String SQL_CREATE_EVENT_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + EventEntry.TABLE_NAME + " (" +
                    EventEntry._ID + " INTEGER PRIMARY KEY," +

                    EventEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_EVENT_INDEX + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +
                    EventEntry.COLUMN_NAME_TIMELINE_UUID + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_ACTION_UUID + TEXT_TYPE + COMMA_SEP +
//                    EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID + TEXT_TYPE + COMMA_SEP +

                    EventEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    EventEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +
                    EventEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 +
                    " )";

    private static final String SQL_CREATE_ACTION_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + ActionEntry.TABLE_NAME + " (" +
                    ActionEntry._ID + " INTEGER PRIMARY KEY," +

                    ActionEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    ActionEntry.COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                    ActionEntry.COLUMN_NAME_PARENT_UUID + TEXT_TYPE + COMMA_SEP +
                    ActionEntry.COLUMN_NAME_SIBLING_INDEX + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +
                    ActionEntry.COLUMN_NAME_SCRIPT_UUID + TEXT_TYPE + COMMA_SEP +

                    ActionEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    ActionEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 + COMMA_SEP +
                    ActionEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 +
                    " )";

    private static final String SQL_CREATE_SCRIPT_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + ScriptEntry.TABLE_NAME + " (" +
                    ScriptEntry._ID + " INTEGER PRIMARY KEY," +

                    ScriptEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    ScriptEntry.COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                    ScriptEntry.COLUMN_NAME_STATE_PATTERN + TEXT_TYPE + COMMA_SEP +
                    ScriptEntry.COLUMN_NAME_DEFAULT_STATE + TEXT_TYPE + COMMA_SEP +

                    ScriptEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    ScriptEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 + COMMA_SEP +
                    ScriptEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 +
                    " )";

    private static final String SQL_CREATE_STATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + StateEntry.TABLE_NAME + " (" +
                    StateEntry._ID + " INTEGER PRIMARY KEY," +

                    StateEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    StateEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    StateEntry.COLUMN_NAME_EVENT_UUID + TEXT_TYPE + COMMA_SEP +
                    StateEntry.COLUMN_NAME_STATE_INDEX + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +

                    StateEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    StateEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 + COMMA_SEP +
                    StateEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 +
                    " )";

    private static final String SQL_DELETE_ACTION_ENTRIES =
            "DROP TABLE IF EXISTS " + ActionEntry.TABLE_NAME;

    private static final String SQL_DELETE_SCRIPT_ENTRIES =
            "DROP TABLE IF EXISTS " + ScriptEntry.TABLE_NAME;

    private static final String SQL_DELETE_STATE_ENTRIES =
            "DROP TABLE IF EXISTS " + StateEntry.TABLE_NAME;

    private static final String SQL_DELETE_DEVICE_ENTRIES =
            "DROP TABLE IF EXISTS " + DeviceEntry.TABLE_NAME;

    private static final String SQL_DELETE_TIMELINE_ENTRIES =
            "DROP TABLE IF EXISTS " + TimelineEntry.TABLE_NAME;

    private static final String SQL_DELETE_EVENT_ENTRIES =
            "DROP TABLE IF EXISTS " + EventEntry.TABLE_NAME;

    class SQLiteDatabaseHelper extends SQLiteOpenHelper {

        public SQLiteDatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);

            try {
                writeDatabaseToSD(DATABASE_NAME);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_ACTION_ENTRIES);
            db.execSQL(SQL_CREATE_SCRIPT_ENTRIES);
            db.execSQL(SQL_CREATE_STATE_ENTRIES);
            db.execSQL(SQL_CREATE_DEVICE_ENTRIES);
            db.execSQL(SQL_CREATE_TIMELINE_ENTRIES);
            db.execSQL(SQL_CREATE_EVENT_ENTRIES);
        }

        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over

            resetDatabase(db);

            onCreate(db);
        }

        private void resetDatabase(SQLiteDatabase db) {

            // Delete tables
            db.execSQL(SQL_DELETE_ACTION_ENTRIES);
            db.execSQL(SQL_DELETE_SCRIPT_ENTRIES);
            db.execSQL(SQL_DELETE_STATE_ENTRIES);
            db.execSQL(SQL_DELETE_DEVICE_ENTRIES);
            db.execSQL(SQL_DELETE_TIMELINE_ENTRIES);
            db.execSQL(SQL_DELETE_EVENT_ENTRIES);

            // Create tables
            db.execSQL(SQL_CREATE_ACTION_ENTRIES);
            db.execSQL(SQL_CREATE_SCRIPT_ENTRIES);
            db.execSQL(SQL_CREATE_STATE_ENTRIES);
            db.execSQL(SQL_CREATE_DEVICE_ENTRIES);
            db.execSQL(SQL_CREATE_TIMELINE_ENTRIES);
            db.execSQL(SQL_CREATE_EVENT_ENTRIES);
        }

        private void writeDatabaseToSD(String dbName) throws IOException {

            Log.v("Content_Manager", "");
            Log.v("Content_Manager", "writeDatabaseToSD:");
            File sd = Environment.getExternalStorageDirectory();

            String DB_PATH;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                DB_PATH = ApplicationView.getContext().getFilesDir().getAbsolutePath().replace("files", "databases") + File.separator;
            }
            else {
                DB_PATH = ApplicationView.getContext().getFilesDir().getPath() + ApplicationView.getContext().getPackageName() + "/databases/";
            }

            Log.v("Content_Manager", "Trying to write...");
            if (sd.canWrite()) {;
                String currentDBPath = dbName;
                String backupDBPath = "clay.db";
                File currentDB = new File(DB_PATH, currentDBPath);
                File backupDB = new File(sd, backupDBPath);

                Log.v("Content_Manager", "Writing...");
                if (currentDB.exists()) {
                    Log.v("Content_Manager", "Found database...");
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileOutputStream dstFileStream = new FileOutputStream(backupDB);
                    FileChannel dst = dstFileStream.getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    Log.v("Content_Manager", "Success! Wrote to " + backupDB.getAbsolutePath());
                } else {
                    Log.v("Content_Manager", "Failed!");
                }
            }
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        /** Actions */

        public void saveAction(Action action, Action parentAction) {
            Log.v ("Content_Manager", "saveAction");

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys...
            ContentValues values = new ContentValues();
            values.put(ActionEntry.COLUMN_NAME_UUID, action.getUuid().toString());
            values.put(ActionEntry.COLUMN_NAME_TAG, action.getTag());

            // ...and if the action node has a parent, store the parent and sibling index.
            if (parentAction != null) {
                Log.v("Content_Manager", "non-root");
                // Non-root node
                values.put(ActionEntry.COLUMN_NAME_PARENT_UUID, parentAction.getUuid().toString());
                values.put(ActionEntry.COLUMN_NAME_SIBLING_INDEX, parentAction.getActions().indexOf(action));
            } else {
                // Root node
                values.put(ActionEntry.COLUMN_NAME_PARENT_UUID, "");
                values.put(ActionEntry.COLUMN_NAME_SIBLING_INDEX, "");
            }

            if (action.hasScript()) {
                Log.v("Content_Manager", "leaf");
                // Leaf node (associated with basic action). Usually a non-root leaf node,
                // but can be leaf node for basic action single-node trees.
                values.put(ActionEntry.COLUMN_NAME_SCRIPT_UUID, action.getScript().getUuid().toString());
            } else {
                Log.v("Content_Manager", "non-leaf");
                // Non-leaf, non-root node (intermediate node)
                values.put(ActionEntry.COLUMN_NAME_SCRIPT_UUID, "");
            }

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(ActionEntry.TABLE_NAME, null, values);
            Log.v("Content_Manager", "Inserted action into database " + entryId + " (UUID: " + action.getUuid() + ")");

//            // Store action state object
//            saveState(action, action.getState());

        }

        // TODO: updateBehavior is not needed because entries in "behavior" are never updated once created.
        // TODO: ... When a behavior event is modified on the timeline, the event is updated.
        // TODO: ... When a sequence of actions is chunked, new behavior entries are added,
        // TODO: ... a new event is created, the events associated with the chunked actions
        // TODO: ... are flagged as hidden/chunked (so they won't be on the timeline), and the
        // TODO: ... newly created event (for the newly created behavior) is added to the timeline,
        // TODO: ... then all the events' position index on the timeline are updated.

        // * Query for timeline
        // * Query for events on the timeline (available and not hidden/disabled)
        // ... (below)
        // * Query for behavior state associated with the event.

        // - Get root behavior UUIDs, unique only (from table of tree edges)
        // - For each root, get actions with parent with root UUID, addDevice to parent's list of
        //   children, to reconstruct the graph. Do this recursively until the query for
        //   children returns no results (leaf nodes).
        // - For children, query for the associated behavior script.

        public void queryActions() {
            queryActions(null);
        }

        /**
         * Starts with root nodes and recursively constructs the behavior trees.
         * @param parentAction
         */
        public void queryActions(Action parentAction) {

            Log.v("Content_Manager", "queryActions");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    ActionEntry._ID,

                    ActionEntry.COLUMN_NAME_UUID,
                    ActionEntry.COLUMN_NAME_TAG,
                    ActionEntry.COLUMN_NAME_PARENT_UUID,
                    ActionEntry.COLUMN_NAME_SIBLING_INDEX,
                    ActionEntry.COLUMN_NAME_SCRIPT_UUID,

                    ActionEntry.COLUMN_NAME_TIME_CREATED
            };

            // Sort the actions by their sibling index so they will be added to the parent,
            // if any, in the correct order.
            String sortOrder = ActionEntry.COLUMN_NAME_SIBLING_INDEX + " ASC";

            // Only get the actions with no parent (the root actions).
            String selection = null;
            String[] selectionArgs = null; // { timeline.getUuid().toString() };
            if (parentAction == null) {
                selection = ActionEntry.COLUMN_NAME_PARENT_UUID + " = \"\" AND " + ActionEntry.COLUMN_NAME_HIDDEN + " = 0";
                selectionArgs = null;
            } else {
                selection = ActionEntry.COLUMN_NAME_PARENT_UUID + " LIKE ? AND " + ActionEntry.COLUMN_NAME_HIDDEN + " = 0";
                selectionArgs = new String[] { parentAction.getUuid().toString() };
            }

            Cursor cursor = db.query(
                    ActionEntry.TABLE_NAME,  // The table to query
                    projection,                // The columns to return
                    selection,                 // The columns for the WHERE clause
                    selectionArgs,             // The values for the WHERE clause
                    null,                      // don't group the rows
                    null,                      // don't filter by row groups
                    sortOrder                  // The sort order
            );

            Log.v("Content_Manager", "\tCount: " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_UUID));
                String tag = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_TAG));
                String scriptUuidString = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_SCRIPT_UUID));
                /*
                String parentBehaviorUuid = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_PARENT_UUID));
                int siblingIndex = cursor.getInt(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_SIBLING_INDEX));
                */

                // Create the action object
                Action action = new Action(UUID.fromString (uuidString), tag);

                // Add action as a child action to the parent action
                if (parentAction != null) {
                    parentAction.addAction(action);
                    Log.v("Content_Manager", "\tAdding action to parent (UUID: " + action.getUuid() + ")");
                    Log.v("Content_Manager", "\t\tParent UUID: " + parentAction.getUuid());
                }

                /*
                // Add the action to Clay
                getClay().cacheAction(action);
                */

                // Recursive call to reconstruct the action's children
                Log.v("flem", "action.getUuid(): " + action.getUuid());
                if (isParentAction(action.getUuid())) {
                    // Recursive query to get the children of the action just created.
                    queryActions(action);
                } else {
                    // Basic action, so set script.
                    Script script = getClay().getCache().getScript(UUID.fromString(scriptUuidString));
                    action.setScript(script);
                }

                // TODO: action.setState
                // TODO: action.setScript

                // Add the action to Clay
                // TODO: Only add root actions to Clay? Maybe only those should be available for selection.
                getClay().getCache().cache(action);
                Log.v("Content_Manager", "> added beahvior: " + action.getUuid());

                // Move to the next entry returned by the query
                cursor.moveToNext();
            }
        }

        // TODO: queryScripts () // Get all actions, for use to create the first actions in the database.

        /** Action Script */

        public void saveScript(Script script) {

            if (script == null) {
                return;
            }

            Log.v("Content_Manager", "saveScript");

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            Log.v("Content_Manager", "script: " + script);
            Log.v("Content_Manager", "uuid: " + script.getUuid());
            Log.v("Content_Manager", "tag: " + script.getTag());
            Log.v("Content_Manager", "pattern: " + script.getStatePattern());
            Log.v("Content_Manager", "defaultState: " + script.getDefaultState());

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(ScriptEntry.COLUMN_NAME_UUID, script.getUuid().toString());
            values.put(ScriptEntry.COLUMN_NAME_TAG, script.getTag());
            values.put(ScriptEntry.COLUMN_NAME_STATE_PATTERN, script.getStatePattern());
            values.put(ScriptEntry.COLUMN_NAME_DEFAULT_STATE, script.getDefaultState());

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert (ScriptEntry.TABLE_NAME, null, values);

            Log.v("Content_Manager", "Inserted script into database (_id: " + entryId + ")");

        }

        public void queryScripts() {

            Log.v("Content_Manager", "queryScripts");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    ScriptEntry._ID,
                    ScriptEntry.COLUMN_NAME_UUID,
                    ScriptEntry.COLUMN_NAME_TAG,
                    ScriptEntry.COLUMN_NAME_STATE_PATTERN,
                    ScriptEntry.COLUMN_NAME_DEFAULT_STATE
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = null;
            String[] selectionArgs = null;
            Cursor cursor = db.query(
                    ScriptEntry.TABLE_NAME,  // The table to query
                    projection,                      // The columns to return
                    selection,                       // The columns for the WHERE clause
                    selectionArgs,                   // The values for the WHERE clause
                    null,                            // don't group the rows
                    null,                            // don't filter by row groups
                    sortOrder                        // The sort order
            );

            Log.v("Content_Manager", "\tCount: " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(ScriptEntry.COLUMN_NAME_UUID));
                String tag = cursor.getString(cursor.getColumnIndexOrThrow(ScriptEntry.COLUMN_NAME_TAG));
                String stateSpacePattern = cursor.getString(cursor.getColumnIndexOrThrow(ScriptEntry.COLUMN_NAME_STATE_PATTERN));
                String defaultState = cursor.getString(cursor.getColumnIndexOrThrow(ScriptEntry.COLUMN_NAME_DEFAULT_STATE));

                // Cache the behavior script
                if (!getClay().getCache().hasScript(UUID.fromString(uuidString))) {

                    // Create the object
                    Script script = new Script(UUID.fromString(uuidString), tag, stateSpacePattern, defaultState);

                    // Cache the script
                    getClay().getCache().cache(script);
                }

                // Continue to next behavior script
                cursor.moveToNext();
            }
        }

//        public Script queryBehaviorScript (UUID uuid) {
//            Log.v("Content_Manager", "queryBehaviorScript");
//            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();
//
//            db.execSQL(SQL_CREATE_SCRIPT_ENTRIES);
//
//            // Define a projection that specifies which columns from the database
//            // you will actually use after this query.
//            String[] projection = {
//                    ScriptEntry._ID,
//                    ScriptEntry.COLUMN_NAME_UUID,
//                    ScriptEntry.COLUMN_NAME_TAG,
//                    ScriptEntry.COLUMN_NAME_DEFAULT_STATE
//            };
//
//            // How you want the results sorted in the resulting Cursor
//            String sortOrder = null;
//
//            String selection = ScriptEntry.COLUMN_NAME_UUID + " LIKE ?";
//            String[] selectionArgs = { uuid.toString() };
//            Cursor cursor = db.query(
//                    ScriptEntry.TABLE_NAME,  // The table to query
//                    projection,                               // The columns to return
//                    selection,                                // The columns for the WHERE clause
//                    selectionArgs,                            // The values for the WHERE clause
//                    null,                                     // don't group the rows
//                    null,                                     // don't filter by row groups
//                    sortOrder                                 // The sort order
//            );
//
//            Log.v("Content_Manager", "cursor.getCount = " + cursor.getCount());
//
//            // Iterate over the results
//            cursor.moveToFirst();
//            while (!cursor.isAfterLast()) {
//                long itemId = cursor.getLong(
//                        cursor.getColumnIndexOrThrow(DeviceEntry._ID)
//                );
//
//                // Read the entry
//                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(ScriptEntry.COLUMN_NAME_UUID));
//                String tag = cursor.getString(cursor.getColumnIndexOrThrow(ScriptEntry.COLUMN_NAME_TAG));
//                String defaultState = cursor.getString(cursor.getColumnIndexOrThrow(ScriptEntry.COLUMN_NAME_DEFAULT_STATE));
//
//                // Create the behavior
//                Script behaviorScript = new Script (UUID.fromString(uuidString), tag, defaultState);
//
//                return behaviorScript;
//            }
//
//            return null;
//        }

        /** Action States */

        public void saveState(Event event, State state) {

            Log.v("Content_Manager", "saveState");

            if (state == null) {
                return;
            }

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(StateEntry.COLUMN_NAME_UUID, state.getUuid().toString());
            values.put(StateEntry.COLUMN_NAME_STATE, state.getState());
            values.put(StateEntry.COLUMN_NAME_EVENT_UUID, event.getUuid().toString());
            values.put(StateEntry.COLUMN_NAME_STATE_INDEX, event.getState().indexOf(state));

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert (StateEntry.TABLE_NAME, null, values);

            Log.v("Content_Manager", "\tSaving state index " + event.getState().indexOf(state));

//            Log.v("Content_Manager", "Inserted behavior state into database (_id: " + entryId + ")");

        }

        /*
        public void queryState(Event event, UUID behaviorStateUuid) {

            Log.v("Content_Manager", "queryState");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    StateEntry._ID,
                    StateEntry.COLUMN_NAME_UUID,
                    StateEntry.COLUMN_NAME_STATE,
                    StateEntry.COLUMN_NAME_EVENT_UUID,
                    StateEntry.COLUMN_NAME_STATE_INDEX,
                    StateEntry.COLUMN_NAME_TIME_CREATED
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = StateEntry.COLUMN_NAME_TIME_CREATED + " DESC";

            String selection = StateEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { behaviorStateUuid.toString() };
            Cursor cursor = db.query(
                    StateEntry.TABLE_NAME,  // The table to query
                    projection,                     // The columns to return
                    selection,                      // The columns for the WHERE clause
                    selectionArgs,                  // The values for the WHERE clause
                    null,                           // don't group the rows
                    null,                           // don't filter by row groups
                    sortOrder                       // The sort order
            );

            Log.v("Content_Manager", "\tCount: " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();

            // Read the entry
            String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(StateEntry.COLUMN_NAME_UUID));
            String state = cursor.getString(cursor.getColumnIndexOrThrow(StateEntry.COLUMN_NAME_STATE));
            String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(StateEntry.COLUMN_NAME_ACTION_UUID));
            String behaviorScriptUuidString = cursor.getString(cursor.getColumnIndexOrThrow(StateEntry.COLUMN_NAME_BEHAVIOR_SCRIPT_UUID));

            // Get the behavior and behavior script from the cache. Here, these are assumed to
            // be available in the cache, since it is assumed they are loaded and cached when
            // Clay is first opened.
            Script behaviorScript = getClay ().getCache ().getScript(UUID.fromString(behaviorScriptUuidString));
            Action behavior = getClay().getCache().getAction(UUID.fromString(behaviorUuidString));
            behavior.setScript(behaviorScript);

            // Reconstruct behavior state object
//            Log.v ("Query_Behavior_State", "state: " + state);
//            Log.v("Query_Behavior_State", "uuid: " + UUID.fromString(uuidString));
            State behaviorState = new State(UUID.fromString (uuidString), state);
            event.setBehaviorState(behaviorState); // event.getAction ().setState (behaviorState);
//            Log.v("Query_Behavior_State", "behavior.state: " + behavior.getState().getState());
//            Log.v("Query_Behavior_State", "behavior.state.uuid: " + behavior.getState().getUuid());
//            Log.v("Query_Behavior_State", "---");

            // Add the behavior object to the event object
            if (event != null) {
                event.setAction(behavior);
            }
        }
        */

        public void queryState(Event event) {

//            Action behavior = event.getAction();

            Log.v("Content_Manager", "queryState");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    StateEntry._ID,
                    StateEntry.COLUMN_NAME_UUID,
                    StateEntry.COLUMN_NAME_STATE,
                    StateEntry.COLUMN_NAME_STATE_INDEX,
                    StateEntry.COLUMN_NAME_EVENT_UUID
            };

            // Prepare the query
            String sortOrder = StateEntry.COLUMN_NAME_STATE_INDEX + " ASC";

            String selection = StateEntry.COLUMN_NAME_EVENT_UUID + " LIKE ? AND "
                    + StateEntry.COLUMN_NAME_HIDDEN + " = 0";

            String[] selectionArgs = { event.getUuid().toString() };

            // Query the database
            Cursor cursor = db.query(
                    StateEntry.TABLE_NAME,  // The table to query
                    projection,             // The columns to return
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            Log.v("Content_Manager", "\tCount: " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();

            while (!cursor.isAfterLast()) {

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(StateEntry.COLUMN_NAME_UUID));
                String state = cursor.getString(cursor.getColumnIndexOrThrow(StateEntry.COLUMN_NAME_STATE));

                // Get the behavior and behavior script from the cache. Here, these are assumed to
                // be available in the cache, since it is assumed they are loaded and cached when
                // Clay is first opened.
//            Script behaviorScript = getClay().getCache().getScript(UUID.fromString(behaviorScriptUuidString));
//            Log.v ("Content_Manager", "> behavior: " + behavior.getUuid());
//            behavior.setScript(behaviorScript);

                // Reconstruct list of behavior state objects
                State actionState = new State(UUID.fromString(uuidString), state);
                event.addActionState(actionState);

                cursor.moveToNext();
            }
        }

        /** Units */

        public void insertDevice(Device device) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DeviceEntry.COLUMN_NAME_UUID, device.getUuid().toString());
            values.put(DeviceEntry.COLUMN_NAME_TIMELINE_UUID, device.getTimeline().getUuid().toString());

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(DeviceEntry.TABLE_NAME, null, values);

            Log.v("Content_Manager", "Inserted device into database (_id: " + entryId + ")");

        }

        public Device queryUnit (UUID uuid) {
            Log.v("Content_Manager", "queryUnit");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    DeviceEntry._ID,
                    DeviceEntry.COLUMN_NAME_UUID,
                    DeviceEntry.COLUMN_NAME_TIMELINE_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = DeviceEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { uuid.toString() };
            Cursor cursor = db.query(
                    DeviceEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            Log.v("Content_Manager", "\tCount: " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_UUID));
                String timelineUuidString = cursor.getString(cursor.getColumnIndexOrThrow(DeviceEntry.COLUMN_NAME_TIMELINE_UUID));

                // Create the behavior
                Device device = new Device(getClay(), UUID.fromString(uuidString));
                device.getTimeline().setUuid(UUID.fromString(timelineUuidString));

                // Reconstruct the device's timeline
                queryTimeline (device, UUID.fromString(timelineUuidString));

                return device;
            }

            return null;
        }

        /** Timeline */

        public void saveTimeline (Timeline timeline) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TimelineEntry.COLUMN_NAME_UUID, timeline.getUuid().toString());

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(TimelineEntry.TABLE_NAME, null, values);

            // Update event indices
            for (Event event : timeline.getEvents()) {
                if (event != null) {
                    if (!queryEventExists(event.getUuid())) {
                        saveEvent(event.getTimeline(), event);
                    } else {
                        updateEvent(event.getTimeline(), event);
                    }
                }
            }

            Log.v("Content_Manager", "Inserted timeline state into database (_id: " + entryId + ")");

        }

        public Timeline queryTimeline (Device device, UUID timelineUuid) {

            Log.v("Content_Manager", "queryTimeline");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            Log.v("Content_Manager", "\tLooking for UUID: " + timelineUuid);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TimelineEntry._ID,
                    TimelineEntry.COLUMN_NAME_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = TimelineEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { timelineUuid.toString() };
            Cursor cursor = db.query(
                    TimelineEntry.TABLE_NAME,  // The table to query
                    projection,                // The columns to return
                    selection,                 // The columns for the WHERE clause
                    selectionArgs,             // The values for the WHERE clause
                    null,                      // don't group the rows
                    null,                      // don't filter by row groups
                    sortOrder                  // The sort order
            );

            Log.v("Content_Manager", "\tCount: " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();

            // Read the entry
            String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(TimelineEntry.COLUMN_NAME_UUID));
            Log.v("Content_Manager", "\tFound: " + uuidString);

            // Create the timeline
            Timeline timeline = new Timeline (UUID.fromString(uuidString));

            // Populate the timeline with its events
            queryEvents (device, timeline);

            // Assign the timeline to the device
            device.setTimeline(timeline);

            return timeline;
        }

        /** Event */

        public void saveEvent (Timeline timeline, Event event) {
            Log.v ("Content_Manager", "saveEvent");

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_EVENT_INDEX, timeline.getEvents().indexOf(event));
            values.put(EventEntry.COLUMN_NAME_TIMELINE_UUID, event.getTimeline().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_ACTION_UUID, event.getAction().getUuid().toString());
//            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID, (event.getState() != null ? event.getState().getUuid().toString() : ""));

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(EventEntry.TABLE_NAME, null, values);

            // Store list of behavior state objects
            int i = 0;
            for (State state : event.getState()) {
                Log.v ("Content_Manager", "\tStoring state " + i + " (UUID: " + state.getUuid() + ".");
                saveState(event, state);
                i++;
            }

            Log.v("Content_Manager", "Inserted event into database at " + timeline.getEvents().indexOf(event) + " (_id: " + entryId + ")");
        }

        public boolean isParentAction(UUID actionUuid) {

            Log.v("Content_Manager", "selectBehaviorsWithParent");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    ActionEntry._ID,
                    ActionEntry.COLUMN_NAME_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = ActionEntry.COLUMN_NAME_PARENT_UUID + " LIKE ?";
            String[] selectionArgs = { actionUuid.toString() };
            Cursor cursor = db.query(
                    ActionEntry.TABLE_NAME,  // The table to query
                    projection,                // The columns to return
                    selection,                 // The columns for the WHERE clause
                    selectionArgs,             // The values for the WHERE clause
                    null,                      // don't group the rows
                    null,                      // don't filter by row groups
                    sortOrder                  // The sort order
            );

            Log.v("flem", "\tbehavior.getUuid(): " + cursor);

            Log.v("Content_Manager", "\tcursor.getCount = " + cursor.getCount());

            // Return whether or not an entry exists with the UUID
            if (cursor.getCount () > 0) {
                return true;
            } else {
                return false;
            }
        }

        // TODO: Update the behavior table so basic actions contain script UUID
        public Action getScriptAction(Script script) {

            Action scriptAction = null;

            Log.v("Content_Manager", "getAction");

            // Get connection to database
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Get the actions' UUIDs
            UUID uuid = script.getUuid();

            // Define a projection that specifies which columns from the database you will...
            // ...actually use after this query.
            String[] projection = {
                    ActionEntry._ID,
                    ActionEntry.COLUMN_NAME_UUID,
                    ActionEntry.COLUMN_NAME_PARENT_UUID,
                    ActionEntry.COLUMN_NAME_TAG
            };

            // Specify how to sort the retrieved data
            String sortOrder = null;

            // TODO: if parentUuid is null, then compare the tag, and get the UUID if exists, for reuse

            String selection = null;
            String[] selectionArgs = null;
            Log.v ("New_Behavior", "adding new behavior with tag " + script.getTag());
            selection = ActionEntry.COLUMN_NAME_TAG + " = ? AND "
                    + ActionEntry.COLUMN_NAME_PARENT_UUID + " LIKE ?";
            selectionArgs = new String[] { script.getTag(), "" };

            Cursor cursor = db.query(
                    ActionEntry.TABLE_NAME,  // The table to query
                    projection,                // The columns to return
                    selection,                 // The columns for the WHERE clause
                    selectionArgs,             // The values for the WHERE clause
                    null,                      // don't group the rows
                    null,                      // don't filter by row groups
                    sortOrder                  // The sort order
            );

            Log.v("Content_Manager", "\tCount = " + cursor.getCount());

            // Return whether or not an entry exists with the UUID
            cursor.moveToFirst();
            if (cursor.getCount () > 0) {
                Log.v ("New_Behavior", "\tThe behavior exists.");

                String actionUuidString = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_UUID));
                UUID actionUuid = UUID.fromString(actionUuidString);

                if (getClay().getCache().hasAction(actionUuid)) {
                    scriptAction = getClay().getCache().getAction(actionUuid);
                    return scriptAction;
                } else {
                    scriptAction = new Action(actionUuid, script); // TODO: sets default state for script
                    storeAction(scriptAction);
                    getClay().getCache().getActions().add(scriptAction);
                    return scriptAction;
                }
            } else {
                Log.v ("New_Behavior", "\tThe behavior does not exist.");
                scriptAction = new Action(script);
                storeAction(scriptAction);
                getClay().getCache().getActions().add(scriptAction);
                return scriptAction;
            }
        }

        public Action getActionComposition(ArrayList<Action> children) {

            Action parentAction = null;

            Log.v("Content_Manager", "getActionComposition");

            // Get connection to database
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            ArrayList<UUID> candidateParentUuids = new ArrayList<UUID>();

            for (Action childAction : children) {

                // Get the actions' UUIDs
                UUID uuid = childAction.getUuid();
//                UUID parentUuid = null;
//                if (parentAction != null) {
//                    parentUuid = parentAction.getUuid();
//                }

                // Define a projection that specifies which columns from the database you will...
                // ...actually use after this query.
                String[] projection = {
                        ActionEntry._ID,
                        ActionEntry.COLUMN_NAME_UUID,
                        ActionEntry.COLUMN_NAME_PARENT_UUID,
                        ActionEntry.COLUMN_NAME_SIBLING_INDEX,
                        ActionEntry.COLUMN_NAME_SCRIPT_UUID
                };

                // Specify how to sort the retrieved data
                String sortOrder = null;
//                String sortOrder = ActionEntry.COLUMN_NAME_PARENT_UUID + " ASC, "
//                        + ActionEntry.COLUMN_NAME_SIBLING_INDEX + " ASC";

                // TODO: if parentUuid is null, then compare the tag, and get the UUID if exists, for reuse

                String selection = null;
                String[] selectionArgs = null;
//                if (parentUuid == null) {
//                    Log.v("New_Behavior", "adding new behavior with tag " + behavior.getTag());
//                    selection = ActionEntry.COLUMN_NAME_TAG + " = ?";
//                    selectionArgs = new String[]{behavior.getTag()};
//                } else {
                    selection = ActionEntry.COLUMN_NAME_UUID + " LIKE ? AND "
                            + ActionEntry.COLUMN_NAME_SIBLING_INDEX + " = ?";
                    selectionArgs = new String[]{ uuid.toString(), "" + children.indexOf (childAction) };
//                }
                Cursor cursor = db.query(
                        ActionEntry.TABLE_NAME,  // The table to query
                        projection,                // The columns to return
                        selection,                 // The columns for the WHERE clause
                        selectionArgs,             // The values for the WHERE clause
                        null,                      // don't group the rows
                        null,                      // don't filter by row groups
                        sortOrder                  // The sort order
                );

//                Log.v("Content_Manager", "\tCount = " + cursor.getCount());
                Log.v("New_Behavior_Parent", "\tCount = " + cursor.getCount());

                // Return whether or not an entry exists with the UUID
                if (cursor.getCount() > 0) {

                    // Iterate over the results
                    cursor.moveToFirst();
                    while (!cursor.isAfterLast()) {

                        // Add the UUID to the list of candidate parent UUIDs
                        String parentActionUuidString = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_PARENT_UUID));
                        UUID parentActionUuid = UUID.fromString(parentActionUuidString);
                        if (candidateParentUuids.contains(parentActionUuid)) {
                            candidateParentUuids.add(parentActionUuid);
                        }

                        cursor.moveToNext();

                    }
                }
            }

            // TODO: Check for structure using parents collected above...

            Log.v ("New_Behavior_Parent", "# candidates: " + candidateParentUuids.size());

            // No parent was found, so create a new one and return it
            if (parentAction == null) {

                Log.v ("New_Behavior_Parent", "Creating new parent behavior.");

                parentAction = new Action("so high");
                parentAction.setDescription("oh yeah!");

                for (Action childAction : children) {
                    parentAction.addAction(childAction);
                }
//                parentAction.addAction(foundUnit.getTimeline().getEvents().get(0).getAction());
//                parentAction.addAction(foundUnit.getTimeline().getEvents().get(1).getAction());

//                storeAction(parentAction);
//
//                getClay().getCache().getActions().add(parentAction);
            }

            return parentAction;
        }

        // TODO: Add sibling index, because sibling order is part of unique tree structure!
        //public boolean queryActionExists (UUID uuid, UUID parentUuid) {
        public boolean queryActionExists(Action action, Action parentAction) {

            Log.v("Content_Manager", "queryActionExists");

            // Get connection to database
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Get the actions' UUIDs
            UUID uuid = action.getUuid();
            UUID parentUuid = null;
            if (parentAction != null) {
                parentUuid = parentAction.getUuid();
            }

            // Define a projection that specifies which columns from the database you will...
            // ...actually use after this query.
            String[] projection = {
                    ActionEntry._ID,
                    ActionEntry.COLUMN_NAME_UUID,
                    ActionEntry.COLUMN_NAME_PARENT_UUID
            };

            // Specify how to sort the retrieved data
            String sortOrder = null;

            // TODO: if parentUuid is null, then compare the tag, and get the UUID if exists, for reuse

            String selection = null;
            String[] selectionArgs = null;
            // TODO!!!!!!!!!!!! Check if action exists (by structure, varies for leaf/basic, intermediate, root)
//            if (parentUuid == null) {
//                Log.v ("New_Behavior", "adding new action with tag " + action.getTag());
//                selection = ActionEntry.COLUMN_NAME_TAG + " = ?";
//                selectionArgs = new String[] { action.getTag() };
//            } else {
                selection = ActionEntry.COLUMN_NAME_UUID + " LIKE ? AND "
                        + ActionEntry.COLUMN_NAME_PARENT_UUID + " LIKE ?";
                selectionArgs = new String[] { uuid.toString(), (parentUuid != null ? parentUuid.toString() : "") };
//            }
            Cursor cursor = db.query(
                    ActionEntry.TABLE_NAME,  // The table to query
                    projection,                // The columns to return
                    selection,                 // The columns for the WHERE clause
                    selectionArgs,             // The values for the WHERE clause
                    null,                      // don't group the rows
                    null,                      // don't filter by row groups
                    sortOrder                  // The sort order
            );

            Log.v("Content_Manager", "\tCount = " + cursor.getCount());

            // Return whether or not an entry exists with the UUID
            if (cursor.getCount () > 0) {
                Log.v ("New_Behavior", "\tThe action exists.");
                if (parentAction == null) {
                    Log.v ("New_Behavior", "\tUpdating all object references to action to existing action.");
                    // If the action exists, then update the action with the existing
                    // action's with the same structure's UUID.
//                    String existingBehaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(ActionEntry.COLUMN_NAME_UUID));
//                    UUID existingBehaviorUuid = UUID.fromString (existingBehaviorUuidString);
//                    action.setUuid (existingBehaviorUuid);
                    // TODO: Point to existing reference! OR error?
                }
                return true;
            } else {
                Log.v("New_Behavior", "\tThe action does not exist.");
                return false;
            }
        }

        public boolean queryActionStateExists(UUID uuid) {

            Log.v("Content_Manager", "queryActionStateExists");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Create the table if it doesn't exist
            db.execSQL(SQL_CREATE_STATE_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    StateEntry._ID,
                    StateEntry.COLUMN_NAME_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = StateEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { uuid.toString() };
            Cursor cursor = db.query(
                    StateEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            Log.v("Content_Manager", "cursor.getCount = " + cursor.getCount());

            // Return whether or not an entry exists with the UUID
            if (cursor.getCount () > 0) {
                return true;
            } else {
                return false;
            }
        }

        public boolean queryEventExists (UUID uuid) {

            Log.v("Content_Manager", "queryEventExists");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Create the table if it doesn't exist
            db.execSQL(SQL_CREATE_EVENT_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    EventEntry._ID,
                    EventEntry.COLUMN_NAME_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = DeviceEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { uuid.toString() };
            Cursor cursor = db.query(
                    EventEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            Log.v("Content_Manager", "cursor.getCount = " + cursor.getCount());

            // Return whether or not an entry exists with the UUID
            if (cursor.getCount () > 0) {
                return true;
            } else {
                return false;
            }
        }

        /**
         * Called by queryTimeline after it reconstructs a timeline.
         *
         * @param device
         * @param timeline
         */
        public void queryEvents (Device device, Timeline timeline) {

            Log.v("Content_Manager", "queryEvents");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    EventEntry._ID,
                    EventEntry.COLUMN_NAME_UUID,
                    EventEntry.COLUMN_NAME_EVENT_INDEX,
                    EventEntry.COLUMN_NAME_TIMELINE_UUID,
                    EventEntry.COLUMN_NAME_ACTION_UUID
            };

            // Sort events by their position on the timeline
            String sortOrder = EventEntry.COLUMN_NAME_EVENT_INDEX + " ASC";

            String selection = EventEntry.COLUMN_NAME_TIMELINE_UUID + " LIKE ? AND "
                    + EventEntry.COLUMN_NAME_HIDDEN + " = 0";
            String[] selectionArgs = { timeline.getUuid().toString() };
            Cursor cursor = db.query(
                    EventEntry.TABLE_NAME,  // The table to query
                    projection,             // The columns to return
                    selection,              // The columns for the WHERE clause
                    selectionArgs,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrder               // The sort order
            );

            Log.v("Content_Manager", "\tCount: " + cursor.getCount());

            int eventCount = cursor.getCount();

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_UUID));
                String actionUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_ACTION_UUID));

                // Reconstruct the event object
                Event event = new Event (UUID.fromString (uuidString), timeline);

                // Get the Action and Script objects. These are assumed to be
                // available in the cache at this point, since they should be loaded when Clay
                // is first oepned.
                //Action action = getClay ().getAction (UUID.fromString (behaviorUuidString));

                // Reconstruct the associated action
                Action action = getClay().getCache().getAction(UUID.fromString(actionUuidString));
                event.setAction(action);

                // Reconstruct the event state objects
                queryState (event);

                /*
                // Reconstruct the action object
                boolean isNonLeafNode = behaviorStateUuidString.equals("");
                if (isNonLeafNode) {
                    // Reconstruct root or non-leaf node. There is no action script or state
                    // associated with these. Note that at this point, the action is assumed to
                    // be present in the cache. This should be the case since all actions are
                    // cached when the app starts.
                    Action action = getClay().getCache().getAction(UUID.fromString(behaviorUuidString));
                    event.setAction (action);
                } else {
                    // Reconstruct leaf node.
                    queryState(event, UUID.fromString(behaviorStateUuidString));
                }
                */

                // Add the action object to the event object
                // event.setAction(action);

//                // Get action state object by UUID
//                State behaviorState = null;
//                if (!behaviorStateUuid.equals("")) {
//                    // TODO: Check if the action state is cached. First need to implement action state caching in cache manager.
//                    behaviorState = queryState (UUID.fromString(behaviorStateUuid));
//                }

                // TODO: Query timeline, action, action state if they're not cached...
                // TODO: ...then only draw events for which all fields are instantiated.
                // TODO:    i.e., Use the event sort of as a filter, and only propagate use
                // TODO:          of the event when it's populated. Or show it as "loading."

                // Add the reconstructed event to the timeline
                timeline.addEvent(event);


                // If no action state was found in the store, then assign the default state
//                if (behaviorState == null) {
//                    behaviorState = new State(action, action.getDefaultState());
//                }

                // Create the event
                Log.v ("Content_Manager", "Adding event to the timeline.");
//                Event event = new Event(UUID.fromString(uuidString), timeline, action, behaviorState);

                cursor.moveToNext();
            }
        }

        public void updateEvent (Timeline timeline, Event event) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            Log.v ("move", "event.uuid " + event.getUuid());
            Log.v ("move", "updating moving to: " + timeline.getEvents().indexOf(event));

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_EVENT_INDEX, timeline.getEvents().indexOf(event));
            values.put(EventEntry.COLUMN_NAME_TIMELINE_UUID, event.getTimeline().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_ACTION_UUID, event.getAction().getUuid().toString());
//            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID,
//                    (event.getState() != null
//                            ? event.getState().getUuid().toString()
//                            : ""));

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.update(
                    EventEntry.TABLE_NAME,
                    values,
                    EventEntry.COLUMN_NAME_UUID + " LIKE \"" + event.getUuid().toString() + "\"", null);

            Log.v("Content_Manager", "Updated event in database " + entryId);
            Log.v("move", "Updated event in database " + entryId);

        }

        public boolean removeEvent (Event event) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_AVAILABLE, 1);
            values.put(EventEntry.COLUMN_NAME_HIDDEN, 1); // This is the flag indicating removal, if true.

            Log.v("Content_Manager", "event.timeline: " + event.getTimeline());
            Log.v ("Content_Manager", "event.timeline.uuid: " + event.getTimeline().getUuid());
            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline().getUuid());
            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline().getUuid());

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.update(
                    EventEntry.TABLE_NAME,
                    values,
                    EventEntry.COLUMN_NAME_UUID + " LIKE \"" + event.getUuid().toString() + "\"", null);

            if (entryId > 0) {
                Log.v("Content_Manager", "Removed event from timeline " + entryId);
                return true;
            } else {
                Log.v("Content_Manager", "Could not remove event in database " + entryId);
                return false;
            }
        }

        public boolean removeState (State state) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(StateEntry.COLUMN_NAME_UUID, state.getUuid().toString());
            values.put(StateEntry.COLUMN_NAME_AVAILABLE, 1);
            values.put(StateEntry.COLUMN_NAME_HIDDEN, 1); // This is the flag indicating removal, if true.

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.update(
                    StateEntry.TABLE_NAME,
                    values,
                    StateEntry.COLUMN_NAME_UUID + " LIKE \"" + state.getUuid().toString() + "\"", null);

            if (entryId > 0) {
                Log.v("Content_Manager", "Removed state from timeline " + entryId);
                return true;
            } else {
                Log.v("Content_Manager", "Could not remove state in database " + entryId);
                return false;
            }
        }

        public boolean deleteEvent (Event event) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.delete(
                    EventEntry.TABLE_NAME,
                    EventEntry.COLUMN_NAME_UUID + " LIKE \"" + event.getUuid().toString() + "\"",
                    null);

            if (entryId > 0) {
                Log.v("Content_Manager", "Deleted event in database " + entryId);
                return true;
            } else {
                Log.v("Content_Manager", "Could not delete event in database " + entryId);
                return false;
            }
        }
    }

    public SQLiteContentManager (Clay clay, String type) {
        this.clay = clay;
        this.type = type;
        this.db = new SQLiteDatabaseHelper(ApplicationView.getContext());
    }

    public Clay getClay () {
        return ApplicationView.getApplicationView().getClay();
    }

    /**
     * Store the action. Recursively stores the action tree graph by performing a breadth
     * first traversal.
     * @param action The action to store.
     */
    public void storeAction(Action action) {
        Log.v ("Content_Manager", "storeAction");

        if (action.hasScript()) {

            // TODO: Update the basic action IF it now has a parent

            if (!db.queryActionExists(action, null)) {
                Log.v ("Content_Manager", "Saving basic action.");
                db.saveAction(action, null);
            } else {
                // This is called when a basic action's state is updated.
                Log.v ("Content_Manager", "Updating basic action.");
                Log.v("Content_Manager", "NULL!!!!!!!!!!!!!!!!! SHOULD NEVER GET HERE!!!!!!!!!!");
//                db.updateBehavior (action, null);
            }
        } else {
            Log.v("Content_Manager", "Saving non-basic action.");

            storeActionTree(action, null);
        }
    }

    private void storeActionTree(Action action, Action parentAction) {

        Log.v ("Content_Manager", "storeActionTree");

        // Breadth first storage, to ensure that a relation to a action's children can be
        // created. The parent must be in the database before children can store a relation to
        // their parent.

        Log.v ("Content_Manager", "\t\t\t\t\tBEHAVIOR SCRIPT: " + action.getScript());

        // Store this node with a relation to its parent (if any)
        // TODO: Only store if the tree STRUCTURE hasn't already been stored. When storing a...
        // TODO: ...action, this means the database must be queried for the tree structure...
        // TODO: ...before saving a action tree, not just the action node UUID.
        if (!db.queryActionExists(action, parentAction)) {

            // TODO: Update the basic action that has a script, addDevice a parent! Yes, the action can have both a parent and a script! (leaf node!)

            db.saveAction(action, parentAction);
        } else {
            // This is a action node added to represent the hierarchical structure.
//            db.saveAction(action, parentAction);
            //db.updateBehavior(action, parentAction);
            Log.v("Content_Manager", "NULL?????????????? SHOULD NEVER GET HERE?????????????");
        }

        // Store children (if any)
//        if (parentAction != null) {

        // Recursively store the action tree if this action is not a basic action
        if (action.getActions() != null) {
            for (Action childAction : action.getActions()) {
//                if (!hasAction(action)) {
                // TODO: Store action index in list
                storeActionTree(childAction, action);
//                db.saveAction(childAction, action);
//                }
            }
        }
    }

    public void restoreAction(UUID uuid) {
        Log.v("Content_Manager", "restoreAction");
//        Action behavior = db.queryBehavior(uuid);
//        if (behavior == null) {
//            callback.onFailure();
//        } else {
//            callback.onSuccess(behavior);
//        }
        Log.v("Content_Manager", "NULL!!!!!!!!!!!!!!!!! SHOULD NEVER GET HERE!!!!!!!!!!");
    }

    public void storeTimeline(Timeline timeline) {
        db.saveTimeline(timeline);
    }

    public Timeline restoreTimeline (Device device, UUID uuid) {
        Timeline timeline = db.queryTimeline(device, uuid);
        return timeline;
    }

    public boolean hasEvent (Event event) {
        return db.queryEventExists(event.getUuid());
    }

    public void storeEvent(Event event) {
        if (!hasEvent (event)) {
            db.saveEvent(event.getTimeline(), event);
        } else {
            db.updateEvent(event.getTimeline(), event);
        }
    }

    public boolean removeEvent(Event event) {
        // boolean result = db.deleteEvent(event);
        boolean result = db.removeEvent(event);
        return result;
    }

    public void erase() {
        Log.v("Content_Manager", "empty");
        db.resetDatabase(db.getWritableDatabase());
    }

    public void storeScript(Script script) {
        Log.v ("Content_Manager", "storeScript");
        db.saveScript(script);
    }

    public void restoreScripts() {
        db.queryScripts();
    }

    public void restoreActions() {
        Log.v ("Content_Manager", "restoreActions");
        db.queryActions();
    }

    public void storeState(Event event, State state) {
        Log.v ("Content_Manager", "storeState");
        db.saveState(event, state);
    }

    public void restoreState(Event event) {
        db.queryState(event);
    }

    public void storeDevice(Device device) {
        db.insertDevice(device);
    }

    public Device restoreDevice(UUID uuid) {
        Device device = db.queryUnit(uuid);
        return device;
    }

    public Action getActionComposition(ArrayList<Action> children) {
        return db.getActionComposition(children);
    }

    public Action getAction(Script script) {
        return db.getScriptAction(script);
    }

    public void removeState(State state) {
        db.removeState (state);
    }

    public void writeDatabase () {
        try {
            db.writeDatabaseToSD(SQLiteContentManager.DATABASE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
