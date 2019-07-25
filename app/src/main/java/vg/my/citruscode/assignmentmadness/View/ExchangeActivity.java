package vg.my.citruscode.assignmentmadness.View;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.*;
import android.util.Log;
import android.view.*;
import android.widget.*;


import vg.my.citruscode.assignmentmadness.R;
import vg.my.citruscode.assignmentmadness.Model.*;

public class ExchangeActivity extends AppCompatActivity
{
    // So both of our adaptor can use either view holder
    private abstract class DataVHolderAbstract extends RecyclerView.ViewHolder
    {
        public DataVHolderAbstract(LayoutInflater li, ViewGroup parent, int layout)
        {
            super(li.inflate(layout, parent, false));
        }

        public abstract void bind(Item data);
    }

    // RV Adaptor
    private class BuySellAdaptor extends RecyclerView.Adapter<DataVHolderAbstract>
    {
        // Buy / Take menu vh
        private class BuyDataVH extends DataVHolderAbstract
        {
            private TextView itemDesc; // Reference to UI element(s)
            private Button itemBtn;
            private Item item;

            public BuyDataVH(LayoutInflater li, ViewGroup parent)
            {
                super(li, parent, R.layout.vh_buy_sell);
                itemDesc = itemView.findViewById(R.id.itemDesc); // Grab UI element reference(s)
                itemBtn = itemView.findViewById(R.id.itemBtn);
            }

