/***
 * So that smelloscope, improb drive and ben can all be used
 * agnostic to their instance
 */
package vg.my.citruscode.assignmentmadness.Model;

import vg.my.citruscode.assignmentmadness.View.ExchangeActivity;

public abstract class UsableEquipment extends Equipment
{
    public UsableEquipment(int id, String description, int value, double mass)
    {
        super(id, description, value, mass);
    }

    public abstract void useAbility(ExchangeActivity ea);
}
