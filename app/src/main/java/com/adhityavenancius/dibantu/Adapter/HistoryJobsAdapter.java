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
import com.adhityavenancius.dibantu.Model.HistoryjobsItem;
import com.adhityavenancius.dibantu.OrderDetailActivity;
import com.adhityavenancius.dibantu.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.List;

/**
 * Created by Adhitya Venancius on 5/12/2018.
 */

public class HistoryJobsAdapter extends RecyclerView.Adapter<HistoryJobsAdapter.HistoryjobsHolder> {
    List<HistoryjobsItem> historyjobsItemList;
    Context mContext;
    String categoryImageURL;

    public HistoryJobsAdapter(Context context, List<HistoryjobsItem> jobList){
        this.mContext = context;
        historyjobsItemList = jobList;
    }

    @Override
    public HistoryjobsHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_jobs_card, parent, false);
        return new HistoryjobsHolder(itemView);
    }

    @Override
    public void onBindViewHolder(HistoryjobsHolder holder, int position) {
        final HistoryjobsItem historyjobsitem = historyjobsItemList.get(position);
        holder.categoryname.setText(historyjobsitem.getCategoryname());
        holder.workername.setText(historyjobsitem.getWorkername());
        holder.jobs_id.setText(historyjobsitem.getId());

        RequestOptions options = new RequestOptions()
                .centerCrop()
                .placeholder(R.mipmap.ic_launcher_round)
                .error(R.mipmap.ic_launcher_round);

        categoryImageURL = UtilsApi.UPLOAD_URL + historyjobsitem.getWorkerpicture();

        Glide.with(mContext).load(categoryImageURL).apply(options).into(holder.thumbnail);
//        Glide.with(mContext).load(R.drawable.tmp_logo).into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return historyjobsItemList.size();
    }

    public class HistoryjobsHolder extends RecyclerView.ViewHolder{
        public TextView categoryname, workername,jobs_id;
        public ImageView thumbnail;

        public HistoryjobsHolder(View itemView) {
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
