package com.example.codetrack;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {


    private String URL = "https://contesttrackerapi.herokuapp.com/";
    RequestQueue mContestQueue;
    private Context mContext;
    private ContestAdapter mAdapter;
    private RecyclerView mRecyclerView;

    private RadioGroup mRadioGroup;
    private RadioButton mRadioButton, mRadioButton1;

    private LinearLayout linearLayoutContest, linearLayoutNoContest;
    private ProgressBar progressBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = findViewById(R.id.recycler_view);
        mRadioGroup = findViewById(R.id.radio_group);
        mRadioButton1 = findViewById(R.id.radio_ongoing);
        mRadioButton1.setChecked(true);
        linearLayoutContest = findViewById(R.id.layout_contest);
        linearLayoutNoContest = findViewById(R.id.layout_no_contest);
        progressBar = findViewById(R.id.progress);







        //CHECK FOR UPCOMING AND ON GOING
        mRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @SuppressLint("ResourceType")
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                mRadioButton = radioGroup.findViewById(i);
                if (mRadioButton != null && i > -1) {
                    if (mRadioButton.getText().equals("Ongoing")) {
                        setData(1); //1 for ongoing
                    } else if (mRadioButton.getText().equals("Upcoming")) {
                        setData(2); //2 for upcoming
                    }
                }
            }
        });
        setData(1);

        //CHECK FOR DATA CONNECTION
        ConnectivityManager conMag = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (conMag.getActiveNetworkInfo() != null && conMag.getActiveNetworkInfo().isAvailable() && conMag.getActiveNetworkInfo().isConnected()) {
            Log.i("INTERNET", "CONNECTED");
        } else {
            Log.i("INTERNET", "NOT CONNECTION");
            progressBar.setVisibility(View.GONE);
            linearLayoutNoContest.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Check INTERNET Connection", Toast.LENGTH_SHORT).show();
        }
    }


    void setData(final int pos) {

        linearLayoutContest.setVisibility(View.GONE);
        progressBar.setVisibility(View.VISIBLE);
        mContestQueue = Volley.newRequestQueue(this);
        final ArrayList<Contest> list = new ArrayList<>();
        JsonObjectRequest objectRequest = new JsonObjectRequest(Request.Method.GET, URL, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            Log.d("getxxx", URL);

                            System.out.println("Response when HTTPCONNECT" + response);

                            String jsonData = response.toString();

                            JSONObject obj1 = new JSONObject(jsonData);
                            JSONObject obj = obj1.getJSONObject("result");
                            JSONArray arr1 = obj.getJSONArray("ongoing");
                            JSONArray arr2 = obj.getJSONArray("upcoming");

                            if (pos == 1) {

                                if (arr1.length() == 0) {
                                    linearLayoutContest.setVisibility(View.GONE);
                                    linearLayoutNoContest.setVisibility(View.VISIBLE);
                                    progressBar.setVisibility(View.GONE);
                                } else {
                                    linearLayoutContest.setVisibility(View.VISIBLE);
                                    linearLayoutNoContest.setVisibility(View.GONE);
                                    for (int i = 0; i < arr1.length(); i++) {
                                        String Name = arr1.getJSONObject(i).getString("Name");
                                        String Platform = arr1.getJSONObject(i).getString("Platform");
                                        String EndTime = arr1.getJSONObject(i).getString("EndTime");
                                        String url = arr1.getJSONObject(i).getString("url");
                                        Log.i("URL1", url);

                                        list.add(new Contest(Name, EndTime, Platform, url, "", ""));
                                    }

                                }
                            } else if (pos == 2) {

                                if (arr2.length() == 0) {
                                    linearLayoutContest.setVisibility(View.GONE);
                                    linearLayoutNoContest.setVisibility(View.VISIBLE);
                                } else {
                                    linearLayoutContest.setVisibility(View.VISIBLE);
                                    linearLayoutNoContest.setVisibility(View.GONE);
                                    for (int i = 0; i < arr2.length(); i++) {
                                        String Name = arr2.getJSONObject(i).getString("Name");
                                        String Platform = arr2.getJSONObject(i).getString("Platform");
                                        String EndTime = arr2.getJSONObject(i).getString("EndTime");
                                        String url = arr2.getJSONObject(i).getString("url");
                                        String Duration = arr2.getJSONObject(i).getString("Duration");
                                        String StartTime = arr2.getJSONObject(i).getString("StartTime");
                                        list.add(new Contest(Name, EndTime, Platform, url, Duration, StartTime));

                                    }
                                }
                            }
                            mAdapter = new ContestAdapter(list, getApplicationContext(), mRecyclerView);
                            LinearLayoutManager layoutManager = new LinearLayoutManager(MainActivity.this);
                            layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
                            mRecyclerView.setHasFixedSize(true);
                            mRecyclerView.setNestedScrollingEnabled(true);
                            mRecyclerView.setLayoutManager(layoutManager);
                            mRecyclerView.setItemAnimator(new DefaultItemAnimator());
                            progressBar.setVisibility(View.GONE);
                            mRecyclerView.setAdapter(mAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });
        mContestQueue.add(objectRequest);


    }
}
