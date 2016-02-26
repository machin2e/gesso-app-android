package camp.computer.clay.system;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.util.UUID;

import camp.computer.clay.sequencer.ApplicationView;

public class SQLiteContentManager implements ContentManagerInterface {

    private Clay clay;

    private String type;

    private SQLiteDatabaseHelper dbHelper;

    // If you change the database schema, you must increment the database version.

    /* Database */

    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "clay.db";

    /* Tables */

    private static final String BEHAVIOR_TABLE_NAME = "behavior";
    private static final String BEHAVIOR_STATE_TABLE_NAME = "behavior_state";
    private static final String UNIT_TABLE_NAME = "unit";
    private static final String TIMELINE_TABLE_NAME = "timeline";
    private static final String EVENT_TABLE_NAME = "event";

    /* Table schemas (these inner classes define the table schemas) */

    public static abstract class BehaviorEntry implements BaseColumns {
        public static final String TABLE_NAME = BEHAVIOR_TABLE_NAME;
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_TAG = "tag";
        public static final String COLUMN_NAME_DEFAULT_STATE = "defaultState";
    }

    public static abstract class BehaviorStateEntry implements BaseColumns {
        public static final String TABLE_NAME = BEHAVIOR_STATE_TABLE_NAME;
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_BEHAVIOR_UUID = "behavior_uuid";
        public static final String COLUMN_NAME_STATE = "state";
    }

    public static abstract class UnitEntry implements BaseColumns {
        public static final String TABLE_NAME = UNIT_TABLE_NAME;
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_TIMELINE_UUID = "timeline_uuid";
    }

    public static abstract class TimelineEntry implements BaseColumns {
        public static final String TABLE_NAME = TIMELINE_TABLE_NAME;
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_UUID = "uuid";
    }

    public static abstract class EventEntry implements BaseColumns {
        public static final String TABLE_NAME = EVENT_TABLE_NAME;
        public static final String COLUMN_NAME_ENTRY_ID = "id";
        public static final String COLUMN_NAME_UUID = "uuid";
        public static final String COLUMN_NAME_TIMELINE_UUID = "timeline_uuid";
        public static final String COLUMN_NAME_BEHAVIOR_UUID = "behavior_uuid";
        public static final String COLUMN_NAME_BEHAVIOR_STATE_UUID = "behavior_state_uuid";
    }

    private static final String TEXT_TYPE = " TEXT";
    private static final String COMMA_SEP = ",";

    private static final String SQL_CREATE_BEHAVIOR_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + BehaviorEntry.TABLE_NAME + " (" +
                    BehaviorEntry._ID + " INTEGER PRIMARY KEY," +
                    BehaviorEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_TAG + TEXT_TYPE + COMMA_SEP +
                    BehaviorEntry.COLUMN_NAME_DEFAULT_STATE + TEXT_TYPE +
                    // Insert any other options for the CREATE command here.
                    " )";

    private static final String SQL_CREATE_BEHAVIOR_STATE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + BehaviorStateEntry.TABLE_NAME + " (" +
                    BehaviorStateEntry._ID + " INTEGER PRIMARY KEY," +
                    BehaviorStateEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    BehaviorStateEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID + TEXT_TYPE + COMMA_SEP +
                    BehaviorStateEntry.COLUMN_NAME_STATE + TEXT_TYPE +
                    // Insert any other options for the CREATE command here.
                    " )";

    private static final String SQL_CREATE_UNIT_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + UnitEntry.TABLE_NAME + " (" +
                    UnitEntry._ID + " INTEGER PRIMARY KEY," +
                    UnitEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    UnitEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    UnitEntry.COLUMN_NAME_TIMELINE_UUID + TEXT_TYPE +
                    // Insert any other options for the CREATE command here.
                    " )";

