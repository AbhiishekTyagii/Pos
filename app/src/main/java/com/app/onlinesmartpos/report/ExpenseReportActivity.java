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
import com.app.onlinesmartpos.adapter.ExpenseAdapter;
import com.app.onlinesmartpos.model.Expense;
import com.app.onlinesmartpos.model.ExpenseReport;
import com.app.onlinesmartpos.networking.ApiClient;
import com.app.onlinesmartpos.networking.ApiInterface;
import com.app.onlinesmartpos.utils.BaseActivity;
import com.facebook.shimmer.ShimmerFrameLayout;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ExpenseReportActivity extends BaseActivity {


    private RecyclerView recyclerView;

    ImageView imgNoProduct;
    TextView txtNoProducts, txtTotalPrice;

    private ShimmerFrameLayout mShimmerViewContainer;
    SharedPreferences sp;
    String currency;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_expense_report);

        recyclerView = findViewById(R.id.recycler);
        imgNoProduct = findViewById(R.id.image_no_product);

        txtNoProducts = findViewById(R.id.txt_no_products);
        txtTotalPrice = findViewById(R.id.txt_total_price);
        mShimmerViewContainer = findViewById(R.id.shimmer_view_container);

        sp = getSharedPreferences(Constant.SHARED_PREF_NAME, Context.MODE_PRIVATE);
        currency = sp.getString(Constant.SP_CURRENCY_SYMBOL, "N/A");

        imgNoProduct.setVisibility(View.GONE);
        txtNoProducts.setVisibility(View.GONE);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.all_expense);


        // set a GridLayoutManager with default vertical orientation and 3 number of columns
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ExpenseReportActivity.this, LinearLayoutManager.VERTICAL, false);
        recyclerView.setLayoutManager(linearLayoutManager); // set LayoutManager to RecyclerView

        recyclerView.setHasFixedSize(true);


        //sum of all transaction
        getExpenseData("all");
        //to view all sales data
        getExpenseReport("all");


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
                getExpenseData("all");

                return true;

            case R.id.menu_daily:
                getReport("daily");
                getExpenseData("daily");

                return true;

            case R.id.menu_weekly:
                getReport("weekly");
                getExpenseData("weekly");

                return true;


            case R.id.menu_monthly:
                getReport("monthly");
                getExpenseData("monthly");


                return true;

            case R.id.menu_yearly:
                getReport("yearly");
                getExpenseData("yearly");


                return true;


            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getReport(String type) {

        getExpenseData(type);
        getExpenseReport(type);
        //Stopping Shimmer Effects
        mShimmerViewContainer.startShimmer();
        mShimmerViewContainer.setVisibility(View.VISIBLE);


    }


    public void getExpenseData(String type) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<Expense>> call;
        call = apiInterface.getAllExpense(type);

        call.enqueue(new Callback<List<Expense>>() {
            @Override
            public void onResponse(@NonNull Call<List<Expense>> call, @NonNull Response<List<Expense>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<Expense> expenseList;
                    expenseList = response.body();


                    if (expenseList.isEmpty()) {

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
                        ExpenseAdapter expenseAdapter = new ExpenseAdapter(ExpenseReportActivity.this, expenseList);

                        recyclerView.setAdapter(expenseAdapter);

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Expense>> call, @NonNull Throwable t) {

                Toast.makeText(ExpenseReportActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }


    public void getExpenseReport(String type) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<ExpenseReport>> call;
        call = apiInterface.getExpenseReport(type);

        call.enqueue(new Callback<List<ExpenseReport>>() {
            @Override
            public void onResponse(@NonNull Call<List<ExpenseReport>> call, @NonNull Response<List<ExpenseReport>> response) {


                if (response.isSuccessful() && response.body() != null) {
                    List<ExpenseReport> expenseReports;
                    expenseReports = response.body();


                    if (expenseReports.isEmpty()) {


                        Log.d("Data", "Empty");
                        Toasty.warning(ExpenseReportActivity.this, R.string.no_data_found, Toast.LENGTH_SHORT).show();


                    } else {


                        String totalExpense = expenseReports.get(0).getTotalExpensePrice();

                        if (totalExpense!=null) {

                            txtTotalPrice.setText(getString(R.string.total_expense) + "="+currency + totalExpense);
                        }

                        else
                        {
                            txtTotalPrice.setVisibility(View.INVISIBLE);
                        }

                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ExpenseReport>> call, @NonNull Throwable t) {

                Toast.makeText(ExpenseReportActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }


}