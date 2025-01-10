package com.app.onlinesmartpos.settings.shop;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.onlinesmartpos.Constant;
import com.app.onlinesmartpos.R;
import com.app.onlinesmartpos.model.ShopInformation;
import com.app.onlinesmartpos.networking.ApiClient;
import com.app.onlinesmartpos.networking.ApiInterface;
import com.app.onlinesmartpos.utils.BaseActivity;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ShopInformationActivity extends BaseActivity {


    EditText etxtShopName, etxtTax, etxtShopContact, etxtShopEmail, etxtShopAddress;
    ProgressDialog loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shop_information);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.shop_information);


        etxtShopName = findViewById(R.id.etxt_shop_name);
        etxtShopContact = findViewById(R.id.etxt_shop_contact);
        etxtShopEmail = findViewById(R.id.etxt_shop_email);
        etxtShopAddress = findViewById(R.id.etxt_shop_address);
        etxtTax = findViewById(R.id.etxt_tax);


        getShopInfo(Constant.SHOP_NUMBER);

        etxtShopName.setEnabled(false);
        etxtShopContact.setEnabled(false);
        etxtShopEmail.setEnabled(false);
        etxtShopAddress.setEnabled(false);
        etxtTax.setEnabled(false);



    }



    public void getShopInfo(String shopId) {

        loading=new ProgressDialog(this);
        loading.setMessage(getString(R.string.please_wait));
        loading.setCancelable(false);
        loading.show();
        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);
        Call<List<ShopInformation>> call;
        call = apiInterface.shopInformation(shopId);

        call.enqueue(new Callback<List<ShopInformation>>() {
            @Override
            public void onResponse(@NonNull Call<List<ShopInformation>> call, @NonNull Response<List<ShopInformation>> response) {


                if (response.isSuccessful() && response.body() != null) {

                    loading.dismiss();
                    List<ShopInformation> shopInformation;
                    shopInformation = response.body();



                    if (shopInformation.isEmpty()) {


                        Toasty.warning(ShopInformationActivity.this, R.string.no_product_found, Toast.LENGTH_SHORT).show();


                    } else {


                        String shopName=shopInformation.get(0).getShopName();
                        String shopContact=shopInformation.get(0).getShopContact();
                        String shopEmail=shopInformation.get(0).getShopEmail();
                        String shopAddress=shopInformation.get(0).getShopAddress();
                        String tax=shopInformation.get(0).getTax();


                        etxtShopName.setText(shopName);
                        etxtShopContact.setText(shopContact);
                        etxtShopEmail.setText(shopEmail);
                        etxtShopAddress.setText(shopAddress);
                        etxtTax.setText(tax);




                    }

                }
            }

            @Override
            public void onFailure(@NonNull Call<List<ShopInformation>> call, @NonNull Throwable t) {

                loading.dismiss();
                Toast.makeText(ShopInformationActivity.this, R.string.something_went_wrong, Toast.LENGTH_SHORT).show();
                Log.d("Error : ", t.toString());
            }
        });


    }



    //for back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
