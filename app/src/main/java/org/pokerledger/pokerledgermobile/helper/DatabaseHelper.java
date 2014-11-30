package org.pokerledger.pokerledgermobile.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.pokerledger.pokerledgermobile.model.Break;
import org.pokerledger.pokerledgermobile.model.Game;
import org.pokerledger.pokerledgermobile.model.Location;
import org.pokerledger.pokerledgermobile.model.Session;
import org.pokerledger.pokerledgermobile.model.Structure;

import java.lang.reflect.Array;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Max on 9/12/14.
 */
public class DatabaseHelper extends SQLiteOpenHelper {
    //database name
    private static final String DATABASE_NAME = "sessionManager";

    //database version
    private static final int DATABASE_VERSION = 1;

    //table names
    private static final String TABLE_BREAK = "breaks";
    private static final String TABLE_CASH = "cash";
    private static final String TABLE_GAME = "games";
    private static final String TABLE_LOCATION = "locations";
    private static final String TABLE_NOTE = "notes";
    private static final String TABLE_SESSION = "sessions";
    private static final String TABLE_STRUCTURE = "structures";
    private static final String TABLE_SYNC = "sync";
    private static final String TABLE_TOURNAMENT = "tournament";

    //common column names
    private static final String KEY_ID = "id";  //this is deprecated, delete soon
    private static final String KEY_SESSION_ID = "session_id";
    private static final String KEY_START = "start";
    private static final String KEY_END = "end";
    private static final String KEY_GAME = "game";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_STRUCTURE = "structure";
    private static final String KEY_ACTIVE = "active"; //this is deprecated, delete soon

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
    private static final String KEY_SYNCED = "synced";

    //STRUCTURE table - column names
    private static final String KEY_STRUCTURE_ID = "structure_id";

    //TOURNAMENT table - column names
    private static final String KEY_ENTRANTS = "entrants";
    private static final String KEY_PLACED = "placed";

    //SYNC table - column names
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_SYNC_NUM = "sync_num";

    //create statements for tables
    //BREAKS
    private static final String CREATE_TABLE_BREAKS = "CREATE TABLE " + TABLE_BREAK + "(" + KEY_BREAK_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_SESSION_ID + " INTEGER, " + KEY_START + " DATETIME, " + KEY_END + " DATETIME);";

    //CASH
    private static final String CREATE_TABLE_CASH = "CREATE TABLE " + TABLE_CASH + "(" + KEY_SESSION_ID + " INTEGER UNIQUE, " + KEY_BLINDS + " VARCHAR(40));";

    //GAME
    private static final String CREATE_TABLE_GAMES = "CREATE TABLE " + TABLE_GAME + "(" + KEY_GAME_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            KEY_GAME + " VARCHAR(40));";

    //LOCATION
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATION + "(" + KEY_LOCATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_LOCATION + " VARCHAR(40));";

    //NOTE
    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTE + "(" + KEY_SESSION_ID + " INTEGER, " + KEY_NOTE + " TEXT);";

    //SESSIONS
    private static final String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSION + "(" + KEY_SESSION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_START + " DATETIME, " + KEY_END + " DATETIME, " + KEY_BUY_IN + " INTEGER, " + KEY_CASH_OUT + " INTEGER, " + KEY_STRUCTURE + " INTEGER, "
            + KEY_GAME + " INTEGER, " + KEY_LOCATION + " INTEGER, " + KEY_STATE + " INTEGER, " + KEY_SYNCED + " BOOLEAN);";

    //STRUCTURE
    private static final String CREATE_TABLE_STRUCTURES = "CREATE TABLE " + TABLE_STRUCTURE + "(" + KEY_STRUCTURE_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_STRUCTURE + " VARCHAR(40));";

    //SYNC
    private static final String CREATE_TABLE_SYNC = "CREATE TABLE " + TABLE_SYNC + " (" + KEY_USERNAME + "  VARCHAR(20), " + KEY_PASSWORD + " VARCHAR(20), " +
            KEY_SYNC_NUM + " INTEGER);";

    //TOURNAMENT
    private static final String CREATE_TABLE_TOURNAMENT = "CREATE TABLE " + TABLE_TOURNAMENT + "(" + KEY_SESSION_ID + " INTEGER UNIQUE, " + KEY_ENTRANTS + " INTEGER, "
            + KEY_PLACED + " INTEGER);";

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
        db.execSQL(CREATE_TABLE_SYNC);

