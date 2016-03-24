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

import camp.computer.clay.sequencer.ApplicationView;

public class SQLiteContentManager implements ContentManagerInterface {

    private Clay clay;

    private String type;

    private SQLiteDatabaseHelper db;

    /* Database */

    // If you change the database schema, you must increment the database version.

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "Clay.db";

    /* Tables */

    private static final String DEVICE_TABLE_NAME          = "Device";
    private static final String TIMELINE_TABLE_NAME        = "Timeline";
    private static final String EVENT_TABLE_NAME           = "Event";
    private static final String BEHAVIOR_TABLE_NAME        = "Action";
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
        public static final String COLUMN_NAME_BEHAVIOR_UUID        = "behaviorUuid";

        public static final String COLUMN_NAME_TIME_CREATED         = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN               = "hidden";
        public static final String COLUMN_NAME_AVAILABLE            = "available";
    }

    public static abstract class BehaviorEntry implements BaseColumns {

        public static final String TABLE_NAME                       = BEHAVIOR_TABLE_NAME;

        public static final String COLUMN_NAME_UUID                 = "uuid";
        public static final String COLUMN_NAME_TAG                  = "tag";
        public static final String COLUMN_NAME_PARENT_UUID          = "parentBehaviorUuid";
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
                    EventEntry.COLUMN_NAME_BEHAVIOR_UUID + TEXT_TYPE + COMMA_SEP +
//                    EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID + TEXT_TYPE + COMMA_SEP +

                    EventEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    EventEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +
                    EventEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 +
                    " )";

    private static final String SQL_CREATE_BEHAVIOR_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + BehaviorEntry.TABLE_NAME + " (" +
                    BehaviorEntry._ID + " INTEGER PRIMARY KEY," +

                    BehaviorEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_PARENT_UUID + TEXT_TYPE + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_SIBLING_INDEX + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_SCRIPT_UUID + TEXT_TYPE + COMMA_SEP +

                    BehaviorEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 +
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

    private static final String SQL_DELETE_BEHAVIOR_ENTRIES =
            "DROP TABLE IF EXISTS " + BehaviorEntry.TABLE_NAME;

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

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_BEHAVIOR_ENTRIES);
            db.execSQL(SQL_CREATE_SCRIPT_ENTRIES);
            db.execSQL(SQL_CREATE_STATE_ENTRIES);
            db.execSQL(SQL_CREATE_DEVICE_ENTRIES);
            db.execSQL(SQL_CREATE_TIMELINE_ENTRIES);
            db.execSQL(SQL_CREATE_EVENT_ENTRIES);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // This database is only a cache for online data, so its upgrade policy is
            // to simply to discard the data and start over

            resetDatabase(db);

            onCreate(db);
        }

        private void resetDatabase(SQLiteDatabase db) {

            // Delete tables
            db.execSQL(SQL_DELETE_BEHAVIOR_ENTRIES);
            db.execSQL(SQL_DELETE_SCRIPT_ENTRIES);
            db.execSQL(SQL_DELETE_STATE_ENTRIES);
            db.execSQL(SQL_DELETE_DEVICE_ENTRIES);
            db.execSQL(SQL_DELETE_TIMELINE_ENTRIES);
            db.execSQL(SQL_DELETE_EVENT_ENTRIES);

            // Create tables
            db.execSQL(SQL_CREATE_BEHAVIOR_ENTRIES);
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

        /** Behaviors */

        public void saveBehavior(Action action, Action parentAction) {
            Log.v ("Content_Manager", "saveBehavior");

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys...
            ContentValues values = new ContentValues();
            values.put(BehaviorEntry.COLUMN_NAME_UUID, action.getUuid().toString());
            values.put(BehaviorEntry.COLUMN_NAME_TAG, action.getTag());

            // ...and if the action node has a parent, store the parent and sibling index.
            if (parentAction != null) {
                Log.v("Content_Manager", "non-root");
                // Non-root node
                values.put(BehaviorEntry.COLUMN_NAME_PARENT_UUID, parentAction.getUuid().toString());
                values.put(BehaviorEntry.COLUMN_NAME_SIBLING_INDEX, parentAction.getActions().indexOf(action));

//                if (action.hasScript()) {
//                    Log.v("Content_Manager", "leaf");
//                    // Leaf, non-root node (associated with basic action)
//                    values.put(BehaviorEntry.COLUMN_NAME_SCRIPT_UUID, action.getScript().getUuid().toString());
//                } else {
//                    Log.v("Content_Manager", "intermediate");
//                    // Non-leaf, non-root node (intermediate node)
//                    values.put(BehaviorEntry.COLUMN_NAME_SCRIPT_UUID, "");
//                }
            } else {
                // Root node
                values.put(BehaviorEntry.COLUMN_NAME_PARENT_UUID, "");
                values.put(BehaviorEntry.COLUMN_NAME_SIBLING_INDEX, "");
//                values.put(BehaviorEntry.COLUMN_NAME_SCRIPT_UUID, "");
            }

            if (action.hasScript()) {
                Log.v("Content_Manager", "leaf");
                // Leaf node (associated with basic action). Usually a non-root leaf node,
                // but can be leaf node for basic action single-node trees.
                values.put(BehaviorEntry.COLUMN_NAME_SCRIPT_UUID, action.getScript().getUuid().toString());
            } else {
                Log.v("Content_Manager", "non-leaf");
                // Non-leaf, non-root node (intermediate node)
                values.put(BehaviorEntry.COLUMN_NAME_SCRIPT_UUID, "");
            }

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(BehaviorEntry.TABLE_NAME, null, values);
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
        // - For each root, get actions with parent with root UUID, addUnit to parent's list of
        //   children, to reconstruct the graph. Do this recursively until the query for
        //   children returns no results (leaf nodes).
        // - For children, query for the associated behavior script.

        public void queryBehaviors () {
            queryBehaviors (null);
        }

        /**
         * Starts with root nodes and recursively constructs the behavior trees.
         * @param parentAction
         */
        public void queryBehaviors (Action parentAction) {

            Log.v("Content_Manager", "queryBehaviors");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorEntry._ID,

                    BehaviorEntry.COLUMN_NAME_UUID,
                    BehaviorEntry.COLUMN_NAME_TAG,
                    BehaviorEntry.COLUMN_NAME_PARENT_UUID,
                    BehaviorEntry.COLUMN_NAME_SIBLING_INDEX,
                    BehaviorEntry.COLUMN_NAME_SCRIPT_UUID,

                    BehaviorEntry.COLUMN_NAME_TIME_CREATED
            };

            // Sort the actions by their sibling index so they will be added to the parent,
            // if any, in the correct order.
            String sortOrder = BehaviorEntry.COLUMN_NAME_SIBLING_INDEX + " ASC";

            // Only get the actions with no parent (the root actions).
            String selection = null;
            String[] selectionArgs = null; // { timeline.getUuid().toString() };
            if (parentAction == null) {
                selection = BehaviorEntry.COLUMN_NAME_PARENT_UUID + " = \"\" AND " + BehaviorEntry.COLUMN_NAME_HIDDEN + " = 0";
                selectionArgs = null;
            } else {
                selection = BehaviorEntry.COLUMN_NAME_PARENT_UUID + " LIKE ? AND " + BehaviorEntry.COLUMN_NAME_HIDDEN + " = 0";
                selectionArgs = new String[] { parentAction.getUuid().toString() };
            }

            Cursor cursor = db.query(
                    BehaviorEntry.TABLE_NAME,  // The table to query
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
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_UUID));
                String tag = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_TAG));
                String scriptUuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_SCRIPT_UUID));
                /*
                String parentBehaviorUuid = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_PARENT_UUID));
                int siblingIndex = cursor.getInt(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_SIBLING_INDEX));
                */

                // Create the action object
                Action action = new Action(UUID.fromString (uuidString), tag);

                // Add action as a child action to the parent action
                if (parentAction != null) {
                    parentAction.addBehavior(action);
                    Log.v("Content_Manager", "\tAdding action to parent (UUID: " + action.getUuid() + ")");
                    Log.v("Content_Manager", "\t\tParent UUID: " + parentAction.getUuid());
                }

                /*
                // Add the action to Clay
                getClay().cacheBehavior(action);
                */

                // Recursive call to reconstruct the action's children
                Log.v("flem", "action.getUuid(): " + action.getUuid());
                if (isParentBehavior (action.getUuid())) {
                    // Recursive query to get the children of the action just created.
                    queryBehaviors(action);
                } else {
                    // Basic action, so set script.
                    Script script = getClay().getCache().getBehaviorScript(UUID.fromString(scriptUuidString));
                    action.setScript(script);
                }

                // TODO: action.setState
                // TODO: action.setScript

                // Add the action to Clay
                // TODO: Only add root actions to Clay? Maybe only those should be available for selection.
                getClay().cacheBehavior(action);
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

                // Create the object
                Script script = new Script(UUID.fromString(uuidString), tag, stateSpacePattern, defaultState);

                // Cache the behavior script
                getClay ().cacheScript(script);

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

        /**
         *
         * @param event
         * @param behaviorStateUuid
         * @return
         */
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
            String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(StateEntry.COLUMN_NAME_BEHAVIOR_UUID));
            String behaviorScriptUuidString = cursor.getString(cursor.getColumnIndexOrThrow(StateEntry.COLUMN_NAME_BEHAVIOR_SCRIPT_UUID));

            // Get the behavior and behavior script from the cache. Here, these are assumed to
            // be available in the cache, since it is assumed they are loaded and cached when
            // Clay is first opened.
            Script behaviorScript = getClay ().getCache ().getBehaviorScript(UUID.fromString(behaviorScriptUuidString));
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
//            Script behaviorScript = getClay().getCache().getBehaviorScript(UUID.fromString(behaviorScriptUuidString));
//            Log.v ("Content_Manager", "> behavior: " + behavior.getUuid());
//            behavior.setScript(behaviorScript);

                // Reconstruct list of behavior state objects
                State behaviorState = new State(UUID.fromString(uuidString), state);
                event.addBehaviorState(behaviorState);

                cursor.moveToNext();
            }
        }

        /** Units */

        public void insertDevice(Unit unit) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(DeviceEntry.COLUMN_NAME_UUID, unit.getUuid().toString());
            values.put(DeviceEntry.COLUMN_NAME_TIMELINE_UUID, unit.getTimeline().getUuid().toString());

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(DeviceEntry.TABLE_NAME, null, values);

            Log.v("Content_Manager", "Inserted unit into database (_id: " + entryId + ")");

        }

        public Unit queryUnit (UUID uuid) {
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
                Unit unit = new Unit(getClay(), UUID.fromString(uuidString));
                unit.setTimelineUuid(UUID.fromString(timelineUuidString));

                // Reconstruct the unit's timeline
                queryTimeline (unit, UUID.fromString(timelineUuidString));

                return unit;
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
                if (!queryEventExists(event.getUuid())) {
                    saveEvent(event.getTimeline(), event);
                } else {
                    updateEvent(event.getTimeline(), event);
                }
            }

            Log.v("Content_Manager", "Inserted timeline state into database (_id: " + entryId + ")");

        }

        public void queryTimeline (Unit unit, UUID timelineUuid) {

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
            queryEvents (unit, timeline);

            // Assign the timeline to the device
            unit.setTimeline(timeline);
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
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_UUID, event.getAction().getUuid().toString());
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

        public boolean isParentBehavior (UUID behaviorUuid) {

            Log.v("Content_Manager", "selectBehaviorsWithParent");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorEntry._ID,
                    BehaviorEntry.COLUMN_NAME_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = BehaviorEntry.COLUMN_NAME_PARENT_UUID + " LIKE ?";
            String[] selectionArgs = { behaviorUuid.toString() };
            Cursor cursor = db.query(
                    BehaviorEntry.TABLE_NAME,  // The table to query
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
        public Action getBasicBehavior (Script script) {

            Action basicAction = null;

            Log.v("Content_Manager", "getBasicBehavior");

            // Get connection to database
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Get the actions' UUIDs
            UUID uuid = script.getUuid();

            // Define a projection that specifies which columns from the database you will...
            // ...actually use after this query.
            String[] projection = {
                    BehaviorEntry._ID,
                    BehaviorEntry.COLUMN_NAME_UUID,
                    BehaviorEntry.COLUMN_NAME_PARENT_UUID,
                    BehaviorEntry.COLUMN_NAME_TAG
            };

            // Specify how to sort the retrieved data
            String sortOrder = null;

            // TODO: if parentUuid is null, then compare the tag, and get the UUID if exists, for reuse

            String selection = null;
            String[] selectionArgs = null;
            Log.v ("New_Behavior", "adding new behavior with tag " + script.getTag());
            selection = BehaviorEntry.COLUMN_NAME_TAG + " = ? AND "
                    + BehaviorEntry.COLUMN_NAME_PARENT_UUID + " LIKE ?";
            selectionArgs = new String[] { script.getTag(), "" };

            Cursor cursor = db.query(
                    BehaviorEntry.TABLE_NAME,  // The table to query
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

                String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_UUID));
                UUID behaviorUuid = UUID.fromString (behaviorUuidString);

                if (getClay().getCache().hasBehavior(behaviorUuid)) {
                    basicAction = getClay().getCache().getBehavior(behaviorUuid);
                    return basicAction;
                } else {
                    basicAction = new Action(behaviorUuid, script); // TODO: sets default state for script
                    storeBehavior (basicAction);
                    getClay().getCache().getActions().add(basicAction);
                    return basicAction;
                }
            } else {
                Log.v ("New_Behavior", "\tThe behavior does not exist.");
                basicAction = new Action(script);
                storeBehavior (basicAction);
                getClay().getCache().getActions().add(basicAction);
                return basicAction;
            }
        }

        public Action getBehaviorComposition (ArrayList<Action> children) {

            Action parentAction = null;

            Log.v("Content_Manager", "getBehaviorComposition");

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
                        BehaviorEntry._ID,
                        BehaviorEntry.COLUMN_NAME_UUID,
                        BehaviorEntry.COLUMN_NAME_PARENT_UUID,
                        BehaviorEntry.COLUMN_NAME_SIBLING_INDEX,
                        BehaviorEntry.COLUMN_NAME_SCRIPT_UUID
                };

                // Specify how to sort the retrieved data
                String sortOrder = null;
