package udc.psi.busgo;

import static udc.psi.busgo.StringUtils.compress;

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
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;


public class WorkerMap extends Worker {
    public static final String RECIVE_TASK_KEY="recive_massage";

    public interface SearchStopsCallback {
        void onSearchStops(String parades);

    }
    String jsonLatLngList;

    String jsonLatLngList2;


    public WorkerMap(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }


    void searchAllStops(final SearchStopsCallback interfaceName) {
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.GET,
                "https://bus.delthia.com/api/paradas",
                null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("_TAG", response.toString());
                        try {
                            List<LatLng> stopsCoordsList = new ArrayList<>();

                            for (int i = 0; i < response.getJSONArray("paradas").length(); i++){
                                JSONObject currentObject = (JSONObject) response.getJSONArray("paradas").get(i);

                                double coordsLat = currentObject.getJSONArray("coords").getDouble(1);
                                double coordsLong = currentObject.getJSONArray("coords").getDouble(0);

                                LatLng coords = new LatLng(coordsLat, coordsLong);
                                Log.d("_TAG2", coords.toString());

                                stopsCoordsList.add(coords);
                                Log.d("_TAG3", stopsCoordsList.toString());
                            }


                            Gson gson = new Gson();
                            jsonLatLngList = gson.toJson(stopsCoordsList);


                            interfaceName.onSearchStops(jsonLatLngList);

                            Log.d("_TAG2", jsonLatLngList);

                        } catch (JSONException e) {
                            throw new RuntimeException(e);
                        }

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // Maneja errores aquí
                    }
                }
        );

        requestQueue.add(jsonObjectRequest);
    }

    @NonNull
    @Override
    public Result doWork() {
        Log.d("MyWorker", "Background task executed");

        CountDownLatch countDownLatch = new CountDownLatch(1);




        searchAllStops(new SearchStopsCallback() {
            @Override
            public void onSearchStops(String parades) {
                jsonLatLngList2 = parades;
                countDownLatch.countDown();
            }
        });


        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }


        try {
            String compressedString = compress(jsonLatLngList2);
            Data outputData = new Data.Builder()
                    .putString(RECIVE_TASK_KEY, compressedString)
                    .build();
            Log.d("_TAG2", "bbbb: " + compressedString);


            return Result.success(outputData);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }



    }






}
