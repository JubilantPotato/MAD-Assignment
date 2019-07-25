package vg.my.citruscode.assignmentmadness.Model;

public class RandomUtil
{
    public static int randInt(int min, int max)
    {
        return min + (int)(Math.random() * ((max - min) + 1));
    }
}
