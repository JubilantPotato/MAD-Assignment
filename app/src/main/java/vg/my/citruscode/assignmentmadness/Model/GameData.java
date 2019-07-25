package vg.my.citruscode.assignmentmadness.Model;

import android.util.Log;
import java.util.*;
import vg.my.citruscode.assignmentmadness.Database.*;

public class GameData
{
    private Area[][] map;
    private Player player;
    private static GameData instance = null;
    private static GameStore db = null;

    public static final int MAP_WIDTH = 15; // Default: 15
    public static final int MAP_HEIGHT = 9; // Default: 9

    /**** Singleton Methods ****/
    private GameData(Area[][] map, Player player)
    {
        this.map = map;
        this.player = player;
    }

    public static GameData getInstance()
    {
        if(instance == null)
        {
            if (db != null)
            {
                //db.wipeDatabase(); // For debugging purposes
                if (db.hasData())
                {
                    Log.d("ASSIGNMENT-DEBUG", "LOADING OLD GAME");
                    instance = generateFromDb();
                }
                else
                {

                    Log.d("ASSIGNMENT-DEBUG", "STARTED A NEW GAME");
                    Player newPlayer = new Player(GameData.db);

                    instance = new GameData(generateMap(), newPlayer);
                    Log.d("ASSIGNMENT-DEBUG", "Adding new player to db @ idx " + Long.toString(db.addPlayer(newPlayer)));
                    Log.d("ASSIGNMENT-DEBUG", "New player @ (" + newPlayer.getRowLocation() + ", " + newPlayer.getColLocation() + ")");
                }

            }
            else
            {
                throw new IllegalStateException("Database not set.");
            }
        }
        return instance;
    }

    private static Area[][] generateMap()
    {
        Area[][] map = new Area[MAP_WIDTH][MAP_HEIGHT];
        int areaIdCounter = 0;
        int itemIdCounter = 0;

        double townProbability = 0.55;
        double equipmentProbability = 0.17;
        double foodProbability = 0.09;

        for (int ii = 0; ii < MAP_WIDTH; ii++)
        {
            for (int jj = 0; jj < MAP_HEIGHT; jj++)
            {
                Map<Integer, Item> items = new HashMap<>();

                // Add random items
                for (int kk = 0; kk < Equipment.EQUIPMENT_STRINGS.length; kk++)
                {
                    while (Math.random() < equipmentProbability)
                    {
                        items.put(itemIdCounter, Equipment.equipmentFactory(itemIdCounter, Equipment.EQUIPMENT_STRINGS[kk]));
                        itemIdCounter++;
                    }
                }

                while (Math.random() < equipmentProbability)
                {
                    items.put(itemIdCounter, new BenKenobi(itemIdCounter));
                    itemIdCounter++;
                }

                while (Math.random() < equipmentProbability)
                {
                    items.put(itemIdCounter, new PortableSmellOScope(itemIdCounter));
                    itemIdCounter++;
                }

                while (Math.random() < equipmentProbability)
                {
                    items.put(itemIdCounter, new ImprobabilityDrive(itemIdCounter));
                    itemIdCounter++;
                }

                for (int kk = 0; kk < Food.FOOD_STRINGS.length; kk++)
                {
                    while (Math.random() < foodProbability)
                    {
                        items.put(itemIdCounter, Food.foodFactory(itemIdCounter, Food.FOOD_STRINGS[kk]));
                        itemIdCounter++;
                    }
                }

                map[ii][jj] = new Area(areaIdCounter, ii, jj, Math.random() < townProbability, items, db);
                areaIdCounter++;

                db.addArea(map[ii][jj]);
            }
        }

        return map;
    }


    /**** Recreating From Database Methods ****/
    private static GameData generateFromDb()
    {
        GameData game;

        if (db != null)
        {
            /**** STEP 1 - Get a list of Area Id's ****/
            List<Integer> areaIds = getAreaIds();

            /**** STEP 2 - With our Area Id's, map them to an Item Map ****/
            Map<Integer, Map<Integer, Item>> areaItems = new HashMap<>();
            for (Integer id : areaIds)
            {
                areaItems.put(id, getAreaItems(id));
            }

            /**** STEP 3 - Now we have all our area id's and their items, we can create Area objects ****/
            Area[][] areaGrid = getAreaGrid(areaItems);

            /**** STEP 4 - Re-create the player's inventory ****/
            Map<Integer, Item> playerInventory = getPlayerInventory();

            /**** STEP 5 - Create the player with their inventory ****/
            Player player = getPlayerFromDb(playerInventory);

            /**** STEP 6 - FINISH! ****/
            game = new GameData(areaGrid, player);
        }
        else
        {
            throw new IllegalStateException("Database not set.");
        }

        return game;
    }