        Log.v("create tables", CREATE_TABLE_SESSIONS + CREATE_TABLE_BREAKS + CREATE_TABLE_CASH + CREATE_TABLE_GAMES + CREATE_TABLE_LOCATIONS + CREATE_TABLE_NOTES +
                CREATE_TABLE_STRUCTURES + CREATE_TABLE_TOURNAMENT + CREATE_TABLE_SYNC);

        ContentValues StructureValues;

        //populate structure table
        StructureValues = new ContentValues();
        StructureValues.put(KEY_STRUCTURE, "No Limit");
        db.insert(TABLE_STRUCTURE, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_STRUCTURE, "Pot Limit");
        db.insert(TABLE_STRUCTURE, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_STRUCTURE, "Fixed Limit");
        db.insert(TABLE_STRUCTURE, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_STRUCTURE, "Mixed Limit");
        db.insert(TABLE_STRUCTURE, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_STRUCTURE, "Points");
        db.insert(TABLE_STRUCTURE, null, StructureValues);

        //populate game table
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Hold'em");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Omaha");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Omaha HiLo");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Stud");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Stud HiLo");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Razz");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "HORSE");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "HOSE");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "8 Game");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Mixed");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "2-7 Lowball");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "5 Card Draw");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Badugi");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Pineapple");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Open Face Chinese");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Chinese Pineapple");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Big O");
        db.insert(TABLE_GAME, null, StructureValues);
        StructureValues = new ContentValues();
        StructureValues.put(KEY_GAME, "Big Easy");
        db.insert(TABLE_GAME, null, StructureValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //ContentValues StructureValues = new ContentValues();

        for (int i = oldVersion; i < newVersion; i++)
        {
            switch(i)
            {

            }
        }
    }

    public ArrayList<Structure> getAllStructures() {
        ArrayList<Structure> structures = new ArrayList<Structure>();
        String query = "SELECT * FROM " + TABLE_STRUCTURE;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to structures array if any results returned
        if (c.moveToFirst()) {
            do {
                Structure s = new Structure();
                s.setId(c.getInt(c.getColumnIndex(KEY_STRUCTURE_ID)));
                s.setStructure(c.getString(c.getColumnIndex(KEY_STRUCTURE)));

                structures.add(s);
            } while (c.moveToNext());
        }
        db.close();
        return structures;
    }

    public ArrayList<Game> getAllGames() {
        ArrayList<Game> games = new ArrayList<Game>();
        String query = "SELECT * FROM " + TABLE_GAME;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to games array if any results returned
        if (c.moveToFirst()) {
            do {
                Game g = new Game();
                g.setId(c.getInt(c.getColumnIndex(KEY_GAME_ID)));
                g.setGame(c.getString(c.getColumnIndex(KEY_GAME)));

                games.add(g);
            } while (c.moveToNext());
        }
        db.close();
        return games;
    }

