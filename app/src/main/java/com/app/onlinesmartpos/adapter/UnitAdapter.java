package com.app.onlinesmartpos.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.onlinesmartpos.Constant;
import com.app.onlinesmartpos.R;
import com.app.onlinesmartpos.customers.EditCustomersActivity;
import com.app.onlinesmartpos.model.Customer;
import com.app.onlinesmartpos.model.WeightUnit;
import com.app.onlinesmartpos.networking.ApiClient;
import com.app.onlinesmartpos.networking.ApiInterface;
import com.app.onlinesmartpos.settings.unit.EditUnitActivity;
import com.app.onlinesmartpos.utils.Utils;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class UnitAdapter extends RecyclerView.Adapter<UnitAdapter.MyViewHolder> {


    private List<WeightUnit> unitData;
    private Context context;
    Utils utils;


    public UnitAdapter(Context context, List<WeightUnit> unitData) {
        this.context = context;
        this.unitData = unitData;
        utils=new Utils();

    }


    @NonNull
    @Override
    public UnitAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final UnitAdapter.MyViewHolder holder, int position) {

        final String unitId = unitData.get(position).getWeightUnitId();
        String unitName = unitData.get(position).getWeightUnitName();

        holder.txtUnitName.setText(unitName);


        holder.imgDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                NiftyDialogBuilder dialogBuilder = NiftyDialogBuilder.getInstance(context);
                dialogBuilder
                        .withTitle(context.getString(R.string.delete))
                        .withMessage(context.getString(R.string.want_to_delete))
                        .withEffect(Slidetop)
                        .withDialogColor("#2979ff") //use color code for dialog
                        .withButton1Text(context.getString(R.string.yes))
                        .withButton2Text(context.getString(R.string.cancel))
                        .setButton1Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {


                                if (utils.isNetworkAvailable(context)) {
                                    deleteUnit(unitId);
                                    unitData.remove(holder.getAdapterPosition());
                                    dialogBuilder.dismiss();
                                }
                                else
                                {
                                    dialogBuilder.dismiss();
                                    Toasty.error(context, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                                }
                            }
                        })
                        .setButton2Click(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                dialogBuilder.dismiss();
                            }
                        })
                        .show();


            }
        });

    }

    @Override
    public int getItemCount() {
        return unitData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtUnitName;
        ImageView imgDelete;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtUnitName = itemView.findViewById(R.id.txt_unit_name);
            imgDelete = itemView.findViewById(R.id.img_delete);
            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditUnitActivity.class);
            i.putExtra("unit_id", unitData.get(getAdapterPosition()).getWeightUnitId());
            i.putExtra("unit_name", unitData.get(getAdapterPosition()).getWeightUnitName());
            context.startActivity(i);
        }
    }


    //delete from server
    private void deleteUnit(String unitId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<WeightUnit> call = apiInterface.deleteUnit(unitId);
        call.enqueue(new Callback<WeightUnit>() {
            @Override
            public void onResponse(@NonNull Call<WeightUnit> call, @NonNull Response<WeightUnit> response) {


                if (response.isSuccessful() && response.body() != null) {

                    String value = response.body().getValue();

                    if (value.equals(Constant.KEY_SUCCESS)) {
                        Toasty.error(context, context.getString(R.string.unit_deleted), Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();

                    }

                    else if (value.equals(Constant.KEY_FAILURE)){
                        Toasty.error(context, R.string.error, Toast.LENGTH_SHORT).show();
                    }

                    else {
                        Toast.makeText(context, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WeightUnit> call, Throwable t) {
                Toast.makeText(context, "Error! " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}
