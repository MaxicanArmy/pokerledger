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

import java.text.DecimalFormat;
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
    private static final String TABLE_TOURNAMENT = "tournament";

    //common column names
    private static final String KEY_ID = "id";
    private static final String KEY_START = "start";
    private static final String KEY_END = "end";
    private static final String KEY_GAME = "game";
    private static final String KEY_LOCATION = "location";
    private static final String KEY_STRUCTURE = "structure";
    private static final String KEY_ACTIVE = "active";

    //CASH table - column names
    private static final String KEY_BLINDS = "blinds";

    //NOTES table
    private static final String KEY_NOTE = "note";

    //SESSION table - column names
    private static final String KEY_BUY_IN = "buy_in";
    private static final String KEY_CASH_OUT = "cash_out";

    //TOURNAMENT table - column names
    private static final String KEY_ENTRANTS = "entrants";
    private static final String KEY_PLACED = "placed";

    //create statements for tables

    //SESSIONS
    private static final String CREATE_TABLE_SESSIONS = "CREATE TABLE " + TABLE_SESSION + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_START + " DATETIME, " + KEY_END + " DATETIME, " + KEY_BUY_IN + " INTEGER, " + KEY_CASH_OUT + " INTEGER, " + KEY_STRUCTURE + " INTEGER, "
            + KEY_GAME + " INTEGER, " + KEY_LOCATION + " INTEGER, " + KEY_ACTIVE + " BOOLEAN, is_deleted BOOLEAN, is_synced BOOLEAN);";

    //GAME
    private static final String CREATE_TABLE_GAMES = "CREATE TABLE " + TABLE_GAME + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + KEY_GAME + " VARCHAR(40));";

    //LOCATION
    private static final String CREATE_TABLE_LOCATIONS = "CREATE TABLE " + TABLE_LOCATION + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_LOCATION + " VARCHAR(40));";

    //STRUCTURE
    private static final String CREATE_TABLE_STRUCTURES = "CREATE TABLE " + TABLE_STRUCTURE + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + KEY_STRUCTURE + " VARCHAR(40));";

    //BREAKS
    private static final String CREATE_TABLE_BREAKS = "CREATE TABLE " + TABLE_BREAK + "(" + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, session INTEGER, "
            + KEY_START + " DATETIME, " + KEY_END + " DATETIME);";

    //CASH
    private static final String CREATE_TABLE_CASH = "CREATE TABLE " + TABLE_CASH + "(" + KEY_ID + " INTEGER, " + KEY_BLINDS + " VARCHAR(40));";

    //NOTE
    private static final String CREATE_TABLE_NOTES = "CREATE TABLE " + TABLE_NOTE + "(" + KEY_ID + " INTEGER, " + KEY_NOTE + " TEXT);";

    //TOURNAMENT
    private static final String CREATE_TABLE_TOURNAMENT = "CREATE TABLE " + TABLE_TOURNAMENT + "(" + KEY_ID + " INTEGER, " + KEY_ENTRANTS + " INTEGER, "
            + KEY_PLACED + " INTEGER);";

    //SYNC
    private static final String CREATE_TABLE_SYNC = "CREATE TABLE sync (username VARCHAR(20), password VARCHAR(20), sync_num INTEGER);";

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
                s.setId(c.getInt(c.getColumnIndex(KEY_ID)));
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
                g.setId(c.getInt(c.getColumnIndex(KEY_ID)));
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
                l.setId(c.getInt(c.getColumnIndex(KEY_ID)));
                l.setLocation(c.getString(c.getColumnIndex(KEY_LOCATION)));

                locations.add(l);
            } while (c.moveToNext());
        }
        db.close();
        return locations;
    }

    public ArrayList<Session> getActiveSessions() {
        ArrayList<Session> sessions = new ArrayList<Session>();

        String query = "SELECT sessions.id AS sessionID, sessions.start, sessions.end, sessions.buy_in, sessions.cash_out, sessions.active, " +
                "structures.id AS structureID, structures.structure, games.id AS gameID, games.game, locations.id AS locationID, locations.location, cash.blinds, " +
                "tournament.entrants, notes.note FROM sessions INNER JOIN structures ON sessions.structure=structures.id " +
                "INNER JOIN games ON sessions.game=games.id INNER JOIN locations ON sessions.location=locations.id LEFT JOIN notes ON sessions.id=notes.id " +
                "LEFT JOIN tournament ON sessions.id=tournament.id LEFT JOIN cash ON sessions.id=cash.id WHERE sessions.active=1;";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to session if any results returned
        if (c.moveToFirst()) {
            do {
                Session s = new Session();
                s.setId(c.getInt(c.getColumnIndex("sessionID")));
                s.setStart(c.getString(c.getColumnIndex(KEY_START)));
                s.setEnd(c.getString(c.getColumnIndex(KEY_END)));
                s.setBuyIn(c.getInt(c.getColumnIndex(KEY_BUY_IN)));
                s.setCashOut(c.getInt(c.getColumnIndex(KEY_CASH_OUT)));
                s.setStructure(new Structure(c.getInt(c.getColumnIndex("structureID")), c.getString(c.getColumnIndex(KEY_STRUCTURE))));
                s.setGame(new Game(c.getInt(c.getColumnIndex("gameID")), c.getString(c.getColumnIndex(KEY_GAME))));
                s.setLocation(new Location(c.getInt(c.getColumnIndex("locationID")), c.getString(c.getColumnIndex(KEY_LOCATION))));
                s.setActive(c.getInt(c.getColumnIndex(KEY_ACTIVE)) == 1);

                if (!c.isNull(c.getColumnIndex("blinds"))) {
                    s.setBlinds(c.getString(c.getColumnIndex(KEY_BLINDS)));
                }
                else if (!c.isNull(c.getColumnIndex("entrants"))) {
                    s.setEntrants(c.getInt(c.getColumnIndex("entrants")));
                }

                if (!c.isNull(c.getColumnIndex("note"))) {
                    s.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                }

                SQLiteDatabase db2 = this.getReadableDatabase();
                Cursor b = db2.rawQuery("SELECT * FROM breaks WHERE breaks.session=" + Integer.toString(c.getInt(c.getColumnIndex("sessionID"))) + " ORDER BY id ASC", null);

                if (b.moveToFirst()) {
                    ArrayList<Break> breaks = new ArrayList<Break>();
                    do {
                        breaks.add(new Break(b.getInt(b.getColumnIndex("id")), b.getString(b.getColumnIndex("start")), b.getString(b.getColumnIndex("end"))));
                    } while (b.moveToNext());

                    s.setBreaks(breaks);
                }

                sessions.add(s);
            } while (c.moveToNext());
        }
        db.close();
        return sessions;
    }

    public ArrayList<Session> getFinishedSessions() {
        ArrayList<Session> sessions = new ArrayList<Session>();

        String query = "SELECT sessions.id AS sessionID, sessions.start, sessions.end, sessions.buy_in, sessions.cash_out, sessions.active, " +
                "structures.id AS structureID, structures.structure, games.id AS gameID, games.game, locations.id AS locationID, locations.location, cash.blinds, " +
                "tournament.entrants, notes.note FROM sessions INNER JOIN structures ON sessions.structure=structures.id " +
                "INNER JOIN games ON sessions.game=games.id INNER JOIN locations ON sessions.location=locations.id LEFT JOIN notes ON sessions.id=notes.id " +
                "LEFT JOIN tournament ON sessions.id=tournament.id LEFT JOIN cash ON sessions.id=cash.id WHERE sessions.active=0;";


        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(query, null);

        //loop through rows and add to session if any results returned
        if (c.moveToFirst()) {
            do {
                Session s = new Session();
                s.setId(c.getInt(c.getColumnIndex("sessionID")));
                s.setStart(c.getString(c.getColumnIndex(KEY_START)));
                s.setEnd(c.getString(c.getColumnIndex(KEY_END)));
                s.setBuyIn(c.getInt(c.getColumnIndex(KEY_BUY_IN)));
                s.setCashOut(c.getInt(c.getColumnIndex(KEY_CASH_OUT)));
                s.setStructure(new Structure(c.getInt(c.getColumnIndex("structureID")), c.getString(c.getColumnIndex(KEY_STRUCTURE))));
                s.setGame(new Game(c.getInt(c.getColumnIndex("gameID")), c.getString(c.getColumnIndex(KEY_GAME))));
                s.setLocation(new Location(c.getInt(c.getColumnIndex("locationID")), c.getString(c.getColumnIndex(KEY_LOCATION))));
                s.setActive(c.getInt(c.getColumnIndex(KEY_ACTIVE)) == 1);

                if (!c.isNull(c.getColumnIndex("blinds"))) {
                    s.setBlinds(c.getString(c.getColumnIndex(KEY_BLINDS)));
                }
                else if (!c.isNull(c.getColumnIndex("entrants"))) {
                    s.setEntrants(c.getInt(c.getColumnIndex("entrants")));
                }

                if (!c.isNull(c.getColumnIndex("note"))) {
                    s.setNote(c.getString(c.getColumnIndex(KEY_NOTE)));
                }

                SQLiteDatabase db2 = this.getReadableDatabase();
                Cursor b = db2.rawQuery("SELECT * FROM breaks WHERE breaks.session=" + Integer.toString(c.getInt(c.getColumnIndex("sessionID"))) + " ORDER BY id ASC", null);

                if (b.moveToFirst()) {
                    ArrayList<Break> breaks = new ArrayList<Break>();
                    do {
                        breaks.add(new Break(b.getInt(b.getColumnIndex("id")), b.getString(b.getColumnIndex("start")), b.getString(b.getColumnIndex("end"))));
                    } while (b.moveToNext());

                    s.setBreaks(breaks);
                }

                sessions.add(s);
            } while (c.moveToNext());
        }
        db.close();
        return sessions;
    }

    public int saveActive(Session s) {
        int flag = 1;
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT INTO sessions (start, end, buy_in, cash_out, structure, game, location, active) VALUES ('" +
                s.getStart() + "', '" + s.getEnd() + "', " + s.getBuyIn() + ", " + s.getCashOut() + ", " + s.getStructure().getId() + ", " +
                s.getGame().getId() + ", " + s.getLocation().getId() + ", 1);";

        db.execSQL(query);

        Cursor c = db.rawQuery("SELECT last_insert_rowid() AS id;", null);
        if (c.moveToFirst()) {
            int session_id = c.getInt(c.getColumnIndex("id"));

            if (s.getBlinds() != null) {
                db.execSQL("INSERT INTO cash (id, blinds) VALUES (" + session_id + ", '" + s.getBlinds() + "');");
            }
            else {
                db.execSQL("INSERT INTO tournament (id, entrants, placed) VALUES (" + session_id + ", " + s.getEntrants() + ", " + s.getPlaced() + ");");
            }

            if (s.getNote() != null) {
                db.execSQL("INSERT INTO notes (id, note) VALUES (" + session_id + ", " + DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
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

        String query = "INSERT INTO sessions (start, end, buy_in, cash_out, structure, game, location, active) VALUES ('" +
                s.getStart() + "', '" + s.getEnd() + "', " + s.getBuyIn() + ", " + s.getCashOut() + ", " + s.getStructure().getId() + ", " +
                s.getGame().getId() + ", " + s.getLocation().getId() + ", 0);";

        db.execSQL(query);

        Cursor c = db.rawQuery("SELECT last_insert_rowid() AS id;", null);
        if (c.moveToFirst()) {
            int session_id = c.getInt(c.getColumnIndex("id"));

            if (s.getBlinds() != null) {
                db.execSQL("INSERT INTO cash (id, blinds) VALUES (" + session_id + ", '" + s.getBlinds() + "');");
            }
            else {
                db.execSQL("INSERT INTO tournament (id, entrants, placed) VALUES (" + session_id + ", " + s.getEntrants() + ", " + s.getPlaced() + ");");
            }

            if (s.getBreaks() != null) {
                ArrayList<Break> breaks = s.getBreaks();
                for (int i = 0; i < breaks.size(); i++) {
                    db.execSQL("INSERT INTO breaks (session, start, end) VALUES (" + session_id + ", '" + breaks.get(i).getStart() + "', '" + breaks.get(i).getEnd() + "');");
                }
            }

            if (s.getNote() != null) {
                db.execSQL("INSERT INTO notes (id, note) VALUES (" + session_id + ", " + DatabaseUtils.sqlEscapeString(s.getNote()) + ");");
            }
        }
        else {
            flag = 0;
        }

        db.close();
        this.deleteSession(s.getId());
        return flag;
    }

    public void toggleBreak(int id) {
        String query = "SELECT * FROM breaks WHERE breaks.session=" + Integer.toString(id) + " ORDER BY id ASC;";

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor c = db.rawQuery(query, null);

        Calendar cal = Calendar.getInstance();

        DecimalFormat df = new DecimalFormat("00");
        String currentDateTime = cal.get(Calendar.YEAR) + "-" + df.format(cal.get(Calendar.MONTH) + 1) + "-" + df.format(cal.get(Calendar.DAY_OF_MONTH)) + " " + df.format(cal.get(Calendar.HOUR_OF_DAY)) + ":" + df.format(cal.get(Calendar.MINUTE));

        if (c.moveToLast()) {
            if (c.isNull(c.getColumnIndex("end"))) {
                String setBreakEndQuery = "UPDATE breaks SET end='" + currentDateTime + "' WHERE id=" + c.getInt(c.getColumnIndex("id")) + ";";
                db.execSQL(setBreakEndQuery);
                return;
            }
        }

        String insertBreakQuery = "INSERT INTO breaks (session, start, end) VALUES (" + id + ", '" + currentDateTime + "', null);";
        db.execSQL(insertBreakQuery);
        db.close();
    }
    /*
    public void deleteActive(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sessionQuery = "DELETE FROM sessions WHERE id=" + Integer.toString(id) + ";";
        db.execSQL(sessionQuery);

        String tournamentQuery = "DELETE FROM tournament WHERE id=" + Integer.toString(id) + ";";
        db.execSQL(tournamentQuery);

        String cashQuery = "DELETE FROM cash WHERE id=" + Integer.toString(id) + ";";
        db.execSQL(cashQuery);

        String noteQuery = "DELETE FROM notes WHERE id=" + Integer.toString(id) + ";";
        db.execSQL(noteQuery);

        String breakQuery = "DELETE FROM breaks WHERE session=" + Integer.toString(id) + ";";
        db.execSQL(breakQuery);
        db.close();
    }
    */

    public void deleteSession(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        String sessionQuery = "DELETE FROM sessions WHERE id=" + Integer.toString(id) + ";";
        db.execSQL(sessionQuery);

        String tournamentQuery = "DELETE FROM tournament WHERE id=" + Integer.toString(id) + ";";
        db.execSQL(tournamentQuery);

        String cashQuery = "DELETE FROM cash WHERE id=" + Integer.toString(id) + ";";
        db.execSQL(cashQuery);

        String noteQuery = "DELETE FROM notes WHERE id=" + Integer.toString(id) + ";";
        db.execSQL(noteQuery);

        String breakQuery = "DELETE FROM breaks WHERE session=" + Integer.toString(id) + ";";
        db.execSQL(breakQuery);
        db.close();
    }

    public void rebuyAddon(int id, int amount) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "UPDATE sessions SET buy_in=buy_in+" + amount + " WHERE id=" + id + ";";
        db.execSQL(query);
        db.close();
    }

    public void addLocation(String name) {
        SQLiteDatabase db = this.getWritableDatabase();

        String query = "INSERT INTO locations (location) VALUES (" + DatabaseUtils.sqlEscapeString(name) + ");";
        db.execSQL(query);
        db.close();
    }

    public int getProfit() {
        SQLiteDatabase db = this.getWritableDatabase();
        int total = 0;

        String query = "SELECT sum(cash_out) cash_out, sum(buy_in) buy_in FROM sessions WHERE active=0;";
        Cursor c = db.rawQuery(query, null);

        if (c.moveToFirst()) {
            total = c.getInt(c.getColumnIndex("cash_out")) - c.getInt(c.getColumnIndex("buy_in"));
        }

        db.close();
        return total;
    }
}