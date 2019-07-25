package vg.my.citruscode.assignmentmadness.Model;

import java.util.*;
import vg.my.citruscode.assignmentmadness.Database.GameStore;
import vg.my.citruscode.assignmentmadness.R;

public class Area extends Watchable implements AdaptorData
{
    private int id;
    private int row;
    private int col;
    private boolean town;
    private Map<Integer, Item> items;
    private List<Integer> itemKeyList; // Required in recycler view adaptor
    private String description;
    private boolean starred;
    private boolean explored;
    private GameStore db; // Reference to db so we can update when needed


    public Area(int id, int row, int col, boolean town, Map<Integer, Item> items, GameStore db)
    {
        this.id = id;
        this.row = row;
        this.col = col;
        this.town = town;
        this.description = "";
        this.starred = false;
        this.explored = false;
        this.items = items;
        this.itemKeyList = new LinkedList<>(this.items.keySet());
        this.db = db;
    }

    public Area(int id,
                int row,
                int col,
                boolean town,
                String description,
                boolean starred,
                boolean explored,
                Map<Integer, Item> items,
                GameStore db)
    {
        this.id = id;
        this.row = row;
        this.col = col;
        this.town = town;
        this.description = description;
        this.starred = starred;
        this.explored = explored;
        this.items = items;
        this.itemKeyList = new LinkedList<>(this.items.keySet());
        this.db = db;
    }

    /**** Class field Getters ****/
    public boolean isTown()
    {
        return town;
    }

    public Map<Integer, Item> getItems()
    {
        return items;
    }

    public String getDescription()
    {
        return description;
    }

    public boolean isStarred()
    {
        return starred;
    }

    public boolean isExplored()
    {
        return explored;
    }

    public int getRow()
    {
        return row;
    }

    public int getCol()
    {
        return col;
    }

    public int getId()
    {
        return id;
    }


    /**** Class field Setters ****/
    public void setDescription(String description)
    {
        this.description = description;
        db.updateArea(this);
        alertWatchers();
    }

    public void toggleStarred()
    {
        starred = !starred;
        db.updateArea(this);
        alertWatchers();
    }

    public void setExplored()
    {
        explored = true;
        db.updateArea(this);
    }


    /**** Area's Item interface methods ****/
    public void addItem(Item item)
    {
        items.put(item.getId(), item);
        itemKeyList.add(item.getId());
        db.areaTakeItem(this, item);
    }

    public void removeItem(Item item)
    {
        items.remove(item.getId());
        itemKeyList.remove(getItemIndex(item));
        db.areaRemoveItem(item);
    }

    public void removeAllItems()
    {
        db.areaRemoveAllItems(this);
        this.items = new HashMap<>();
        this.itemKeyList =  new LinkedList<>(this.items.keySet());

    }

    public int getItemIndex(Item item)
    {
        return itemKeyList.indexOf(item.getId());
    }


    /**** AdapaterData overrides ****/
    @Override
    public Item getItemByIndex(int idx)
    {
        return items.get(itemKeyList.get(idx));
    }

    @Override
    public int getNumItems()
    {
        return items.size();
    }


    /**** Image Resource Getters ****/
    public int getCenter()
    {
        int res;
        if (GameData.getInstance().getPlayerArea().getId() == id)
        {
            res = R.drawable.issayou;
        }
        else if (isExplored())
        {
            if (isStarred())
            {
                res = R.drawable.star_filled;
            }
            else if (isTown())
            {
                res = R.drawable.ic_building1;
            }
            else
            {
                res = R.drawable.ic_tree1;
            }
        }
        else
        {
            res = R.drawable.unexplored;
        }

        return res;
    }

    public int getNorthWest(int row, int col)
    {
        int res;

        if (row == 0 && col == GameData.MAP_HEIGHT-1)
        {
            res = R.drawable.ic_coast_northwest;
        }
        else if (row == 0)
        {
            res = R.drawable.ic_coast_west;
        }
        else if (col == GameData.MAP_HEIGHT-1)
        {
            res = R.drawable.ic_coast_north;
        }
        else
        {
            res = R.drawable.ic_grass1;
        }

        return res;
    }

    public int getSouthWest(int row, int col)
    {
        int res;

        if (row == 0 && col == 0)
        {
            res = R.drawable.ic_coast_southwest;
        }
        else if (row == 0)
        {
            res = R.drawable.ic_coast_west;
        }
        else if (col == 0)
        {
            res = R.drawable.ic_coast_south;
        }
        else
        {
            res = R.drawable.ic_grass1;
        }

        return res;
    }

    public int getNorthEast(int row, int col)
    {
        int res;

        if (row == GameData.MAP_WIDTH-1 && col == GameData.MAP_HEIGHT-1)
        {
            res = R.drawable.ic_coast_northeast;
        }
        else if (row == GameData.MAP_WIDTH-1)
        {
            res = R.drawable.ic_coast_east;
        }
        else if (col == GameData.MAP_HEIGHT-1)
        {
            res = R.drawable.ic_coast_north;
        }
        else
        {
            res = R.drawable.ic_grass1;
        }

        return res;
    }

    public int getSouthEast(int row, int col)
    {
        int res;

        if (row == GameData.MAP_WIDTH-1 && col == 0)
        {
            res = R.drawable.ic_coast_southeast;
        }
        else if (row == GameData.MAP_WIDTH-1)
        {
            res = R.drawable.ic_coast_east;
        }
        else if (col == 0)
        {
            res = R.drawable.ic_coast_south;
        }
        else
        {
            res = R.drawable.ic_grass1;
        }

        return res;
    }
}
