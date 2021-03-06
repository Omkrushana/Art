package com.example.lukas.artgallerydrow.controller.Controller;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.design.widget.NavigationView;
import android.support.v4.graphics.drawable.RoundedBitmapDrawable;
import android.support.v4.graphics.drawable.RoundedBitmapDrawableFactory;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import com.example.lukas.artgallerydrow.R;
import com.example.lukas.artgallerydrow.controller.Model.CustomRecyclerViewSeller;
import com.example.lukas.artgallerydrow.controller.Model.DBOperations;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;

public class SellerActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private static final int PICK_IMAGE = 100;
    private static final int CHANGE_PROFILE = 50;
    private DrawerLayout sellerDrowerLayout;
    private ActionBarDrawerToggle sellerToggle;

    private TextView headerUser;
    private TextView headerEmail;
    private TextView headerMoney;
    private ImageView imageHeader;
    private int userID;
    private  byte[] bytes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seller);

        userID = getIntent().getExtras().getInt("userID");

        sellerDrowerLayout = (DrawerLayout) findViewById(R.id.seller_drow_layout);
        sellerDrowerLayout.setBackgroundResource(R.color.colorAccent);
        sellerToggle = new ActionBarDrawerToggle(this, sellerDrowerLayout, R.string.seller_open, R.string.seller_close);
        sellerDrowerLayout.addDrawerListener(sellerToggle);
        sellerToggle.syncState();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        NavigationView navViewSeller = (NavigationView) findViewById(R.id.seller_nav_view);
        navViewSeller.setBackgroundResource(R.color.colorAccent);
        View v = navViewSeller.getHeaderView(0);
        navViewSeller.setNavigationItemSelectedListener(this);

        headerUser = (TextView) v.findViewById(R.id.nav_header_seller_name);
        headerUser.setText(getUsername());

        headerEmail = (TextView) v.findViewById(R.id.nav_header_seller_email);
        headerEmail.setText(getIntent().getExtras().getString("email"));

        headerMoney = (TextView) v.findViewById(R.id.nav_header_seller_money);
        headerMoney.setText("Wallet: " + getMoneyFromDb(userID) + "$");

        imageHeader = (ImageView) v.findViewById(R.id.imgHeaderSeller);

        allItemsForSale(userID);

        bytes = bytesImage();
        if(bytes == null){
            imageHeader.setImageResource(R.mipmap.emptyprofile);
        }else{
            Bitmap bitmap = BitmapFactory.decodeByteArray(bytes,0,bytes.length);

            RoundedBitmapDrawable round = RoundedBitmapDrawableFactory.create(getResources(),bitmap);
            round.setCircular(true);

            imageHeader.setImageDrawable(round);
        }

    }

    private String getMoneyFromDb(int userID) {
        DBOperations dbo = DBOperations.getInstance(this);
        Cursor cursor = dbo.getInfoForUserByID(userID);
        String money = "-1";
        while (cursor.moveToNext()){
            money = cursor.getString(6);
        }
        return money;
    }

    private byte[] bytesImage(){
        DBOperations dbOper = DBOperations.getInstance(SellerActivity.this);
        Cursor res = dbOper.checkUserForImage(userID);
        byte[] b = null;
        while (res.moveToNext()){
            b = res.getBlob(0);
        }
        if(b == null){
            return null;
        }else{
            return b;
        }
    }

    private String getUsername() {
        DBOperations dbOper = DBOperations.getInstance(SellerActivity.this);
        Cursor res = dbOper.getUserName(userID);
        String name = null;
        while (res.moveToNext()){
            name = res.getString(0);
        }
        return name;
    }

    @Override
    protected void onResume() {
        super.onResume();
        allItemsForSale(userID);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if (sellerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_soldItems) {

            DBOperations dbOper = DBOperations.getInstance(SellerActivity.this);
            Cursor res =  res = dbOper.getSoldItems(userID);

            RecyclerView recycler = (RecyclerView) findViewById(R.id.my_recycler_view);
            recycler.setLayoutManager(new LinearLayoutManager(this));
            CustomRecyclerViewSeller adapter = new CustomRecyclerViewSeller(this,res);
            recycler.setAdapter(adapter);

        } else if (id == R.id.nav_itemsForSale) {

            allItemsForSale(userID);

        } else if (id == R.id.nav_addItem) {

            openGallery();

        } else if(id == R.id.nav_profile_settings){

            ArrayList builder = getUserInfo(userID);
            Intent intent1 = new Intent(SellerActivity.this,UserProfileActivity.class);
            intent1.putExtra("userID",userID);
            intent1.putExtra("name",builder.get(0).toString());
            intent1.putExtra("address", builder.get(1).toString());
            intent1.putExtra("pass", builder.get(2).toString());
            startActivityForResult(intent1, CHANGE_PROFILE);

        } else if(id == R.id.nav_logout){

            Intent intent = new Intent(this,LoginActivity.class);
            startActivity(intent);
            finish();

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.seller_drow_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private ArrayList getUserInfo(int userID) {
        DBOperations dbOper = DBOperations.getInstance(this);
        Cursor res = dbOper.getInfoForUserByID(userID);
        ArrayList builder = new ArrayList();
        while (res.moveToNext()){
            builder.add(res.getString(2));
            builder.add(res.getString(1));
            builder.add(res.getString(4));
        }
        return builder;
    }

    private void allItemsForSale(int id) {
        DBOperations dbOper = DBOperations.getInstance(SellerActivity.this);
        Cursor res = dbOper.gelAllItems(id);

        if(res == null){
            Toast.makeText(getApplicationContext(),"No image for sale!", Toast.LENGTH_SHORT).show();
            return;
        }

        RecyclerView recycler = (RecyclerView) findViewById(R.id.my_recycler_view);
        recycler.setLayoutManager(new LinearLayoutManager(this));
        CustomRecyclerViewSeller adapter = new CustomRecyclerViewSeller(this,res);

        recycler.setAdapter(adapter);

    }

    public void openGallery() {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == RESULT_OK) {

                Uri uriImg = data.getData();

                CropImage.activity(uriImg)
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setCropShape(CropImageView.CropShape.RECTANGLE)
                        .setAspectRatio(1,1)
                        .setMinCropResultSize(100,100)
                        .setMaxCropResultSize(1281,1282)
                        .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {

                Uri resultUri = result.getUri();

                InputStream inputStream;

                try {
                    inputStream = getContentResolver().openInputStream(resultUri);

                    Bitmap image = BitmapFactory.decodeStream(inputStream);

                    if (image.getWidth() > 1300 || image.getHeight() > 1300) {
                        Toast.makeText(getApplicationContext(), "Picture is too big!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    Intent intent = new Intent(SellerActivity.this, UploadPreview.class);
                    intent.putExtra("userID", userID);
                    intent.putExtra("image", resultUri.toString());
                    startActivity(intent);

                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(), "Unable to open image...", Toast.LENGTH_SHORT).show();
                }

            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Toast.makeText(getApplicationContext(),"Something wrong with cropping...",Toast.LENGTH_SHORT).show();
            }
        }

        if(requestCode == CHANGE_PROFILE){
            if(resultCode == RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), "Canceled updates..", Toast.LENGTH_SHORT).show();
            }else if(resultCode == RESULT_OK){
                recreate();
            }
        }

    }

}
