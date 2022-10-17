package com.ahm.plantcare

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper


class FertilizerDatabaseHelper(context: Context?) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    var fertilizers = mutableListOf<Fertilizer>()
    var watering = Water(null, null)


    override fun onCreate(db: SQLiteDatabase) {

        // creating table
        db.execSQL(CREATE_TABLE_FERTILIZER)
        db.execSQL(CREATE_TABLE_WATERING)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + DB_TABLE)
        db.execSQL("DROP TABLE IF EXISTS " + DB_WATERING_TABLE)

        // create new table
        onCreate(db)
    }

    companion object {
        // Database Version
        private const val DATABASE_VERSION = 1

        // Database Name
        private const val DATABASE_NAME = "fertilizer_db"

        // Table Names
        private const val DB_TABLE = "table_fertilizer"
        private const val DB_WATERING_TABLE = "table_watring"


        // column names
        private const val KEY_NAME = "plant_name"
        private const val KEY_PLANT_NAME = "plant_name"
        private const val KEY_FERTILIZER_NAME = "fertilizer_name"
        private const val KEY_FERTILIZER_DAYS = "fertilizer_days"
        private const val KEY_FERTILIZER_ID = "fertilizer_id"
        private const val KEY_WATERING_DAYS = "watering_days"
        private const val KEY_WATERING_ID = "watering_id"


        // Table create statement
        private const val CREATE_TABLE_FERTILIZER = "CREATE TABLE " + DB_TABLE + "(" +
                KEY_NAME + " TEXT," +
                KEY_FERTILIZER_NAME + " TEXT," +
                KEY_FERTILIZER_DAYS + " TEXT," +
                KEY_FERTILIZER_ID + " TEXT);"

        private const val CREATE_TABLE_WATERING = "CREATE TABLE " + DB_WATERING_TABLE + "(" +
                KEY_PLANT_NAME + " TEXT," +
                KEY_WATERING_DAYS + " TEXT," +
                KEY_WATERING_ID + " TEXT);"

    }

    @Throws(SQLiteException::class)
    fun addFertilizer(plantName: String?,fertilizerName: String?,
                      fertilizerDays: String? ,fertilizer_id: String?) {
        val database = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_NAME, plantName)
        cv.put(KEY_FERTILIZER_NAME, fertilizerName)
        cv.put(KEY_FERTILIZER_DAYS, fertilizerDays)
        cv.put(KEY_FERTILIZER_ID, fertilizer_id)
        database.insert(DB_TABLE, null, cv)
        database.close()
    }

    @Throws(SQLiteException::class)
    fun addWateringEntry(name: String?, days: String?, watring_id: String?) {
        val database = this.writableDatabase
        val cv = ContentValues()
        cv.put(KEY_NAME, name)
        cv.put(KEY_WATERING_DAYS, days)
        cv.put(KEY_WATERING_ID, watring_id)
        database.insert(DB_WATERING_TABLE, null, cv)
        database.close()
    }

    @JvmName("getFertilizers1")
    fun getFertilizers(name: String?): MutableList<Fertilizer> {
        val db = this.writableDatabase
        val query = "SELECT * FROM $DB_TABLE where $KEY_NAME='$name';"
        val cursor = db.rawQuery(query, null)
        while (cursor.moveToNext()) {
            val plant_name = cursor.getString(0)
            val fertilizer_name = cursor.getString(1)
            val fertilizer_days = cursor.getString(2)
            val fertilizer_id = cursor.getString(3)
            val fertilizer = Fertilizer(plant_name, fertilizer_name,
                fertilizer_days, fertilizer_id)
            fertilizers.add(fertilizer)
        }
        cursor.close()
        db.close()
        return fertilizers
    }

    fun getFertilizerId(name: String?, fertilizer: String?) : String{
        val db = this.writableDatabase
        val query = "SELECT * FROM " + DB_TABLE + " WHERE " + KEY_NAME + "='" + name + "' AND " +
                KEY_FERTILIZER_NAME + "='" + fertilizer + "';"
        val cursor = db.rawQuery(query, null)
        var id = ""
        if (cursor.moveToNext()) {
            id = cursor.getString(3)
        }
        cursor.close()
        db.close()
        return id
    }

    fun removeFertilizer(name: String?, fertilizer: String?){
        val db = this.writableDatabase
        db.execSQL("delete from table_fertilizer where" +
                " plant_name = \"" + name + "\" and fertilizer_name = \"" + fertilizer + "\";");
        db.close()
    }

    @JvmName("getWaterings1")
    fun getWatering(plantName: String?): Water {
        val db = this.writableDatabase
        val query = "SELECT * FROM " + DB_WATERING_TABLE + " where " +
                KEY_PLANT_NAME + "=\"" + plantName + "\";"
        val cursor = db.rawQuery(query, null)
        if (cursor.moveToNext()) {
            val plant_name = cursor.getString(0)
            val days = cursor.getString(1)
            val water = Water(plant_name, days)
            watering = water
        }
        cursor.close()
        db.close()
        return watering
    }

    fun getWateringId(plantName: String?): String? {
        val db = this.writableDatabase
        val query = "SELECT " + KEY_WATERING_ID + " FROM " + DB_WATERING_TABLE +
                " WHERE " + KEY_PLANT_NAME + "='" + plantName + "';"
        val cursor = db.rawQuery(query, null)
        var id = ""
        if (cursor.moveToNext()) {
            id = cursor.getString(0)
        }
        cursor.close()
        db.close()
        return id
    }

    fun removeWatering(plantName: String?) {
        val db = this.writableDatabase
        db.execSQL("delete from table_watring where" +
                " plant_name = \"" + plantName + "\";");
        db.close()
    }
}