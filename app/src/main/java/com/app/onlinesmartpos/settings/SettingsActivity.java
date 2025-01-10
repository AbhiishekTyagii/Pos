package com.app.onlinesmartpos.settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.cardview.widget.CardView;

import com.app.onlinesmartpos.R;
import com.app.onlinesmartpos.settings.categories.CategoriesActivity;
import com.app.onlinesmartpos.settings.payment_method.PaymentMethodActivity;
import com.app.onlinesmartpos.settings.shop.ShopInformationActivity;
import com.app.onlinesmartpos.settings.unit.UnitActivity;
import com.app.onlinesmartpos.utils.BaseActivity;

public class SettingsActivity extends BaseActivity {


    CardView cardShopInfo,cardCategory,cardPaymentMethod,cardUnit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        getSupportActionBar().setHomeButtonEnabled(true); //for back button
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//for back button
        getSupportActionBar().setTitle(R.string.action_settings);


        cardShopInfo = findViewById(R.id.card_shop_info);
        cardCategory=findViewById(R.id.card_category);
        cardPaymentMethod=findViewById(R.id.card_payment_method);
        cardUnit=findViewById(R.id.card_unit);



        cardShopInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, ShopInformationActivity.class);
                startActivity(intent);
            }
        });


        cardCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SettingsActivity.this, CategoriesActivity.class);
                startActivity(intent);
            }
        });


        cardPaymentMethod.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, PaymentMethodActivity.class);
                startActivity(intent);
            }
        });

        cardUnit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(SettingsActivity.this, UnitActivity.class);
                startActivity(intent);
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
