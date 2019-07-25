package vg.my.citruscode.assignmentmadness.Model;

public class Food extends Item
{
    private double health;
    public static final String[] FOOD_STRINGS = {"Apple", "Cake", "Cooked Beef", "Avocado"};

    public Food(int id, String description, int value, double health)
    {
        super(id, description, value);
        this.health = health;
    }

    public double getHealth()
    {
        return health;
    }

    public static boolean isFood(String name)
    {
        return java.util.Arrays.asList(FOOD_STRINGS).indexOf(name) >= 0;
    }

    // Returns a new Food item if a correct name is supplied, null if incorrect
    public static Food foodFactory(int id, String name)
    {
        Food newFood = null;

        int[] foodCost = {2, 10, 15, 40};
        double[] foodHealth = {10, 20, 30, 10};

        int index = java.util.Arrays.asList(FOOD_STRINGS).indexOf(name);

        if (index >= 0)
        {
            newFood = new Food(id, name, foodCost[index], foodHealth[index]);
        }

        return newFood;
    }
}

