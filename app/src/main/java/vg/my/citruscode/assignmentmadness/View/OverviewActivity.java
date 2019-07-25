package vg.my.citruscode.assignmentmadness.View;

import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.view.*;
import android.widget.ImageView;

import vg.my.citruscode.assignmentmadness.R;
import vg.my.citruscode.assignmentmadness.Model.*;

public class OverviewActivity extends AppCompatActivity
{
    private class GridCellVH extends RecyclerView.ViewHolder implements Watcher
    {
        private ImageView northWest;
        private ImageView northEast;
        private ImageView southWest;
        private ImageView southEast;
        private ImageView center;
        private Area area;
        private int row;
        private int col;

        private GridCellVH(LayoutInflater li, ViewGroup parent)
        {
            super(li.inflate(R.layout.vh_overview, parent, false));

            int size = parent.getMeasuredHeight() / GameData.MAP_HEIGHT + 1;
            ViewGroup.LayoutParams lp = itemView.getLayoutParams();
            lp.width = size;
            lp.height = size;

            northWest = itemView.findViewById(R.id.northWest);
            northEast = itemView.findViewById(R.id.northEast);
            southWest = itemView.findViewById(R.id.southWest);
            southEast = itemView.findViewById(R.id.southEast);
            center = itemView.findViewById(R.id.center);
        }

        public void bind(Area area, int row, int col) // Called by your adapter
        {
            this.area = area;
            this.row = row;
            this.col = col;


            center.setImageResource(area.getCenter());

            if (area.isExplored())
            {
                northWest.setImageResource(area.getNorthWest(row, col));
                southWest.setImageResource(area.getSouthWest(row, col));
                northEast.setImageResource(area.getNorthEast(row, col));
                southEast.setImageResource(area.getSouthEast(row, col));

                itemView.setOnClickListener(new View.OnClickListener()
                {
                    @Override
                    public void onClick(View v)
                    {
                        GridCellVH.this.area.watch(GridCellVH.this);
                        ai_frag.updateArea(GridCellVH.this.area);
                    }
                });
            }
            else
            {
                itemView.setOnClickListener(null);
            }

        }

        public void update()
        {
            int index = row * GameData.MAP_HEIGHT+ (GameData.MAP_HEIGHT-1)-col;
            adapter.notifyItemChanged(index);
        }
    }

    private class MapRVAdapter extends RecyclerView.Adapter<GridCellVH>
    {
        private GameData game;

        public MapRVAdapter()
        {
            this.game = GameData.getInstance();
        }

        @Override
        public int getItemCount()
        {
            return GameData.MAP_WIDTH * GameData.MAP_HEIGHT;
        }

        @Override
        public GridCellVH onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater li = LayoutInflater.from(OverviewActivity.this);
            return new GridCellVH(li, parent);
        }

        @Override
        public void onBindViewHolder(GridCellVH vh, int index)
        {
            int row = index / GameData.MAP_HEIGHT;
            int col = (GameData.MAP_HEIGHT-1) - index % GameData.MAP_HEIGHT;
            vh.bind(game.getArea(row, col), row, col);
        }

    }

    private AreaInfoFragment ai_frag; // Area Info Fragment
    private GameData game;
    //private Player player;
    private MapRVAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_overview);

        game = GameData.getInstance();

        FragmentManager fragMngr = getSupportFragmentManager();

        /**** CREATE Area Info Fragment ****/
        ai_frag = (AreaInfoFragment)fragMngr.findFragmentById(R.id.areaInfoContainer);
        if (ai_frag == null)
        {
            ai_frag = new AreaInfoFragment();
            fragMngr.beginTransaction().add(R.id.areaInfoContainer, ai_frag).commit();
        }
        updateAreaInfo();

        /**** CREATE Status Bar Fragment ****/
        StatusBarFragment sb_frag = (StatusBarFragment)fragMngr.findFragmentById(R.id.statusBarContainer);
        if (sb_frag == null)
        {
            sb_frag = new StatusBarFragment();
            fragMngr.beginTransaction().add(R.id.statusBarContainer, sb_frag).commit();
        }


        /**** Create Recycler View for Map ****/
        RecyclerView rv = findViewById(R.id.overviewRV);
        rv.setLayoutManager(new GridLayoutManager(OverviewActivity.this, GameData.MAP_HEIGHT, GridLayoutManager.HORIZONTAL,false));
        adapter = new MapRVAdapter();
        rv.setAdapter(adapter);

        /**** ASSIGN Button Event Listeners */
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

    // Update Area Data in screen center
    private void updateAreaInfo()
    {
        if (ai_frag != null)
        {
            ai_frag.updateArea(game.getPlayerArea());
        }
    }
}
