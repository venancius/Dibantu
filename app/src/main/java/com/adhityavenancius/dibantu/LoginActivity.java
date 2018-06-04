package com.adhityavenancius.dibantu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.adhityavenancius.dibantu.Apihelper.BaseApiService;
import com.adhityavenancius.dibantu.Apihelper.UtilsApi;
import com.adhityavenancius.dibantu.Model.CitydataItem;
import com.adhityavenancius.dibantu.Model.ResponseCity;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {
    //login viewswitcher
    EditText etEmail,etPassword;

    //register viewswitcher
    EditText etName,etEmail1,etPhone,etPassword1,etAddress;

    Button btnLogin,btnRegister;

    ProgressDialog loading;
    String role = "user",btnText;
    TextView tvRegister;
    ViewSwitcher loginSwitcher;
    Spinner spinnerCity;

    String loginText = "Dont have an account? Sign Up";
    String registerText = "Already have an account? Sign in" ;


    Context mContext;
    BaseApiService mApiService;
    SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        session = new SessionManager(getApplicationContext());
//        boolean loginstatus = session.checkLogin();
//        if(loginstatus==true){
//            finish();
//        }

        mContext = this;
        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper
        initComponents();
        initSpinnerCity();
        spinnerCity.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedName = parent.getItemAtPosition(position).toString();
//                requestDetailDosen(selectedName);
//                Toast.makeText(mContext,selectedName, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    private boolean formValidation(String type){
        boolean tmp_boolean=false;
        switch (type){
            case "register" :
                if( etEmail1.getText().toString().trim().equals("") || etPassword1.getText().toString().trim().equals("") ||etName.getText().toString().trim().equals("")
                        ||etPhone.getText().toString().trim().equals("") || etAddress.getText().toString().trim().equals(""))
                {
                    tmp_boolean = false;
                } else { tmp_boolean = true; }
                break;
            case "login" :
                if( etEmail.getText().toString().trim().equals("") || etPassword.getText().toString().trim().equals(""))
            {
                tmp_boolean = false;
            } else { tmp_boolean = true; }
                break;

        }

        return tmp_boolean;

    }

    public void initComponents(){
        //viewswitcher
        loginSwitcher = (ViewSwitcher)findViewById(R.id.loginSwitcher);
        //animation untuk viewswitcher
        Animation in = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_in_left);
        Animation out = AnimationUtils.loadAnimation(mContext, android.R.anim.slide_out_right);
        //set animasi ke viewswitcher
        loginSwitcher.setInAnimation(in);
        loginSwitcher.setOutAnimation(out);
        //edittext di bind di viewswitcher

        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        
        etEmail = (EditText) loginSwitcher.findViewById(R.id.etEmail);
        etPassword = (EditText) loginSwitcher.findViewById(R.id.etPassword);


        etName = (EditText) loginSwitcher.findViewById(R.id.etName);
        etEmail1 = (EditText) loginSwitcher.findViewById(R.id.etEmail1);
        etPhone = (EditText) loginSwitcher.findViewById(R.id.etPhone);
        etPassword1 = (EditText) loginSwitcher.findViewById(R.id.etPassword1);
        etAddress = (EditText) loginSwitcher.findViewById(R.id.etAddress);


        btnRegister = (Button) findViewById(R.id.btnRegister);
        btnLogin = (Button) findViewById(R.id.btnLogin);
        tvRegister = (TextView) findViewById(R.id.tvRegister);

        tvRegister.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){

                    btnText = tvRegister.getText().toString();
                    if(btnText.equals(loginText)){
                        tvRegister.setText(registerText);
                    }

                    if(btnText.equals(registerText)){
                        tvRegister.setText(loginText);
                    }
                    loginSwitcher.showNext();


            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "login";
                if(formValidation(tmp)==true){
                    loading = ProgressDialog.show(mContext, null, "Processing Request..", true, false);
                    requestLogin();
                }
                else{
                    Toasty.error(mContext,"Please fill in required fields",Toast.LENGTH_SHORT).show();
                }

            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tmp = "register";
                if(formValidation(tmp)==true){
                    loading = ProgressDialog.show(mContext, null, "Processing Request..", true, false);
                    requestRegister();
                }
                else{
                    Toasty.error(mContext,"Please fill in required fields",Toast.LENGTH_SHORT).show();
                }
            }
        });

    }


    private void requestRegister(){
        int id_city = spinnerCity.getSelectedItemPosition()+1;

        mApiService.registerRequest(etName.getText().toString(),etEmail1.getText().toString(), etPhone.getText().toString(),
                etPassword1.getText().toString(),id_city, etAddress.getText().toString(),role)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("error").equals("false")){
                                    btnText = tvRegister.getText().toString();
                                    if(btnText.equals(loginText)){
                                        tvRegister.setText(registerText);
                                    }

                                    if(btnText.equals(registerText)){
                                        tvRegister.setText(loginText);
                                    }
                                    loginSwitcher.showNext();

                                    String success_message = jsonRESULTS.getString("message");
                                    Toasty.success(mContext,success_message, Toast.LENGTH_SHORT).show();

                                } else {
                                    // Jika login gagal
                                    String error_message = jsonRESULTS.getString("message");
                                    Toasty.error(mContext, error_message, Toast.LENGTH_SHORT).show();
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

    private void requestLogin(){
        mApiService.loginRequest(etEmail.getText().toString(), etPassword.getText().toString(),role)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("error").equals("false")){
                                    String name = jsonRESULTS.getJSONObject("userdata").getString("name");
                                    String email = jsonRESULTS.getJSONObject("userdata").getString("email");
                                    String id = jsonRESULTS.getJSONObject("userdata").getString("id");
                                    String id_city = jsonRESULTS.getJSONObject("userdata").getString("id_city");
                                    String address = jsonRESULTS.getJSONObject("userdata").getString("address");

                                    session.createLoginSession(id,name, email,role,id_city,address);
                                    // Jika login berhasil maka data nama yang ada di response API
                                    // akan diparsing ke activity selanjutnya.
                                    String success_message = jsonRESULTS.getString("message")+" as "+name;
                                    Toasty.success(mContext,success_message, Toast.LENGTH_SHORT).show();
//                                    String nama = jsonRESULTS.getJSONObject("user").getString("nama");
                                    Intent intent = new Intent(mContext, MainActivity.class);
//                                    intent.putExtra("result_nama", nama);
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // Jika login gagal
                                    String error_message = jsonRESULTS.getString("message");
                                    Toasty.error(mContext, error_message, Toast.LENGTH_SHORT).show();
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
    private void initSpinnerCity(){

        loading = ProgressDialog.show(mContext, null, "Processing Request..", true, false);

        mApiService.getCity().enqueue(new Callback<ResponseCity>() {
            @Override
            public void onResponse(Call<ResponseCity> call, Response<ResponseCity> response) {
                if (response.isSuccessful()) {
                    loading.dismiss();
                    List<CitydataItem> cityItems = response.body().getCitydata();
                    List<String> listSpinner = new ArrayList<String>();
                    for (int i = 0; i < cityItems.size(); i++){
                        listSpinner.add(cityItems.get(i).getName());
                    }
                    // Set hasil result json ke dalam adapter spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(mContext,
                            android.R.layout.simple_spinner_item, listSpinner);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spinnerCity.setAdapter(adapter);
                } else {
                    loading.dismiss();
                    Toasty.error(mContext, "Request Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseCity> call, Throwable t) {
                loading.dismiss();
                Toasty.error(mContext, "No Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


}
