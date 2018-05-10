package com.adhityavenancius.dibantu.Adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adhityavenancius.dibantu.Apihelper.UtilsApi;
import com.adhityavenancius.dibantu.Model.AllcategoryItem;
import com.adhityavenancius.dibantu.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.w3c.dom.Text;

import java.util.List;

/**
 * Created by Adhitya Venancius on 5/10/2018.
 */

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryHolder> {
    List<AllcategoryItem> allcategoryItemList;
    Context mContext;
    String categoryImageURL;

    public CategoryAdapter(Context context, List<AllcategoryItem> categoryList){
        this.mContext = context;
        allcategoryItemList = categoryList;
    }

    @Override
    public CategoryHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.category_card, parent, false);
        return new CategoryHolder(itemView);
    }

    @Override
    public void onBindViewHolder(CategoryHolder holder, int position) {
        final AllcategoryItem allcategoryitem = allcategoryItemList.get(position);
        holder.name.setText(allcategoryitem.getName());
        holder.description.setText(allcategoryitem.getDescription());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        categoryImageURL = UtilsApi.UPLOAD_URL + allcategoryitem.getImage();

        Glide.with(mContext).load(categoryImageURL).apply(options).into(holder.thumbnail);
//        Glide.with(mContext).load(R.drawable.tmp_logo).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return allcategoryItemList.size();
    }

    public class CategoryHolder extends RecyclerView.ViewHolder{
        public TextView name, description;
        public ImageView thumbnail;

        public CategoryHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
        }
    }


}