            @Override
            public void bind(Item data) // Called by your adapter
            {
                if (data != null)
                {
                    itemDesc.setText(data.getDescription());

                    if (area.isTown())
                    {
                        itemBtn.setText("$"+ String.valueOf(data.getValue()));
                    }
                    else
                    {
                        itemBtn.setText("Take");
                    }

                    item = data;

                    itemBtn.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (item.getValue() <= player.getCash() || !area.isTown()) // Can the player afford the item
                            {
                                if (area.isTown())
                                {
                                    player.setCash(player.getCash() - item.getValue()); // Good, they bought it
                                }

                                int position = area.getItemIndex(item);
                                player.addItem(item);
                                area.removeItem(item);
                                adapter.notifyItemRemoved(position);

                                checkWinLose();
                            }

                        }
                    });
                }

            }
        }

        // Sell / Drop menu vh
        private class SellDataVH extends DataVHolderAbstract
        {
            private TextView itemDesc;
            private Button itemBtn;
            private Button useBtn;
            private Item item;

            public SellDataVH(LayoutInflater li, ViewGroup parent)
            {
                super(li, parent, R.layout.vh_buy_sell);
                itemDesc = itemView.findViewById(R.id.itemDesc); // Grab UI element reference(s)
                itemBtn = itemView.findViewById(R.id.itemBtn);
                useBtn = itemView.findViewById(R.id.useBtn);
            }

            @Override
            public void bind(Item data)
            {
                item = data;
                if (data != null)
                {
                    itemDesc.setText(data.getDescription());

                    if (area.isTown())
                    {
                        itemBtn.setText("$" + String.valueOf((int)Math.round(.75*item.getValue())));
                    }
                    else
                    {
                        itemBtn.setText("Drop");
                    }

                    itemBtn.setOnClickListener(new View.OnClickListener()
                    {
                        @Override
                        public void onClick(View v)
                        {
                            if (area.isTown())
                            {
                                player.setCash(player.getCash() + (int)Math.round(.75*item.getValue())); // Item sold
                            }

                            int itemIndex = player.getItemIndex(item);
                            player.removeItem(item, false);
                            area.addItem(item);

                            adapter.notifyItemRemoved(itemIndex);
                        }


                    });

                    if (data instanceof UsableEquipment)
                    {
                        useBtn.setVisibility(View.VISIBLE);
                        useBtn.setOnClickListener(new View.OnClickListener()
                        {
                            @Override
                            public void onClick(View v)
                            {
                                Log.d("ASSIGNMENT-DEBUG", "STARTING EQUIPMENT USE");
                                ((UsableEquipment)item).useAbility(ExchangeActivity.this);

                                int itemIndex = player.getItemIndex(item);
                                player.removeItem(item, true);

                                adapter.notifyItemRemoved(itemIndex);

                                checkWinLose(); // In case of Ben Kenobi
                            }
                        });
                    }
                    else
                    {
                        // Otherwise, as soon as the VH has a 'UsableEquipment' in it
                        // it will then have a 'USE' button regardless of it's contents
                        useBtn.setVisibility(View.GONE);
                    }
                }

            }
        }

        private AdaptorData data;

        public BuySellAdaptor(AdaptorData data)
        {
            this.data = data;
        }

        @Override
        public int getItemCount()
        {
            return data.getNumItems();
        }

        @Override
        public DataVHolderAbstract onCreateViewHolder(ViewGroup parent, int viewType)
        {
            LayoutInflater li = LayoutInflater.from(ExchangeActivity.this);
            DataVHolderAbstract vh;

            if (inBuyMenu)
            {
                vh = new BuyDataVH(li, parent);
            }
            else
            {
                vh = new SellDataVH(li, parent);
            }

            return vh;
        }

        @Override
        public void onBindViewHolder(DataVHolderAbstract vh, int index)
        {
            try
            {
                vh.bind(data.getItemByIndex(index));
            }
            catch (IndexOutOfBoundsException e)
            {
                Log.wtf("ASSIGNMENT-DEBUG", "BuySellAdaptor.onBindViewHolder tried to access invalid index.");
            }
        }
    }

    private Area area;
    private Player player;
    private boolean inBuyMenu;
    private RecyclerView rv;
    private BuySellAdaptor adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_exchange);

        /**** INITIALISE Activity ****/
        GameData game = GameData.getInstance();
        area = game.getPlayerArea();
        player = game.getPlayer();
        inBuyMenu = true;

        updateButtonText();


        // For our buy/sell buttons to be switchable
        final Button buyBtn = findViewById(R.id.buyBtn);
        final Button sellBtn = findViewById(R.id.sellBtn);
        buyBtn.setTypeface(buyBtn.getTypeface(), Typeface.BOLD); // Shows it's the active one


        /**** CREATE Status Bar Fragment ****/
        FragmentManager fragMngr = getSupportFragmentManager();
        StatusBarFragment sb_frag = (StatusBarFragment)fragMngr.findFragmentById(R.id.statusBarContainer);

        if (sb_frag == null)
        {
            sb_frag = new StatusBarFragment();
            fragMngr.beginTransaction().add(R.id.statusBarContainer, sb_frag).commit();
        }

        /**** Create Recycler View ****/
        rv = findViewById(R.id.itemList);
        rv.setLayoutManager(new LinearLayoutManager(ExchangeActivity.this));
        adapter = new BuySellAdaptor(area); // Start in the buy menu (show area items)
        rv.setAdapter(adapter);

        /**** ASSIGN Button Event Listeners */
        findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                setResult(Codes.Result.NORMAL, null);
                finish(); // Maybe send back current area for our AI frag ?
            }
        });

        // Switch to the buy / take menu
        buyBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (!inBuyMenu)
                {
                    buyBtn.setTypeface(buyBtn.getTypeface(), Typeface.BOLD);
                    sellBtn.setTypeface(null, Typeface.NORMAL);
                    adapter = new BuySellAdaptor(area);
                    rv.setAdapter(adapter);

                    inBuyMenu = true;
                }
            }
        });

        // Switch to the sell / drop menu
        sellBtn.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                if (inBuyMenu)
                {
                    sellBtn.setTypeface(sellBtn.getTypeface(), Typeface.BOLD);
                    buyBtn.setTypeface(null, Typeface.NORMAL);
                    adapter = new BuySellAdaptor(player);
                    rv.setAdapter(adapter);
                    inBuyMenu = false;
                }
            }
        });


    }

    // Called when the Improbability Drive is used.
    // This may change the current exchange from being in the wilderness to being a market
    // This means we have to update the current sell/drop menu to reflect this
    public void updateArea(Area area)
    {
        this.area = area;
        updateButtonText();
        adapter = new BuySellAdaptor(player);
        rv.setAdapter(adapter);
    }

    public void updateButtonText()
    {
        if (area.isTown())
        {
            ((TextView)findViewById(R.id.buyBtn)).setText("Buy");
            ((TextView)findViewById(R.id.sellBtn)).setText("Sell");
        }
        else
        {
            ((TextView)findViewById(R.id.buyBtn)).setText("Ground");
            ((TextView)findViewById(R.id.sellBtn)).setText("Inventory");
        }
    }

    public Area getArea()
    {
        return area;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (requestCode == Codes.Request.SMELL_O_SCOPE)
        {
            switch (resultCode)
            {
                case Codes.Result.NORMAL:
                    break;
                case Codes.Result.RESTART:
                    setResult(Codes.Result.RESTART, null);
                    finish();
                    break;
            }
        }
    }

    public void startSmellOScope()
    {
        Intent intent = new Intent(this, SmellOScopeActivity.class);
        this.startActivityForResult(intent, Codes.Request.SMELL_O_SCOPE);
    }

    // Called when a player eats something to see if they have died
    // and when they take equipment to see if it was the winning item
    public void checkWinLose()
    {
        if (player.isDead())
        {
            setResult(Codes.Result.LOSE, null);
            finish();
        }
        else if (player.hasWon())
        {
            setResult(Codes.Result.WIN, null);
            finish();
        }
    }
}
