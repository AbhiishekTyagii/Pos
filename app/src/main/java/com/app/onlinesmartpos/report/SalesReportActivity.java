package com.app.onlinesmartpos.report;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onlinesmartpos.Constant;
import com.app.onlinesmartpos.R;
import com.app.onlinesmartpos.adapter.OrderDetailsAdapter;
import com.app.onlinesmartpos.model.OrderDetails;
import com.app.onlinesmartpos.model.SalesReport;
import com.app.onlinesmartpos.networking.ApiClient;
import com.app.onlinesmartpos.networking.ApiInterface;
import com.app.onlinesmartpos.utils.BaseActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.text.DecimalFormat;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class SalesReportActivity extends BaseActivity {



    private RecyclerView recyclerView;
    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice,txtTotalTax,txtTotalDiscount,txtNetSales;
    private ShimmerFrameLayout mShimmerViewContainer;
    SharedPreferences sp;
    String currency;
    DecimalFormat f;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_report);

        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);

        txtNoProducts = findViewById(R.id.txt_no_products);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        txtTotalTax=findViewById(R.id.txt_total_tax);
        txtTotalDiscount=findViewById(R.id.txt_total_discount);
        txtNetSales=findViewById(R.id.txt_net_sales);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);
        f = new DecimalFormat("#0.00");
        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "N/A");

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_sales);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(SalesReportActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        //sum of all transaction
        getSalesReport("all");
        //to view all sales data
        getReport("all");




    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.all_sales_menu, menu);
        return true;
    }


    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case android.R.id.home:
                // app icon in action bar clicked; goto parent activity.
                this.finish();
                return true;
            case R.id.menu_all_sales:
                getReport("all");

                return true;

            case R.id.menu_daily:
                getReport("daily");
                getSupportActionBar().setTitle(R.string.daily);

                return true;

            case R.id.menu_weekly:
                getReport("weekly");
                getSupportActionBar().setTitle(R.string.weekly);

                return true;


            case R.id.menu_monthly:
                getReport("monthly");
                getSupportActionBar().setTitle(R.string.monthly);


                return true;

            case R.id.menu_yearly:
                getReport("yearly");
                getSupportActionBar().setTitle(R.string.yearly);


                return true;



            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getReport(String type) {

       getSalesReport(type);
       getReportList(type);
        //Stopping Shimmer Effects
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);



    }





    public void getSalesReport(String type) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<SalesReport>> call;
        call = apiInterface.getSalesReport(type);

        call.enqueue(new Callback<List<SalesReport>>() {
            @Override
            public void onResponse(@NonNull Call<List<SalesReport>> call, @NonNull Response<List<SalesReport>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<SalesReport> salesReport;
                    salesReport = response.body();



                    if (salesReport.isEmpty()) {


                        Log.d("Data", "Empty");


                    } else {


                        String totalOrderPrice=salesReport.get(0).getTotalOrderPrice();
                        String totalTax=salesReport.get(0).getTotalTax();
                        String totalDiscount=salesReport.get(0).getTotalDiscount();

                        if (totalOrderPrice!=null) {


                            double orderPrice = Double.parseDouble(totalOrderPrice);
                            double getTax = Double.parseDouble(totalTax);
                            double getDiscount = Double.parseDouble(totalDiscount);
                            double netSales = orderPrice + getTax - getDiscount;

                            txtTotalPrice.setText(getString(R.string.total_price) + "=" + currency + f.format(orderPrice));
                            txtTotalTax.setText(getString(R.string.total_tax) + "=" + currency +f.format(getTax));
                            txtTotalDiscount.setText(getString(R.string.total_discount) + "=" + currency + f.format(getDiscount));

                            txtNetSales.setText(getString(R.string.net_sales) + "=" + currency + f.format(netSales));
                        }
                        else
                        {
                            txtTotalPrice.setVisibility(View.INVISIBLE);
                            txtTotalTax.setVisibility(View.INVISIBLE);
                            txtTotalDiscount.setVisibility(View.INVISIBLE);
                            txtNetSales.setVisibility(View.INVISIBLE);
                        }

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<SalesReport>> call, @NonNull Throwable t) {

                Toast.makeText(SalesReportActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }






    public void getReportList(String type) {


        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<OrderDetails>> call;
        call = apiInterface.getReportList(type);

        call.enqueue(new Callback<List<OrderDetails>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderDetails>> call, @NonNull Response<List<OrderDetails>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<OrderDetails> orderDetails;
                    orderDetails = response.body();



                    if (orderDetails.isEmpty()) {

                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);


                        recyclerView.setVisibility(View.GONE);
                        imgNoProduct.setVisibility(View.VISIBLE);
                        imgNoProduct.setImageResource(R.drawable.not_found);
                        txtNoProducts.setVisibility(View.VISIBLE);
                        txtTotalPrice.setVisibility(View.GONE);
                        Toasty.warning(SalesReportActivity.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();


                    } else {


                        //Stopping Shimmer Effects
                        mShimmerViewContainer.stopShimmer();
                        mShimmerViewContainer.setVisibility(View.GONE);

                        OrderDetailsAdapter orderDetailsAdapter = new OrderDetailsAdapter(SalesReportActivity.this, orderDetails);

                        recyclerView.setAdapter(orderDetailsAdapter);

                        recyclerView.setVisibility(View.VISIBLE);
                        imgNoProduct.setVisibility(View.GONE);
                        txtNoProducts.setVisibility(View.GONE);
                        txtTotalPrice.setVisibility(View.VISIBLE);



                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderDetails>> call, @NonNull Throwable t) {


                Toast.makeText(SalesReportActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }



}