    private static final String SQL_CREATE_TIMELINE_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + TimelineEntry.TABLE_NAME + " (" +
                    TimelineEntry._ID + " INTEGER PRIMARY KEY," +
                    TimelineEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    TimelineEntry.COLUMN_NAME_UUID + TEXT_TYPE +
                    // Insert any other options for the CREATE command here.
                    " )";

    private static final String SQL_CREATE_EVENT_ENTRIES =
            "CREATE TABLE IF NOT EXISTS " + EventEntry.TABLE_NAME + " (" +
                    EventEntry._ID + " INTEGER PRIMARY KEY," +
                    EventEntry.COLUMN_NAME_ENTRY_ID + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_UUID + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_TIMELINE_UUID + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_BEHAVIOR_UUID + TEXT_TYPE + COMMA_SEP +
                    EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID + TEXT_TYPE +
                    // Insert any other options for the CREATE command here.
                    " )";

    private static final String SQL_DELETE_BEHAVIOR_ENTRIES =
            "DROP TABLE IF EXISTS " + BehaviorEntry.TABLE_NAME;

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
        }


        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(SQL_CREATE_BEHAVIOR_ENTRIES);
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
            db.execSQL(SQL_DELETE_BEHAVIOR_STATE_ENTRIES);
            db.execSQL(SQL_DELETE_UNIT_ENTRIES);
            db.execSQL(SQL_DELETE_TIMELINE_ENTRIES);
            db.execSQL(SQL_DELETE_EVENT_ENTRIES);
        }

        public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            onUpgrade(db, oldVersion, newVersion);
        }

        /** Behaviors */

        public void insertBehavior (Behavior behavior) {

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            db.execSQL(SQL_CREATE_BEHAVIOR_ENTRIES);

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BehaviorEntry.COLUMN_NAME_ENTRY_ID, "1"); // <HACK>
            values.put(BehaviorEntry.COLUMN_NAME_UUID, behavior.getUuid().toString());
            values.put(BehaviorEntry.COLUMN_NAME_TAG, behavior.getTag());
            values.put(BehaviorEntry.COLUMN_NAME_DEFAULT_STATE, behavior.getDefaultState());

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    BehaviorEntry.TABLE_NAME,
                    null,
                    values);
            Log.v("Content_Manager", "Inserted row into database " + newRowId);

        }

        public Behavior queryBehavior(UUID uuid) {
            Log.v("Content_Manager", "queryBehavior");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            db.execSQL(SQL_CREATE_BEHAVIOR_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorEntry._ID,
                    BehaviorEntry.COLUMN_NAME_UUID,
                    BehaviorEntry.COLUMN_NAME_TAG,
                    BehaviorEntry.COLUMN_NAME_DEFAULT_STATE
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

            Log.v("Content_Manager", "cursor.getCount = " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(UnitEntry._ID)
                );

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_UUID));
                String tag = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_TAG));
                String defaultState = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_DEFAULT_STATE));

                // Create the behavior
                Behavior behavior = new Behavior(UUID.fromString(uuidString), tag, defaultState);

                return behavior;
            }

            return null;
        }

        public void queryBehaviors () {
            Log.v("Content_Manager", "queryBehaviors");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            db.execSQL(SQL_CREATE_BEHAVIOR_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorEntry._ID,
                    BehaviorEntry.COLUMN_NAME_UUID,
                    BehaviorEntry.COLUMN_NAME_TAG,
                    BehaviorEntry.COLUMN_NAME_DEFAULT_STATE
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = BehaviorEntry.COLUMN_NAME_TAG + " ASC";

            String selection = null;
            String[] selectionArgs = null;
            Cursor cursor = db.query(
                    BehaviorEntry.TABLE_NAME,  // The table to query
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

                // long itemId = cursor.getLong(cursor.getColumnIndexOrThrow(BehaviorEntry._ID));

                // Read the entry
                String uuid = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_UUID));
                String tag = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_TAG));
                String defaultState = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorEntry.COLUMN_NAME_DEFAULT_STATE));

                // Create the behavior
                getClay().createBehavior(uuid, tag, defaultState);

                cursor.moveToNext();
            }
        }

        /** Behavior States */

        public void insertBehaviorState (BehaviorState behaviorState) {

            Log.v("Content_Manager", "insertBehaviorState");

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(BehaviorStateEntry.COLUMN_NAME_ENTRY_ID, "1"); // <HACK>
            values.put(BehaviorStateEntry.COLUMN_NAME_UUID, behaviorState.getUuid().toString());
            values.put(BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID, behaviorState.getBehaviorUuid().toString());
            values.put(BehaviorStateEntry.COLUMN_NAME_STATE, behaviorState.getState());

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    BehaviorStateEntry.TABLE_NAME,
                    null,
                    values);

            Log.v("Content_Manager", "Inserted behavior state into database (_id: " + newRowId + ")");

        }

        public BehaviorState queryBehaviorState (UUID uuid) {
            Log.v("Content_Manager", "queryBehaviorState");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            db.execSQL(SQL_CREATE_BEHAVIOR_STATE_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    BehaviorStateEntry._ID,
                    BehaviorStateEntry.COLUMN_NAME_UUID,
                    BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID,
                    BehaviorStateEntry.COLUMN_NAME_STATE
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

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(UnitEntry._ID)
                );

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_UUID));
                String behaviorUuid = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_BEHAVIOR_UUID));
                String state = cursor.getString(cursor.getColumnIndexOrThrow(BehaviorStateEntry.COLUMN_NAME_STATE));

                // Get the behavior from the graph (or other location)
                Behavior behavior = getClay().getBehavior(UUID.fromString(behaviorUuid));

                // Create the behavior
                //Behavior behavior = new Behavior(UUID.fromString(uuidString), tag, defaultState);
                BehaviorState behaviorState = new BehaviorState(UUID.fromString(uuidString), behavior, state);

                return behaviorState;
            }

            return null;
        }

        /** Units */

        public void insertUnit (Unit unit) {

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(UnitEntry.COLUMN_NAME_ENTRY_ID, "1"); // <HACK>
            values.put(UnitEntry.COLUMN_NAME_UUID, unit.getUuid().toString());
            values.put(UnitEntry.COLUMN_NAME_TIMELINE_UUID, unit.getTimeline().getUuid().toString());

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    UnitEntry.TABLE_NAME,
                    null,
                    values);

            Log.v("Content_Manager", "Inserted unit into database (_id: " + newRowId + ")");

        }

        public Unit queryUnit (UUID uuid) {
            Log.v("Content_Manager", "queryUnit");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            db.execSQL(SQL_CREATE_UNIT_ENTRIES);

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

            Log.v("Content_Manager", "cursor.getCount = " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(UnitEntry._ID)
                );

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(UnitEntry.COLUMN_NAME_UUID));
                String timelineUuidString = cursor.getString(cursor.getColumnIndexOrThrow(UnitEntry.COLUMN_NAME_TIMELINE_UUID));

                // Create the behavior
                Unit unit = new Unit(getClay(), UUID.fromString(uuidString));
                unit.setTimelineUuid(UUID.fromString(timelineUuidString));

                return unit;
            }

            return null;
        }

        /** Timeline */

        public void insertTimeline (Timeline timeline) {

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(TimelineEntry.COLUMN_NAME_ENTRY_ID, "1"); // <HACK>
            values.put(TimelineEntry.COLUMN_NAME_UUID, timeline.getUuid().toString());

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    TimelineEntry.TABLE_NAME,
                    null,
                    values);

            Log.v("Content_Manager", "Inserted timeline state into database (_id: " + newRowId + ")");

        }

        public Timeline queryTimeline (UUID uuid) {
            Log.v("Content_Manager", "queryTimeline");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Create the table if it doesn't exist
            db.execSQL(SQL_CREATE_TIMELINE_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    TimelineEntry._ID,
                    TimelineEntry.COLUMN_NAME_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder =
                    TimelineEntry.COLUMN_NAME_UUID + " DESC";
            // UnitEntry.COLUMN_NAME_UPDATED + " DESC";

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

            Log.v("Content_Manager", "cursor.getCount = " + cursor.getCount());

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(TimelineEntry._ID)
                );

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(TimelineEntry.COLUMN_NAME_UUID));

                // Create the timeline
                Timeline timeline = new Timeline(UUID.fromString(uuidString));
                return timeline;
                // TODO: getClay().createBehavior(uuidString, tag, defaultState);

