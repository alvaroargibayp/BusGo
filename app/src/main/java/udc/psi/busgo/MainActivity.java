package udc.psi.busgo;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import udc.psi.busgo.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity{
    private ActivityMainBinding binding;
    private static final String TAG = "_TAG";

    TabLayout tabLayout;
    ViewPager2 viewPager;
    ViewPagerAdapter viewPagerAdapter;
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

}

