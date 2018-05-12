package com.adhityavenancius.dibantu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.adhityavenancius.dibantu.Apihelper.BaseApiService;
import com.adhityavenancius.dibantu.Apihelper.UtilsApi;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.adhityavenancius.dibantu.SessionManager.KEY_ID;

public class InputJobActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnMapLongClickListener,
        View.OnClickListener{

    private GoogleMap mMap;
    EditText etLocation, etStartDate, etEndDate, etTime, etFare,etNotes;
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
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mContext = InputJobActivity.this;
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

        Toast.makeText(mContext,id_user ,Toast.LENGTH_SHORT).show();

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera

        LatLng mercubuana = new LatLng(-6.2091759,106.738447);
        mMap.addMarker(new MarkerOptions().position(mercubuana).title("Universitas Mercu Buana"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(mercubuana));
    }


    @Override
    public void onClick(View view) {

        switch(view.getId()){
            case R.id.btnInputJob:
                loading = ProgressDialog.show(mContext, null, "Harap Tunggu...", true, false);
                requestInputJob();
                break;


        }

    }

    private void requestInputJob(){

        mApiService.inputJobRequest(id_user,id_worker,tvCategoryId.getText().toString(),etStartDate.getText().toString(),etEndDate.getText().toString(),etLocation.getText().toString(),
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
                                    Toast.makeText(mContext,success_message, Toast.LENGTH_SHORT).show();
//                                    String nama = jsonRESULTS.getJSONObject("user").getString("nama");
                                    Intent intent = new Intent(mContext, MainActivity.class);
//                                    intent.putExtra("result_nama", nama);
                                    startActivity(intent);
                                    finish();
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onMapLongClick(LatLng latLng) {

    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {
        LatLng position = marker.getPosition(); //
        Toast.makeText(
                InputJobActivity.this,
                "Lat " + position.latitude + " "
                        + "Long " + position.longitude,
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }
}
