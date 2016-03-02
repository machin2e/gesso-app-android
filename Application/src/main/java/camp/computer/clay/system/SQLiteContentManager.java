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
import java.util.UUID;

import camp.computer.clay.sequencer.ApplicationView;

public class SQLiteContentManager implements ContentManagerInterface {

    private Clay clay;

    private String type;

    private SQLiteDatabaseHelper db;

    // If you change the database schema, you must increment the database version.

    /* Database */

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "clay.db";

    /* Tables */

    private static final String UNIT_TABLE_NAME            = "unit";
    private static final String TIMELINE_TABLE_NAME        = "timeline";
    private static final String EVENT_TABLE_NAME           = "event";
    private static final String BEHAVIOR_TABLE_NAME        = "behavior";
    private static final String BEHAVIOR_SCRIPT_TABLE_NAME = "behaviorScript";
    private static final String BEHAVIOR_STATE_TABLE_NAME  = "behaviorState";

    /* Table schemas (these inner classes define the table schemas) */

    public static abstract class UnitEntry implements BaseColumns {
        public static final String TABLE_NAME                = UNIT_TABLE_NAME;
        public static final String COLUMN_NAME_UUID          = "uuid";
        public static final String COLUMN_NAME_TIMELINE_UUID = "timelineUuid";

        public static final String COLUMN_NAME_TIME_CREATED  = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN = "disabled";
        public static final String COLUMN_NAME_AVAILABLE     = "available";
    }

    public static abstract class TimelineEntry implements BaseColumns {
        public static final String TABLE_NAME               = TIMELINE_TABLE_NAME;
        public static final String COLUMN_NAME_UUID         = "uuid";
        public static final String COLUMN_NAME_UNIT_UUID    = "unitUuid";

        public static final String COLUMN_NAME_TIME_CREATED = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN       = "disabled";
        public static final String COLUMN_NAME_AVAILABLE    = "available";
    }

    public static abstract class EventEntry implements BaseColumns {
        public static final String TABLE_NAME                       = EVENT_TABLE_NAME;
        public static final String COLUMN_NAME_UUID                 = "uuid";
        public static final String COLUMN_NAME_EVENT_INDEX          = "eventIndex";

        public static final String COLUMN_NAME_TIMELINE_UUID        = "timelineUuid";
        public static final String COLUMN_NAME_BEHAVIOR_UUID        = "behaviorUuid";
        public static final String COLUMN_NAME_BEHAVIOR_STATE_UUID  = "behaviorStateUuid";

        public static final String COLUMN_NAME_TIME_CREATED         = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN = "hidden";
        public static final String COLUMN_NAME_AVAILABLE            = "available";
    }

    public static abstract class BehaviorEntry implements BaseColumns {
        public static final String TABLE_NAME                       = BEHAVIOR_TABLE_NAME;
        public static final String COLUMN_NAME_UUID                 = "uuid";
        public static final String COLUMN_NAME_TAG                  = "tag";
        public static final String COLUMN_NAME_PARENT_UUID          = "parentBehaviorUuid";
        public static final String COLUMN_NAME_SIBLING_INDEX        = "siblingIndex"; // i.e., Index in parent behavior's list of children.

        public static final String COLUMN_NAME_TIME_CREATED         = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN               = "hidden";
        public static final String COLUMN_NAME_AVAILABLE            = "available";
    }

    public static abstract class BehaviorScriptEntry implements BaseColumns {
        public static final String TABLE_NAME                = BEHAVIOR_SCRIPT_TABLE_NAME;
        public static final String COLUMN_NAME_UUID          = "uuid";
        public static final String COLUMN_NAME_TAG           = "tag"; // Used by interpretter on devices
        public static final String COLUMN_NAME_DEFAULT_STATE = "defaultState"; // Used to initialize BehaviorState for a (Event, Behavior) pair

        public static final String COLUMN_NAME_TIME_CREATED  = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN = "hidden";
        public static final String COLUMN_NAME_AVAILABLE     = "available";
    }

    public static abstract class BehaviorStateEntry implements BaseColumns {
        public static final String TABLE_NAME                       = BEHAVIOR_STATE_TABLE_NAME;
        public static final String COLUMN_NAME_UUID                 = "uuid";
        public static final String COLUMN_NAME_STATE                = "state";
        public static final String COLUMN_NAME_BEHAVIOR_UUID        = "behaviorUuid";
        public static final String COLUMN_NAME_BEHAVIOR_SCRIPT_UUID = "behaviorScriptUuid";

