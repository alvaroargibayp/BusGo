package udc.psi.busgo;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TabHost;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.json.JSONObject;

import udc.psi.busgo.databinding.ActivityMainBinding;
import udc.psi.busgo.tabs.HomeTab;
import udc.psi.busgo.tabs.LinesTab;
import udc.psi.busgo.tabs.MapTab;
import udc.psi.busgo.tabs.SettingsTab;
import udc.psi.busgo.tabs.StopsTab;

public class MainActivity extends AppCompatActivity{

    private ActivityMainBinding binding;
    private static final String TAG = "_TAG Main Activity";

    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;

    HomeTab homeTab;
    LinesTab linesTab;
    MapTab mapTab;
    SettingsTab settingsTab;
    StopsTab stopsTab;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        Log.d(TAG, "OnCreate");

        configureTabs();
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

}

