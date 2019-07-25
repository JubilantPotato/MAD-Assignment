package vg.my.citruscode.assignmentmadness.View;

import android.support.v4.app.*;
import android.os.Bundle;
import android.view.*;
import android.widget.*;

import vg.my.citruscode.assignmentmadness.R;
import vg.my.citruscode.assignmentmadness.Model.*;

public class AreaInfoFragment extends Fragment
{
    private Area area;
    private TextView areaType;
    private EditText areaDesc;
    private ImageView starred;
    private TextView row;
    private TextView col;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View parentView = inflater.inflate(R.layout.fragment_area_info, container, false);

        /**** Assign Callbacks ****/
        areaType = parentView.findViewById(R.id.areaType);
        areaDesc = parentView.findViewById(R.id.areaDesc);
        starred = parentView.findViewById(R.id.starred);
        row = parentView.findViewById(R.id.row);
        col = parentView.findViewById(R.id.col);


        parentView.findViewById(R.id.changeDesc).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                area.setDescription(areaDesc.getText().toString());
            }
        });

        starred.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                area.toggleStarred();
                updateStarred();
            }
        });

        return parentView;
    }


    /**** Fragment Updaters ****/
    public void updateArea(Area area)
    {
        this.area = area;
        updateAreaType();
        updateDescription();
        updateStarred();
        updateCoords();
    }

    private void updateAreaType()
    {
        String areaTypeStr = "Wilderness";
        if (area.isTown())
        {
            areaTypeStr = "Town";
        }

        areaType.setText(areaTypeStr);
    }

    private void updateDescription()
    {
        areaDesc.setText(area.getDescription());
    }

    private void updateStarred()
    {
        int starredImg = R.drawable.star_outline;
        if (area.isStarred())
        {
            starredImg = R.drawable.star_filled;
        }

        starred.setImageResource(starredImg);
    }

    private void updateCoords()
    {
        row.setText("X: " + area.getRow());
        col.setText("Y: " + area.getCol());
    }
}