        public static final String COLUMN_NAME_TIME_CREATED         = "timeCreated";
        public static final String COLUMN_NAME_HIDDEN = "hidden";
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

    private static final String SQL_CREATE_UNIT_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + UnitEntry.TABLE_NAME + " (" +
                    UnitEntry._ID + " INTEGER PRIMARY KEY," +
                    UnitEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    UnitEntry.COLUMN_NAME_TIMELINE_UUID + TEXT_TYPE + COMMA_SEP + // TODO: REMOVE THIS!

                    UnitEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    UnitEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 + COMMA_SEP +
                    UnitEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 +
                    " )";

    private static final String SQL_CREATE_TIMELINE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TimelineEntry.TABLE_NAME + " (" +
                    TimelineEntry._ID + " INTEGER PRIMARY KEY," +
                    TimelineEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    TimelineEntry.COLUMN_NAME_UNIT_UUID + TEXT_TYPE + COMMA_SEP +

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
                    EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID + TEXT_TYPE + COMMA_SEP +

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

                    BehaviorEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 +
                    " )";

    private static final String SQL_CREATE_BEHAVIOR_SCRIPT_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + BehaviorScriptEntry.TABLE_NAME + " (" +
                    BehaviorScriptEntry._ID + " INTEGER PRIMARY KEY," +
                    BehaviorScriptEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    BehaviorScriptEntry.COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                    BehaviorScriptEntry.COLUMN_NAME_DEFAULT_STATE + TEXT_TYPE + COMMA_SEP +

                    BehaviorScriptEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    BehaviorScriptEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 + COMMA_SEP +
                    BehaviorScriptEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 +
                    " )";

    private static final String SQL_CREATE_BEHAVIOR_STATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + BehaviorStateEntry.TABLE_NAME + " (" +
                    BehaviorStateEntry._ID + " INTEGER PRIMARY KEY," +
                    BehaviorStateEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    BehaviorStateEntry.COLUMN_NAME_STATE + TEXT_TYPE + COMMA_SEP +
                    BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID + TEXT_TYPE + COMMA_SEP +
                    BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_SCRIPT_UUID + TEXT_TYPE + COMMA_SEP +

                    BehaviorStateEntry.COLUMN_NAME_TIME_CREATED + DATETIME_TYPE + DATETIME_DEFAULT_NOW + COMMA_SEP +
                    BehaviorStateEntry.COLUMN_NAME_AVAILABLE + INTEGER_TYPE + INTEGER_DEFAULT_1 + COMMA_SEP +
                    BehaviorStateEntry.COLUMN_NAME_HIDDEN + INTEGER_TYPE + INTEGER_DEFAULT_0 +
                    " )";

    private static final String SQL_DELETE_BEHAVIOR_ENTRIES =
            "DROP TABLE IF EXISTS " + BehaviorEntry.TABLE_NAME;

    private static final String SQL_DELETE_BEHAVIOR_SCRIPT_ENTRIES =
            "DROP TABLE IF EXISTS " + BehaviorScriptEntry.TABLE_NAME;

    private static final String SQL_DELETE_BEHAVIOR_STATE_ENTRIES =
            "DROP TABLE IF EXISTS " + BehaviorStateEntry.TABLE_NAME;

    private static final String SQL_DELETE_UNIT_ENTRIES =
            "DROP TABLE IF EXISTS " + UnitEntry.TABLE_NAME;

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
            db.execSQL(SQL_CREATE_BEHAVIOR_SCRIPT_ENTRIES);
            db.execSQL(SQL_CREATE_BEHAVIOR_STATE_ENTRIES);
            db.execSQL(SQL_CREATE_UNIT_ENTRIES);
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

            Log.v ("Content_Manager", "resetDatabase");

            db.execSQL(SQL_DELETE_BEHAVIOR_ENTRIES);
            db.execSQL(SQL_DELETE_BEHAVIOR_SCRIPT_ENTRIES);
            db.execSQL(SQL_DELETE_BEHAVIOR_STATE_ENTRIES);
            db.execSQL(SQL_DELETE_UNIT_ENTRIES);
            db.execSQL(SQL_DELETE_TIMELINE_ENTRIES);
            db.execSQL(SQL_DELETE_EVENT_ENTRIES);

