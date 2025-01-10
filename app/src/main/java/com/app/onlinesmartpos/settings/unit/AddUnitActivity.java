package com.app.onlinesmartpos.settings.unit;

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
import com.app.onlinesmartpos.model.WeightUnit;
import com.app.onlinesmartpos.networking.ApiClient;
import com.app.onlinesmartpos.networking.ApiInterface;
import com.app.onlinesmartpos.utils.BaseActivity;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddUnitActivity extends BaseActivity {


    ProgressDialog loading;
    EditText etUnitName;
    TextView txtAddUnit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_unit);


        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.add_unit);


        txtAddUnit = findViewById(R.id.txt_add_unit);
        etUnitName = findViewById(R.id.et_unit_name);

        txtAddUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String unitName = etUnitName.getText().toString().trim();
                if (unitName.isEmpty()) {
                    etUnitName.setError(getString(R.string.unit_name));
                    etUnitName.requestFocus();

                } else {
                    addUnit(unitName);

                }


            }
        });

    }


    private void addUnit(String unitName) {


        loading = new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<WeightUnit> call = apiInterface.addUnit(unitName);
        call.enqueue(new Callback<WeightUnit>() {
            @Override
            public void onResponse(@NonNull Call<WeightUnit> call, @NonNull Response<WeightUnit> response) {


                if (response.isSuccessful() && response.body() != null) {
                    String value = response.body().getValue();

                    Log.d("value", value);

                    if (value.equals(Constant.KEY_SUCCESS)) {

                        loading.dismiss();

                        Toasty.success(AddUnitActivity.this, R.string.successfully_added, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(AddUnitActivity.this, UnitActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    } else if (value.equals(Constant.KEY_EXISTS)) {

                        loading.dismiss();

                        Toasty.error(AddUnitActivity.this, getString(R.string.unit_already_exists), Toast.LENGTH_SHORT).show();


                    } else if (value.equals(Constant.KEY_FAILURE)) {

                        loading.dismiss();

                        Toasty.error(AddUnitActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                        finish();

                    } else {
                        loading.dismiss();
                        Toasty.error(AddUnitActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    loading.dismiss();
                    Log.d("Error", "Error");
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeightUnit> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.d("Error! ", t.toString());
                Toasty.error(AddUnitActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
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