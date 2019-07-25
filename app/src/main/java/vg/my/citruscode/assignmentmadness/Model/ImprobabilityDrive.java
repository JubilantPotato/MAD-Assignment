package vg.my.citruscode.assignmentmadness.Model;

import android.util.Log;
import vg.my.citruscode.assignmentmadness.View.ExchangeActivity;

public class ImprobabilityDrive extends UsableEquipment
{
    public ImprobabilityDrive(int id)
    {
        super(id, "Improbability Drive", 30, -Math.PI);
    }

    // Completely regenerates map and area items, player is unchanged
    @Override
    public void useAbility(ExchangeActivity ea)
    {
        GameData game = GameData.getInstance();
        game.regenerateMap();
        ea.updateArea(game.getPlayerArea());

    }
}
