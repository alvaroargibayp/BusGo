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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import udc.psi.busgo.StringUtils;

public class StringRequestWorker extends Worker {
    private final String TAG = "_TAG String Request Worker";
    public static final String STRING_REQUEST_WORKER_URL = "stringRequestWorkerUrl";
    public static final String STRING_REQUEST_WORKER_OUTPUT = "stringRequestWorkerOutput";
    private CountDownLatch countDownLatch;
    private String result;

    public StringRequestWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    void searchURLString(String url, Context context){
        Log.d(TAG, "Search: " + url);
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        StringRequest stringRequest = new StringRequest(
                Request.Method.GET,
                url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        //Log.d(TAG, response);
                        String prefijo = "var paradas = ";
                        if (response.startsWith(prefijo)) {
                            String respuestaRecortada = response.substring(prefijo.length());
                            if (respuestaRecortada.endsWith(";")) {
                                respuestaRecortada = respuestaRecortada.substring(0, respuestaRecortada.length()-1);
                            }
                            Log.d(TAG, respuestaRecortada);
                            result = respuestaRecortada;
                            endRequest();
                        } else {
                            result = response;
                            endRequest();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }
        );

        requestQueue.add(stringRequest);

    }

    private void endRequest(){
        countDownLatch.countDown();

    }

    @NonNull
    @Override
    public Result doWork() {
        Context context = getApplicationContext();
        countDownLatch = new CountDownLatch(1);

        String url = getInputData().getString(STRING_REQUEST_WORKER_URL);
        searchURLString(url, context);

        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        try {
            String compressResulta = StringUtils.compress(result);
            Data outputData = new Data.Builder().putString(STRING_REQUEST_WORKER_OUTPUT,compressResulta).build();
            return Result.success(outputData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
