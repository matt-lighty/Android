package hanu.a2_1801040104.mycart.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.squareup.picasso.Picasso;

import org.json.JSONException;


import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import hanu.a2_1801040104.mycart.R;
import hanu.a2_1801040104.mycart.database.Database;
import hanu.a2_1801040104.mycart.models.CartItem;
import hanu.a2_1801040104.mycart.models.Product;

public class ProductListAdapter extends RecyclerView.Adapter<ProductListAdapter.ProductListHolder> {
    private List<Product> products;
    private Bitmap bitmap;

    public ProductListAdapter(List<Product> products) {
        this.products = products;
    }

    @Override
    public int getItemCount() {
        return this.products.size();
    }

    @NonNull
    @Override
    public ProductListHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context =  parent.getContext();
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        View productView = layoutInflater.inflate(R.layout.product_view_layout, parent, false);
        return new ProductListHolder(productView);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductListHolder holder, int position) {
        Product product = this.products.get(position);
        holder.bind(product);

        ImageButton btnAddToCard = holder.view.findViewById(R.id.btnAddToCart);
        Product finalProduct = product;
        btnAddToCard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Database database = new Database(v.getContext(), null, null, 3);
                CartItem  cartItem =new CartItem();
                cartItem.setId(finalProduct.getId());
                cartItem.setThumbnail(finalProduct.getThumbnail());
                cartItem.setName(finalProduct.getName());
                cartItem.setQuantity(1);
                cartItem.setUnitPrice(finalProduct.getUnitPrice());
                cartItem.setTotal(cartItem.getUnitPrice()*cartItem.getQuantity());

                if(database.exists(finalProduct.getId())){
                    database.inscreaseQtyBy1(cartItem.getId());
                    Toast.makeText(v.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                } else {
                    database.addProductToCart(cartItem);
                    Toast.makeText(v.getContext(), "Added to cart", Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    public void filterList(ArrayList<Product> filteredProducts) {
        products = filteredProducts;
        notifyDataSetChanged();
    }

    public class ProductListHolder extends RecyclerView.ViewHolder{
        View view;
        TextView tvName, tvPrice;
        ImageView ivThumbnail;
        public ProductListHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.productName);
            tvPrice = itemView.findViewById(R.id.productUnitPrice);
            ivThumbnail = itemView.findViewById(R.id.productThumbnail);
            view = itemView;
        }
        public void bind(Product product) {
            tvPrice.setText(String.valueOf(product.getUnitPrice()));
            tvName.setText(product.getName());

            ImageDownload task = new ImageDownload(ivThumbnail);
            task.execute(product.getThumbnail());
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