//                cursor.moveToNext();
            }

            return null;
        }

        /** Event */

        public void insertEvent(Event event) {
            Log.v ("Content_Manager", "insertEvent");

            Log.v ("Content_Manager", "\tuuid: " + event.getUuid());
            Log.v ("Content_Manager", "\ttimeline uuid: " + event.getTimeline().getUuid());
            Log.v ("Content_Manager", "\tbehavior uuid: " + event.getBehavior().getUuid());
            Log.v("Content_Manager", "\tbehavior state uuid: " + event.getBehaviorState().getUuid());

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_NAME_ENTRY_ID, "1"); // <HACK>
            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_TIMELINE_UUID, event.getTimeline().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_UUID, event.getBehavior().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID, event.getBehaviorState().getUuid().toString());

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.insert(
                    EventEntry.TABLE_NAME,
                    null,
                    values);

            Log.v("Content_Manager", "Inserted event into database (_id: " + newRowId + ")");
        }

        public boolean queryBehaviorExists (UUID uuid) {

            Log.v("Content_Manager", "queryBehaviorExists");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

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

            Log.v("Content_Manager", "cursor.getCount = " + cursor.getCount());

            // Return whether or not an entry exists with the UUID
            if (cursor.getCount () > 0) {
                return true;
            } else {
                return false;
            }
        }

        public boolean queryBehaviorStateExists (UUID uuid) {

            Log.v("Content_Manager", "queryBehaviorStateExists");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

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
            SQLiteDatabase db = dbHelper.getReadableDatabase();

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

        public Event queryEvent(UUID uuid) {

            Log.v("Content_Manager", "queryEvent");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Create the table if it doesn't exist
            db.execSQL(SQL_CREATE_EVENT_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    EventEntry._ID,
                    EventEntry.COLUMN_NAME_UUID,
                    EventEntry.COLUMN_NAME_TIMELINE_UUID,
                    EventEntry.COLUMN_NAME_BEHAVIOR_UUID,
                    EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder =
                    EventEntry.COLUMN_NAME_UUID + " DESC";
            // UnitEntry.COLUMN_NAME_UPDATED + " DESC";

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

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(EventEntry._ID)
                );

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_UUID));
                String timelineUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_TIMELINE_UUID));
                String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_BEHAVIOR_UUID));
                String behaviorStateString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID));

                // TODO: Query timeline, behavior, behavior state if they're not cached...
                // TODO: ...then only draw events for which all fields are instantiated.
                // TODO:    i.e., Use the event sort of as a filter, and only propagate use
                // TODO:          of the event when it's populated. Or show it as "loading."

                // Create the event
                // TODO: Event event = new Event(UUID.fromString(uuidString));
                // TODO: return event;

