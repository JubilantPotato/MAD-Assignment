/***
 * Note: This class is required, so I don't have to re-write the my recycler view adaptor
 * for Player and Area in the Exchange activity. This way I can pass either in and the adaptor
 * doesn't care- just so long as it has access to these methods.
 */

package vg.my.citruscode.assignmentmadness.Model;

public interface AdaptorData
{
    int getNumItems();
    Item getItemByIndex(int idx);
}
