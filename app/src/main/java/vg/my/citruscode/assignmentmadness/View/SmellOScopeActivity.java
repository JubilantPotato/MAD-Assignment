package vg.my.citruscode.assignmentmadness.View;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.TextView;

import java.util.*;

import vg.my.citruscode.assignmentmadness.Model.*;
import vg.my.citruscode.assignmentmadness.R;

public class SmellOScopeActivity extends AppCompatActivity
{
    private class SmellOScopeVH extends RecyclerView.ViewHolder
    {
        private TextView itemDesc;
        private TextView directions;

        public SmellOScopeVH(LayoutInflater li, ViewGroup parent) {
            super(li.inflate(R.layout.vh_smelloscope, parent, false));
            itemDesc = itemView.findViewById(R.id.itemDesc);
            directions = itemView.findViewById(R.id.directions);
        }

        public void bind(String[] data)
        {
            itemDesc.setText(data[0]);
            directions.setText(data[1]);
        }
    }

    private class DirectionsAdaptor extends RecyclerView.Adapter<SmellOScopeVH>
    {
        private List<String[]> data;

        public DirectionsAdaptor(List<String[]> data)
        {
            this.data = data;
        }

        @Override
        public int getItemCount()
        {
            return data.size();
        }

        @Override
        public SmellOScopeVH onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater li = LayoutInflater.from(SmellOScopeActivity.this);
            return new SmellOScopeVH(li, parent);
        }

        @Override
        public void onBindViewHolder(SmellOScopeVH vh, int index)
        {
            vh.bind(data.get(index));
        }
    }

    private GameData game;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_smelloscope);

        game = GameData.getInstance();
        player = game.getPlayer();

        List<String[]> directionData = generateDirectionData();

        /**** Create Status Bar Fragment ****/
        FragmentManager fragMngr = getSupportFragmentManager();
        StatusBarFragment sb_frag = (StatusBarFragment)fragMngr.findFragmentById(R.id.statusBarContainer);

        if (sb_frag == null)
        {
            sb_frag = new StatusBarFragment();
            fragMngr.beginTransaction().add(R.id.statusBarContainer, sb_frag).commit();
        }

        /**** Create Recycler View ****/
        RecyclerView rv = findViewById(R.id.resultsRV);
        rv.setLayoutManager(new LinearLayoutManager(SmellOScopeActivity.this)); // Specify how it should be laid out
        DirectionsAdaptor adapter = new DirectionsAdaptor(directionData);
        rv.setAdapter(adapter);

        /**** Assign Button Event Listeners */
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(Codes.Result.NORMAL, null);
                finish();
            }
        });
    }

    private List<String[]> generateDirectionData()
    {
        List<String[]> dirList = new LinkedList<>();
        int currentRow = player.getRowLocation();
        int currentCol = player.getColLocation();

        for (int ii = -2; ii <= 2; ii++)
        {
            for (int jj = -2; jj <= 2; jj++)
            {
                if (!(ii == 0 && jj == 0)) // Don't include the current area
                {
                    try
                    {

                        Area currentArea = game.getArea(currentRow + ii, currentCol + jj);
                        // ((ii != 0 && jj != 0)?" ":"") == if the horizontal or vertical component is 0, then don't add a space
                        String directions = formatHorizontal(jj) + ((ii != 0 && jj != 0)?" ":"") + formatVertical(ii);

                        // Create a set of entry data for each item in the current area, using the directions we found above
                        for (Map.Entry<Integer, Item> entry : currentArea.getItems().entrySet())
                        {
                            String[] dirData = {entry.getValue().getDescription(), directions};

                            dirList.add(dirData);
                        }
                    }
                    catch (IllegalArgumentException e) {} // Trying to get something off the edge of the map, so disregard
                }
            }
        }

        return dirList;
    }

    // for vector = -2, method will return something like --> South [2]
    private String formatHorizontal(int vector)
    {
        String dir = "";
        if (vector != 0)
        {
            dir = ((vector > 0)?"North":"South") + " [" + Math.abs(vector) + "]";
        }
        return dir;
    }

    private String formatVertical(int vector)
    {
        String dir = "";
        if (vector != 0)
        {
            dir = ((vector > 0)?"East":"West") + " [" + Math.abs(vector) + "]";
        }
        return dir;
    }
}
