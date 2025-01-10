package com.app.onlinesmartpos.settings.unit;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
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

public class EditUnitActivity extends BaseActivity {


    ProgressDialog loading;
    TextView txtEdit, txtUpdate;
    EditText etUnitName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_unit);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.units);

        txtEdit=findViewById(R.id.txt_edit);
        txtUpdate=findViewById(R.id.txt_update);
        etUnitName=findViewById(R.id.et_unit_name);

        txtUpdate.setVisibility(View.INVISIBLE);

        String unitId=getIntent().getExtras().getString("unit_id");
        String unitName=getIntent().getExtras().getString("unit_name");

        etUnitName.setText(unitName);

        etUnitName.setEnabled(false);

        txtEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                etUnitName.setEnabled(true);

                etUnitName.setTextColor(Color.RED);
                txtUpdate.setVisibility(View.VISIBLE);

                txtEdit.setVisibility(View.GONE);

            }
        });


        txtUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String unitName = etUnitName.getText().toString().trim();

                if (unitName.isEmpty())
                {
                    etUnitName.setError(getString(R.string.unit_name));
                    etUnitName.requestFocus();

                }

                else

                {
                    updateUnit(unitId,unitName);

                }
            }
        });




    }



    private void updateUnit(String unitId,String unitName) {

        loading=new ProgressDialog(this);
        loading.setCancelable(false);
        loading.setMessage(getString(R.string.please_wait));
        loading.show();

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<WeightUnit> call = apiInterface.updateUnit(unitId,unitName);
        call.enqueue(new Callback<WeightUnit>() {
            @Override
            public void onResponse(@NonNull Call<WeightUnit> call, @NonNull Response<WeightUnit> response) {


                if (response.isSuccessful() && response.body() != null) {
                    String value = response.body().getValue();

                    if (value.equals(Constant.KEY_SUCCESS)) {

                        loading.dismiss();

                        Toasty.success(EditUnitActivity.this, R.string.update_successfully, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(EditUnitActivity.this, UnitActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);

                    }
                    else if (value.equals(Constant.KEY_FAILURE)) {

                        loading.dismiss();

                        Toasty.error(EditUnitActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                        finish();

                    }

                    else {
                        loading.dismiss();
                        Toasty.error(EditUnitActivity.this, R.string.failed, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeightUnit> call, @NonNull Throwable t) {
                loading.dismiss();
                Log.d("Error! ", t.toString());
                Toasty.error(EditUnitActivity.this, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
            }
        });
    }



    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}