//                String sortOrder = BehaviorEntry.COLUMN_NAME_PARENT_UUID + " ASC, "
//                        + BehaviorEntry.COLUMN_NAME_SIBLING_INDEX + " ASC";

                // TODO: if parentUuid is null, then compare the tag, and get the UUID if exists, for reuse

                String selection = null;
                String[] selectionArgs = null;
//                if (parentUuid == null) {
//                    Log.v("New_Behavior", "adding new behavior with tag " + behavior.getTag());
//                    selection = BehaviorEntry.COLUMN_NAME_TAG + " = ?";
//                    selectionArgs = new String[]{behavior.getTag()};
//                } else {
                    selection = BehaviorEntry.COLUMN_NAME_UUID + " LIKE ? AND "
                            + BehaviorEntry.COLUMN_NAME_SIBLING_INDEX + " = ?";
                    selectionArgs = new String[]{ uuid.toString(), "" + children.indexOf (childAction) };
//                }
                Cursor cursor = db.query(
                        BehaviorEntry.TABLE_NAME,  // The table to query
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
                        String parentBehaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_PARENT_UUID));
                        UUID parentBehaviorUuid = UUID.fromString(parentBehaviorUuidString);
                        if (candidateParentUuids.contains(parentBehaviorUuid)) {
                            candidateParentUuids.add(parentBehaviorUuid);
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
                    parentAction.addBehavior(childAction);
                }
