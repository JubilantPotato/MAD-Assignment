package vg.my.citruscode.assignmentmadness.Model;

import java.util.*;
import vg.my.citruscode.assignmentmadness.View.ExchangeActivity;

public class BenKenobi extends UsableEquipment
{
    public BenKenobi(int id)
    {
        super(id, "Ben Kenobi", 24, 0.0);
    }

    // Take all items from the area's inventory and put in player's inventory
    @Override
    public void useAbility(ExchangeActivity ea)
    {
        Player player = GameData.getInstance().getPlayer();
        Map<Integer, Item> areaItems = ea.getArea().getItems(); // Get a ref to area's items before we delete it
        ea.getArea().removeAllItems(); // Required because modifying in the loop throws ConcurrentModificationException

        // Iterate over all the area's old items and give them to the player
        for (Map.Entry<Integer, Item> entry : areaItems.entrySet())
        {
            Item item = entry.getValue();

            player.addItem(item);
        }
    }
}