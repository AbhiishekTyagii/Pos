package com.app.onlinesmartpos.pos;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onlinesmartpos.HomeActivity;
import com.app.onlinesmartpos.R;
import com.app.onlinesmartpos.adapter.PosProductAdapter;
import com.app.onlinesmartpos.adapter.ProductCategoryAdapter;
import com.app.onlinesmartpos.database.DatabaseAccess;
import com.app.onlinesmartpos.model.Category;
import com.app.onlinesmartpos.model.Product;
import com.app.onlinesmartpos.networking.ApiClient;
import com.app.onlinesmartpos.networking.ApiInterface;
import com.app.onlinesmartpos.utils.BaseActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosActivity extends BaseActivity {


    private RecyclerView recyclerView, categoryRecyclerView;
    PosProductAdapter productAdapter;
    TextView txtNoProducts, txtReset;
    ProductCategoryAdapter categoryAdapter;

    ImageView imgNoProduct, imgScanner, imgBack, imgCart;
    public static EditText etxtSearch;
    public static TextView txtCount;
    private ShimmerFrameLayout mShimmerViewContainer;
    int spanCount = 2;
    DatabaseAccess databaseAccess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pos);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_product);
        getSupportActionBar().hide();

        etxtSearch = findViewById(R.id.etxt_search);
        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);
        txtNoProducts = findViewById(R.id.txt_no_products);
        imgScanner = findViewById(R.id.img_scanner);
        categoryRecyclerView = findViewById(R.id.category_recyclerview);
        txtReset = findViewById(R.id.txt_reset);
        txtCount = findViewById(R.id.txt_count);
        imgBack = findViewById(R.id.img_back);
        imgCart = findViewById(R.id.img_cart);

        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        databaseAccess = DatabaseAccess.getInstance(PosActivity.this);

        imgScanner.setOnClickListener(v -> {
            Intent intent = new Intent(PosActivity.this, ScannerActivity.class);
            startActivity(intent);
        });

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);


        //Determine screen size
        if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE) {

            spanCount = 4;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_NORMAL) {

            spanCount = 2;
        } else if ((getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_SMALL) {

            spanCount = 2;
        } else {

            spanCount = 4;
        }


        getProductCategory();

        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getApplicationContext(), spanCount);
        recyclerView.setLayoutManager(gridLayoutManager); // set LayoutManager to RecyclerView
        recyclerView.setHasFixedSize(true);

        //Load data from server
        getProductsData("");

        txtReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getProductsData("");
            }
        });


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linerLayoutManager = new LinearLayoutManager(PosActivity.this, LinearLayoutManager.HORIZONTAL, false);
        categoryRecyclerView.setLayoutManager(linerLayoutManager); // set LayoutManager to RecyclerView


        categoryRecyclerView.setHasFixedSize(true);


        //swipe refresh listeners


        etxtSearch.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                Log.d("data", s.toString());
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() > 1) {

                    //search data from server
                    getProductsData(s.toString());
                } else {
                    getProductsData("");
                }


            }

            @Override
            public void afterTextChanged(Editable s) {
                Log.d("data", s.toString());
            }


        });


        databaseAccess.open();
        int count = databaseAccess.getCartItemCount();
        if (count == 0) {
            txtCount.setVisibility(View.INVISIBLE);
        } else {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText(String.valueOf(count));
        }

        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PosActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });


        imgCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(PosActivity.this, ProductCart.class);
                startActivity(intent);
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_cart, menu);
        return true;
    }

    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {


            case android.R.id.home:

                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getProductCategory() {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<List<Category>> call;


        call = apiInterface.getCategory();

        call.enqueue(new Callback<List<Category>>() {
            @Override
            public void onResponse(@NonNull Call<List<Category>> call, @NonNull Response<List<Category>> response) {


                if (response.isSuccessful() && response.body() != null) {


                    List<Category> productCategory;
                    productCategory = response.body();

                    if (productCategory.isEmpty()) {
                        Toasty.info(PosActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();
                        imgNoProduct.setImageResource(R.drawable.no_data);


                    } else {


                        categoryAdapter = new ProductCategoryAdapter(PosActivity.this, productCategory, recyclerView, imgNoProduct, txtNoProducts, mShimmerViewContainer);

                        categoryRecyclerView.setAdapter(categoryAdapter);

                    }


                }

            }

            @Override
            public void onFailure(@NonNull Call<List<Category>> call, @NonNull Throwable t) {

                //write own action
            }
        });


    }


    public void getProductsData(String searchText) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Product>> call;
        call = apiInterface.getProducts(searchText);

        call.enqueue(new Callback<List<Product>>() {
            @Override
            public void onResponse(@NonNull Call<List<Product>> call, @NonNull Response<List<Product>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<Product> productsList;
                    productsList = response.body();


                    if (productsList.isEmpty()) {

                        recyclerView.setVisibility(View.GONE);
                        imgNoProduct.setVisibility(View.VISIBLE);
                        imgNoProduct.setImageResource(R.drawable.not_found);
                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);


                    } else {


                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        recyclerView.setVisibility(View.VISIBLE);
                        imgNoProduct.setVisibility(View.GONE);
                        productAdapter = new PosProductAdapter(PosActivity.this, productsList);

                        recyclerView.setAdapter(productAdapter);

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Product>> call, @NonNull Throwable t) {

                Toast.makeText(PosActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }


    //to recheck item count when back this activity
    @Override
    protected void onResume() {
        super.onResume();


        databaseAccess.open();
        int count = databaseAccess.getCartItemCount();
        if (count == 0) {
            txtCount.setVisibility(View.INVISIBLE);
        } else {
            txtCount.setVisibility(View.VISIBLE);
            txtCount.setText(String.valueOf(count));
        }
    }
}
