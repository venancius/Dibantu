package com.adhityavenancius.dibantu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.adhityavenancius.dibantu.Apihelper.BaseApiService;
import com.adhityavenancius.dibantu.Apihelper.UtilsApi;
import com.adhityavenancius.dibantu.Model.CitydataItem;
import com.adhityavenancius.dibantu.Model.ResponseCity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adhityavenancius.dibantu.SessionManager.KEY_ID;

public class InputJobActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etLocation, etStartDate, etEndDate, etTime, etFare,etNotes;
    Spinner spinnerCity;
    TextView tvCategoryId;
    Button btnInputJob;
    ProgressDialog loading;
    BaseApiService mApiService;
    SessionManager session;
    String id_user;
    String id_worker="0";
    String status="0";


    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input_job);
        // get user data from session
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // obtain id
        id_user = user.get(SessionManager.KEY_ID);

        mContext = InputJobActivity.this;
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        etLocation = (EditText)findViewById(R.id.etLocation);
        etStartDate = (EditText)findViewById(R.id.etStartDate);
        etEndDate = (EditText)findViewById(R.id.etEndDate);
        etTime = (EditText)findViewById(R.id.etTime);
        etFare = (EditText)findViewById(R.id.etFare);
        etNotes = (EditText)findViewById(R.id.etNotes);

        tvCategoryId = (TextView)findViewById(R.id.tvCategoryId);
        btnInputJob = (Button)findViewById(R.id.btnInputJob);
        btnInputJob.setOnClickListener(this);

        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper

        Intent intent = getIntent();
        tvCategoryId.setText(intent.getStringExtra("category_id"));


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

    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnInputJob:
                loading = ProgressDialog.show(mContext, null, "Processing Request..", true, false);
                requestInputJob();
                break;


        }

    }

    private void requestInputJob(){

        int id_city =spinnerCity.getSelectedItemPosition()+1;

        mApiService.inputJobRequest(id_user,id_worker,tvCategoryId.getText().toString(),id_city,etStartDate.getText().toString(),etEndDate.getText().toString(),etLocation.getText().toString(),
                etTime.getText().toString(),etFare.getText().toString(),etNotes.getText().toString(),status)
                .enqueue(new Callback<ResponseBody>() {
                    @Override
                    public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                        if (response.isSuccessful()){
                            loading.dismiss();
                            try {
                                JSONObject jsonRESULTS = new JSONObject(response.body().string());
                                if (jsonRESULTS.getString("error").equals("false")){
                                    // Jika login berhasil maka data nama yang ada di response API
                                    // akan diparsing ke activity selanjutnya.
                                    String success_message = jsonRESULTS.getString("message");
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


    }



