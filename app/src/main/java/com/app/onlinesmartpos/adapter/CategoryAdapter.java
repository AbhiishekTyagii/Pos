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
import com.app.onlinesmartpos.model.Category;
import com.app.onlinesmartpos.networking.ApiClient;
import com.app.onlinesmartpos.networking.ApiInterface;
import com.app.onlinesmartpos.settings.categories.EditCategoryActivity;
import com.app.onlinesmartpos.utils.Utils;
import com.gitonway.lee.niftymodaldialogeffects.lib.NiftyDialogBuilder;

import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.gitonway.lee.niftymodaldialogeffects.lib.Effectstype.Slidetop;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.MyViewHolder> {


    private List<Category> categoryData;
    private Context context;
    Utils utils;


    public CategoryAdapter(Context context, List<Category> categoryData) {
        this.context = context;
        this.categoryData = categoryData;
        utils = new Utils();

    }


    @NonNull
    @Override
    public CategoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_item, parent, false);
        return new MyViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull final CategoryAdapter.MyViewHolder holder, int position) {


        String categoryName = categoryData.get(position).getProductCategoryName();
        String categoryId = categoryData.get(position).getProductCategoryId();
        holder.txtCategoryName.setText(categoryName);


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
                                    deleteCategory(categoryId);
                                    categoryData.remove(holder.getAdapterPosition());
                                    dialogBuilder.dismiss();
                                } else {
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
        return categoryData.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView txtCategoryName;
        ImageView imgDelete;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            txtCategoryName = itemView.findViewById(R.id.txt_category_name);

            imgDelete = itemView.findViewById(R.id.img_delete);

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View v) {
            Intent i = new Intent(context, EditCategoryActivity.class);
            i.putExtra("category_id", categoryData.get(getAdapterPosition()).getProductCategoryId());
            i.putExtra("category_name", categoryData.get(getAdapterPosition()).getProductCategoryName());
            context.startActivity(i);

        }
    }


    //delete from server
    private void deleteCategory(String categoryId) {

        ApiInterface apiInterface = ApiClient.getApiClient().create(ApiInterface.class);

        Call<Category> call = apiInterface.deleteCategory(categoryId);
        call.enqueue(new Callback<Category>() {
            @Override
            public void onResponse(@NonNull Call<Category> call, @NonNull Response<Category> response) {


                if (response.isSuccessful() && response.body() != null) {

                    String value = response.body().getValue();

                    if (value.equals(Constant.KEY_SUCCESS)) {
                        Toasty.error(context, context.getString(R.string.category_deleted), Toast.LENGTH_SHORT).show();
                        notifyDataSetChanged();

                    } else if (value.equals(Constant.KEY_FAILURE)) {
                        Toasty.error(context, R.string.error, Toast.LENGTH_SHORT).show();
                    } else {
                        Toasty.error(context, R.string.no_network_connection, Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<Category> call, Throwable t) {
                Toast.makeText(context, "Error! " + t.toString(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
