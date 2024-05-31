package udc.psi.busgo;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import udc.psi.JsonUtils;

public class WorkerMap extends Worker {
    private static final String TAG = "WorkerMap";

    public WorkerMap(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    private void searchAllStops(CountDownLatch latch) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/paradas",
                null,
                response -> {
                    Log.d(TAG, response.toString());
                    if (JsonUtils.saveJsonToFile(getApplicationContext(), "paradas.json", response)) {
                        Log.d(TAG, "File saved successfully");

                        String[] fileList = getApplicationContext().fileList();
                        StringBuilder fileListString = new StringBuilder("Files in internal storage:\n");
                        for (String fileName : fileList) {
                            fileListString.append(fileName).append("\n");
                        }
                        Log.d(TAG, fileListString.toString());
                    } else {
                        Log.d(TAG, "Error saving file");
                    }
                    latch.countDown(); // Signal that the request is complete
                },
                error -> {
                    Log.e(TAG, "Error fetching data", error);
                    latch.countDown(); // Signal that the request is complete
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d(TAG, "Background task executed");

        CountDownLatch latch = new CountDownLatch(1);

        searchAllStops(latch);

        try {
            // Espera 30 segundos a que se complete la peticion al servidor
            boolean completed = latch.await(30, TimeUnit.SECONDS);
            if (!completed) {
                Log.d(TAG, "Network request timeout");
                return Result.failure();
            }
        } catch (InterruptedException e) {
            Log.e(TAG, "Latch interrupted", e);
            return Result.failure();
        }

        String jsonString = JsonUtils.readJsonFromFile2(getApplicationContext(), "paradas.json");

        if (jsonString != null) {
            return Result.success();
        } else {
            Log.d(TAG, "Failed to read the saved file");
            return Result.failure();
        }
    }
}