            db.execSQL(SQL_CREATE_BEHAVIOR_ENTRIES);
            db.execSQL(SQL_CREATE_BEHAVIOR_SCRIPT_ENTRIES);
            db.execSQL(SQL_CREATE_BEHAVIOR_STATE_ENTRIES);
            db.execSQL(SQL_CREATE_UNIT_ENTRIES);
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

        public void insertBehavior (Behavior behavior, Behavior parentBehavior) {
            Log.v ("Content_Manager", "insertBehavior");

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys...
            ContentValues values = new ContentValues();
            values.put(BehaviorEntry.COLUMN_NAME_UUID, behavior.getUuid().toString());
            values.put(BehaviorEntry.COLUMN_NAME_TAG, behavior.getTag());

            // ...and if the behavior node has a parent, store the parent and sibling index.
            if (parentBehavior != null) {
                values.put(BehaviorEntry.COLUMN_NAME_PARENT_UUID, parentBehavior.getUuid().toString());
                values.put(BehaviorEntry.COLUMN_NAME_SIBLING_INDEX, parentBehavior.getBehaviors().indexOf(behavior));
            } else {
                values.put(BehaviorEntry.COLUMN_NAME_PARENT_UUID, "");
                values.put(BehaviorEntry.COLUMN_NAME_SIBLING_INDEX, "");
            }

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(BehaviorEntry.TABLE_NAME, null, values);
            Log.v("Content_Manager", "Inserted behavior into database " + entryId + " (UUID: " + behavior.getUuid() + ")");

            // Store behavior state object
            insertBehaviorState(behavior, behavior.getState());

        }

        // TODO: updateBehavior is not needed because entries in "behavior" are never updated once created.
        // TODO: ... When a behavior event is modified on the timeline, the event is updated.
        // TODO: ... When a sequence of behaviors is chunked, new behavior entries are added,
        // TODO: ... a new event is created, the events associated with the chunked behaviors
        // TODO: ... are flagged as hidden/chunked (so they won't be on the timeline), and the
        // TODO: ... newly created event (for the newly created behavior) is added to the timeline,
        // TODO: ... then all the events' position index on the timeline are updated.

        // * Query for timeline
        // * Query for events on the timeline (available and not hidden/disabled)
        // ... (below)
        // * Query for behavior state associated with the event.

        // - Get root behavior UUIDs, unique only (from table of tree edges)
        // - For each root, get behaviors with parent with root UUID, addUnit to parent's list of
        //   children, to reconstruct the graph. Do this recursively until the query for
        //   children returns no results (leaf nodes).
        // - For children, query for the associated behavior script.

        public void queryBehaviors () {
            queryBehaviors (null);
        }

        // TODO: Replace this will a function that starts with root nodes and recursively...
        // TODO: ...constructs the behavior trees.
        public void queryBehaviors (Behavior parentBehavior) {

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

                    BehaviorEntry.COLUMN_NAME_TIME_CREATED
            };

            // Sort the behaviors by their sibling index so they will be added to the parent,
            // if any, in the correct order.
            String sortOrder = BehaviorEntry.COLUMN_NAME_SIBLING_INDEX + " ASC";

            // Only get the behaviors with no parent (the root behaviors).
            String selection = null;
            String[] selectionArgs = null; // { timeline.getUuid().toString() };
            if (parentBehavior == null) {
                selection = BehaviorEntry.COLUMN_NAME_PARENT_UUID + " = \"\" AND " + BehaviorEntry.COLUMN_NAME_HIDDEN + " = 0";
                selectionArgs = null;
            } else {
                selection = BehaviorEntry.COLUMN_NAME_PARENT_UUID + " LIKE ? AND " + BehaviorEntry.COLUMN_NAME_HIDDEN + " = 0";
                selectionArgs = new String[] { parentBehavior.getUuid().toString() };
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
                /*
                String parentBehaviorUuid = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_PARENT_UUID));
                int siblingIndex = cursor.getInt(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_SIBLING_INDEX));
                */

                // Create the behavior object
                Behavior behavior = new Behavior (UUID.fromString (uuidString), tag);

                // Add behavior as a child behavior to the parent behavior
                if (parentBehavior != null) {
                    parentBehavior.addBehavior(behavior);
                    Log.v("Content_Manager", "\tAdding behavior to parent (UUID: " + behavior.getUuid() + ")");
                    Log.v("Content_Manager", "\t\tParent UUID: " + parentBehavior.getUuid());
                }

                /*
                // Add the behavior to Clay
                getClay().addBehavior(behavior);
                */

                // Recursive call to reconstruct the behavior's children
                if (isParentBehavior (behavior.getUuid())) {
                    // Recursive query to get the children of the behavior just created.
                    queryBehaviors(behavior);
                } else {
                    // TODO: (?) Query to get the behavior script associated with the behavior.
                }

                // TODO: behavior.setState
                // TODO: behavior.setScript

                // Add the behavior to Clay
                // TODO: Only add root behaviors to Clay? Maybe only those should be available for selection.
                getClay().addBehavior(behavior);
                Log.v("Content_Manager", "> added beahvior: " + behavior.getUuid());

                // Move to the next entry returned by the query
                cursor.moveToNext();
            }
        }

