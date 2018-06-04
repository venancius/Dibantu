package com.adhityavenancius.dibantu;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.icu.text.NumberFormat;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
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
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import es.dmoral.toasty.Toasty;
import io.socket.client.IO;
import io.socket.client.Socket;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adhityavenancius.dibantu.SessionManager.KEY_ID;
import static java.lang.Integer.parseInt;

public class InputJobActivity extends AppCompatActivity implements View.OnClickListener {

    EditText etLocation, etStartDate, etEndDate, etTime,etEndTime, etFare,etNotes;
    Spinner spinnerCity;
    TextView tvCategoryId;
    Button btnInputJob;
    ProgressDialog loading;
    BaseApiService mApiService;
    SessionManager session;
    String id_user,user_address,user_city;

    String id_worker="0";
    String status="0";
    String str_startdate="startdate";
    String str_enddate="enddate";
    Calendar myCalendar;

    Integer cityspinnerselected;

    private Context mContext;

    private Socket mSocket;
    {
        try {
            mSocket = IO.socket("http://192.168.43.172:3000");
        } catch (URISyntaxException e) {}
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init socket
        mSocket.connect();

        setContentView(R.layout.activity_input_job);
        // get user data from session
        session = new SessionManager(getApplicationContext());
        HashMap<String, String> user = session.getUserDetails();
        // obtain id
        id_user = user.get(SessionManager.KEY_ID);
        user_address = user.get(SessionManager.KEY_ADDRESS);
        user_city = (user.get(SessionManager.KEY_CITY));


        // -1 karena arraylist dari 0, sedangkan id city di db dari 1
        cityspinnerselected = parseInt(user_city)-1;

        mContext = InputJobActivity.this;
        spinnerCity = (Spinner) findViewById(R.id.spinnerCity);
        etLocation = (EditText)findViewById(R.id.etLocation);
        etStartDate = (EditText)findViewById(R.id.etStartDate);
        etEndDate = (EditText)findViewById(R.id.etEndDate);
        etTime = (EditText)findViewById(R.id.etTime);
        etEndTime = (EditText)findViewById(R.id.etEndTime);
        etFare = (EditText)findViewById(R.id.etFare);
        etNotes = (EditText)findViewById(R.id.etNotes);
        myCalendar = Calendar.getInstance();

        tvCategoryId = (TextView)findViewById(R.id.tvCategoryId);
        btnInputJob = (Button)findViewById(R.id.btnInputJob);
        btnInputJob.setOnClickListener(this);

        mApiService = UtilsApi.getAPIService(); // meng-init yang ada di package apihelper

        Intent intent = getIntent();
        tvCategoryId.setText(intent.getStringExtra("category_id"));

        etTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(InputJobActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });

        etEndTime.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(InputJobActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        etEndTime.setText( selectedHour + ":" + selectedMinute);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.setTitle("Select Time");
                mTimePicker.show();

            }
        });


        etLocation.setText(user_address);
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

        final DatePickerDialog.OnDateSetListener startdate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(str_startdate);
            }

        };

        final DatePickerDialog.OnDateSetListener enddate = new DatePickerDialog.OnDateSetListener() {

            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                // TODO Auto-generated method stub
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(str_enddate);
            }

        };

        etStartDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(mContext, startdate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        etEndDate.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                new DatePickerDialog(mContext, enddate, myCalendar
                        .get(Calendar.YEAR), myCalendar.get(Calendar.MONTH),
                        myCalendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });



    }

    private void updateLabel(String whichdate) {
        String myFormat = "yyyy/MM/dd"; //In which you need put here
        SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);

        switch (whichdate){
            case "startdate": etStartDate.setText(sdf.format(myCalendar.getTime()));
                              break;
            case "enddate": etEndDate.setText(sdf.format(myCalendar.getTime()));
                            break;
        }

    }

    private boolean formValidation(){
        if( etLocation.getText().toString().trim().equals("") || etStartDate.getText().toString().trim().equals("") || etEndDate.getText().toString().trim().equals("") ||
                etTime.getText().toString().trim().equals("") || etFare.getText().toString().trim().equals(""))
        {
            return false;
        } else { return true; }
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
                    spinnerCity.setSelection(cityspinnerselected);
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
                if(formValidation()==true){
                    loading = ProgressDialog.show(mContext, null, "Processing Request..", true, false);
                    requestInputJob();
                }
                else{
                    Toasty.error(mContext,"Please fill in required fields",Toast.LENGTH_SHORT).show();
                }

                break;


        }

    }

    private String reverseFormat(String formatted_string){
        String newStr = formatted_string.replaceAll("[,]", "");
        return newStr;
    }

    private void requestInputJob(){

        int id_city =spinnerCity.getSelectedItemPosition()+1;
        String fare = reverseFormat(etFare.getText().toString());

        mApiService.inputJobRequest(id_user,id_worker,tvCategoryId.getText().toString(),id_city,etStartDate.getText().toString(),etEndDate.getText().toString(),etLocation.getText().toString(),
                etTime.getText().toString(),etEndTime.getText().toString(),fare,etNotes.getText().toString(),status)
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
                                    mSocket.emit("new job");
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



