package com.adhityavenancius.dibantu.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.adhityavenancius.dibantu.Apihelper.UtilsApi;
import com.adhityavenancius.dibantu.MainActivity;
import com.adhityavenancius.dibantu.Model.ActivejobsItem;
import com.adhityavenancius.dibantu.OrderDetailActivity;
import com.adhityavenancius.dibantu.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by Adhitya Venancius on 5/10/2018.
 */

public class ActiveJobsAdapter extends RecyclerView.Adapter<ActiveJobsAdapter.ActiveJobsHolder> {
    List<ActivejobsItem> activejobsItemList;
    Context mContext;
    String categoryImageURL;

    public ActiveJobsAdapter(Context context, List<ActivejobsItem> jobList){
        this.mContext = context;
        activejobsItemList = jobList;
    }

    @Override
    public ActiveJobsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.active_jobs_card, parent, false);
        return new ActiveJobsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ActiveJobsHolder holder, int position) {
        final ActivejobsItem activejobsitem = activejobsItemList.get(position);
        holder.categoryname.setText(activejobsitem.getCategoryname());
        holder.workername.setText(activejobsitem.getWorkername());
        holder.jobs_id.setText(activejobsitem.getId());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        categoryImageURL = UtilsApi.UPLOAD_URL + activejobsitem.getWorkerpicture();

        Glide.with(mContext).load(categoryImageURL).apply(options).into(holder.thumbnail);
//        Glide.with(mContext).load(R.drawable.tmp_logo).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return activejobsItemList.size();
    }

    public class ActiveJobsHolder extends RecyclerView.ViewHolder{
        public TextView categoryname, workername,jobs_id;
        public ImageView thumbnail;

        public ActiveJobsHolder(View itemView) {
            super(itemView);
            categoryname = (TextView) itemView.findViewById(R.id.categoryname);
            workername = (TextView) itemView.findViewById(R.id.workername);
            thumbnail = (ImageView) itemView.findViewById(R.id.thumbnail);
            jobs_id = (TextView) itemView.findViewById(R.id.jobs_id);

            itemView.setOnClickListener(new View.OnClickListener(){
                @Override public void onClick(View v){
                    Intent intent = new Intent(v.getContext(), OrderDetailActivity.class);

                    intent.putExtra("id_jobs",jobs_id.getText() );
                    v.getContext().startActivity(intent);
                }
            });

        }
    }


}