    private static List<Integer> getAreaIds()
    {
        List<Integer> areaIds = new LinkedList<>();

        GameCursor cursor = new GameCursor(db.getAreaIdCursor());

        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                areaIds.add(cursor.getAreaId());
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        return areaIds;
    }

    private static Map<Integer, Item> getAreaItems(int id)
    {
        Map<Integer, Item> areaItems = new HashMap<>();

        GameCursor cursor = new GameCursor(db.getAreaItemsCursor(id));

        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                Item newItem = cursor.getItem();
                areaItems.put(newItem.getId(), newItem);
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        return areaItems;
    }

    private static Area[][] getAreaGrid(Map<Integer, Map<Integer, Item>> areaItems)
    {
        Area[][] areaGrid = new Area[MAP_WIDTH][MAP_HEIGHT];

        for (Map.Entry<Integer, Map<Integer, Item>> entry : areaItems.entrySet())
        {
            int id = entry.getKey();

            GameCursor cursor = new GameCursor(db.getAreaCursor(id));

            try
            {
                cursor.moveToFirst();
                Area newArea = cursor.getArea(entry.getValue(), db);
                areaGrid[newArea.getRow()][newArea.getCol()] = newArea;
            }
            finally
            {
                cursor.close();
            }
        }

        return areaGrid;
    }

    private static Map<Integer, Item> getPlayerInventory()
    {
        Map<Integer, Item> playerItems = new HashMap<>();

        GameCursor cursor = new GameCursor(db.getPlayerInventoryCursor());

        try
        {
            cursor.moveToFirst();
            while (!cursor.isAfterLast())
            {
                Item newItem = cursor.getItem();
                if (newItem instanceof Food)
                {
                    throw new IllegalStateException("FOUND FOOD IN PLAYER INVENTORY - "+ newItem.getDescription());
                }
                playerItems.put(newItem.getId(), newItem);
                cursor.moveToNext();
            }
        }
        finally
        {
            cursor.close();
        }

        return playerItems;
    }

    private static Player getPlayerFromDb(Map<Integer, Item> playerInventory)
    {
        Player player;

        GameCursor cursor = new GameCursor(db.getPlayerCursor());

        try
        {
            cursor.moveToFirst();
            player = cursor.getPlayer(playerInventory, db);

        }
        finally
        {
            cursor.close();
        }

        return player;
    }


    /**** Getters ****/
    public Area getArea(int row, int col)
    {
        Area outArea;

        if (validateCoords(row, col))
        {
            outArea = map[row][col];
        }
        else
        {
            throw new IllegalArgumentException("Invalid coordinates (" + row + ", " + col + ").");
        }

        return outArea;
    }

    public Player getPlayer()
    {
        return player;
    }

    public Area getPlayerArea()
    {
        return map[player.getRowLocation()][player.getColLocation()];
    }


    /**** Misc. ****/
    public boolean validateCoords(int row, int col)
    {
        return (row >= 0 && row < MAP_WIDTH) && (col >= 0 && col < MAP_HEIGHT);
    }

    // Used by NavigationActivity for restarting game
    public void restartGame()
    {
        db.wipe();
        map = generateMap();
        player.reset();
        db.addPlayer(player);
        Log.d("ASSIGNMENT-DEBUG", "FINISHED RESTARTING GAME");
        Log.d("ASSIGNMENT-DEBUG", "New player @ (" + player.getRowLocation() + ", " + player.getColLocation() + ")");

    }

    // Used by the improbability drive
    public void regenerateMap()
    {
        Log.d("ASSIGNMENT-DEBUG", "db.wipeMap();");
        db.wipeMap();
        map = generateMap();
        getPlayerArea().setExplored();
    }

    public static void setDb(GameStore db)
    {
        GameData.db = db;
    }

}

