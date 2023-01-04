package hanu.a2_1801040104.mycart.activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import hanu.a2_1801040104.mycart.R;
import hanu.a2_1801040104.mycart.adapters.CartListAdapter;
import hanu.a2_1801040104.mycart.database.Database;
import hanu.a2_1801040104.mycart.models.CartItem;

public class CartActivity extends AppCompatActivity implements TotalCash {
    RecyclerView cartView;
    Database database;
    TextView tvTotal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cart);

        List<CartItem> cart;
        database = new Database(this, null, null, 3);
        cart = database.getAll();

        cartView = findViewById(R.id.cartItemList);
        cartView.setLayoutManager(new LinearLayoutManager(this));
        CartListAdapter cartListAdapter = new CartListAdapter(cart, this);
        cartView.setAdapter(cartListAdapter);
        tvTotal = findViewById(R.id.totalPrice);

        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT ) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                new AlertDialog.Builder(viewHolder.itemView.getContext())
                        .setMessage("Are you sure")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                int position = viewHolder.getAdapterPosition();
                                database.deleteCartItem(cart.get(position).getId());
                                cart.remove(position);
                                cartListAdapter.notifyDataSetChanged();
                                calculateTotalPrice();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                cartListAdapter.notifyDataSetChanged();
                            }
                        }).show();
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(cartView);

        calculateTotalPrice();
    }

    public void calculateTotalPrice() {
        tvTotal.setText(String.valueOf(database.sumPrice()));
    }
}