/***
 * Very basic implementation of the observer design pattern.
 * Classes that extend this can have 'Watcher' classes be updated as required.
 */

package vg.my.citruscode.assignmentmadness.Model;

import java.util.*;

public abstract class Watchable
{
    private Map<Integer, Watcher> watchers;
    private int watcherId;

    public Watchable()
    {
        this.watchers = new HashMap<>();
        this.watcherId = 0;
    }

    public int watch(Watcher watcher)
    {
        int id = watcherId;
        watchers.put(id, watcher);
        watcherId++;

        return id;
    }

    public void removeWatcher(int id)
    {
        watchers.remove(id);
    }

    public void alertWatchers()
    {
        for (Map.Entry<Integer, Watcher> entry : watchers.entrySet())
        {
            entry.getValue().update();
        }
    }
}
