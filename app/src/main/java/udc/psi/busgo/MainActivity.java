package udc.psi.busgo;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import udc.psi.busgo.databinding.ActivityMainBinding;
import udc.psi.busgo.tabs.MapFragment;

public class MainActivity extends AppCompatActivity implements MapFragment.OnMapClickedListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private ActivityMainBinding binding;
    private static final String TAG = "_TAG";

    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    boolean mLocationPermissionGranted;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Log.d(TAG, "OnCreate");


        checkLocationPermission();

        configureTabs();
    }

    private void checkLocationPermission() {
        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PERMISSION_GRANTED) {
            mLocationPermissionGranted = true;
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[] { android.Manifest.permission.ACCESS_FINE_LOCATION }, PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
        }

        if (!mLocationPermissionGranted) {
            Log.d("_TAG", "Error loading Navigation SDK: The user has not granted location permission.");
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        mLocationPermissionGranted = false;
        if (requestCode == PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION) {// If request is canceled, the result arrays are empty.
            if (grantResults.length > 0
                    && grantResults[0] == PERMISSION_GRANTED) {
                mLocationPermissionGranted = true;
            }
        }
    }

    void configureTabs(){

        tabLayout = binding.tabLayout;
        viewPager = binding.viewPager;

        viewPagerAdapter = new ViewPagerAdapter(this);
        viewPager.setAdapter(viewPagerAdapter);



        // Comportamiento de las pestañas
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // Desactivar el poder deslizar para cambiar de pestaña
        viewPager.setUserInputEnabled(false);

        // Si se quiere reaactivar el desplazamento por deslizamiento se necesita descomentar este
        // código para sincronizar el tabLayout con el view pager
//        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
//            @Override
//            public void onPageSelected(int position) {
//                super.onPageSelected(position);
//                tabLayout.getTabAt(position).select();
//            }
//       });

        // Ajustar el tab y el pager para que la primera pantalla sea el home
        tabLayout.getTabAt(2).select();
        viewPager.setCurrentItem(2);
    }

    @Override
    public void onMapClicked(GoogleMap googleMap, LatLng latLng) {
        String[] options = {"Colocar punto de origen", "Colocar punto de destino"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Elige pibe");

        builder.setItems(options, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // the user clicked on colors[which]
                FragmentManager fragmentManager = getSupportFragmentManager();

                MapFragment mapFragment = (MapFragment) fragmentManager
                        .findFragmentByTag("f0");

                assert mapFragment != null;
                mapFragment.placeMarker(googleMap, latLng, which);
            }
        });

        // Mostrar el AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }
}

