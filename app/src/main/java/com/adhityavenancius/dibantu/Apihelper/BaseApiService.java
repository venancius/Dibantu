package com.adhityavenancius.dibantu.Apihelper;

import com.adhityavenancius.dibantu.Model.ResponseCategory;

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




}
