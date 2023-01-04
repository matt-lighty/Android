package hanu.a2_1801040104.mycart.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import hanu.a2_1801040104.mycart.models.CartItem;
import hanu.a2_1801040104.mycart.models.Product;

public class Database extends SQLiteOpenHelper {
    private static final int DATABASE_VERSION = 3;
    private static final String DATABASE_NAME = "cartDB";
    private static final String TABLE_NAME = "cart";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_THUMBNAIL = "thumbnail";
    private static final String COLUMN_NAME= "name";
    private static final String COLUMN_QUANTITY = "quantity";
    private static final String COLUMN_UNITPRICE = "unitPrice";
    private static final String COLUMN_TOTAL = "total";

    public Database(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, DATABASE_NAME, factory, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String query = "CREATE TABLE " + TABLE_NAME + " ( " + COLUMN_ID + " INTEGER PRIMARY KEY ," + COLUMN_THUMBNAIL + " TEXT ," + COLUMN_NAME + " TEXT ," + COLUMN_QUANTITY + " TEXT ," + COLUMN_UNITPRICE + " TEXT," + COLUMN_TOTAL + " TEXT" + " ) ";
        db.execSQL(query);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public List<CartItem> getAll(){
        List<CartItem> cartItems = new ArrayList<CartItem>();
        String query = "SELECT * FROM " + TABLE_NAME;
        SQLiteDatabase db = getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.moveToFirst()){
            do{
                CartItem cartItem = new CartItem();

                cartItem.setId(Integer.parseInt(cursor.getString(0)));
                cartItem.setThumbnail(cursor.getString(1));
                cartItem.setName((cursor.getString(2)));
                cartItem.setQuantity(Integer.parseInt(cursor.getString(3)));
                cartItem.setUnitPrice(Integer.parseInt(cursor.getString(4)));
                cartItem.setTotal(Integer.parseInt(cursor.getString(5)));

                cartItems.add(cartItem);
            } while(cursor.moveToNext());
        }
        return cartItems;
    }

    public void addProductToCart(CartItem cartItem){
        ContentValues values = new ContentValues();
        int total = cartItem.getUnitPrice()*(cartItem.getQuantity());

        values.put(COLUMN_ID, cartItem.getId());
        values.put(COLUMN_NAME, cartItem.getName());
        values.put(COLUMN_THUMBNAIL, cartItem.getThumbnail());
        values.put(COLUMN_QUANTITY, 1);
        values.put(COLUMN_UNITPRICE, cartItem.getUnitPrice());
        values.put(COLUMN_TOTAL, total);

        SQLiteDatabase db = getWritableDatabase();
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public int inscreaseQtyBy1(int id ) {
        SQLiteDatabase db = getWritableDatabase();
        CartItem cartItem = getCartItem(id);
        int total = cartItem.getUnitPrice()*(cartItem.getQuantity()+1);

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, cartItem.getId());
        values.put(COLUMN_NAME, cartItem.getName());
        values.put(COLUMN_THUMBNAIL, cartItem.getThumbnail());
        values.put(COLUMN_QUANTITY, cartItem.getQuantity()+1);
        values.put(COLUMN_UNITPRICE, cartItem.getUnitPrice());
        values.put(COLUMN_TOTAL, total);

        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(cartItem.getId())});
    }

    public int decreaseQtyBy1(int id)  {
        SQLiteDatabase db = getWritableDatabase();
        CartItem cartItem = getCartItem(id);
        int total = cartItem.getUnitPrice()*(cartItem.getQuantity()-1);
        if (cartItem.getQuantity()==1){
            deleteCartItem(id);
            return 0;
        }

        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, cartItem.getId());
        values.put(COLUMN_NAME, cartItem.getName());
        values.put(COLUMN_THUMBNAIL, cartItem.getThumbnail());
        values.put(COLUMN_QUANTITY, cartItem.getQuantity()-1);
        values.put(COLUMN_UNITPRICE, cartItem.getUnitPrice());
        values.put(COLUMN_TOTAL, total);


        return db.update(TABLE_NAME, values, COLUMN_ID + "=?", new String[]{String.valueOf(cartItem.getId())});
    }

    public CartItem getCartItem(int id)  {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_NAME, new String[]{COLUMN_ID, COLUMN_THUMBNAIL, COLUMN_NAME, COLUMN_QUANTITY, COLUMN_UNITPRICE, COLUMN_TOTAL}, COLUMN_ID + "=?", new String[]{String.valueOf(id)},null, null, null, null);
        if(cursor != null)
            cursor.moveToFirst();
            CartItem cartItem = new CartItem(Integer.parseInt(cursor.getString(0)),cursor.getString(1),(cursor.getString(2)),Integer.parseInt(cursor.getString(3)), Integer.parseInt(cursor.getString(4)), Integer.parseInt(cursor.getString(5)));
           return cartItem;
    }

    public void deleteCartItem(int id){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + "=?", new String[]{String.valueOf(id)});
        db.close();
    }

    public boolean exists(int id){
        SQLiteDatabase db = getWritableDatabase();
        String selectString = "SELECT * FROM " + TABLE_NAME + " WHERE " + COLUMN_ID + " =?";
        Cursor cursor = db.rawQuery(selectString, new String[]{String.valueOf(id)});
        boolean exists = false;
        if(cursor.moveToFirst()){
            exists = true;
        }
        cursor.close();
        db.close();
        return exists;
    }

    public int sumPrice(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT SUM( " + COLUMN_TOTAL+ ") FROM " + TABLE_NAME, null);
        c.moveToFirst();
        int i = c.getInt(0);
        c.close();
        return i;
    }
}
