package com.thoughtworks.myapplication;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Spinner;
import android.widget.AdapterView;
import com.thoughtworks.myapplication.domain.PM25;
import com.thoughtworks.myapplication.service.AirServiceClient;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MainActivity extends AppCompatActivity {

    private TextView pm25TextView;
    private TextView airQualityTextView;
    private ProgressDialog loadingDialog;
    private Spinner spinner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        pm25TextView = (TextView) findViewById(R.id.text_view_pm25);
        airQualityTextView = (TextView) findViewById(R.id.textView_quality);
        spinner=(Spinner) findViewById(R.id.spinner);

        loadingDialog = new ProgressDialog(this);
        loadingDialog.setMessage(getString(R.string.loading_message));
        final Map<String , String> map = new HashMap<String , String>(){{
            put("西安","xian");
            put("北京","beijing");
            put("上海","shanghai");
            put("广州","guangzhou");
            put("成都","chengdu");
        }};



        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int position, long id) {
                Spinner spinner = (Spinner) parent;
                String city = map.get(spinner.getItemAtPosition(position).toString());
                onQueryPM25Click(city);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }

        });
    }

    private void onQueryPM25Click(String city) {
        if (!TextUtils.isEmpty(city)) {
            showLoading();
            AirServiceClient.getInstance().requestPM25(city, new Callback<List<PM25>>() {
                @Override
                public void onResponse(Response<List<PM25>> response, Retrofit retrofit) {
                    showSuccessScreen(response);
                }

                @Override
                public void onFailure(Throwable t) {
                    showErrorScreen();
                }
            });
        }
    }

    private void showSuccessScreen(Response<List<PM25>> response) {
        hideLoading();
        if (response != null) {
            populate(response.body());
        }
    }

    private void showErrorScreen() {
        hideLoading();
        pm25TextView.setText(R.string.error_message_query_pm25);
    }

    private void showLoading() {
        loadingDialog.show();
    }

    private void hideLoading() {
        loadingDialog.dismiss();
    }

    private void populate(List<PM25> data) {
        if (data != null && !data.isEmpty()) {
            PM25 pm25 = data.get(data.size()-1);
            pm25TextView.setText(pm25.getPm25());
            airQualityTextView.setText(pm25.getQuality());
        }
    }
}
