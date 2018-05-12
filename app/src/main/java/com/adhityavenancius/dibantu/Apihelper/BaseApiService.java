package com.adhityavenancius.dibantu.Apihelper;

import com.adhityavenancius.dibantu.Model.ResponseCategory;
import com.adhityavenancius.dibantu.Model.ResponseJobs;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

/**
 * Created by Adhitya Venancius on 5/8/2018.
 */

public interface BaseApiService {

    @FormUrlEncoded
    @POST("auth/login")
    Call<ResponseBody> loginRequest(@Field("email") String email,
                                    @Field("password") String password,
                                    @Field("role") String user);


    @FormUrlEncoded
    @POST("auth/register")
    Call<ResponseBody> registerRequest(@Field("nama") String nama,
                                       @Field("email") String email,
                                       @Field("password") String password);


    @GET("category/getlist")
    Call<ResponseCategory> getAllCategory();


    @FormUrlEncoded
    @POST("jobs/inputjob")
    Call<ResponseBody> inputJobRequest(@Field("id_user") String id_user,
                                    @Field("id_worker") String id_worker,
                                    @Field("id_category") String id_category,
                                       @Field("startdate") String startdate,
                                       @Field("enddate") String enddate,
                                       @Field("location") String location,
                                       @Field("time") String time,
                                       @Field("fare") String fare,
                                       @Field("notes") String notes,
                                       @Field("status") String status
                                       );

    @FormUrlEncoded
    @POST("jobs/getlist")
    Call<ResponseJobs> getJobsRequest(@Field("id_user") String id_user);

    @FormUrlEncoded
    @POST("jobs/getsingle")
    Call<ResponseBody> getJobsDetail(@Field("id_jobs") String id_jobs,
                                     @Field("role") String role);





}
