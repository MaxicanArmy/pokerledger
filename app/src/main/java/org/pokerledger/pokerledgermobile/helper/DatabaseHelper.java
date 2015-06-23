package org.pokerledger.pokerledgermobile.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.pokerledger.pokerledgermobile.model.Blinds;
import org.pokerledger.pokerledgermobile.model.Break;
import org.pokerledger.pokerledgermobile.model.FormatType;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.GameFormat;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Session;
import org.pokerledger.pokerledgermobile.model.Structure;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by Max on 9/12/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    //database name
    private static final String DATABASE_NAME = "sessionManager";

    //database version
    private static final int DATABASE_VERSION = 1;

    //table names
    private static final String TABLE_BLINDS = "blinds";
    private static final String TABLE_BREAK = "breaks";
    private static final String TABLE_CASH = "cash";
    private static final String TABLE_GAME = "games";
    private static final String TABLE_LOCATION = "locations";
    private static final String TABLE_NOTE = "notes";
    private static final String TABLE_SESSION = "sessions";
    private static final String TABLE_STRUCTURE = "structures";
    private static final String TABLE_TOURNAMENT = "tournament";
    private static final String TABLE_DATE_FILTER = "date_filter";
    private static final String TABLE_FORMAT = "formats";
    private static final String TABLE_FORMAT_TYPES = "format_types";

    //common column names
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_START_DATE = "start_date";
    private static final String KEY_START_TIME = "start_time";
    private static final String KEY_END_DATE = "end_date";
    private static final String KEY_END_TIME = "end_time";
    private static final String KEY_GAME = "game";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_STRUCTURE = "structure";
    private static final String KEY_FILTERED = "filtered";
    private static final String KEY_FORMAT = "format";
    private static final String KEY_FORMAT_TYPE = "format_type";

    //BLINDS table - column names
    private static final String KEY_BLIND_ID = "blind_id";
    private static final String KEY_SMALL_BLIND = "sb";
    private static final String KEY_BIG_BLIND = "bb";
    private static final String KEY_STRADDLE = "straddle";
    private static final String KEY_BRING_IN = "bring_in";
    private static final String KEY_ANTE = "ante";
    private static final String KEY_PER_POINT = "per_point";

    //BREAKS table - column names
    private static final String KEY_BREAK_ID = "break_id";

    //CASH table - column names
    private static final String KEY_BLINDS = "blinds";

    //GAMES table - column names
    private static final String KEY_GAME_ID = "game_id";

    //LOCATIONS table - column names
    private static final String KEY_LOCATION_ID = "location_id";

    //NOTES table
    private static final String KEY_NOTE = "note";

    //SESSION table - column names
    private static final String KEY_BUY_IN = "buy_in";
    private static final String KEY_CASH_OUT = "cash_out";
    private static final String KEY_STATE = "state";

    //STRUCTURE table - column names
    private static final String KEY_STRUCTURE_ID = "structure_id";

    //TOURNAMENT table - column names
    private static final String KEY_ENTRANTS = "entrants";
    private static final String KEY_PLACED = "placed";

    //FILTER table - column names
    private static final String KEY_FILTER_ID = "filter_id";

    //FORMAT table - column names
    private static final String KEY_FORMAT_ID = "format_id";

    //FORMAT_TYPE table - column names
    private static final String KEY_FORMAT_TYPE_ID = "format_type_id";

    //create statements for tables

    //SESSIONS
    private static final String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSION + " (" + KEY_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_START_DATE + " DATE, " + KEY_START_TIME + " TIME, " + KEY_END_DATE + " DATE, " + KEY_END_TIME + " TIME, " + KEY_BUY_IN + " INTEGER, " +
            KEY_CASH_OUT + " INTEGER, " + KEY_STRUCTURE + " INTEGER, " + KEY_GAME + " INTEGER, " + KEY_FORMAT + " INTEGER, " + KEY_LOCATION + " INTEGER, " +
            KEY_STATE + " INTEGER, " + KEY_FILTERED + " INTEGER);";

    //STRUCTURES
    private static final String CREATE_TABLE_STRUCTURES = "CREATE TABLE " + TABLE_STRUCTURE + " (" + KEY_STRUCTURE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_STRUCTURE + " VARCHAR(40), " + KEY_FILTERED + " INTEGER);";

    //GAMES
    private static final String CREATE_TABLE_GAMES = "CREATE TABLE " + TABLE_GAME + " (" + KEY_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_GAME + " VARCHAR(40), " + KEY_FILTERED + " INTEGER);";

    //FORMATS
    private static final String CREATE_TABLE_FORMATS = "CREATE TABLE " + TABLE_FORMAT + " (" + KEY_FORMAT_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_FORMAT + " VARCHAR(40), " + KEY_FORMAT_TYPE + " INTEGER, " + KEY_FILTERED + " INTEGER);";

    //LOCATIONS
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATION + " (" + KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_LOCATION + " VARCHAR(40), " + KEY_FILTERED + " INTEGER);";

    //BLINDS
    private static final String CREATE_TABLE_BLINDS = "CREATE TABLE " + TABLE_BLINDS + " (" + KEY_BLIND_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_SMALL_BLIND + " INTEGER, " + KEY_BIG_BLIND + " INTEGER, " + KEY_STRADDLE + " INTEGER, " + KEY_BRING_IN + " INTEGER, " +
            KEY_ANTE + " INTEGER, " + KEY_PER_POINT + " INTEGER, " + KEY_FILTERED + " INTEGER)";

    //BREAKS
    private static final String CREATE_TABLE_BREAKS = "CREATE TABLE " + TABLE_BREAK + " (" + KEY_BREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_SESSION_ID + " INTEGER, " + KEY_START_DATE + " DATE, " + KEY_START_TIME + " TIME, " + KEY_END_DATE + " DATE, " + KEY_END_TIME + " TIME);";

    //NOTES
    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTE + " (" + KEY_SESSION_ID + " INTEGER, " + KEY_NOTE + " TEXT);";

    //CASH
    private static final String CREATE_TABLE_CASH = "CREATE TABLE " + TABLE_CASH + " (" + KEY_SESSION_ID + " INTEGER UNIQUE, " + KEY_BLINDS + " INTEGER);";

    //TOURNAMENT
    private static final String CREATE_TABLE_TOURNAMENT = "CREATE TABLE " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + " INTEGER UNIQUE, " + KEY_ENTRANTS + " INTEGER, "
            + KEY_PLACED + " INTEGER);";

    //FORMAT_TYPES
    private static final String CREATE_TABLE_FORMAT_TYPES = "CREATE TABLE " + TABLE_FORMAT_TYPES + " (" + KEY_FORMAT_TYPE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_FORMAT_TYPE + " VARCHAR(40));";

    //FILTER
    private static final String CREATE_TABLE_DATE_FILTER = "CREATE TABLE " + TABLE_DATE_FILTER + " (" + KEY_FILTER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_START_DATE + " DATE, " + KEY_START_TIME + " TIME, " + KEY_END_DATE + " DATE, " + KEY_END_TIME + " TIME);";

    //constructor
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(CREATE_TABLE_SESSIONS);
        db.execSQL(CREATE_TABLE_BREAKS);
        db.execSQL(CREATE_TABLE_CASH);
        db.execSQL(CREATE_TABLE_GAMES);
        db.execSQL(CREATE_TABLE_LOCATIONS);
        db.execSQL(CREATE_TABLE_NOTES);
        db.execSQL(CREATE_TABLE_STRUCTURES);
        db.execSQL(CREATE_TABLE_TOURNAMENT);
        db.execSQL(CREATE_TABLE_BLINDS);
        db.execSQL(CREATE_TABLE_DATE_FILTER);
        db.execSQL(CREATE_TABLE_FORMATS);
        db.execSQL(CREATE_TABLE_FORMAT_TYPES);

        ContentValues values;

        //populate structure table
        values = new ContentValues();
        values.put(KEY_STRUCTURE, "No Limit");
        db.insert(TABLE_STRUCTURE, null, values);
        values = new ContentValues();
        values.put(KEY_STRUCTURE, "Pot Limit");
        db.insert(TABLE_STRUCTURE, null, values);
        values = new ContentValues();
        values.put(KEY_STRUCTURE, "Fixed Limit");
        db.insert(TABLE_STRUCTURE, null, values);
        values = new ContentValues();
        values.put(KEY_STRUCTURE, "Mixed Limit");
        db.insert(TABLE_STRUCTURE, null, values);
        values = new ContentValues();
        values.put(KEY_STRUCTURE, "Points");
        db.insert(TABLE_STRUCTURE, null, values);

        //populate game table
        values = new ContentValues();
        values.put(KEY_GAME, "Hold'em");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Omaha");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Omaha HiLo");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Stud");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Stud HiLo");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Razz");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "HORSE");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "HOSE");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "8 Game");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Mixed");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "2-7 Lowball");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "5 Card Draw");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Badugi");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Pineapple");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Open Face Chinese");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Chinese Pineapple");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Big O");
        db.insert(TABLE_GAME, null, values);
        values = new ContentValues();
        values.put(KEY_GAME, "Big Easy");
        db.insert(TABLE_GAME, null, values);

        //populate filter table
        values = new ContentValues();
        values.put(KEY_START_DATE, "NULL");
        values.put(KEY_START_TIME, "NULL");
        values.put(KEY_END_DATE, "NULL");
        values.put(KEY_END_TIME, "NULL");
        db.insert(TABLE_DATE_FILTER, null, values);

        //populate format_types table
        values = new ContentValues();
        values.put(KEY_FORMAT_TYPE, "Cash Game");
        db.insert(TABLE_FORMAT_TYPES, null, values);
        values = new ContentValues();
        values.put(KEY_FORMAT_TYPE, "Tournament");
        db.insert(TABLE_FORMAT_TYPES, null, values);

        //populate formats table
        values = new ContentValues();
        values.put(KEY_FORMAT, "Full Ring");
        values.put(KEY_FORMAT_TYPE, 1);
        values.put(KEY_FILTERED, 0);
        db.insert(TABLE_FORMAT, null, values);
        values = new ContentValues();
        values.put(KEY_FORMAT, "Full Ring");
        values.put(KEY_FORMAT_TYPE, 2);
        values.put(KEY_FILTERED, 0);
        db.insert(TABLE_FORMAT, null, values);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        for (int i = oldVersion; i < newVersion; i++)
        {
            switch(i)
            {

            }
        }
    }

    public boolean runQuery(String s) {
        SQLiteDatabase db = this.getWritableDatabase();

        db.execSQL(s);

        return true;
    }

    public ArrayList<Structure> getAllStructures() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Structure> structures = new ArrayList<Structure>();
        String query = "SELECT * FROM " + TABLE_STRUCTURE;

        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to structures array if any results returned
        if (c.moveToFirst()) {
            do {
                Structure s = new Structure();
                s.setId(c.getInt(c.getColumnIndex(KEY_STRUCTURE_ID)));
                s.setStructure(c.getString(c.getColumnIndex(KEY_STRUCTURE)));
                s.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));

                structures.add(s);
            } while (c.moveToNext());
        }
        c.close();
        return structures;
    }

    public ArrayList<Game> getAllGames() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Game> games = new ArrayList<Game>();
        String query = "SELECT * FROM " + TABLE_GAME;

        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to games array if any results returned
        if (c.moveToFirst()) {
            do {
                Game g = new Game();
                g.setId(c.getInt(c.getColumnIndex(KEY_GAME_ID)));
                g.setGame(c.getString(c.getColumnIndex(KEY_GAME)));
                g.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));

                games.add(g);
            } while (c.moveToNext());
        }
        c.close();
        return games;
    }

    public ArrayList<Location> getAllLocations() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Location> locations = new ArrayList<Location>();
        String query = "SELECT * FROM " + TABLE_LOCATION;

        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to location array if any results returned
        if (c.moveToFirst()) {
            do {
                Location l = new Location();
                l.setId(c.getInt(c.getColumnIndex(KEY_LOCATION_ID)));
                l.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));
                l.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));

                locations.add(l);
            } while (c.moveToNext());
        }
        c.close();
        return locations;
    }

    public ArrayList<Blinds> getAllBlinds() {
        SQLiteDatabase db = this.getReadableDatabase();

        ArrayList<Blinds> blinds = new ArrayList<Blinds>();
        String query = "SELECT * FROM " + TABLE_BLINDS + " ORDER BY " + KEY_PER_POINT + " ASC," + KEY_BIG_BLIND + " ASC, " + KEY_SMALL_BLIND + " ASC, " +
                KEY_STRADDLE + " ASC, " + KEY_ANTE + " ASC, " + KEY_BRING_IN + " ASC;";

        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to location array if any results returned
        if (c.moveToFirst()) {
            do {
                Blinds b = new Blinds();
                b.setId(c.getInt(c.getColumnIndex(KEY_BLIND_ID)));
                b.setSB(c.getInt(c.getColumnIndex(KEY_SMALL_BLIND)));
                b.setBB(c.getInt(c.getColumnIndex(KEY_BIG_BLIND)));
                b.setStraddle(c.getInt(c.getColumnIndex(KEY_STRADDLE)));
                b.setBringIn(c.getInt(c.getColumnIndex(KEY_BRING_IN)));
                b.setAnte(c.getInt(c.getColumnIndex(KEY_ANTE)));
                b.setPerPoint(c.getInt(c.getColumnIndex(KEY_PER_POINT)));
                b.setFiltered(c.getInt(c.getColumnIndex(KEY_FILTERED)));

                blinds.add(b);
            } while (c.moveToNext());
        }
        c.close();
        return blinds;
    }

    public ArrayList<Session> getSessions(int state) {
        SQLiteDatabase db = this.getReadableDatabase();
        ArrayList<Session> sessions = new ArrayList<Session>();

        String query = "SELECT " + TABLE_SESSION + "." + KEY_SESSION_ID + ", " + KEY_START_DATE + ", " + KEY_START_TIME + ", " + KEY_END_DATE + ", " + KEY_END_TIME + ", " +
                KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_FORMAT_ID + ", " + TABLE_FORMAT + "." + KEY_FORMAT + ", " + TABLE_FORMAT + "." + KEY_FILTERED + ", " +
                KEY_FORMAT_TYPE_ID + ", " + TABLE_FORMAT_TYPES + "." + KEY_FORMAT_TYPE + ", " + KEY_STATE + ", " + KEY_STRUCTURE_ID + ", " +
                TABLE_STRUCTURE + "." + KEY_STRUCTURE + ", " + KEY_GAME_ID + ", " + TABLE_GAME + "." + KEY_GAME + ", " + KEY_LOCATION_ID + ", " +
                TABLE_LOCATION + "." + KEY_LOCATION + ", " + TABLE_SESSION + "." + KEY_FILTERED + ", " + TABLE_BLINDS + "." + KEY_BLIND_ID + ", " +
                KEY_SMALL_BLIND + ", " + KEY_BIG_BLIND + ", " + KEY_STRADDLE + ", " + KEY_BRING_IN + ", " + KEY_ANTE + ", " + KEY_PER_POINT + ", " +
                KEY_ENTRANTS + ", " + KEY_PLACED + ", " + KEY_NOTE + " FROM " + TABLE_SESSION + " INNER JOIN " + TABLE_STRUCTURE +
                " ON " + TABLE_SESSION + "." + KEY_STRUCTURE + "=" + KEY_STRUCTURE_ID + " INNER JOIN " + TABLE_GAME +
                " ON " + TABLE_SESSION + "." + KEY_GAME + "=" + KEY_GAME_ID + " INNER JOIN " + TABLE_LOCATION +
                " ON " + TABLE_SESSION + "." + KEY_LOCATION + "=" + KEY_LOCATION_ID + " INNER JOIN " + TABLE_FORMAT +
                " ON " + TABLE_SESSION + "." + KEY_FORMAT + "=" + KEY_FORMAT_ID + " INNER JOIN " + TABLE_FORMAT_TYPES +
                " ON " + TABLE_FORMAT + "." + KEY_FORMAT_TYPE + "=" + TABLE_FORMAT_TYPES + "." + KEY_FORMAT_TYPE_ID + " LEFT JOIN " + TABLE_NOTE +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_NOTE + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_TOURNAMENT +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_TOURNAMENT + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_CASH +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_CASH + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_BLINDS +
                " ON " + TABLE_CASH + "." + KEY_BLINDS + "=" + TABLE_BLINDS + "." + KEY_BLIND_ID + " WHERE " + KEY_STATE + "=" + state +
                " AND " + TABLE_SESSION + "." + KEY_FILTERED + "=0 ORDER BY " + KEY_START_DATE + " DESC, " + KEY_START_TIME + " DESC;";

        Log.v("getAllSessionsQuery", query);
        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to session if any results returned
        if (c.moveToFirst()) {
            do {
                Session s = new Session();
                s.setId(c.getInt(c.getColumnIndex(KEY_SESSION_ID)));
                s.setStartDate(c.getString(c.getColumnIndex(KEY_START_DATE)));
                s.setStartTime(c.getString(c.getColumnIndex(KEY_START_TIME)));
                s.setEndDate(c.getString(c.getColumnIndex(KEY_END_DATE)));
                s.setEndTime(c.getString(c.getColumnIndex(KEY_END_TIME)));
                s.setBuyIn(c.getInt(c.getColumnIndex(KEY_BUY_IN)));
                s.setCashOut(c.getInt(c.getColumnIndex(KEY_CASH_OUT)));
                s.setStructure(new Structure(c.getInt(c.getColumnIndex(KEY_STRUCTURE_ID)), c.getString(c.getColumnIndex(KEY_STRUCTURE))));
                s.setGame(new Game(c.getInt(c.getColumnIndex(KEY_GAME_ID)), c.getString(c.getColumnIndex(KEY_GAME))));
                s.setFormat(new GameFormat(c.getInt(c.getColumnIndex(KEY_FORMAT_ID)), c.getString(c.getColumnIndex(KEY_FORMAT)),
                        new FormatType(c.getInt(c.getColumnIndex(KEY_FORMAT_TYPE_ID)), c.getString(c.getColumnIndex(KEY_FORMAT_TYPE)))));
                s.setLocation(new Location(c.getInt(c.getColumnIndex(KEY_LOCATION_ID)), c.getString(c.getColumnIndex(KEY_LOCATION))));
                s.setState(c.getInt(c.getColumnIndex(KEY_STATE)));

                if (!c.isNull(c.getColumnIndex(KEY_BLIND_ID))) {
                    s.setBlinds(new Blinds(c.getInt(c.getColumnIndex(KEY_BLIND_ID)), c.getInt(c.getColumnIndex(KEY_SMALL_BLIND)), c.getInt(c.getColumnIndex(KEY_BIG_BLIND)),
                            c.getInt(c.getColumnIndex(KEY_STRADDLE)), c.getInt(c.getColumnIndex(KEY_BRING_IN)), c.getInt(c.getColumnIndex(KEY_ANTE)),
                            c.getInt(c.getColumnIndex(KEY_PER_POINT)), c.getInt(c.getColumnIndex(KEY_FILTERED))));
                }
                else {
                    s.setBlinds(null);
                }

                if (!c.isNull(c.getColumnIndex(KEY_ENTRANTS))) {
                    s.setEntrants(c.getInt(c.getColumnIndex(KEY_ENTRANTS)));
                }

                if (state == 0 && !c.isNull(c.getColumnIndex(KEY_PLACED))) {
                    s.setPlaced(c.getInt(c.getColumnIndex(KEY_PLACED)));
                }

                if (!c.isNull(c.getColumnIndex(KEY_NOTE))) {
                    s.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                }

                SQLiteDatabase db2 = this.getReadableDatabase();
                Cursor b = db2.rawQuery("SELECT * FROM "+ TABLE_BREAK +" WHERE " + KEY_SESSION_ID + "=" + s.getId() + " ORDER BY " + KEY_BREAK_ID + " ASC", null);

                if (b.moveToFirst()) {
                    ArrayList<Break> breaks = new ArrayList<Break>();
                    do {
                        breaks.add(new Break(b.getInt(b.getColumnIndex(KEY_BREAK_ID)), b.getString(b.getColumnIndex(KEY_START_DATE)), b.getString(b.getColumnIndex(KEY_START_TIME)),
                                b.getString(b.getColumnIndex(KEY_END_DATE)), b.getString(b.getColumnIndex(KEY_END_TIME))));
                    } while (b.moveToNext());

                    s.setBreaks(breaks);
                }
                b.close();
                sessions.add(s);
            } while (c.moveToNext());
        }
        c.close();

        return sessions;
    }

    public int saveSession(Session s) {
        int flag = 1;
        SQLiteDatabase db = this.getWritableDatabase();

        if (s.getId() == 0) {
            String query = "INSERT INTO " + TABLE_SESSION + " (" + KEY_START_DATE + ", " + KEY_START_TIME + ", " + KEY_END_DATE + ", " + KEY_END_TIME + ", " +
                    KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " + KEY_STRUCTURE + ", " + KEY_GAME + ", " + KEY_FORMAT + ", " + KEY_LOCATION + ", " + KEY_STATE + ") VALUES ('" + s.getStartDate() +
                    "', '" + s.getStartTime() + "', '" + s.getEndDate() + "', '" + s.getEndTime() + "', " + s.getBuyIn() + ", " + s.getCashOut() + ", " +
                    s.getStructure().getId() + ", " + s.getGame().getId() + ", " + s.getFormat().getId() + ", " + s.getLocation().getId() + ", " + s.getState() + ");";

            db.execSQL(query);

            Cursor c = db.rawQuery("SELECT last_insert_rowid() AS " + KEY_SESSION_ID + ";", null);
            if (c.moveToFirst()) {
                int session_id = c.getInt(c.getColumnIndex(KEY_SESSION_ID));

                if (s.getBlinds() != null) {
                    db.execSQL("INSERT INTO " + TABLE_CASH + " (" + KEY_SESSION_ID + ", " + KEY_BLINDS + ") VALUES (" + session_id + ", " + s.getBlinds().getId() + ");");
                } else {
                    db.execSQL("INSERT INTO " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + ", " + KEY_ENTRANTS + ", " + KEY_PLACED + ") VALUES (" + session_id + ", " +
                            s.getEntrants() + ", " + s.getPlaced() + ");");
                }

                if (s.getBreaks() != null) {
                    ArrayList<Break> breaks = s.getBreaks();
                    for (int i = 0; i < breaks.size(); i++) {
                        db.execSQL("INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START_DATE + ", " + KEY_START_TIME + ", " + KEY_END_DATE +
                                ", " + KEY_END_TIME + ") VALUES (" + session_id + ", '" + breaks.get(i).getStartDate() + "', '" + breaks.get(i).getStartTime() +
                                "', " + breaks.get(i).getEndDate() + "', '" + breaks.get(i).getEndTime() + "');");
                    }
                }

                if (s.getNote() != null) {
                    db.execSQL("INSERT INTO " + TABLE_NOTE + " (" + KEY_SESSION_ID + ", " + KEY_NOTE + ") VALUES (" + session_id + ", " +
                            DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
                }
            } else {
                flag = 0;
            }

            c.close();
        } else if (s.getId() > 0) {
            String query = "UPDATE " + TABLE_SESSION + " SET " + KEY_START_DATE + "='" + s.getStartDate() + "', " + KEY_START_TIME + "='" + s.getStartTime() + "' " +
                    KEY_END_DATE + "='" + s.getEndDate() + "', " + KEY_END_TIME + "='" + s.getEndTime() + "' " + KEY_BUY_IN + "=" +
                    s.getBuyIn() + ", " + KEY_CASH_OUT + "=" + s.getCashOut() + ", " + KEY_STRUCTURE + "=" + s.getStructure().getId() + ", " + KEY_GAME +
                    "=" + s.getGame().getId() + ", " + KEY_FORMAT + "=" + s.getFormat().getId() + ", " + KEY_LOCATION + "=" + s.getLocation().getId() + ", " +
                    KEY_STATE + "=" + s.getState() + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";";

            db.execSQL(query);

            if (s.getBlinds() != null) {
                //if the user saves an active session as a tournament then changes it to a cash game when finishing the session the database would be compromised
                //without the insert or ignore and delete queries
                db.execSQL("INSERT OR IGNORE INTO " + TABLE_CASH + " (" + KEY_SESSION_ID + ", " + KEY_BLINDS + ") VALUES (" + s.getId() + ", " + s.getBlinds().getId() + ");");
                db.execSQL("UPDATE " + TABLE_CASH + " SET " + KEY_SESSION_ID + "=" + s.getId() + ", " + KEY_BLINDS + "=" + s.getBlinds().getId() + " WHERE " +
                        KEY_SESSION_ID + "=" + s.getId() + ";");
                //run delete query on tournament table to be certain the user didn't change session type between creating and finishing
                db.execSQL("DELETE FROM " + TABLE_TOURNAMENT + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
            } else {
                db.execSQL("INSERT OR IGNORE INTO " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + ", " + KEY_ENTRANTS + ", " + KEY_PLACED + ") VALUES (" + s.getId() + ", " +
                        s.getEntrants() + ", " + s.getPlaced() + ");");
                db.execSQL("UPDATE " + TABLE_TOURNAMENT + " SET " + KEY_SESSION_ID + "=" + s.getId() + ", " + KEY_ENTRANTS + "=" + s.getEntrants() + ", " +
                        KEY_PLACED + "=" + s.getPlaced() + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
                //run delete query on cash table to be certain the user didn't change session type between creating and finishing
                db.execSQL("DELETE FROM " + TABLE_CASH + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
            }

            if (s.getBreaks() != null) {
                //run delete statement then insert them all fresh to clear out any breaks that were created by toggleBreak then deleted in finishSessionActivity
                db.execSQL("DELETE FROM " + TABLE_BREAK + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
                ArrayList<Break> breaks = s.getBreaks();
                for (int i = 0; i < breaks.size(); i++) {
                    db.execSQL("INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START_DATE + ", " + KEY_START_TIME + ", " + KEY_END_DATE + ", " + KEY_END_TIME +
                            ") VALUES (" + s.getId() + ", '" + breaks.get(i).getStartDate() + "', '" + breaks.get(i).getStartTime() + "', '" + breaks.get(i).getEndDate() +
                            ", " + breaks.get(i).getEndTime() + "');");
                }
            }

            //run delete query for this id on notes table so we can just run an insert and not have to check for duplicates
            db.execSQL("DELETE FROM " + TABLE_NOTE + " WHERE " + KEY_SESSION_ID + "=" + s.getId() + ";");
            if (s.getNote() != null) {
                db.execSQL("INSERT INTO " + TABLE_NOTE + " (" + KEY_SESSION_ID + ", " + KEY_NOTE + ") VALUES (" + s.getId() + ", " +
                        DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
            }
        } else {
            flag = 0;
        }

        db.close();
        return flag;
    }

    public void toggleBreak(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_BREAK + " WHERE " + KEY_SESSION_ID + "=" + id + " ORDER BY " + KEY_BREAK_ID + " ASC;";

        Cursor c = db.rawQuery(query, null);

        Calendar cal = Calendar.getInstance();

        DecimalFormat df = new DecimalFormat("00");
        String currentDate = cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH));
        String currentTime = df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE));

        if (c.moveToLast()) {
            if (c.isNull(c.getColumnIndex(KEY_END_DATE))) {
                String setBreakEndQuery = "UPDATE " + TABLE_BREAK + " SET " + KEY_END_DATE + "='" + currentDate + "', " + KEY_END_TIME + "='" + currentTime +
                        "' WHERE " + KEY_SESSION_ID + "=" + c.getInt(c.getColumnIndex(KEY_SESSION_ID)) + ";";
                db.execSQL(setBreakEndQuery);
                return;
            }
        }

        String insertBreakQuery = "INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START_DATE + ", " + KEY_START_TIME + ", " +
                KEY_END_DATE + ", " + KEY_END_TIME + ") VALUES (" + id + ", '" + currentDate + "', '" + currentTime + "', null, null);";
        db.execSQL(insertBreakQuery);
        c.close();
    }

    public void deleteSession(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + TABLE_SESSION + " SET " + KEY_STATE + "=-1 WHERE " + KEY_SESSION_ID + "=" + id + ";";
        db.execSQL(query);
        db.close();
    }

    public void rebuyAddon(int id, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE " + TABLE_SESSION + " SET " + KEY_BUY_IN + "=" + KEY_BUY_IN + "+" + amount + " WHERE " + KEY_SESSION_ID + "=" + id + ";";
        db.execSQL(query);
        db.close();
    }

    public void addLocation(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT INTO " + TABLE_LOCATION + " (" + KEY_LOCATION + ") VALUES (" + DatabaseUtils.sqlEscapeString(name) + ");";
        db.execSQL(query);
        db.close();
    }

    public Blinds addBlinds(Blinds blindSet) {
        SQLiteDatabase db = this.getWritableDatabase();

        String duplicateCheckQuery = "SELECT * FROM " + TABLE_BLINDS + " WHERE " + KEY_SMALL_BLIND + "=" + blindSet.getSB() + " AND " +
                KEY_BIG_BLIND + "=" + blindSet.getBB() + " AND " + KEY_STRADDLE + "=" + blindSet.getStraddle() + " AND " +
                KEY_BRING_IN + "=" + blindSet.getBringIn() + " AND " + KEY_ANTE + "=" + blindSet.getAnte() + " AND " +
                KEY_PER_POINT + "=" + blindSet.getPerPoint() + ";";

        Cursor c = db.rawQuery(duplicateCheckQuery, null);

        if (c.moveToFirst()) {
            //this creates a blind object that will match an object in the spinner
            blindSet.setId(c.getInt(c.getColumnIndex(KEY_BLIND_ID)));
        }
        else {
            ContentValues blindValues = new ContentValues();
            blindValues.put(KEY_SMALL_BLIND, blindSet.getSB());
            blindValues.put(KEY_BIG_BLIND, blindSet.getBB());
            blindValues.put(KEY_STRADDLE, blindSet.getStraddle());
            blindValues.put(KEY_BRING_IN, blindSet.getBringIn());
            blindValues.put(KEY_ANTE, blindSet.getAnte());
            blindValues.put(KEY_PER_POINT, blindSet.getPerPoint());

            long blind_id = db.insert(TABLE_BLINDS, null, blindValues);

            if (blind_id != -1) {
                blindSet.setId((int) blind_id);
            }
        }

        c.close();
        return blindSet;
    }

    public HashMap<String, String> getFilterDates() {
        HashMap<String, String> result = new HashMap<String, String>();

        /*

        SQLiteDatabase db = this.getWritableDatabase();

        String query = "SELECT * FROM " + TABLE_DATE_FILTER + " WHERE " + KEY_FILTER_ID + "=1;";

        Cursor c;
        int flag = 0;

        do {
            c = db.rawQuery(query, null);

            if (c.moveToFirst()) {
                if (c.isNull(c.getColumnIndex(KEY_START_DATE))) {
                    db.execSQL("UPDATE " + TABLE_FILTER + " SET " + KEY_START + "=(SELECT MIN(" + KEY_START + ") FROM " + TABLE_SESSION + ");");
                }

                if (c.isNull(c.getColumnIndex(KEY_END))) {
                    db.execSQL("UPDATE " + TABLE_FILTER + " SET " + KEY_END + "=(SELECT MAX(" + KEY_START + ") FROM " + TABLE_SESSION + ");");
                }
            }
            flag++;
        } while ((c.isNull(c.getColumnIndex(KEY_START)) || c.isNull(c.getColumnIndex(KEY_END))) && flag < 2); //without flag this will loop infinitely if there are no sessions

        String startDateTime = c.getString(c.getColumnIndex(KEY_START));

        Pattern DATE_PATTERN = Pattern.compile("^(\\d{4}-\\d{2}-\\d{2})");
        Matcher m = DATE_PATTERN.matcher(startDateTime);
        String startDate;

        if (m.find()) {
            startDate = m.group(1);
            result.put("start", startDate);
        }
        else {
            result.put("start", "Start Date");
        }

        String endDateTime = c.getString(c.getColumnIndex(KEY_END));

        m = DATE_PATTERN.matcher(endDateTime);
        String endDate;

        if (m.find()) {
            endDate = m.group(1);
            result.put("end", endDate);
        }
        else {
            result.put("end", "End Date");
        }
        c.close();
*/
        return result;
    }
}