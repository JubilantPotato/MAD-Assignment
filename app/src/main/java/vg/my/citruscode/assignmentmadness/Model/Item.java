package vg.my.citruscode.assignmentmadness.Model;

public abstract class Item
{
    private int id;
    private String description;
    private int value;

    public Item()
    {
        this.id = -1;
        this.description = "";
        this.value = -1;
    }

    public Item(int id, String description, int value)
    {
        this.id = id;
        this.description = description;
        this.value = value;
    }

    public int getId()
    {
        return id;
    }

    public String getDescription()
    {
        return description;
    }

    public int getValue()
    {
        return value;
    }
}
