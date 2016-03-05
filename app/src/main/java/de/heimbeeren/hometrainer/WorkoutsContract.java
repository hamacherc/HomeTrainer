package de.heimbeeren.hometrainer;

import android.provider.BaseColumns;

/**
 * Created by chris on 01.03.2016.
 */
public final class WorkoutsContract {
    public WorkoutsContract() {}

    public static abstract class WorkoutEntry implements BaseColumns {
        public static final String TABLE_NAME = "WorkoutTable";
        public static final String COLOUMN_NAME_ENTRY_ID = "id";
        public static final String COLOUMN_NAME_WORKOUTNAME = "workoutname";
        public static final String COLOUMN_NAME_SLOPE = "slope";
        public static final String COLOUMN_NAME_TIME = "time";
        public static final String COLOUMN_NAME_GEARFRONT = "gearfront";
        public static final String COLOUMN_NAME_GEARBACK = "gearback";
        public static final String COLOUMN_NAME_LOWERCADENCE = "lowercadence";
        public static final String COLOUMN_NAME_UPPERCADENCE = "uppercadence";
        public static final String COLOUMN_NAME_DETAILS = "details";
    }



}
