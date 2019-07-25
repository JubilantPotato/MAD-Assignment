package vg.my.citruscode.assignmentmadness.View;

import android.support.v4.app.*;
import android.os.Bundle;
import android.view.*;
import android.view.animation.*;
import android.widget.*;

import vg.my.citruscode.assignmentmadness.R;
import vg.my.citruscode.assignmentmadness.Model.*;

public class StatusBarFragment extends Fragment implements Watcher
{
    private View parentView;
    private GameData game;
    private Player player;
    int watcherId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        parentView = inflater.inflate(R.layout.fragment_status_bar, container, false);

        parentView.findViewById(R.id.restartBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                FragmentActivity activity = getActivity();

                if (activity instanceof NavigationActivity)
                {
                    ((NavigationActivity)activity).restart();
                }
                else
                {
                    activity.setResult(Codes.Result.RESTART, null);
                    activity.finish();
                }
            }
        });

        return parentView;
    }

    // Initialisation needs to be done here because when onCreate() is called, db isn't assigned to game yet
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        game = GameData.getInstance();
        player = game.getPlayer();
        watcherId = player.watch(this);

        updateAll();
    }


    /**** Updaters ****/
    public void updateAll()
    {
        updateCash();
        updateHealth();
        updateEquipmentMass();
    }

    public void updateCash()
    {
        TextView moneyDisp = parentView.findViewById(R.id.moneyDisp);
        moneyDisp.setText(Integer.toString(player.getCash()));
    }

    public void updateHealth()
    {
        TextView healthDisp = parentView.findViewById(R.id.healthDisp);
        healthDisp.setText(String.format("%.1f", player.getHealth()));
    }

    public void updateEquipmentMass()
    {
        TextView inventoryDisp = parentView.findViewById(R.id.inventoryDisp);
        inventoryDisp.setText(String.format("%.1f", player.getEquipmentMass()));
    }

    // Overriding the Watcher interface
    @Override
    public void update()
    {
        updateAll();
    }

    public void updatePlayer()
    {
        player.removeWatcher(watcherId);
        player = game.getPlayer();
        updateAll();
    }


    /**** Misc. ****/
    // Display message to user in the status bar
    public void displayMessage(String message)
    {
        final Animation in = new AlphaAnimation(0.0f, 1.0f);
        in.setDuration(1000); // Fade in time

        final Animation out = new AlphaAnimation(1.0f, 0.0f);
        out.setDuration(2000); // Fade out time
        out.setStartOffset(1500); // How long message stays on the screen

        final TextView messageText = parentView.findViewById(R.id.messageText);
        final ImageView messageBackground = parentView.findViewById(R.id.messageBackground);

        messageText.setText(message);
        messageText.startAnimation(in);
        messageBackground.startAnimation(in);

        in.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationStart(Animation animation)
            {
                messageText.setVisibility(View.VISIBLE);
                messageBackground.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation)
            {
                messageText.startAnimation(out);
                messageBackground.startAnimation(out);

            }
        });

        out.setAnimationListener(new Animation.AnimationListener()
        {
            @Override
            public void onAnimationRepeat(Animation animation){}

            @Override
            public void onAnimationStart(Animation animation){}

            @Override
            public void onAnimationEnd(Animation animation)
            {
                messageText.setVisibility(View.GONE);
                messageBackground.setVisibility(View.GONE);
            }
        });
    }
}