//                parentAction.addBehavior(foundUnit.getTimeline().getEvents().get(0).getAction());
//                parentAction.addBehavior(foundUnit.getTimeline().getEvents().get(1).getAction());

//                storeBehavior(parentAction);
//
//                getClay().getCache().getActions().add(parentAction);
            }

            return parentAction;
        }

        // TODO: Add sibling index, because sibling order is part of unique tree structure!
        //public boolean queryBehaviorExists (UUID uuid, UUID parentUuid) {
        public boolean queryBehaviorExists (Action action, Action parentAction) {

            Log.v("Content_Manager", "queryBehaviorExists");

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
                    BehaviorEntry._ID,
                    BehaviorEntry.COLUMN_NAME_UUID,
                    BehaviorEntry.COLUMN_NAME_PARENT_UUID
            };

            // Specify how to sort the retrieved data
            String sortOrder = null;

            // TODO: if parentUuid is null, then compare the tag, and get the UUID if exists, for reuse

            String selection = null;
            String[] selectionArgs = null;
            // TODO!!!!!!!!!!!! Check if action exists (by structure, varies for leaf/basic, intermediate, root)
//            if (parentUuid == null) {
//                Log.v ("New_Behavior", "adding new action with tag " + action.getTag());
//                selection = BehaviorEntry.COLUMN_NAME_TAG + " = ?";
//                selectionArgs = new String[] { action.getTag() };
//            } else {
                selection = BehaviorEntry.COLUMN_NAME_UUID + " LIKE ? AND "
                        + BehaviorEntry.COLUMN_NAME_PARENT_UUID + " LIKE ?";
                selectionArgs = new String[] { uuid.toString(), (parentUuid != null ? parentUuid.toString() : "") };
