package vg.my.citruscode.assignmentmadness.View;

import android.content.Intent;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import vg.my.citruscode.assignmentmadness.Database.GameStore;
import vg.my.citruscode.assignmentmadness.R;
import vg.my.citruscode.assignmentmadness.Model.*;

public class NavigationActivity extends AppCompatActivity
{
    private AreaInfoFragment ai_frag; // Area Info Fragment
    private StatusBarFragment sb_frag; // Status Bar Fragment
    private GameData game;
    private Player player;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        GameData.setDb(new GameStore(this)); // Needed so we have a reference to the DB everywhere in our program
        game = GameData.getInstance();
        player = game.getPlayer();
        Area playerArea = game.getPlayerArea();
        if (!playerArea.isExplored()) // We might be reloading the game
        {
            playerArea.setExplored();
        }

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
        sb_frag = (StatusBarFragment)fragMngr.findFragmentById(R.id.statusBarContainer);
        if (sb_frag == null)
        {
            sb_frag = new StatusBarFragment();
            fragMngr.beginTransaction().add(R.id.statusBarContainer, sb_frag).commit();
        }


        /**** ASSIGN Button Callbacks ****/
        Button eastBtn     = findViewById(R.id.eastBtn);
        Button northBtn    = findViewById(R.id.northBtn);
        Button westBtn     = findViewById(R.id.westBtn);
        Button southBtn    = findViewById(R.id.southBtn);
        Button optionBtn   = findViewById(R.id.optionBtn);
        Button overviewBtn = findViewById(R.id.overviewBtn);

        // Start the Exchange Activity
        optionBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(NavigationActivity.this, ExchangeActivity.class);
                startActivityForResult(intent, Codes.Request.EXCHANGE);
            }
        });

        // Start the Overview Activity
        overviewBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                Intent intent = new Intent(NavigationActivity.this, OverviewActivity.class);
                startActivityForResult(intent, Codes.Request.OVERVIEW);
            }
        });

        // Movement callbacks
        eastBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePlayer(Direction.EAST);
            }
        });
        northBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePlayer(Direction.NORTH);
            }
        });
        westBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePlayer(Direction.WEST);
            }
        });
        southBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                updatePlayer(Direction.SOUTH);
            }
        });
    }

    // Update the player's information and push any developments to the screen
    private void updatePlayer(int dir)
    {
        try
        {
            player.move(dir, game);

            if (player.isDead())
            {
                lose();
            }

            game.getPlayerArea().setExplored();

            updateAreaInfo();
            updateStatusBar();
        }
        catch (IllegalStateException e)
        {
            // Tell user you cant move off map
        }
    }

    // Update Area Data in screen center
    private void updateAreaInfo()
    {
        if (ai_frag != null)
        {
            ai_frag.updateArea(game.getPlayerArea());
        }
    }

    // Update player stats in the status bar
    private void updateStatusBar()
    {
        if (sb_frag != null)
        {
            sb_frag.updateAll();
        }
    }

    // Handle finishing activities
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        switch (requestCode)
        {
            case Codes.Request.OVERVIEW:
                switch (resultCode)
                {
                    case Codes.Result.NORMAL:
                        ai_frag.updateArea(game.getPlayerArea());
                        break;
                    case Codes.Result.RESTART:
                        restart();
                        break;
                }
                break;


            case Codes.Request.EXCHANGE:
                switch (resultCode)
                {
                    case Codes.Result.NORMAL:
                        ai_frag.updateArea(game.getPlayerArea());
                        sb_frag.update();
                        break;
                    case Codes.Result.WIN:
                        win();
                        break;
                    case Codes.Result.LOSE:
                        lose();
                        break;
                    case Codes.Result.RESTART:
                        restart();
                        break;
                }
                break;
        }
    }

    // Restart the whole game
    public void restart()
    {
        game.restartGame();
        game.getPlayerArea().setExplored();
        ai_frag.updateArea(game.getPlayerArea());
        sb_frag.updatePlayer();
        sb_frag.displayMessage("Restarted Game");
    }

    // User has won the game
    public void win()
    {
        restart();
        sb_frag.displayMessage("You Have WON!");
        Log.d("ASSIGNMENT-DEBUG", "THEY WONN!!!!!");
    }

    // User has lost the game
    public void lose()
    {
        restart();
        sb_frag.displayMessage("You Have LOST :(");
        Log.d("ASSIGNMENT-DEBUG", "THEY LOSSTT :((");
    }
}