//                cursor.moveToNext();
            }

            return null;
        }

        public void queryEvents (Timeline timeline) {

            Log.v("Content_Manager", "queryEvents");
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            // Create the table if it doesn't exist
            db.execSQL(SQL_CREATE_EVENT_ENTRIES);

            // Define a projection that specifies which columns from the database
            // you will actually use after this query.
            String[] projection = {
                    EventEntry._ID,
                    EventEntry.COLUMN_NAME_UUID,
                    EventEntry.COLUMN_NAME_TIMELINE_UUID,
                    EventEntry.COLUMN_NAME_BEHAVIOR_UUID,
                    EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID
            };

            // How you want the results sorted in the resulting Cursor
            String sortOrder = null;

            String selection = EventEntry.COLUMN_NAME_TIMELINE_UUID + " LIKE ?";
            String[] selectionArgs = { timeline.getUuid().toString() };
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

            // Iterate over the results
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                long itemId = cursor.getLong(
                        cursor.getColumnIndexOrThrow(EventEntry._ID)
                );

                // Read the entry
                String uuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_UUID));
                String timelineUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_TIMELINE_UUID));
                String behaviorUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_BEHAVIOR_UUID));
                String behaviorStateUuidString = cursor.getString(cursor.getColumnIndexOrThrow(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID));

                // TODO: Query timeline, behavior, behavior state if they're not cached...
                // TODO: ...then only draw events for which all fields are instantiated.
                // TODO:    i.e., Use the event sort of as a filter, and only propagate use
                // TODO:          of the event when it's populated. Or show it as "loading."

                /*
                Behavior behavior = getClay().getBehavior(UUID.fromString(behaviorUuidString));
                //BehaviorState behaviorState = restoreBehaviorState(behavior, UUID.fromString(behaviorStateUuidString));
//                BehaviorState behaviorState = restoreBehaviorState(behavior, UUID.fromString(behaviorStateUuidString));
//                        new BehaviorState (UUID.fromString(behaviorStateUuidString), behavior, state)
                BehaviorState behaviorState = new BehaviorState (behavior, behavior.getDefaultState()); // <HACK />
                */

                Behavior behavior = getClay().getBehavior(UUID.fromString(behaviorUuidString));
                if (behavior == null) {
                    behavior = queryBehavior(UUID.fromString(behaviorUuidString));
                }
                Log.v ("Content_Manager", "behavior: " + behavior);

                BehaviorState behaviorState = queryBehaviorState(UUID.fromString(behaviorStateUuidString));
                Log.v ("Content_Manager", "behavior state: " + behaviorState);

                // If no behavior state was found in the store, then assign the default state
                if (behaviorState == null) {
                    behaviorState = new BehaviorState(behavior, behavior.getDefaultState());
                }

                // Create the event
                Log.v ("Content_Manager", "Adding event to the timeline.");
                Event event = new Event(UUID.fromString(uuidString), timeline, behavior, behaviorState);
                timeline.addEvent(event);

                cursor.moveToNext();
            }
        }

        public void updateEvent (Event event) {

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

            // Create a new map of values, where column names are the keys
            ContentValues values = new ContentValues();
            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_TIMELINE_UUID, event.getTimeline().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_UUID, event.getBehavior().getUuid().toString());
            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID, event.getBehaviorState().getUuid().toString());

            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline());
            Log.v ("Content_Manager", "event.timeline.uuid: " + event.getTimeline().getUuid());
            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline().getUuid());
            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline().getUuid());

            // Insert the new row, returning the primary key value of the new row
            long newRowId;
            newRowId = db.update(
                    EventEntry.TABLE_NAME,
                    values,
                    EventEntry.COLUMN_NAME_UUID + " LIKE \"" + event.getUuid().toString() + "\"", null);
            Log.v("Content_Manager", "Updated event in database " + newRowId);

        }

        public boolean deleteEvent(Event event) {

            // Gets the data repository in write mode
            SQLiteDatabase db = dbHelper.getWritableDatabase();

//            // Create a new map of values, where column names are the keys
//            ContentValues values = new ContentValues();
//            values.put(EventEntry.COLUMN_NAME_UUID, event.getUuid().toString());
//            values.put(EventEntry.COLUMN_NAME_TIMELINE_UUID, event.getTimeline().getUuid().toString());
//            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_UUID, event.getBehavior().getUuid().toString());
//            values.put(EventEntry.COLUMN_NAME_BEHAVIOR_STATE_UUID, event.getBehaviorState().getUuid().toString());
//
//            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline());
//            Log.v ("Content_Manager", "event.timeline.uuid: " + event.getTimeline().getUuid());
//            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline().getUuid());
//            Log.v ("Content_Manager", "event.timeline: " + event.getTimeline().getUuid());

            // Insert the new row, returning the primary key value of the new row
            long newRowId = db.delete(
                    EventEntry.TABLE_NAME,
                    EventEntry.COLUMN_NAME_UUID + " LIKE \"" + event.getUuid().toString() + "\"",
                    null);

            if (newRowId > 0) {
                Log.v("Content_Manager", "Deleted event in database " + newRowId);
                return true;
            } else {
                Log.v("Content_Manager", "Could not delete event in database " + newRowId);
                return false;
            }
        }
    }

    public SQLiteContentManager (Clay clay, String type) {
        this.clay = clay;
        this.type = type;
        this.dbHelper = new SQLiteDatabaseHelper(ApplicationView.getContext());

        // Force reset the database
        // dbHelper.resetDatabase(dbHelper.getWritableDatabase());
    }

    public Clay getClay () {
        return ApplicationView.getApplicationView().getClay();
    }

    public void storeBehavior (Behavior behavior) {
        if (!hasBehavior (behavior)) {
            dbHelper.insertBehavior(behavior);
        }
    }

    @Override
    public void restoreBehavior(UUID uuid, CallbackInterface callback) {
        Behavior behavior = dbHelper.queryBehavior(uuid);
        if (behavior == null) {
            callback.onFailure();
        } else {
            callback.onSuccess(behavior);
        }
    }

    @Override
    public boolean hasBehaviorState(BehaviorState behaviorState) {
        return dbHelper.queryBehaviorStateExists(behaviorState.getUuid());
    }

    @Override
    public void storeBehaviorState(BehaviorState behaviorState) {
        dbHelper.insertBehaviorState(behaviorState);
    }

    @Override
    public void restoreBehaviorState(Behavior behavior, UUID uuid, CallbackInterface callback) {
        BehaviorState behaviorState = dbHelper.queryBehaviorState(uuid);
        if (behaviorState == null) {
            callback.onFailure();
        } else {
            callback.onSuccess(behaviorState);
        }
    }

    @Override
    public void storeTimeline(Timeline timeline) {
        dbHelper.insertTimeline(timeline);
    }

    @Override
    public void restoreTimeline(Unit unit, UUID uuid, CallbackInterface callback) {
        Timeline timeline = dbHelper.queryTimeline(uuid);
        if (timeline == null) {
            callback.onFailure();
        } else {
            callback.onSuccess(timeline);
        }
    }

    @Override
    public boolean hasEvent (Event event) {
        return dbHelper.queryEventExists(event.getUuid());
    }

    @Override
    public void storeEvent(Event event) {
        if (!hasEvent (event)) {
            dbHelper.insertEvent(event);
        } else {
            dbHelper.updateEvent(event);
        }
    }

    @Override
    public void restoreEvent(Timeline timeline, UUID uuid) {
        Event event = dbHelper.queryEvent(uuid);
    }

    @Override
    public void restoreEvents (Timeline timeline) {
        Log.v("Content_Manager", "restoreEvents");
        dbHelper.queryEvents(timeline);
    }

    @Override
    public void removeEvent(Event event, CallbackInterface callback) {
        boolean result = dbHelper.deleteEvent(event);
        if (!result) {
            callback.onFailure();
        } else {
            callback.onSuccess(null);
        }
    }

    public void restoreBehaviors () {
        Log.v ("Content_Manager", "restoreBehaviors");
        dbHelper.queryBehaviors();
    }

    @Override
    public boolean hasBehavior(Behavior behavior) {
        return dbHelper.queryBehaviorExists(behavior.getUuid());
    }

    @Override
    public void storeUnit(Unit unit) {
        dbHelper.insertUnit(unit);
    }

    @Override
    public void restoreUnit(UUID uuid, CallbackInterface callback) {
        Unit unit = dbHelper.queryUnit(uuid);
        if (unit == null) {
            callback.onFailure();
        } else {
            callback.onSuccess(unit);
        }
    }

}
