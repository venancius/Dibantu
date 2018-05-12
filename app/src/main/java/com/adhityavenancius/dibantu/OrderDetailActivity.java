package com.adhityavenancius.dibantu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.LayerDrawable;
import android.media.Image;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adhityavenancius.dibantu.Apihelper.BaseApiService;
import com.adhityavenancius.dibantu.Apihelper.UtilsApi;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderDetailActivity extends AppCompatActivity implements View.OnClickListener{

    public TextView tvCategoryname,tvName,tvLocation,tvStartDate,tvEndDate,tvTime,tvFare,tvPhone;
    public EditText etNotes;
    public ImageView imgPicture;
    String pictureImageURL;
    String id_jobs;
    BaseApiService mApiService;
    SessionManager session;
    ProgressDialog loading;
    String id_user,role;
    RatingBar ratingBar;
    Button btnFinish;

    Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        mContext = this;
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // obtain id
        id_user = user.get(SessionManager.KEY_ID);
        role = user.get(SessionManager.KEY_ROLE);

        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
        initComponents();
        requestOrderDetail();

    }

    public void initComponents(){
        loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
        tvCategoryname = (TextView)findViewById(R.id.tvCategoryname);
        tvName = (TextView)findViewById(R.id.tvName);
        tvLocation = (TextView)findViewById(R.id.tvLocation);
        tvStartDate = (TextView)findViewById(R.id.tvStartDate);
        tvEndDate = (TextView)findViewById(R.id.tvEndDate);
        tvTime = (TextView)findViewById(R.id.tvTime);
        tvFare = (TextView)findViewById(R.id.tvFare);
        tvPhone = (TextView)findViewById(R.id.tvPhone);
        etNotes = (EditText) findViewById(R.id.etNotes);
        imgPicture = (ImageView)findViewById(R.id.imgPicture);
        ratingBar = (RatingBar) findViewById(R.id.ratingBar);
        btnFinish = (Button) findViewById(R.id.btnFinish);

        btnFinish.setOnClickListener(this);

        LayerDrawable stars = (LayerDrawable) ratingBar.getProgressDrawable();
        stars.getDrawable(2).setColorFilter(Color.YELLOW, PorterDuff.Mode.SRC_ATOP);

        Intent intent = getIntent();
        id_jobs = intent.getStringExtra("id_jobs");
    }




    private void requestOrderDetail(){
        mApiService.getJobsDetail(id_jobs,role)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("error").equals("false")){
                                    String categoryname = jsonRESULTS.getJSONObject("jobdata").getString("categoryname");
                                    String name = jsonRESULTS.getJSONObject("jobdata").getString("name");
                                    String phone = jsonRESULTS.getJSONObject("jobdata").getString("phone");
                                    String startdate = jsonRESULTS.getJSONObject("jobdata").getString("startdate");
                                    String enddate = jsonRESULTS.getJSONObject("jobdata").getString("enddate");
                                    String location = jsonRESULTS.getJSONObject("jobdata").getString("location");
                                    String time = jsonRESULTS.getJSONObject("jobdata").getString("time");
                                    String fare = jsonRESULTS.getJSONObject("jobdata").getString("fare");
                                    String notes = jsonRESULTS.getJSONObject("jobdata").getString("notes");
                                    String rate = jsonRESULTS.getJSONObject("jobdata").getString("rate");
                                    String id_worker = jsonRESULTS.getJSONObject("jobdata").getString("id_worker");
                                    String status = jsonRESULTS.getJSONObject("jobdata").getString("status");
                                    //handling force close jika name null
                                    if(!rate.equals("null")) {
                                        Float float_rate = Float.parseFloat(rate);
                                        ratingBar.setRating(float_rate);
                                    }

                                    tvCategoryname.setText(categoryname);
                                    tvName.setText(name);
                                    tvPhone.setText(phone);
                                    tvStartDate.setText(startdate);
                                    tvEndDate.setText(enddate);
                                    tvLocation.setText(location);
                                    tvTime.setText(time);
                                    tvFare.setText(fare);
                                    etNotes.setText(notes);
                                    pictureImageURL = UtilsApi.UPLOAD_URL + jsonRESULTS.getJSONObject("jobdata").getString("picture");
                                    RequestOptions options = new RequestOptions()
                                            .centerCrop()
                                            .placeholder(R.mipmap.ic_launcher_round)
                                            .error(R.mipmap.ic_launcher_round);
                                    Glide.with(mContext).load(pictureImageURL).apply(options).into(imgPicture);

                                    ratingBar.setEnabled(false);
                                    if(id_worker.equals("0")){
                                        btnFinish.setText("Cancel");
                                    }
                                    else {
                                        btnFinish.setText("Finish");
                                    }
                                    if(status.equals("1")){
                                        btnFinish.setVisibility(View.GONE);
                                    }



                                } else {
                                    // Jika login gagal
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btnFinish:
                FinishJob();
                break;

        }
    }

    private void FinishJob(){
        mApiService.finishJob(id_jobs)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("error").equals("false")){
                                    String success_message = jsonRESULTS.getString("message");
                                    Toast.makeText(mContext, success_message, Toast.LENGTH_SHORT).show();

                                    Intent intent = new Intent(mContext, MainActivity.class);
//                                    intent.putExtra("result_nama", nama);
                                    startActivity(intent);

                                } else {
                                    // Jika login gagal
                                    String error_message = jsonRESULTS.getString("message");
                                    Toast.makeText(mContext, error_message, Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        } else {
                            loading.dismiss();
                        }
                    }

                    @Override
                    public void onFailure(Call<ResponseBody> call, Throwable t) {
                        Log.e("debug", "onFailure: ERROR > " + t.toString());
                        loading.dismiss();
                    }
                });
    }
}
