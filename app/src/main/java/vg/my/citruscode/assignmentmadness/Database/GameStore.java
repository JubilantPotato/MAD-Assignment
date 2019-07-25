package vg.my.citruscode.assignmentmadness.Database;

import android.content.*;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import java.util.Map;
import vg.my.citruscode.assignmentmadness.Model.*;

public class GameStore
{
    private SQLiteDatabase db;
    public GameStore(Context context)
    {
        this.db = new GameDbHelper(context.getApplicationContext()).getWritableDatabase();
        db.execSQL("PRAGMA foreign_keys=ON");

    }

    /**** Area Mutators ****/
    public void addArea(Area area)
    {
        //Put area into DB
        ContentValues cv = generateContentValues(area);
        db.insert(GameSchema.AreasTable.NAME, null, cv);

        //foreach over all items, adding the item then registering connection
        for (Map.Entry<Integer, Item> entry : area.getItems().entrySet())
        {
            // Add Item
            Item item = entry.getValue();
            addItem(item);

            // Register connection
            cv = new ContentValues();
            cv.put(GameSchema.AreaItemsTable.Cols.AREA_ID, area.getId());
            cv.put(GameSchema.AreaItemsTable.Cols.ITEM_ID, item.getId());
            db.insert(GameSchema.AreaItemsTable.NAME, null, cv);

        }
    }

    public void updateArea(Area area)
    {
        Log.d("ASSIGNMENT-DEBUG", "GameStore.updateArea()");
        ContentValues cv = generateContentValues(area);
        String[] whereValue = { String.valueOf(area.getId()) };
        db.update(GameSchema.AreasTable.NAME, cv, GameSchema.AreasTable.Cols.ID + " = ?", whereValue);
    }

    public void areaTakeItem(Area area, Item item)
    {
        // remove item-player assoc
        playerRemoveItem(item);

        // create area-item assoc
        ContentValues cv = new ContentValues();
        cv.put(GameSchema.AreaItemsTable.Cols.AREA_ID, area.getId());
        cv.put(GameSchema.AreaItemsTable.Cols.ITEM_ID, item.getId());
        db.insert(GameSchema.AreaItemsTable.NAME, null, cv);

    }

    public void areaRemoveItem(Item item)
    {
        String[] whereValue = { String.valueOf(item.getId()) };
        int num = db.delete(GameSchema.AreaItemsTable.NAME,GameSchema.AreaItemsTable.Cols.ITEM_ID + " = ?", whereValue);
        Log.d("ASSIGNMENT-DEBUG", "Removed Area-Item Assoc: " + num);
    }

    // Remove all associations between an area and it's items
    public void areaRemoveAllItems(Area area)
    {
        Log.d("ASSIGNMENT-DEBUG", "Starting deletion of all Area items");
        int num = 0;
        for (Map.Entry<Integer, Item> entry : area.getItems().entrySet())
        {
            Item item = entry.getValue();
            String[] whereValue = { String.valueOf(item.getId()) };
            num += db.delete(GameSchema.AreaItemsTable.NAME,GameSchema.AreaItemsTable.Cols.ITEM_ID + " = ?", whereValue);

        }
        Log.d("ASSIGNMENT-DEBUG", "Removed Assocs: " + num);
    }


    /**** Item Mutators ****/
    public void addItem(Item item)
    {
        ContentValues cv = new ContentValues();
        cv.put(GameSchema.ItemsTable.Cols.ID, item.getId());
        cv.put(GameSchema.ItemsTable.Cols.DESC, item.getDescription());
        db.insert(GameSchema.ItemsTable.NAME, null, cv);
    }

    public void deleteItem(Item item)
    {
        String[] whereValue = { String.valueOf(item.getId()) };
        db.delete(GameSchema.ItemsTable.NAME,GameSchema.ItemsTable.Cols.ID + " = ?", whereValue);
    }


    /**** Player Mutators ****/
    public long addPlayer(Player player)
    {
        return db.insert(GameSchema.PlayerTable.NAME, null, generateContentValues(player));
    }

    public void updatePlayer(Player player)
    {
        ContentValues cv = generateContentValues(player);

        String[] whereValue = { String.valueOf(player.getId()) };
        db.update(GameSchema.PlayerTable.NAME, cv, GameSchema.PlayerTable.Cols.ID + " = ?", whereValue);
    }

    // NEEDS FIXING
    public void playerTakeEquipment(Player player, Equipment item)
    {
        Log.d("ASSIGNMENT-DEBUG", "GameStore.playerTakeItem()");

        // create player-item assoc
        ContentValues cv = new ContentValues();
        cv.put(GameSchema.PlayerItemsTable.Cols.PLAYER_ID, player.getId());
        cv.put(GameSchema.PlayerItemsTable.Cols.ITEM_ID, item.getId());
        db.insert(GameSchema.PlayerItemsTable.NAME, null, cv);

        // update player bc new weight
        updatePlayer(player);
    }