        // TODO: queryBehaviorScripts () // Get all behaviors, for use to create the first behaviors in the database.

        /** Behavior Script */

        public void insertBehaviorScript (BehaviorScript behaviorScript) {

            if (behaviorScript == null) {
                return;
            }

            Log.v("Content_Manager", "insertBehaviorScript");

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            Log.v("Content_Manager", "behaviorScript: " + behaviorScript);
            Log.v("Content_Manager", "uuid: " + behaviorScript.getUuid());
            Log.v("Content_Manager", "tag: " + behaviorScript.getTag());
            Log.v("Content_Manager", "defaultState: " + behaviorScript.getDefaultState());

//            db.execSQL(SQL_CREATE_BEHAVIOR_SCRIPT_ENTRIES);

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BehaviorScriptEntry.COLUMN_NAME_UUID, behaviorScript.getUuid().toString());
            values.put(BehaviorScriptEntry.COLUMN_NAME_TAG, behaviorScript.getTag());
            values.put(BehaviorScriptEntry.COLUMN_NAME_DEFAULT_STATE, behaviorScript.getDefaultState());

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert (BehaviorScriptEntry.TABLE_NAME, null, values);

            Log.v("Content_Manager", "Inserted behavior script into database (_id: " + entryId + ")");

        }

        public void queryBehaviorScripts () {

            Log.v("Content_Manager", "queryBehaviorScripts");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorScriptEntry._ID,
                    BehaviorScriptEntry.COLUMN_NAME_UUID,
                    BehaviorScriptEntry.COLUMN_NAME_TAG,
                    BehaviorScriptEntry.COLUMN_NAME_DEFAULT_STATE
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = null;
            String[] selectionArgs = null;
            Cursor cursor = db.query(
                    BehaviorScriptEntry.TABLE_NAME,  // The table to query
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
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorScriptEntry.COLUMN_NAME_UUID));
                String tag = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorScriptEntry.COLUMN_NAME_TAG));
                String defaultState = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorScriptEntry.COLUMN_NAME_DEFAULT_STATE));

                // Create the object
                BehaviorScript behaviorScript = new BehaviorScript (UUID.fromString(uuidString), tag, defaultState);

                // Cache the behavior script
                getClay ().addBehaviorScript(behaviorScript);

                // Continue to next behavior script
                cursor.moveToNext();
            }
        }

        public BehaviorScript queryBehaviorScript (UUID uuid) {
            Log.v("Content_Manager", "queryBehaviorScript");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            db.execSQL(SQL_CREATE_BEHAVIOR_SCRIPT_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorScriptEntry._ID,
                    BehaviorScriptEntry.COLUMN_NAME_UUID,
                    BehaviorScriptEntry.COLUMN_NAME_TAG,
                    BehaviorScriptEntry.COLUMN_NAME_DEFAULT_STATE
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = BehaviorScriptEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { uuid.toString() };
            Cursor cursor = db.query(
                    BehaviorScriptEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            Log.v("Content_Manager", "cursor.getCount = " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(UnitEntry._ID)
                );

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorScriptEntry.COLUMN_NAME_UUID));
                String tag = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorScriptEntry.COLUMN_NAME_TAG));
                String defaultState = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorScriptEntry.COLUMN_NAME_DEFAULT_STATE));

                // Create the behavior
                BehaviorScript behaviorScript = new BehaviorScript (UUID.fromString(uuidString), tag, defaultState);

                return behaviorScript;
            }

            return null;
        }

        /** Behavior States */

        public void insertBehaviorState (Behavior behavior, BehaviorState behaviorState) {

            Log.v("Content_Manager", "insertBehaviorState");

            if (behaviorState == null) {
                return;
            }

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BehaviorStateEntry.COLUMN_NAME_UUID, behaviorState.getUuid().toString());
            values.put(BehaviorStateEntry.COLUMN_NAME_STATE, behaviorState.getState());
            values.put(BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID, behavior.getUuid().toString());
            values.put(BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_SCRIPT_UUID, behavior.getScript().getUuid().toString());

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert (BehaviorStateEntry.TABLE_NAME, null, values);

            Log.v("Content_Manager", "Inserted behavior state into database (_id: " + entryId + ")");

        }

        /**
         *
         * @param event
         * @param behaviorStateUuid
         * @return
         */
        public void queryBehaviorState (Event event, UUID behaviorStateUuid) {

            Log.v("Content_Manager", "queryBehaviorState");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorStateEntry._ID,
                    BehaviorStateEntry.COLUMN_NAME_UUID,
                    BehaviorStateEntry.COLUMN_NAME_STATE,
                    BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID,
                    BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_SCRIPT_UUID,
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = BehaviorStateEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { behaviorStateUuid.toString() };
            Cursor cursor = db.query(
                    BehaviorStateEntry.TABLE_NAME,  // The table to query
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
            String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_UUID));
            String state = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_STATE));
            String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID));
            String behaviorScriptUuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_SCRIPT_UUID));

            Log.v ("Content_Manager", "> behaviorUuidString: " + behaviorUuidString);

            // Get the behavior and behavior script from the cache. Here, these are assumed to
            // be available in the cache, since it is assumed they are loaded and cached when
            // Clay is first opened.
            BehaviorScript behaviorScript = getClay ().getCache ().getBehaviorScript(UUID.fromString(behaviorScriptUuidString));
            Behavior behavior = getClay().getCache().getBehavior(UUID.fromString(behaviorUuidString));
            Log.v ("Content_Manager", "> behavior: " + behavior.getUuid());
            behavior.setScript(behaviorScript);

            // Reconstruct behavior state object
            BehaviorState behaviorState = new BehaviorState (UUID.fromString (uuidString), state);
            behavior.setState(behaviorState); // event.getBehavior ().setState (behaviorState);

            // Add the behavior object to the event object
            if (event != null) {
                event.setBehavior(behavior);
            }
        }

        public void queryBehaviorState (Behavior behavior) {

            Log.v("Content_Manager", "queryBehaviorState");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorStateEntry._ID,
                    BehaviorStateEntry.COLUMN_NAME_UUID,
                    BehaviorStateEntry.COLUMN_NAME_STATE,
                    BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID,
                    BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_SCRIPT_UUID,
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID + " LIKE ?";
            String[] selectionArgs = { behavior.getUuid().toString() };
            Cursor cursor = db.query(
                    BehaviorStateEntry.TABLE_NAME,  // The table to query
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
            String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_UUID));
            String state = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_STATE));
            String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID));
            String behaviorScriptUuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_SCRIPT_UUID));

            Log.v ("Content_Manager", "> behaviorUuidString: " + behaviorUuidString);

            // Get the behavior and behavior script from the cache. Here, these are assumed to
            // be available in the cache, since it is assumed they are loaded and cached when
            // Clay is first opened.
            BehaviorScript behaviorScript = getClay().getCache().getBehaviorScript(UUID.fromString(behaviorScriptUuidString));
            Log.v ("Content_Manager", "> behavior: " + behavior.getUuid());
            behavior.setScript(behaviorScript);

            // Reconstruct behavior state object
            BehaviorState behaviorState = new BehaviorState (UUID.fromString (uuidString), state);
            behavior.setState(behaviorState); // event.getBehavior ().setState (behaviorState);
        }

        /** Units */

        public void insertUnit (Unit unit) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(UnitEntry.COLUMN_NAME_UUID, unit.getUuid().toString());
            values.put(UnitEntry.COLUMN_NAME_TIMELINE_UUID, unit.getTimeline().getUuid().toString());

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(UnitEntry.TABLE_NAME, null, values);

            Log.v("Content_Manager", "Inserted unit into database (_id: " + entryId + ")");

        }

        public Unit queryUnit (UUID uuid) {
            Log.v("Content_Manager", "queryUnit");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    UnitEntry._ID,
                    UnitEntry.COLUMN_NAME_UUID,
                    UnitEntry.COLUMN_NAME_TIMELINE_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = UnitEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { uuid.toString() };
            Cursor cursor = db.query(
                    UnitEntry.TABLE_NAME,  // The table to query
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
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(UnitEntry.COLUMN_NAME_UUID));
                String timelineUuidString = cursor.getString(cursor.getColumnIndexOrThrow(UnitEntry.COLUMN_NAME_TIMELINE_UUID));

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

        public void insertTimeline (Timeline timeline) {

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
                    insertEvent(event.getTimeline(), event);
                } else {
                    updateEvent(event.getTimeline(), event);
                }
            }

            Log.v("Content_Manager", "Inserted timeline state into database (_id: " + entryId + ")");

        }

        public void queryTimeline (Unit unit, UUID uuid) {

            Log.v("Content_Manager", "queryTimeline");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            Log.v("Content_Manager", "\tLooking for UUID: " + uuid);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TimelineEntry._ID,
                    TimelineEntry.COLUMN_NAME_UUID,
                    TimelineEntry.COLUMN_NAME_UNIT_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null; // TODO: Sort by index!

            String selection = TimelineEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { uuid.toString() };
            Cursor cursor = db.query(
                    TimelineEntry.TABLE_NAME,  // The table to query
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
//            while (!cursor.isAfterLast()) {

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(TimelineEntry.COLUMN_NAME_UUID));

                Log.v("Content_Manager", "\tFound: " + uuidString);

                // Create the timeline
                Timeline timeline = new Timeline (UUID.fromString(uuidString));

                // Recursively populate the timeline's events
                queryEvents(unit, timeline);

                unit.setTimeline(timeline);


                // TODO: getClay().createBehavior(uuidString, tag, defaultState);

//                cursor.moveToNext();
//            }
        }

        /** Event */

        public void insertEvent(Timeline timeline, Event event) {
            Log.v ("Content_Manager", "insertEvent");

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_EVENT_INDEX, timeline.getEvents().indexOf(event));
            values.put(EventEntry.COLUMN_NAME_TIMELINE_UUID, event.getTimeline().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_UUID, event.getBehavior().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID, (event.getBehavior().getState() != null ? event.getBehavior().getState().getUuid().toString() : ""));

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.insert(EventEntry.TABLE_NAME, null, values);

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

            Log.v("Content_Manager", "\tcursor.getCount = " + cursor.getCount());

            // Return whether or not an entry exists with the UUID
            if (cursor.getCount () > 0) {
                return true;
            } else {
                return false;
            }
        }

        public boolean queryBehaviorExists (UUID uuid) {

            Log.v("Content_Manager", "queryBehaviorExists");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Create the table if it doesn't exist
            db.execSQL(SQL_CREATE_BEHAVIOR_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorEntry._ID,
                    BehaviorEntry.COLUMN_NAME_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = BehaviorEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { uuid.toString() };
            Cursor cursor = db.query(
                    BehaviorEntry.TABLE_NAME,  // The table to query
                    projection,                               // The columns to return
                    selection,                                // The columns for the WHERE clause
                    selectionArgs,                            // The values for the WHERE clause
                    null,                                     // don't group the rows
                    null,                                     // don't filter by row groups
                    sortOrder                                 // The sort order
            );

            Log.v("Content_Manager", "\tcursor.getCount = " + cursor.getCount());

            // Return whether or not an entry exists with the UUID
            if (cursor.getCount () > 0) {
                return true;
            } else {
                return false;
            }
        }

        public boolean queryBehaviorStateExists (UUID uuid) {

            Log.v("Content_Manager", "queryBehaviorStateExists");
            SQLiteDatabase db = SQLiteContentManager.this.db.getReadableDatabase();

            // Create the table if it doesn't exist
            db.execSQL(SQL_CREATE_BEHAVIOR_STATE_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorStateEntry._ID,
                    BehaviorStateEntry.COLUMN_NAME_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = BehaviorStateEntry.COLUMN_NAME_UUID + " LIKE ?";
            String[] selectionArgs = { uuid.toString() };
            Cursor cursor = db.query(
                    BehaviorStateEntry.TABLE_NAME,  // The table to query
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

            String selection = UnitEntry.COLUMN_NAME_UUID + " LIKE ?";
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
                    EventEntry.COLUMN_NAME_BEHAVIOR_UUID,
                    EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = EventEntry.COLUMN_NAME_EVENT_INDEX + " ASC";
            // TODO: Sort by event's timelineIndex

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
                // int eventIndex = cursor.getInt(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_EVENT_INDEX));
                // String timelineUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_TIMELINE_UUID));
                String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_BEHAVIOR_UUID));
                String behaviorStateUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID));

                /*
                Behavior behavior = getClay().getBehavior(UUID.fromString(behaviorUuidString));
                //BehaviorState behaviorState = restoreBehaviorState(behavior, UUID.fromString(behaviorStateUuidString));
//                BehaviorState behaviorState = restoreBehaviorState(behavior, UUID.fromString(behaviorStateUuidString));
//                        new BehaviorState (UUID.fromString(behaviorStateUuidString), behavior, state)
                BehaviorState behaviorState = new BehaviorState (behavior, behavior.getDefaultState()); // <HACK />
                */

                // Reconstruct the event object
                Event event = new Event (UUID.fromString (uuidString), timeline);

                // Get the Behavior and BehaviorScript objects. These are assumed to be
                // available in the cache at this point, since they should be loaded when Clay
                // is first oepned.
                //Behavior behavior = getClay ().getBehavior (UUID.fromString (behaviorUuidString));

                Log.v("Content_Manager", ">> behaviorStateUuidString: " + behaviorStateUuidString);

                // Reconstruct the behavior object
                boolean isNonLeafNode = behaviorStateUuidString.equals("");
                if (isNonLeafNode) {
                    // Reconstruct root or non-leaf node. There is no behavior script or state
                    // associated with these. Note that at this point, the behavior is assumed to
                    // be present in the cache. This should be the case since all behaviors are
                    // cached when the app starts.
                    Behavior behavior = getClay().getCache().getBehavior(UUID.fromString(behaviorUuidString));
                    event.setBehavior (behavior);
                } else {
                    // Reconstruct leaf node.
                    queryBehaviorState(event, UUID.fromString(behaviorStateUuidString));
                }

                // Add the behavior object to the event object
                // event.setBehavior(behavior);

