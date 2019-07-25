package vg.my.citruscode.assignmentmadness.Model;

import android.util.Log;

import java.util.*;

import vg.my.citruscode.assignmentmadness.Database.GameStore;

public class Player extends Watchable implements AdaptorData
{
    private int id;
    private int rowLocation;
    private int colLocation;
    private int cash;
    private double health;
    private Map<Integer, Item> equipment;
    private List<Integer> equipKeyList;
    private double equipmentMass;
    private GameStore db;

    public Player(GameStore db)
    {
        super();
        this.id = 0;
        this.rowLocation = RandomUtil.randInt(0, GameData.MAP_WIDTH - 1);
        this.colLocation = RandomUtil.randInt(0, GameData.MAP_HEIGHT - 1);
        this.cash = 25;
        this.health = 100.0;
        this.equipment = new HashMap<>();
        this.equipKeyList = new LinkedList<>(this.equipment.keySet());
        this.equipmentMass = 0.0;

        this.db = db;
    }

    public Player(int id,
                  int rowLocation,
                  int colLocation,
                  int cash,
                  double health,
                  Map<Integer, Item> equipment,
                  GameStore db)
    {
        super();
        this.id = id;
        this.rowLocation = rowLocation;
        this.colLocation = colLocation;
        this.cash = cash;
        this.health = health;
        this.equipment = equipment;
        this.equipKeyList = new LinkedList<>(this.equipment.keySet());
        this.equipmentMass = calculateEquipmentMass();
        this.db = db;
    }


    /**** Getters ****/
    public int getId()
    {
        return id;
    }

    public int getRowLocation()
    {
        return rowLocation;
    }

    public int getColLocation()
    {
        return colLocation;
    }

    public int getCash()
    {
        return cash;
    }

    public double getHealth()
    {
        return health;
    }

    public double getEquipmentMass()
    {
        return equipmentMass;
    }

    public boolean isDead()
    {
        return health == 0.0;
    }

    public boolean hasWon()
    {
        boolean jadeMonkey = false;
        boolean roadMap = false;
        boolean iceScraper = false;

        for (Map.Entry<Integer, Item> entry : equipment.entrySet())
        {
            String itemName = entry.getValue().getDescription();

            switch (itemName)
            {
                case "Jade Monkey":
                    jadeMonkey = true;
                    break;
                case "Roadmap":
                    roadMap = true;
                    break;
                case "Ice Scraper":
                    iceScraper = true;
                    break;
            }
        }

        Log.d("ASSIGNMENT-DEBUG", this.toString());

        return jadeMonkey && roadMap && iceScraper;
    }

    public int getItemIndex(Item item)
    {
        return equipKeyList.indexOf(item.getId());
    }

    @Override
    public Item getItemByIndex(int idx)
    {
        return equipment.get(equipKeyList.get(idx));
    }

    @Override
    public int getNumItems()
    {
        return equipment.size();
    }


    /**** Setters ****/
    public Area move(int dir, GameData game)
    {
        int newRowLocation = rowLocation;
        int newColLocation = colLocation;
        Area newArea;

        switch(dir)
        {
            case Direction.EAST:
                newRowLocation += 1;
                break;
            case Direction.NORTH:
                newColLocation += 1;
                break;
            case Direction.WEST:
                newRowLocation -= 1;
                break;
            case Direction.SOUTH:
                newColLocation -= 1;
                break;
            default:
                throw new IllegalArgumentException("Invalid direction (" + dir + ").");
        }

        if (game.validateCoords(newRowLocation, newColLocation))
        {
            rowLocation = newRowLocation;
            colLocation = newColLocation;
            newArea = game.getPlayerArea();
        }
        else
        {
            throw new IllegalStateException("Cannot move off map.");
        }

        // No need to update DB here, as its done in updateHealth too
        Log.d("ASSIGNMENT-DEBUG", "moving");
        loseHealth(5.0 + (equipmentMass / 2.0));

        return newArea;
    }

