package udc.psi.busgo;

import static android.content.pm.PackageManager.PERMISSION_GRANTED;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import udc.psi.busgo.databinding.ActivityMainBinding;
import udc.psi.busgo.tabs.HomeTab;
import udc.psi.busgo.tabs.LinesTab;
import udc.psi.busgo.tabs.MapTab;
import udc.psi.busgo.tabs.SettingsTab;
import udc.psi.busgo.tabs.StopsTab;

public class MainActivity extends AppCompatActivity implements MapTab.OnMapClickedListener {

    private static final int PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 0;
    private ActivityMainBinding binding;
    private static final String TAG = "_TAG Main Activity";

    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    boolean mLocationPermissionGranted;

    HomeTab homeTab;
    LinesTab linesTab;
    SettingsTab settingsTab;
    StopsTab stopsTab;

    ShowcaseView userGuide;
    final String PREFS_NAME = "MyPrefsFile";
    private int showcaseStep = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Log.d(TAG, "OnCreate");

        //checkLocationPermission();

        configureTabs();

        if (isFirstTimeOnApp()) // Comprobar si es la primera apertura de la aplicacion
            showShowcaseStep();

    }

    private boolean isFirstTimeOnApp() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        if (settings.getBoolean("my_first_time", true)) { // Si es la primera vez...
            settings.edit().putBoolean("my_first_time", false).apply(); // Cambia la variable a false
            return true;
        }
        return false;
    }

    private void showShowcaseStep() {
        ViewTarget target;
        String title;
        String text;

        switch (showcaseStep) {
            case 0:
                target = new ViewTarget(Objects.requireNonNull(tabLayout.getTabAt(showcaseStep)).view);
                title = getResources().getString(R.string.userGuideMapTabTitle);
                text = getResources().getString(R.string.userGuideMapTabText);
                break;
            case 1:
                target = new ViewTarget(Objects.requireNonNull(tabLayout.getTabAt(showcaseStep)).view);
                title = getResources().getString(R.string.userGuideStopsTabTitle);
                text = getResources().getString(R.string.userGuideStopsTabText);
                break;
            case 2:
                target = new ViewTarget(Objects.requireNonNull(tabLayout.getTabAt(showcaseStep)).view);
                title = getResources().getString(R.string.userGuideHomeTabTitle);
                text = getResources().getString(R.string.userGuideHomeTabText);
                break;
            case 3:
                target = new ViewTarget(Objects.requireNonNull(tabLayout.getTabAt(showcaseStep)).view);
                title = getResources().getString(R.string.userGuideLinesTabTitle);
                text = getResources().getString(R.string.userGuideLinesTabText);
                break;
            case 4:
                target = new ViewTarget(Objects.requireNonNull(tabLayout.getTabAt(showcaseStep)).view);
                title = getResources().getString(R.string.userGuideSettingsTabTitle);
                text = getResources().getString(R.string.userGuideSettingsTabText);
                break;
            default:
                return;
        }

        userGuide = new ShowcaseView.Builder(this, true)
                .setTarget(target)
                .setContentTitle(title)
                .setContentText(text)
                .hideOnTouchOutside()
                .setStyle(R.style.BusGoShowcaseTheme)
                .build();

        userGuide.forceTextPosition(ShowcaseView.BELOW_SHOWCASE);
        userGuide.setOnShowcaseEventListener(new OnShowcaseEventListener() {
            @Override
            public void onShowcaseViewHide(ShowcaseView showcaseView) {
                // Incrementar el paso y mostrar el siguiente ShowcaseView
                showcaseStep++;
                showShowcaseStep();

            }

            @Override
            public void onShowcaseViewDidHide(ShowcaseView showcaseView) {}

            @Override
            public void onShowcaseViewShow(ShowcaseView showcaseView) {}

            @Override
            public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {}
        });
    }



    public boolean checkLocationPermission() {
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
            return false;
        }
        return true;
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

        viewPagerAdapter = new ViewPagerAdapter(this, new LinesTab.DetailSelection() {
            @Override
            public void seeDetail(Fragment lineDetail) {
                viewPagerAdapter.setLineDetail((LineDetail) lineDetail);
                viewPager.setCurrentItem(6, false);
            }
        });
        viewPager.setAdapter(viewPagerAdapter);

        // Comportamiento de las pestañas
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d(TAG, "Seleccionada la pestaña " + tab.getPosition() + "\n Pestaña actual " + viewPager.getCurrentItem());
                if (viewPager.getCurrentItem() == 5){
                    viewPager.setCurrentItem(tab.getPosition(), false);
                } else{
                viewPager.setCurrentItem(tab.getPosition(), true);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition(), false);
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
        viewPager.setCurrentItem(2, false);
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

                MapTab mapTab = (MapTab) fragmentManager
                        .findFragmentByTag("f0");

                assert mapTab != null;
                mapTab.placeMarker(googleMap, latLng, which);
            }
        });

        // Mostrar el AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();

    }

}