    public void playerTakeFood(Player player, Food item)
    {
        deleteItem(item); // Will also remove area-item assoc

        // update player bc new health
        updatePlayer(player);
    }

    // Delete player-item association
    public void playerRemoveItem(Item item)
    {
        String[] whereValue = { String.valueOf(item.getId()) };
        db.delete(GameSchema.PlayerItemsTable.NAME,GameSchema.PlayerItemsTable.Cols.ITEM_ID + " = ?", whereValue);
    }


    /**** ContentValue Creators ****/
    private ContentValues generateContentValues(Area area)
    {
        ContentValues cv = new ContentValues();
        cv.put(GameSchema.AreasTable.Cols.ID, area.getId());
        cv.put(GameSchema.AreasTable.Cols.ROW, area.getRow());
        cv.put(GameSchema.AreasTable.Cols.COL, area.getCol());
        cv.put(GameSchema.AreasTable.Cols.TOWN, area.isTown()?1:0);
        cv.put(GameSchema.AreasTable.Cols.DESC, area.getDescription()); // HOW DO I PASS IN INTEGERS/BOOLEAN TO INT
        cv.put(GameSchema.AreasTable.Cols.STARRED, area.isStarred()?1:0);
        cv.put(GameSchema.AreasTable.Cols.EXPLORED, area.isExplored()?1:0);

        return cv;
    }

    private ContentValues generateContentValues(Player player)
    {
        ContentValues cv = new ContentValues();
        cv.put(GameSchema.PlayerTable.Cols.ID, player.getId());
        cv.put(GameSchema.PlayerTable.Cols.ROW, player.getRowLocation());
        cv.put(GameSchema.PlayerTable.Cols.COL, player.getColLocation());
        cv.put(GameSchema.PlayerTable.Cols.CASH, player.getCash());
        cv.put(GameSchema.PlayerTable.Cols.HEALTH, player.getHealth());
        cv.put(GameSchema.PlayerTable.Cols.EQUIP_MASS, player.getEquipmentMass());

        return cv;
    }


    /**** Database Methods ****/
    // True if database has been written to, False if not
    public boolean hasData()
    {
        return getPlayerCursor().getCount() == 1;
    }

    // Removes all items associated with an area, and all areas
    public void wipeMap()
    {
        // Remove all items associated with areas, this will delete the AreaItem content
        int numDeleted = db.delete(GameSchema.ItemsTable.NAME,
                "EXISTS (SELECT * FROM " + GameSchema.AreaItemsTable.NAME + " WHERE " + GameSchema.ItemsTable.NAME + "." + GameSchema.ItemsTable.Cols.ID + " = " + GameSchema.AreaItemsTable.NAME + "." + GameSchema.AreaItemsTable.Cols.ITEM_ID + ")",
                null);

        Log.d("ASSIGNMENT-DEBUG", "Number of items deleted: " + numDeleted);

        // All area items gone, now just delete all areas
        db.delete(GameSchema.AreasTable.NAME, null, null);
    }

    // Removes all data from the database but retain tables.
    public void wipe()
    {
        db.delete(GameSchema.AreasTable.NAME, null, null);
        db.delete(GameSchema.PlayerTable.NAME, null, null);
        db.delete(GameSchema.ItemsTable.NAME, null, null);
    }


    /**** CURSOR METHODS ****/
    public Cursor getAreaIdCursor()
    {
        return db.query(GameSchema.AreasTable.NAME, null, null, null, null, null, null);
    }

    public Cursor getAreaItemsCursor(int id)
    {
        String query = "SELECT * FROM " + GameSchema.ItemsTable.NAME + " a INNER JOIN " + GameSchema.AreaItemsTable.NAME + " b ON a." + GameSchema.ItemsTable.Cols.ID + "=b." + GameSchema.AreaItemsTable.Cols.ITEM_ID + " WHERE b." + GameSchema.AreaItemsTable.Cols.AREA_ID + "=?";
        return db.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public Cursor getAreaCursor(int id)
    {
        String query = "SELECT * FROM " + GameSchema.AreasTable.NAME + " WHERE " + GameSchema.AreasTable.Cols.ID + "=?";
        return db.rawQuery(query, new String[]{String.valueOf(id)});
    }

    public Cursor getPlayerInventoryCursor()
    {
        String query = "SELECT * FROM " + GameSchema.ItemsTable.NAME + " a INNER JOIN " + GameSchema.PlayerItemsTable.NAME + " b ON a." + GameSchema.ItemsTable.Cols.ID + "=b." + GameSchema.PlayerItemsTable.Cols.ITEM_ID;
        return db.rawQuery(query, null);
    }

    public Cursor getPlayerCursor()
    {
        String query = "SELECT * FROM " + GameSchema.PlayerTable.NAME + " WHERE " + GameSchema.PlayerTable.Cols.ID + "=0";
        return db.rawQuery(query, null);
    }
}
