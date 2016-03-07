package de.heimbeeren.hometrainer;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public final class WorkoutsDBHelper extends SQLiteOpenHelper {

    public String workoutPlanName;
    public int workoutPlanType;
    public int workoutPlanSteps;
    public int[] slope;
    public int[] stepTime;
    public int[] gearFront;
    public int[] gearBack;
    public String[] lowerCadence;
    public String[] upperCadence;
    public String[] stepDetails;

    private static final String TAG = WorkoutsDBHelper.class.getSimpleName();

    // Name und Version der Datenbank
    private static final String DATABASE_NAME = "workoutsdb.db";
    private static final int DATABASE_VERSION = 1;

    //Name und Attribute der Trainingsplan-Tabelle
    public static final String WORKOUT_PLAN_TABLE_NAME = "workoutplans";
    public static final String WORKOUT_PLAN_ENTRY_ID = "_id";
    public static final String WORKOUT_PLAN_WORKOUTPLANNAME = "workoutplanname";
    public static final String WORKOUT_PLAN_WORKOUTTYPE = "workouttype";
    public static final String WORKOUT_PLAN_SLOPE = "slope";
    public static final String WORKOUT_PLAN_TIME = "time";
    public static final String WORKOUT_PLAN_GEARFRONT = "gearfront";
    public static final String WORKOUT_PLAN_GEARBACK = "gearback";
    public static final String WORKOUT_PLAN_LOWERCADENCE = "lowercadence";
    public static final String WORKOUT_PLAN_UPPERCADENCE = "uppercadence";
    public static final String WORKOUT_PLAN_DETAILS = "details";

    WorkoutsDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
        private static final String WORKOUT_PLAN_TABLE_CREATE = "CREATE TABLE " +
				WORKOUT_PLAN_TABLE_NAME + " (" +
				WORKOUT_PLAN_ENTRY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
				WORKOUT_PLAN_WORKOUTPLANNAME + " VARCHAR(20), " +
                WORKOUT_PLAN_WORKOUTTYPE + " INTEGER, " +
                WORKOUT_PLAN_SLOPE + " INTEGER, " +
				WORKOUT_PLAN_TIME + " VARCHAR(10), " +
				WORKOUT_PLAN_GEARFRONT + " INTEGER, " +
				WORKOUT_PLAN_GEARBACK + " INTEGER, " +
				WORKOUT_PLAN_LOWERCADENCE + " VARCHAR(3), " +
				WORKOUT_PLAN_UPPERCADENCE + " VARCHAR(3), " +
				WORKOUT_PLAN_DETAILS + " VARCHAR(128)" + ");";

		private static final String WORKOUT_PLAN_TABLE_DROP = "DROP TABLE IF EXISTS " +
				WORKOUT_PLAN_TABLE_NAME + ";";


		@Override
		public void onCreate(SQLiteDatabase db) {
            db.execSQL(WORKOUT_PLAN_TABLE_CREATE);
		}

		@Override
		public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            Log.w(TAG, "Upgrade der Datenbank von Version " + oldVersion + " auf Version " +
            newVersion + "; Alle Daten werden gelöscht.");
            db.execSQL(WORKOUT_PLAN_TABLE_DROP);
            onCreate(db);
		}

		public void deleteWorkoutPlanList() {
            SQLiteDatabase db = getWritableDatabase();
            Log.d(TAG, "Pfad: " + db.getPath());
            Log.d(TAG, "Trainingsplan-Tabelle gelöscht.");
            db.execSQL(WORKOUT_PLAN_TABLE_DROP);
            onCreate(db);
		}

        public List<String> getPlanList() {

            List<String> plans = new ArrayList<String>();

            SQLiteDatabase db = getReadableDatabase();
            String selectQuery = "SELECT DISTINCT " + WORKOUT_PLAN_WORKOUTPLANNAME + " FROM " +
                    WORKOUT_PLAN_TABLE_NAME +";";
            Cursor cursor = db.rawQuery(selectQuery, null);

            // Alle Reihen durchgehen und an die Liste binden.
            if (cursor.moveToFirst()) {
                do {
                    plans.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }

            // Verbindung schließen
            cursor.close();
            db.close();

            return plans;
        }

        public int getWorkoutType(String workoutPlan) {

            int workoutType = 0;

            SQLiteDatabase db = getReadableDatabase();
            String selectQuery = "SELECT DISTINCT " + WORKOUT_PLAN_WORKOUTTYPE + " FROM " +
                    WORKOUT_PLAN_TABLE_NAME + " WHERE " +
                    WORKOUT_PLAN_WORKOUTPLANNAME +"='" + workoutPlan + "';";

            Cursor cursor = db.rawQuery(selectQuery, null);

            if (cursor.moveToFirst()) {
                workoutType = cursor.getInt(0);
            }

            cursor.close();
            db.close();

            return workoutType;
        }

		public void insertWorkoutStep(String workoutPlanName, int workoutPlanType, int slope, String stepTime, int gearFront,
                   int gearBack, String lowerCadence, String upperCadence, String stepDetails) {

            long rowId = -1;
            try {
                    // Datenbank öffnen
                    SQLiteDatabase db = getWritableDatabase();
                    Log.d(TAG, "Pfad: " + db.getPath());
                    // die zu speichernden Werte
                    ContentValues values = new ContentValues();
                    values.put(WORKOUT_PLAN_WORKOUTPLANNAME, workoutPlanName);
                    values.put(WORKOUT_PLAN_WORKOUTTYPE, workoutPlanType);
                    values.put(WORKOUT_PLAN_SLOPE, slope);
                    values.put(WORKOUT_PLAN_TIME, stepTime);
                    values.put(WORKOUT_PLAN_GEARFRONT, gearFront);
                    values.put(WORKOUT_PLAN_GEARBACK, gearBack);
                    values.put(WORKOUT_PLAN_LOWERCADENCE, lowerCadence);
                    values.put(WORKOUT_PLAN_UPPERCADENCE, upperCadence);
                    values.put(WORKOUT_PLAN_DETAILS, stepDetails);
                    // in die Tabelle mood einfügen
                    rowId = db.insert(WORKOUT_PLAN_TABLE_NAME, null, values);
            } catch (SQLiteException e) {
                    Log.e(TAG, "insertWorkoutStepts()", e);
            } finally {
                    Log.d(TAG, "insertWorkoutStepts(): rowId=" + rowId);
            }
		}


    public int getWorkoutTotalTime(String workoutPlan) {

        int totalTime = 0;

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT SUM(" + WORKOUT_PLAN_TIME + ") FROM " +
                WORKOUT_PLAN_TABLE_NAME + " WHERE " +
                WORKOUT_PLAN_WORKOUTPLANNAME +"='" + workoutPlan + "';";

        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            totalTime = cursor.getInt(0);
        }

        cursor.close();
        db.close();

        return totalTime;

    }

    public void loadCurrentWorkoutPlan(String workoutPlan) {

        int stepCount = 0;

        SQLiteDatabase db = getReadableDatabase();
        String selectQuery = "SELECT " +
                WORKOUT_PLAN_SLOPE + "," +
                WORKOUT_PLAN_TIME + "," +
                WORKOUT_PLAN_GEARFRONT + "," +
                WORKOUT_PLAN_GEARBACK + "," +
                WORKOUT_PLAN_LOWERCADENCE + "," +
                WORKOUT_PLAN_UPPERCADENCE + "," +
                WORKOUT_PLAN_DETAILS + " FROM " +
                WORKOUT_PLAN_TABLE_NAME + " WHERE " +
                WORKOUT_PLAN_WORKOUTPLANNAME +"='" + workoutPlan + "';";

        Cursor cursor = db.rawQuery(selectQuery, null);

        workoutPlanSteps = cursor.getCount();
        workoutPlanName = workoutPlan;
        workoutPlanType = this.getWorkoutType(workoutPlan);
        slope = new int[workoutPlanSteps];
        stepTime = new int[workoutPlanSteps];
        gearFront = new int[workoutPlanSteps];
        gearBack = new int[workoutPlanSteps];
        lowerCadence = new String[workoutPlanSteps];
        upperCadence = new String[workoutPlanSteps];
        stepDetails = new String[workoutPlanSteps];

        if (cursor.moveToFirst()) {
            do {
                slope[stepCount] = cursor.getInt(0);
                stepTime[stepCount] = cursor.getInt(1);
                gearFront[stepCount] = cursor.getInt(2);
                gearBack[stepCount] = cursor.getInt(3);
                lowerCadence[stepCount] = cursor.getString(4);
                upperCadence[stepCount] = cursor.getString(5);
                stepDetails[stepCount] = cursor.getString(6);
                stepCount++;
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
    }

}