    public void setCash(int newCash)
    {
        if (newCash >= 0)
        {
            this.cash = newCash;
        }
        else
        {
            throw new IllegalArgumentException("Cannot go into debt. (newCash < 0)");
        }

        db.updatePlayer(this);
        alertWatchers();
    }

    private void addHealth(double addHealth)
    {
        if (addHealth >= 0)
        {
            health = Math.min(100.0, health + addHealth);

            db.updatePlayer(this);
            alertWatchers();
        }
    }

    private void loseHealth(double loseHealth)
    {
        Log.d("ASSIGNMENT-DEBUG", Double.toString(loseHealth));
        if (loseHealth > 0.0) // To stop equip weight of < -10 adding health using this method potentially going above 100.0
        {
            Log.d("ASSIGNMENT-DEBUG", Double.toString(health-loseHealth));
            health = Math.max(0.0, health - loseHealth);

            db.updatePlayer(this);
            alertWatchers();
        }

    }

    public void addItem(Item item)
    {
        if (item instanceof Equipment) // It's equipment so add to inventory
        {
            equipmentMass += ((Equipment)item).getMass();
            equipment.put(item.getId(), item);
            equipKeyList.add(item.getId());
            db.playerTakeEquipment(this, (Equipment)item); // Adds item to player inv and removes it from area inv
        }
        else // It's food
        {
            double poisonProbability = GameData.getInstance().getPlayerArea().isTown()?0.03:0.1; // 3% chance if town, 10% if wilderness

            if (Math.random() > poisonProbability) // It's not poison so add food's health
            {
                addHealth(((Food)item).getHealth());
            }
            else // Oh no it's poison
            {
                loseHealth(((Food)item).getHealth());
            }
            db.playerTakeFood(this, (Food)item); // Remove item from area inv
        }



        alertWatchers();
    }

    public void removeItem(Item item, Boolean delete)
    {
        equipmentMass -= ((Equipment)item).getMass();
        equipment.remove(item.getId());
        equipKeyList.remove(getItemIndex(item));

        // Even though this is done by Area when it recieves an item, we might just be deleting the item from player inventory
        if (delete)
        {
            db.deleteItem(item); // Removes association with player and removes item from db
        }
        else
        {
            db.playerRemoveItem(item); // Just removes player association
        }

        db.updatePlayer(this);

        alertWatchers();
    }


    /**** Misc. ****/
    private double calculateEquipmentMass()
    {
        double mass = 0.0;

        for (Map.Entry<Integer, Item> entry : equipment.entrySet())
        {
            mass += ((Equipment)entry.getValue()).getMass();
        }

        return mass;
    }

    /*private void updateHealth()
    {
        // This can be exploited by having < -10kg equipmentMass
        // which will add health when a player moves (potentially
        health = Math.max(0.0, health - 5.0 - (equipmentMass / 2.0));
        db.updatePlayer(this);
    }*/

    public void reset()
    {
        this.rowLocation = RandomUtil.randInt(0, GameData.MAP_WIDTH - 1);
        this.colLocation = RandomUtil.randInt(0, GameData.MAP_HEIGHT - 1);
        this.cash = 25;
        this.health = 100.0;
        this.equipment = new HashMap<>();
        this.equipKeyList = new LinkedList<>(this.equipment.keySet());
        this.equipmentMass = 0.0;
    }

    @Override
    public String toString()
    {
        String items = "Player @ (" + rowLocation + ", " + colLocation + "), cash($" + cash + "), health(" + health + "), equipment{";

        int ii = 0;
        for (Map.Entry<Integer, Item> entry : equipment.entrySet())
        {
            items += ((ii==0)?"":", ") + "(" + entry.getValue().getId() + ", " + entry.getValue().getDescription() + ")";
            ii++;
        }

        return items + "}";
    }
}
