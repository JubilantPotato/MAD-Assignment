package vg.my.citruscode.assignmentmadness.Database;

import android.database.*;
import java.util.*;
import vg.my.citruscode.assignmentmadness.Model.*;

public class GameCursor extends CursorWrapper
{
    public GameCursor(Cursor cursor) { super(cursor); }

    public int getAreaId()
    {
        return getInt(getColumnIndex(GameSchema.AreasTable.Cols.ID));
    }

    public Item getItem()
    {
        Item newItem = null;

        String itemName = getString(getColumnIndex(GameSchema.ItemsTable.Cols.DESC));
        int id = getInt(getColumnIndex(GameSchema.ItemsTable.Cols.ID));

        if (itemName.equals("Ben Kenobi"))
        {
            newItem = new BenKenobi(id);
        }
        else if (itemName.equals("Improbability Drive"))
        {
            newItem = new ImprobabilityDrive(id);
        }
        else if (itemName.equals("Smell-O-Scope"))
        {
            newItem = new PortableSmellOScope(id);
        }
        else if (Equipment.isEquipment(itemName))
        {
            newItem = Equipment.equipmentFactory(id, itemName);
        }
        else if (Food.isFood(itemName))
        {
            newItem = Food.foodFactory(id, itemName);
        }

        return newItem;
    }

    public Area getArea(Map<Integer, Item> items, GameStore db)
    {
        int id = getInt(getColumnIndex(GameSchema.AreasTable.Cols.ID));
        int row = getInt(getColumnIndex(GameSchema.AreasTable.Cols.ROW));
        int col = getInt(getColumnIndex(GameSchema.AreasTable.Cols.COL));
        boolean isTown = getInt(getColumnIndex(GameSchema.AreasTable.Cols.TOWN)) == 1; // Convert 1 --> true, 0 --> false
        String desc = getString(getColumnIndex(GameSchema.AreasTable.Cols.DESC));
        boolean isStarred = getInt(getColumnIndex(GameSchema.AreasTable.Cols.STARRED)) == 1;
        boolean isExplored = getInt(getColumnIndex(GameSchema.AreasTable.Cols.EXPLORED)) == 1;

        return new Area(id, row, col, isTown, desc, isStarred, isExplored, items, db);
    }

    public Player getPlayer(Map<Integer, Item> items, GameStore db)
    {
        int id = getInt(getColumnIndex(GameSchema.PlayerTable.Cols.ID));
        int row = getInt(getColumnIndex(GameSchema.PlayerTable.Cols.ROW));
        int col = getInt(getColumnIndex(GameSchema.PlayerTable.Cols.COL));
        int cash = getInt(getColumnIndex(GameSchema.PlayerTable.Cols.CASH));
        double health = getInt(getColumnIndex(GameSchema.PlayerTable.Cols.HEALTH));

        return new Player(id, row, col, cash, health, items, db);
    }
}