//            }
            Cursor cursor = db.query(
                    BehaviorEntry.TABLE_NAME,  // The table to query
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
//                    String existingBehaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_UUID));
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

        public boolean queryBehaviorStateExists (UUID uuid) {

            Log.v("Content_Manager", "queryBehaviorStateExists");
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
         * @param unit
         * @param timeline
         */
        public void queryEvents (Unit unit, Timeline timeline) {

            Log.v("Content_Manager", "queryEvents");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    EventEntry._ID,
                    EventEntry.COLUMN_NAME_UUID,
                    EventEntry.COLUMN_NAME_EVENT_INDEX,
                    EventEntry.COLUMN_NAME_TIMELINE_UUID,
                    EventEntry.COLUMN_NAME_BEHAVIOR_UUID
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
                String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_BEHAVIOR_UUID));

                // Reconstruct the event object
                Event event = new Event (UUID.fromString (uuidString), timeline);

                // Get the Action and Script objects. These are assumed to be
                // available in the cache at this point, since they should be loaded when Clay
                // is first oepned.
                //Action action = getClay ().getAction (UUID.fromString (behaviorUuidString));

                // Reconstruct the associated action
                Action action = getClay().getCache().getBehavior(UUID.fromString(behaviorUuidString));
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
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_UUID, event.getAction().getUuid().toString());
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
    public void storeBehavior (Action action) {
        Log.v ("Content_Manager", "storeBehavior");

        if (action.hasScript()) {

            // TODO: Update the basic action IF it now has a parent

            if (!db.queryBehaviorExists(action, null)) {
                Log.v ("Content_Manager", "Saving basic action.");
                db.saveBehavior(action, null);
            } else {
                // This is called when a basic action's state is updated.
                Log.v ("Content_Manager", "Updating basic action.");
                Log.v("Content_Manager", "NULL!!!!!!!!!!!!!!!!! SHOULD NEVER GET HERE!!!!!!!!!!");
//                db.updateBehavior (action, null);
            }
        } else {
            Log.v("Content_Manager", "Saving non-basic action.");

            storeBehaviorTree(action, null);
        }
    }

    private void storeBehaviorTree (Action action, Action parentAction) {

        Log.v ("Content_Manager", "storeBehaviorTree");

        // Breadth first storage, to ensure that a relation to a action's children can be
        // created. The parent must be in the database before children can store a relation to
        // their parent.

        Log.v ("Content_Manager", "\t\t\t\t\tBEHAVIOR SCRIPT: " + action.getScript());

        // Store this node with a relation to its parent (if any)
        // TODO: Only store if the tree STRUCTURE hasn't already been stored. When storing a...
        // TODO: ...action, this means the database must be queried for the tree structure...
        // TODO: ...before saving a action tree, not just the action node UUID.
        if (!db.queryBehaviorExists(action, parentAction)) {

            // TODO: Update the basic action that has a script, addUnit a parent! Yes, the action can have both a parent and a script! (leaf node!)

            db.saveBehavior(action, parentAction);
        } else {
            // This is a action node added to represent the hierarchical structure.
//            db.saveBehavior(action, parentAction);
            //db.updateBehavior(action, parentAction);
            Log.v("Content_Manager", "NULL?????????????? SHOULD NEVER GET HERE?????????????");
        }

        // Store children (if any)
//        if (parentAction != null) {

        // Recursively store the action tree if this action is not a basic action
        if (action.getActions() != null) {
            for (Action childAction : action.getActions()) {
//                if (!hasBehavior(action)) {
                // TODO: Store action index in list
                storeBehaviorTree(childAction, action);
//                db.saveBehavior(childAction, action);
//                }
            }
        }
    }

    public void restoreBehavior (UUID uuid, Callback callback) {
        Log.v("Content_Manager", "restoreBehavior");
//        Action behavior = db.queryBehavior(uuid);
//        if (behavior == null) {
//            callback.onFailure();
//        } else {
//            callback.onSuccess(behavior);
//        }
        Log.v("Content_Manager", "NULL!!!!!!!!!!!!!!!!! SHOULD NEVER GET HERE!!!!!!!!!!");
    }


    @Override
    public void storeTimeline(Timeline timeline) {
        db.saveTimeline(timeline);
    }

    @Override
    public void restoreTimeline (Unit unit, UUID uuid, Callback callback) {
        db.queryTimeline(unit, uuid);
        if (callback != null) {
            if (unit.getTimeline() == null) {
                callback.onFailure();
            } else {
                callback.onSuccess(unit.getTimeline());
            }
        }
    }

    @Override
    public boolean hasEvent (Event event) {
        return db.queryEventExists(event.getUuid());
    }

    @Override
    public void storeEvent(Event event) {
        if (!hasEvent (event)) {
            db.saveEvent(event.getTimeline(), event);
        } else {
            db.updateEvent(event.getTimeline(), event);
        }
    }

    @Override
    public void removeEvent(Event event, Callback callback) {
        // boolean result = db.deleteEvent(event);
        boolean result = db.removeEvent(event);
        if (callback != null) {
            if (!result) {
                callback.onFailure();
            } else {
                callback.onSuccess(null);
            }
        }
    }

    @Override
    public void resetDatabase() {
        Log.v("Content_Manager", "resetDatabase");
        db.resetDatabase(db.getWritableDatabase());
    }

    @Override
    public void storeScript(Script script) {
        Log.v ("Content_Manager", "storeScript");
        db.saveScript(script);
    }

    @Override
    public void restoreScripts() {
        db.queryScripts();
    }

    public void restoreBehaviors () {
        Log.v ("Content_Manager", "restoreBehaviors");
        db.queryBehaviors();
    }

    @Override
    public void storeState(Event event, State state) {
        Log.v ("Content_Manager", "storeState");
        db.saveState(event, state);
    }

    @Override
    public void restoreState(Event event) {
        db.queryState(event);
    }

    @Override
    public void storeDevice(Unit unit) {
        db.insertDevice(unit);
    }

    @Override
    public void restoreDevice(UUID uuid, Callback callback) {
        Unit unit = db.queryUnit(uuid);
        if (callback != null) {
            if (unit == null) {
                callback.onFailure();
            } else {
                callback.onSuccess(unit);
            }
        }
    }

    @Override
    public Action getBehaviorComposition(ArrayList<Action> children) {
        return db.getBehaviorComposition(children);
    }

    @Override
    public Action getBasicBehavior(Script script) {
        return db.getBasicBehavior(script);
    }

    @Override
    public void removeState(State state) {
        db.removeState (state);
    }

    @Override
    public void writeDatabase () {
        try {
            db.writeDatabaseToSD(SQLiteContentManager.DATABASE_NAME);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
