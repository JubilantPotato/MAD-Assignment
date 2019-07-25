package vg.my.citruscode.assignmentmadness.Model;

public class Equipment extends Item
{
    private double mass;
    public static final String[] EQUIPMENT_STRINGS = {"Shovel", "Bow", "Arrow",  "Jade Monkey", "Roadmap", "Ice Scraper"};

    public Equipment(int id, String description, int value, double mass)
    {
        super(id, description, value);
        this.mass = mass;
    }

    public double getMass()
    {
        return mass;
    }


    public static boolean isEquipment(String name)
    {
        return java.util.Arrays.asList(EQUIPMENT_STRINGS).indexOf(name) >= 0;
    }

    // Returns a new Equipment item if a correct name is supplied, null if incorrect
    public static Equipment equipmentFactory(int id, String name)
    {
        Equipment newEquipment = null;

        int[] equipmentCost = {5, 8, 2, 15, 4, 9};
        double[] equipmentWeight = {5, 2, 1, 8, 1, 5};

        int index = java.util.Arrays.asList(EQUIPMENT_STRINGS).indexOf(name);

        if (isEquipment(name))
        {
            newEquipment = new Equipment(id, name, equipmentCost[index], equipmentWeight[index]);
        }

        return newEquipment;
    }
}