//                // Get behavior state object by UUID
//                BehaviorState behaviorState = null;
//                if (!behaviorStateUuid.equals("")) {
//                    // TODO: Check if the behavior state is cached. First need to implement behavior state caching in cache manager.
//                    behaviorState = queryBehaviorState (UUID.fromString(behaviorStateUuid));
//                }

                // TODO: Query timeline, behavior, behavior state if they're not cached...
                // TODO: ...then only draw events for which all fields are instantiated.
                // TODO:    i.e., Use the event sort of as a filter, and only propagate use
                // TODO:          of the event when it's populated. Or show it as "loading."

                // Add the reconstructed event to the timeline
                timeline.addEvent(event);


                // If no behavior state was found in the store, then assign the default state
//                if (behaviorState == null) {
//                    behaviorState = new BehaviorState(behavior, behavior.getDefaultState());
//                }

                // Create the event
                Log.v ("Content_Manager", "Adding event to the timeline.");
//                Event event = new Event(UUID.fromString(uuidString), timeline, behavior, behaviorState);

                cursor.moveToNext();
            }
        }

        public void updateEvent (Timeline timeline, Event event) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_EVENT_INDEX, timeline.getEvents().indexOf(event));
            values.put(EventEntry.COLUMN_NAME_TIMELINE_UUID, event.getTimeline().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_UUID, event.getBehavior().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID,
                    (event.getBehavior().getState() != null
                            ? event.getBehavior().getState().getUuid().toString()
                            : ""));

            // Insert the new row, returning the primary key value of the new row
            long entryId = db.update(
                    EventEntry.TABLE_NAME,
                    values,
                    EventEntry.COLUMN_NAME_UUID + " LIKE \"" + event.getUuid().toString() + "\"", null);

            Log.v("Content_Manager", "Updated event in database " + entryId);

        }

        public boolean removeEvent (Event event) {

            // Gets the data repository in write mode
            SQLiteDatabase db = SQLiteContentManager.this.db.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_AVAILABLE, 1);
            values.put(EventEntry.COLUMN_NAME_HIDDEN, 1); // This is the flag indicating removal, if true.

            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline());
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
     * Store the behavior. Recursively stores the behavior tree graph by performing a breadth
     * first traversal.
     * @param behavior The behavior to store.
     */
    public void storeBehavior (Behavior behavior) {
        Log.v ("Content_Manager", "storeBehavior");

        if (behavior.hasScript()) {

            // TODO: Update the basic behavior IF it now has a parent

            if (!db.queryBehaviorExists(behavior.getUuid())) {
                Log.v ("Content_Manager", "Saving basic behavior.");
                db.insertBehavior(behavior, null);
            } else {
                Log.v ("Content_Manager", "Updating basic behavior.");
                Log.v("Content_Manager", "NULL!!!!!!!!!!!!!!!!! SHOULD NEVER GET HERE!!!!!!!!!!");
                //db.updateBehavior(behavior, null);
            }
        } else {
            Log.v("Content_Manager", "Saving non-basic behavior.");

            storeBehaviorTree (behavior, null);
        }
    }

    private void storeBehaviorTree (Behavior behavior, Behavior parentBehavior) {

        Log.v ("Content_Manager", "storeBehaviorTree");

        // Breadth first storage, to ensure that a relation to a behavior's children can be
        // created. The parent must be in the database before children can store a relation to
        // their parent.

        Log.v ("Content_Manager", "\t\t\t\t\tBEHAVIOR SCRIPT: " + behavior.getScript());

        // Store this node with a relation to its parent (if any)
        // TODO: Only store if the tree STRUCTURE hasn't already been stored. When storing a...
        // TODO: ...behavior, this means the database must be queried for the tree structure...
        // TODO: ...before saving a behavior tree, not just the behavior node UUID.
        if (!db.queryBehaviorExists(behavior.getUuid())) {

            // TODO: Update the basic behavior that has a script, addUnit a parent! Yes, the behavior can have both a parent and a script! (leaf node!)

            db.insertBehavior(behavior, parentBehavior);
        } else {
            // This is a behavior node added to represent the hierarchical structure
            db.insertBehavior(behavior, parentBehavior);
            //db.updateBehavior(behavior, parentBehavior);
            Log.v("Content_Manager", "NULL?????????????? SHOULD NEVER GET HERE?????????????");
        }

        // Store children (if any)
//        if (parentBehavior != null) {

        if (behavior.getBehaviors() != null) {
            for (Behavior childBehavior : behavior.getBehaviors()) {
//                if (!hasBehavior(behavior)) {
                // TODO: Store behavior index in list
                storeBehaviorTree(childBehavior, behavior);
//                db.insertBehavior(childBehavior, behavior);
//                }
            }
        }
    }

    public void restoreBehavior (UUID uuid, Callback callback) {
        Log.v("Content_Manager", "restoreBehavior");
//        Behavior behavior = db.queryBehavior(uuid);
//        if (behavior == null) {
//            callback.onFailure();
//        } else {
//            callback.onSuccess(behavior);
//        }
        Log.v("Content_Manager", "NULL!!!!!!!!!!!!!!!!! SHOULD NEVER GET HERE!!!!!!!!!!");
    }


    @Override
    public void storeTimeline(Timeline timeline) {
        db.insertTimeline(timeline);
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
            db.insertEvent(event.getTimeline(), event);
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
    public void storeBehaviorScript(BehaviorScript behaviorScript) {
        Log.v ("Content_Manager", "storeBehaviorScript");
        db.insertBehaviorScript(behaviorScript);
    }

    @Override
    public void restoreBehaviorScripts() {
        db.queryBehaviorScripts();
    }

    public void restoreBehaviors () {
        Log.v ("Content_Manager", "restoreBehaviors");
        db.queryBehaviors();
    }

    @Override
    public void restoreBehaviorState (Behavior behavior) {
        db.queryBehaviorState (behavior);
    }

    @Override
    public void storeUnit(Unit unit) {
        db.insertUnit(unit);
    }

    @Override
    public void restoreUnit(UUID uuid, Callback callback) {
        Unit unit = db.queryUnit(uuid);
        if (callback != null) {
            if (unit == null) {
                callback.onFailure();
            } else {
                callback.onSuccess(unit);
            }
        }
    }

}
