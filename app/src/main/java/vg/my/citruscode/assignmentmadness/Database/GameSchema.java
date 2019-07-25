package vg.my.citruscode.assignmentmadness.Database;

public class GameSchema
{
    public static class AreasTable
    {
        public static final String NAME = "areas";
        public static class Cols
        {
            public static final String ID       = "id"; // PK
            public static final String ROW      = "row_pos";
            public static final String COL      = "column_pos";
            public static final String TOWN     = "town";
            public static final String DESC     = "description";
            public static final String STARRED  = "starred";
            public static final String EXPLORED = "explored";
        }
    }

    public static class AreaItemsTable
    {
        public static final String NAME = "area_items";
        public static class Cols
        {
            public static final String AREA_ID = "area_id"; // FK
            public static final String ITEM_ID = "item_id"; // FK
        }
    }

    public static class ItemsTable
    {
        public static final String NAME = "items";
        public static class Cols
        {
            public static final String ID    = "id"; // PK
            public static final String DESC  = "description";
        }
    }

    public static class PlayerItemsTable
    {
        public static final String NAME = "player_items";
        public static class Cols
        {
            public static final String PLAYER_ID = "player_id"; // FK
            public static final String ITEM_ID   = "item_id"; // FK
        }
    }

    public static class PlayerTable
    {
        public static final String NAME = "player";
        public static class Cols
        {
            public static final String ID         = "id"; // PK
            public static final String ROW        = "row_pos";
            public static final String COL        = "column_pos";
            public static final String CASH       = "cash";
            public static final String HEALTH     = "health";
            public static final String EQUIP_MASS = "equipment_mass";
        }
    }
}