    public ArrayList<Location> getAllLocations() {
        ArrayList<Location> locations = new ArrayList<Location>();
        String query = "SELECT * FROM " + TABLE_LOCATION;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to location array if any results returned
        if (c.moveToFirst()) {
            do {
                Location l = new Location();
                l.setId(c.getInt(c.getColumnIndex(KEY_LOCATION_ID)));
                l.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));

                locations.add(l);
            } while (c.moveToNext());
        }
        db.close();
        return locations;
    }

    public ArrayList<Session> getSessions(int state) {
        ArrayList<Session> sessions = new ArrayList<Session>();

        String query = "SELECT " + TABLE_SESSION + "." + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ", " + KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " +
                KEY_STATE + ", " + KEY_STRUCTURE_ID + ", " + TABLE_STRUCTURE + "." + KEY_STRUCTURE + ", " + KEY_GAME_ID + ", " +
                TABLE_GAME + "." + KEY_GAME + ", " + KEY_LOCATION_ID + ", " + TABLE_LOCATION + "." + KEY_LOCATION + ", " + TABLE_CASH + "." + KEY_BLINDS + ", " +
                TABLE_TOURNAMENT + "." + KEY_ENTRANTS + ", " + KEY_NOTE + " FROM " + TABLE_SESSION + " INNER JOIN " + TABLE_STRUCTURE +
                " ON " + TABLE_SESSION + "." + KEY_STRUCTURE + "=" + KEY_STRUCTURE_ID + " INNER JOIN " + TABLE_GAME +
                " ON " + TABLE_SESSION + "." + KEY_GAME + "=" + KEY_GAME_ID +" INNER JOIN " + TABLE_LOCATION +
                " ON " + TABLE_SESSION + "." + KEY_LOCATION + "=" + KEY_LOCATION_ID + " LEFT JOIN " + TABLE_NOTE +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_NOTE + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_TOURNAMENT +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_TOURNAMENT + "." + KEY_SESSION_ID + " LEFT JOIN " + TABLE_CASH +
                " ON " + TABLE_SESSION + "." + KEY_SESSION_ID + "=" + TABLE_CASH + "." + KEY_SESSION_ID + " WHERE " + KEY_STATE + "=" + state + " ORDER BY " +
                KEY_START + " DESC;";

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to session if any results returned
        if (c.moveToFirst()) {
            do {
                Session s = new Session();
                s.setId(c.getInt(c.getColumnIndex(KEY_SESSION_ID)));
                s.setStart(c.getString(c.getColumnIndex(KEY_START)));
                s.setEnd(c.getString(c.getColumnIndex(KEY_END)));
                s.setBuyIn(c.getInt(c.getColumnIndex(KEY_BUY_IN)));
                s.setCashOut(c.getInt(c.getColumnIndex(KEY_CASH_OUT)));
                s.setStructure(new Structure(c.getInt(c.getColumnIndex(KEY_STRUCTURE_ID)), c.getString(c.getColumnIndex(KEY_STRUCTURE))));
                s.setGame(new Game(c.getInt(c.getColumnIndex(KEY_GAME_ID)), c.getString(c.getColumnIndex(KEY_GAME))));
                s.setLocation(new Location(c.getInt(c.getColumnIndex(KEY_LOCATION_ID)), c.getString(c.getColumnIndex(KEY_LOCATION))));
                s.setState(c.getInt(c.getColumnIndex(KEY_STATE)));

                if (!c.isNull(c.getColumnIndex(KEY_BLINDS))) {
                    s.setBlinds(c.getString(c.getColumnIndex(KEY_BLINDS)));
                }

                if (!c.isNull(c.getColumnIndex(KEY_ENTRANTS))) {
                    s.setEntrants(c.getInt(c.getColumnIndex(KEY_ENTRANTS)));
                }

                if (state == 0 && !c.isNull(c.getColumnIndex(KEY_PLACED))) {
                    s.setEntrants(c.getInt(c.getColumnIndex(KEY_PLACED)));
                }

                if (!c.isNull(c.getColumnIndex(KEY_NOTE))) {
                    s.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                }

                SQLiteDatabase db2 = this.getReadableDatabase();
                Cursor b = db2.rawQuery("SELECT * FROM "+ TABLE_BREAK +" WHERE " + KEY_SESSION_ID + "=" + s.getId() + " ORDER BY " + KEY_BREAK_ID + " ASC", null);

                if (b.moveToFirst()) {
                    ArrayList<Break> breaks = new ArrayList<Break>();
                    do {
                        breaks.add(new Break(b.getInt(b.getColumnIndex(KEY_BREAK_ID)), b.getString(b.getColumnIndex(KEY_START)), b.getString(b.getColumnIndex(KEY_END))));
                    } while (b.moveToNext());

                    s.setBreaks(breaks);
                }

                sessions.add(s);
            } while (c.moveToNext());
        }
        db.close();

        return sessions;
    }
    /*
    public int saveActive(Session s) {
        int flag = 1;
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT INTO " + TABLE_SESSION + " (" + KEY_START + ", " + KEY_END + ", " + KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " +
                KEY_STRUCTURE + ", " + KEY_GAME + ", " + KEY_LOCATION + ", " + KEY_STATE + ", " + KEY_SYNCED + ") VALUES ('" + s.getStart() +
                "', '" + s.getEnd() + "', " + s.getBuyIn() + ", " + s.getCashOut() + ", " + s.getStructure().getId() + ", " + s.getGame().getId() +
                ", " + s.getLocation().getId() + ", 1, 0);";

        db.execSQL(query);

        Cursor c = db.rawQuery("SELECT last_insert_rowid() AS " + KEY_SESSION_ID + ";", null);
        if (c.moveToFirst()) {
            int session_id = c.getInt(c.getColumnIndex(KEY_SESSION_ID));

            if (s.getBlinds() != null) {
                db.execSQL("INSERT INTO " + TABLE_CASH + " (" + KEY_SESSION_ID + ", " + KEY_BLINDS + ") VALUES (" + session_id + ", '" + s.getBlinds() + "');");
            }
            else {
                db.execSQL("INSERT INTO " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + ", " + KEY_ENTRANTS + ", " + KEY_PLACED + ") VALUES (" + session_id + ", " +
                        s.getEntrants() + ", " + s.getPlaced() + ");");
            }

            if (s.getNote() != null) {
                db.execSQL("INSERT INTO " + TABLE_NOTE + " (" + KEY_SESSION_ID + ", " + KEY_NOTE + ") VALUES (" + session_id + ", " +
                        DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
            }
        }
        else {
            flag = 0;
        }

        db.close();
        return flag;
    }

    public int saveFinished(Session s) {
        int flag = 1;
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT INTO " + TABLE_SESSION + " (" + KEY_START + ", " + KEY_END + ", " + KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " +
                KEY_STRUCTURE + ", " + KEY_GAME + ", " + KEY_LOCATION + ", " + KEY_STATE + ", " + KEY_SYNCED + ") VALUES ('" + s.getStart() +
                "', '" + s.getEnd() + "', " + s.getBuyIn() + ", " + s.getCashOut() + ", " + s.getStructure().getId() + ", " + s.getGame().getId() +
                ", " + s.getLocation().getId() + ", 0, 0);";

        db.execSQL(query);

        Cursor c = db.rawQuery("SELECT last_insert_rowid() AS " + KEY_SESSION_ID + ";", null);
        if (c.moveToFirst()) {
            int session_id = c.getInt(c.getColumnIndex(KEY_SESSION_ID));

            if (s.getBlinds() != null) {
                db.execSQL("INSERT INTO " + TABLE_CASH + " (" + KEY_SESSION_ID + ", " + KEY_BLINDS + ") VALUES (" + session_id + ", '" + s.getBlinds() + "');");
            }
            else {
                db.execSQL("INSERT INTO " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + ", " + KEY_ENTRANTS + ", " + KEY_PLACED + ") VALUES (" + session_id + ", " +
                        s.getEntrants() + ", " + s.getPlaced() + ");");
            }

            if (s.getBreaks() != null) {
                ArrayList<Break> breaks = s.getBreaks();
                for (int i = 0; i < breaks.size(); i++) {
                    db.execSQL("INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ") VALUES (" + session_id + ", '" +
                            breaks.get(i).getStart() + "', '" + breaks.get(i).getEnd() + "');");
                }
            }

            if (s.getNote() != null) {
                db.execSQL("INSERT INTO " + TABLE_NOTE + " (" + KEY_SESSION_ID + ", " + KEY_NOTE + ") VALUES (" + session_id + ", " +
                        DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
            }
        }
        else {
            flag = 0;
        }

        db.close();
        this.deleteSession(s.getId());
        return flag;
    }
    */
    public int saveSession(Session s) {
        int flag = 1;
        SQLiteDatabase db = this.getWritableDatabase();

        if (s.getId() == 0) {
            String query = "INSERT INTO " + TABLE_SESSION + " (" + KEY_START + ", " + KEY_END + ", " + KEY_BUY_IN + ", " + KEY_CASH_OUT + ", " +
                    KEY_STRUCTURE + ", " + KEY_GAME + ", " + KEY_LOCATION + ", " + KEY_STATE + ", " + KEY_SYNCED + ") VALUES ('" + s.getStart() +
                    "', '" + s.getEnd() + "', " + s.getBuyIn() + ", " + s.getCashOut() + ", " + s.getStructure().getId() + ", " + s.getGame().getId() +
                    ", " + s.getLocation().getId() + ", " + s.getState() + ", 0);";

            db.execSQL(query);

            Cursor c = db.rawQuery("SELECT last_insert_rowid() AS " + KEY_SESSION_ID + ";", null);
            if (c.moveToFirst()) {
                int session_id = c.getInt(c.getColumnIndex(KEY_SESSION_ID));

                if (s.getBlinds() != null) {
                    db.execSQL("INSERT INTO " + TABLE_CASH + " (" + KEY_SESSION_ID + ", " + KEY_BLINDS + ") VALUES (" + session_id + ", '" + s.getBlinds() + "');");
                } else {
                    db.execSQL("INSERT INTO " + TABLE_TOURNAMENT + " (" + KEY_SESSION_ID + ", " + KEY_ENTRANTS + ", " + KEY_PLACED + ") VALUES (" + session_id + ", " +
                            s.getEntrants() + ", " + s.getPlaced() + ");");
                }

                if (s.getBreaks() != null) {
                    ArrayList<Break> breaks = s.getBreaks();
                    for (int i = 0; i < breaks.size(); i++) {
                        db.execSQL("INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ") VALUES (" + session_id + ", '" +
                                breaks.get(i).getStart() + "', '" + breaks.get(i).getEnd() + "');");
                    }
                }

                if (s.getNote() != null) {
                    db.execSQL("INSERT INTO " + TABLE_NOTE + " (" + KEY_SESSION_ID + ", " + KEY_NOTE + ") VALUES (" + session_id + ", " +
                            DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
                }
            } else {
                flag = 0;
            }
        } else if (s.getId() > 0) {
            String query = "UPDATE " + TABLE_SESSION + " SET " + KEY_START + "='" + s.getStart() + "', " + KEY_END + "='" + s.getEnd() + "', " + KEY_BUY_IN + "=" +
                    s.getBuyIn() + ", " + KEY_CASH_OUT + "=" + s.getCashOut() + ", " + KEY_STRUCTURE + "=" + s.getStructure().getId() + ", " + KEY_GAME +
                    "=" + s.getGame().getId() + ", " + KEY_LOCATION + "=" + s.getLocation().getId() + ", " + KEY_STATE + "=" + s.getState() + ", " + KEY_SYNCED + "=0 WHERE " +
                    KEY_SESSION_ID + "=" + s.getId() + ";";

            db.execSQL(query);

            if (s.getBlinds() != null) {
                //if the user saves an active session as a tournament then changes it to a cash game when finishing the session the database would be compromised
                //without the insert or ignore and delete queries
                db.execSQL("INSERT OR IGNORE INTO " + TABLE_CASH + " (" + KEY_SESSION_ID + ", " + KEY_BLINDS + ") VALUES (" + s.getId() + ", '" + s.getBlinds() + "');");
                db.execSQL("UPDATE " + TABLE_CASH + " SET " + KEY_SESSION_ID + "=" + s.getId() + ", " + KEY_BLINDS + "='" + s.getBlinds() + "' WHERE " +
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
                    db.execSQL("INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ") VALUES (" + s.getId() + ", '" +
                            breaks.get(i).getStart() + "', '" + breaks.get(i).getEnd() + "');");
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
        String query = "SELECT * FROM " + TABLE_BREAK + " WHERE " + KEY_SESSION_ID + "=" + id + " ORDER BY " + KEY_BREAK_ID + " ASC;";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);

        Calendar cal = Calendar.getInstance();

        DecimalFormat df = new DecimalFormat("00");
        String currentDateTime = cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH)) + " " +
                df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE));

        if (c.moveToLast()) {
            if (c.isNull(c.getColumnIndex(KEY_END))) {
                String setBreakEndQuery = "UPDATE " + TABLE_BREAK + " SET " + KEY_END + "='" + currentDateTime + "' WHERE " + KEY_SESSION_ID + "=" +
                        c.getInt(c.getColumnIndex(KEY_SESSION_ID)) + ";";
                db.execSQL(setBreakEndQuery);
                return;
            }
        }

        String insertBreakQuery = "INSERT INTO " + TABLE_BREAK + " (" + KEY_SESSION_ID + ", " + KEY_START + ", " + KEY_END + ") VALUES (" + id + ", '" +
                currentDateTime + "', null);";
        db.execSQL(insertBreakQuery);
        db.close();
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

    public int getProfit() {
        SQLiteDatabase db = this.getWritableDatabase();
        int total = 0;

        String query = "SELECT sum(" + KEY_CASH_OUT + ") " + KEY_CASH_OUT + ", sum(" + KEY_BUY_IN + ") " + KEY_BUY_IN + " FROM " + TABLE_SESSION + " WHERE " +
                KEY_STATE + "=0;";
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            total = c.getInt(c.getColumnIndex(KEY_CASH_OUT)) - c.getInt(c.getColumnIndex(KEY_BUY_IN));
        }

        db.close();
        return total;
    }

    public double getTime() {
        SQLiteDatabase db = this.getWritableDatabase();
        int total = 0;
        String query = "SELECT " + KEY_START + ", " + KEY_END + " FROM " + TABLE_SESSION + " WHERE " + KEY_STATE + "=0;";

        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            do {
                Calendar t1 = Calendar.getInstance();
                Calendar t2 = Calendar.getInstance();
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
                try {
                    t1.setTime(sdf.parse(c.getString(c.getColumnIndex(KEY_START))));
                    t2.setTime(sdf.parse(c.getString(c.getColumnIndex(KEY_END))));
                } catch (Exception e) {
                    //fucking parse exception needed to be handled
                }

                int minutes = (int) (t2.getTimeInMillis() - t1.getTimeInMillis()) / 60000;
                total += minutes;
            } while (c.moveToNext());
        }

        int hours = total / 60;
        int remainder = total % 60;

        double time = (double) hours + ((double) remainder / 60); //gives hours and minutes as a decimal representation
        db.close();
        return time;
    }
}