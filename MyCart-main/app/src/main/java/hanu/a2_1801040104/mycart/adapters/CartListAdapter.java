package hanu.a2_1801040104.mycart.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.net.URL;
import java.util.List;

import hanu.a2_1801040104.mycart.R;
import hanu.a2_1801040104.mycart.activities.CartActivity;
import hanu.a2_1801040104.mycart.activities.MainActivity;
import hanu.a2_1801040104.mycart.activities.TotalCash;
import hanu.a2_1801040104.mycart.database.Database;
import hanu.a2_1801040104.mycart.models.CartItem;


public class CartListAdapter extends RecyclerView.Adapter<CartListAdapter.CartListHolder> {
    private List<CartItem> items;
    private Context context;
    private Database database;
    private TotalCash listener;

    public CartListAdapter(List<CartItem> items, TotalCash listener) {
        this.items = items;
        this.listener =listener;
    }

    @Override
    public int getItemCount() {
        return this.items.size();
    }

    @NonNull
    @Override
    public CartListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context =  parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View cartItemView = layoutInflater.inflate(R.layout.cart_item_view_layout, parent, false);

        return new CartListHolder(cartItemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CartListHolder holder, int position) {
        CartItem cartItem = this.items.get(position);
        holder.bind(cartItem);

        ImageButton increase = holder.itemView.findViewById(R.id.cartItemIncrease);
        ImageButton decrease = holder.itemView.findViewById(R.id.cartItemDecrease);
        database = new Database(context, null, null, 3);
        CartItem item = items.get(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(v.getContext(), "Swipe to reomve this product", Toast.LENGTH_LONG).show();
            }
        });

        increase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.inscreaseQtyBy1(cartItem.getId());

                item.setQuantity(item.getQuantity()+1);
                item.setTotal(item.getUnitPrice()*item.getQuantity());
                notifyItemChanged(position);
                listener.calculateTotalPrice();
            }
        });

        decrease.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                database.decreaseQtyBy1(cartItem.getId());
                if(cartItem.getQuantity()>1){
                    item.setQuantity(item.getQuantity()-1);
                    item.setTotal(item.getUnitPrice()*item.getQuantity());
                notifyItemChanged(position);
                } else {
                    items.remove(position);
                    notifyDataSetChanged();
                }
                listener.calculateTotalPrice();
            }
        });
    }

    public class CartListHolder extends RecyclerView.ViewHolder{
        private TextView tvName, tvQuantity, tvTotalPrice;
        private ImageView ivThumbnail;
        private View view;
        public CartListHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.cartItemDescription);
            tvQuantity = itemView.findViewById(R.id.cartItemQuantity);
            ivThumbnail = itemView.findViewById(R.id.cartItemImage);
            tvTotalPrice = itemView.findViewById(R.id.cartItemTotal);
            view =  itemView;
        }

        public void bind(CartItem cartItem)  {
            tvName.setText(cartItem.getName());
            tvQuantity.setText(String.valueOf(cartItem.getQuantity()));
            tvTotalPrice.setText("Total: "+ cartItem.getTotal() +  " VND");

            ImageDownload task = new ImageDownload(ivThumbnail);
            task.execute(cartItem.getThumbnail());
        }
    }




    private class ImageDownload extends AsyncTask<String, Void, String> {
        ImageView ivThumbnail;

        public ImageDownload(ImageView ivThumbnail) {
            this.ivThumbnail = ivThumbnail;
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                URL url = new URL(strings[0]);
                return String.valueOf(url);
            }  catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String string) {
            super.onPostExecute(string);
            Picasso.get().load(String.valueOf(string)).into(ivThumbnail);
        }
    }
}
