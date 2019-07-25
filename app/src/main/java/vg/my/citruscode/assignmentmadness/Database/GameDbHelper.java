package vg.my.citruscode.assignmentmadness.Database;

import android.content.Context;
import android.database.sqlite.*;

public class GameDbHelper extends SQLiteOpenHelper
{
    private static final int VERSION = 6;
    private static final String DATABASE_NAME = "game.db";
    public GameDbHelper(Context context)
    {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL("PRAGMA foreign_keys = ON;"); // Needed for each new connection to DB so we can use FKs

        /* create table areas */
        db.execSQL("create table " + GameSchema.AreasTable.NAME + "(" +
                GameSchema.AreasTable.Cols.ID       + " integer primary key, " +
                GameSchema.AreasTable.Cols.ROW  + " integer, " +
                GameSchema.AreasTable.Cols.COL  + " integer, " +
                GameSchema.AreasTable.Cols.TOWN     + " text, " +
                GameSchema.AreasTable.Cols.DESC     + " text, " +
                GameSchema.AreasTable.Cols.STARRED  + " integer, " +
                GameSchema.AreasTable.Cols.EXPLORED + " integer);");

        /* create table items */
        db.execSQL("create table " + GameSchema.ItemsTable.NAME + "(" +
                GameSchema.ItemsTable.Cols.ID    + " integer primary key, " +
                GameSchema.ItemsTable.Cols.DESC  + " text);");

        /* create table players */
        db.execSQL("create table " + GameSchema.PlayerTable.NAME + "(" +
                GameSchema.PlayerTable.Cols.ID         + " integer primary key, " +
                GameSchema.PlayerTable.Cols.ROW        + " integer, " +
                GameSchema.PlayerTable.Cols.COL        + " integer, " +
                GameSchema.PlayerTable.Cols.CASH       + " integer, " +
                GameSchema.PlayerTable.Cols.HEALTH     + " real, " +
                GameSchema.PlayerTable.Cols.EQUIP_MASS + " real);");

        /* create table area_items */
        String sql = "create table " + GameSchema.AreaItemsTable.NAME + "(" +
                GameSchema.AreaItemsTable.Cols.AREA_ID + " INTEGER REFERENCES " + GameSchema.AreasTable.NAME + " ON DELETE CASCADE, " +
                GameSchema.AreaItemsTable.Cols.ITEM_ID + " INTEGER REFERENCES " + GameSchema.ItemsTable.NAME + " ON DELETE CASCADE);";

        db.execSQL(sql);

        /* create table player_items */
        db.execSQL("create table " + GameSchema.PlayerItemsTable.NAME + "(" +
                GameSchema.PlayerItemsTable.Cols.PLAYER_ID + " INTEGER REFERENCES " + GameSchema.PlayerTable.NAME + "(" + GameSchema.PlayerTable.Cols.ID + ") ON DELETE CASCADE, " +
                GameSchema.PlayerItemsTable.Cols.ITEM_ID + " INTEGER REFERENCES " + GameSchema.ItemsTable.NAME + "(" + GameSchema.ItemsTable.Cols.ID + ") ON DELETE CASCADE);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int v1, int v2)
    {
        db.execSQL("DROP TABLE " + GameSchema.AreasTable.NAME + ";");
        db.execSQL("DROP TABLE " + GameSchema.PlayerTable.NAME + ";");
        db.execSQL("DROP TABLE " + GameSchema.ItemsTable.NAME + ";");
        db.execSQL("DROP TABLE " + GameSchema.AreaItemsTable.NAME + ";");
        db.execSQL("DROP TABLE " + GameSchema.PlayerItemsTable.NAME + ";");
        onCreate(db);
    }
}
