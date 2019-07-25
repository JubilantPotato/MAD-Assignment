package vg.my.citruscode.assignmentmadness.Model;

import vg.my.citruscode.assignmentmadness.View.ExchangeActivity;

public class PortableSmellOScope extends UsableEquipment
{
    public PortableSmellOScope(int id)
    {
        super(id, "Smell-O-Scope", 15, 5);
    }

    // Launches the Smell-O-Scope Activity
    @Override
    public void useAbility(ExchangeActivity ea)
    {
        ea.startSmellOScope();
    }
}