package udc.psi.busgo.workers;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Data;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import udc.psi.busgo.StringUtils;
import udc.psi.busgo.objects.Bus;

public class JSONRequestWorker extends Worker {
    private final String TAG = "_TAG JSON Request Worker";
    public static final String JSON_REQUEST_WORKER_URL = "jsonRequestWorkerUrl";
    public static final String JSON_REQUEST_WORKER_OUTPUT = "jsonRequestWorkerOutput";
    private CountDownLatch countDownLatch;
    private String result;

    public JSONRequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    void searchURLJson(String url, Context context){
        Log.d(TAG, "Search: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Log.d(TAG, response.toString());
                        result = response.toString();
                        endRequest();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);

    }

    private void endRequest(){
        countDownLatch.countDown();

    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        countDownLatch = new CountDownLatch(1);

        String url = getInputData().getString(JSON_REQUEST_WORKER_URL);
        searchURLJson(url, context);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try {
            String compressResulta = StringUtils.compress(result);
            Data outputData = new Data.Builder().putString(JSON_REQUEST_WORKER_OUTPUT,compressResulta).build();
            return Result.success(outputData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }


    }
}
