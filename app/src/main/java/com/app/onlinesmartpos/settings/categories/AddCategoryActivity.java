package com.app.onlinesmartpos.settings.categories;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.onlinesmartpos.Constant;
import com.app.onlinesmartpos.R;
import com.app.onlinesmartpos.model.Category;
import com.app.onlinesmartpos.model.WeightUnit;
import com.app.onlinesmartpos.networking.ApiClient;
import com.app.onlinesmartpos.networking.ApiInterface;
import com.app.onlinesmartpos.settings.unit.UnitActivity;
import com.app.onlinesmartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddCategoryActivity extends BaseActivity {

    ProgressDialog loading;
    EditText etCategoryName;
    TextView txtAddCategory;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_category);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_category);


        etCategoryName=findViewById(R.id.et_category_name);
        txtAddCategory=findViewById(R.id.txt_add_category);

        txtAddCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String categoryName = etCategoryName.getText().toString().trim();
                if (categoryName.isEmpty()) {
                    etCategoryName.setError(getString(R.string.category_name));
                    etCategoryName.requestFocus();

                } else {
                    addCategory(categoryName);

                }


            }
        });

    }


    private void addCategory(String categoryName) {


        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Category> call = apiInterface.addCategory(categoryName);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {


                if (response.isSuccessful() && response.body() != null) {
                    String value = response.body().getValue();



                    if (value.equals(Constant.KEY_SUCCESS)) {

                        loading.dismiss();

                        Toasty.success(AddCategoryActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddCategoryActivity.this, CategoriesActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else if (value.equals(Constant.KEY_EXISTS)) {

                        loading.dismiss();

                        Toasty.error(AddCategoryActivity.this, getString(R.string.already_exists), Toast.LENGTH_SHORT).show();


                    } else if (value.equals(Constant.KEY_FAILURE)) {

                        loading.dismiss();

                        Toasty.error(AddCategoryActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        loading.dismiss();
                        Toasty.error(AddCategoryActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loading.dismiss();
                    Log.d("Error", "Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<Category> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.d("Error! ", t.toString());
                Toasty.error(AddCategoryActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }




    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {// app icon in action bar clicked; goto parent activity.
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }



}