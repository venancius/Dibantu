package com.adhityavenancius.dibantu;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.adhityavenancius.dibantu.Adapter.ActiveJobsAdapter;

import com.adhityavenancius.dibantu.Adapter.HistoryJobsAdapter;
import com.adhityavenancius.dibantu.Apihelper.BaseApiService;
import com.adhityavenancius.dibantu.Apihelper.UtilsApi;
import com.adhityavenancius.dibantu.Model.ActivejobsItem;
import com.adhityavenancius.dibantu.Model.HistoryjobsItem;
import com.adhityavenancius.dibantu.Model.ResponseJobs;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import es.dmoral.toasty.Toasty;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class JobsFragment extends Fragment {
    List<ActivejobsItem> activejobsItemList = new ArrayList<>();
    List<HistoryjobsItem> historyjobsItemList = new ArrayList<>();
    ActiveJobsAdapter activeJobsAdapter;
    HistoryJobsAdapter historyJobsAdapter;
    BaseApiService mApiService;
    RecyclerView rvActiveJobs,rvHistoryJobs;
    ProgressDialog loading;
    TextView tvTaphere;

    SessionManager session;
    String id_user;
    Context mContext;

    private OnFragmentInteractionListener mListener;

    public JobsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment CategoryFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static JobsFragment newInstance(String param1, String param2) {
        JobsFragment fragment = new JobsFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_jobs, container, false);
        session = new SessionManager(getActivity());
        HashMap<String, String> user = session.getUserDetails();
        // obtain id
        id_user = user.get(SessionManager.KEY_ID);

        rvActiveJobs = (RecyclerView) view.findViewById(R.id.rvActiveJobs);
        rvHistoryJobs = (RecyclerView) view.findViewById(R.id.rvHistoryJobs);

        mContext = getActivity();
        mApiService = UtilsApi.getAPIService();

        activeJobsAdapter = new ActiveJobsAdapter(mContext, activejobsItemList);
        historyJobsAdapter = new HistoryJobsAdapter(mContext,historyjobsItemList);

        RecyclerView.LayoutManager mLayoutManagerActive = new LinearLayoutManager(mContext);
        RecyclerView.LayoutManager mLayoutManagerHistory = new LinearLayoutManager(mContext);
        rvActiveJobs.setLayoutManager(mLayoutManagerActive);
        rvActiveJobs.setItemAnimator(new DefaultItemAnimator());
        rvHistoryJobs.setLayoutManager(mLayoutManagerHistory);
        rvHistoryJobs.setItemAnimator(new DefaultItemAnimator());

        getResultListJobs();





        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }
//
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        mListener = null;
//    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void getResultListJobs(){
        loading = ProgressDialog.show(mContext, null, "Processing Request..", true, false);

        mApiService.getJobsRequest(id_user).enqueue(new Callback<ResponseJobs>() {
            @Override
            public void onResponse(Call<ResponseJobs> call, Response<ResponseJobs> response) {
                if (response.isSuccessful()){
                    loading.dismiss();
                    //Untuk Active Jobs
                    final List<ActivejobsItem> activeJobsItems = response.body().getActivejobs();

                    rvActiveJobs.setAdapter(new ActiveJobsAdapter(mContext, activeJobsItems));
                    activeJobsAdapter.notifyDataSetChanged();

                    //Untuk History Jobs
                    final List<HistoryjobsItem> historyjobsItems = response.body().getHistoryjobs();

                    rvHistoryJobs.setAdapter(new HistoryJobsAdapter(mContext, historyjobsItems));
                    historyJobsAdapter.notifyDataSetChanged();


                } else {
                    loading.dismiss();
                    Toasty.error(mContext, "Request Failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ResponseJobs> call, Throwable t) {
                loading.dismiss();
                Toasty.error(mContext, "No Connection", Toast.LENGTH_SHORT).show();
            }
        });
    }






}