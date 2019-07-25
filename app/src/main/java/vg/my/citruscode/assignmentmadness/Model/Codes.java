/***
 * This class simply defines all the activity return codes we need.
 */

package vg.my.citruscode.assignmentmadness.Model;

public class Codes
{
    public class Result // For ending an activity
    {
        public static final int NORMAL = 0;
        public static final int WIN = 1;
        public static final int LOSE = 2;
        public static final int RESTART = 3;
    }

    public class Request // For starting an activity (so we know which activity is returning to us)
    {
        public static final int EXCHANGE = 0;
        public static final int OVERVIEW = 1;
        public static final int SMELL_O_SCOPE = 2;
    }
}
