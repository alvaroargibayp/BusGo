package udc.psi.busgo.tabs;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import android.content.SharedPreferences;


import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;

import java.util.Locale;
import java.util.Objects;

import udc.psi.busgo.R;
import udc.psi.busgo.databinding.FragmentSettingsTabBinding;

public class SettingsTab extends Fragment implements RadioGroup.OnCheckedChangeListener, View.OnClickListener {

    FragmentSettingsTabBinding fragmentSettingsTabBinding;

    private static final String TAG = "_TAG Setttings Tab";

    private RadioGroup themeRadioGroup;

    private RadioGroup languageRadioGrouo;

    private Button userGuideButton;

    String newLocale = "";

    final String PREFS_NAME = "MyPrefsFile";


    public SettingsTab() {
        // Required empty public constructor

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentSettingsTabBinding = FragmentSettingsTabBinding.inflate(inflater, container, false);
        View view = fragmentSettingsTabBinding.getRoot();

        themeRadioGroup = fragmentSettingsTabBinding.radioGroupThemeId;
        themeRadioGroup.setOnCheckedChangeListener(this);

        languageRadioGrouo = fragmentSettingsTabBinding.radioGroupLanguageId;
        languageRadioGrouo.setOnCheckedChangeListener(this);

        userGuideButton = fragmentSettingsTabBinding.settingsShowUserGuideButtonId;
        userGuideButton.setOnClickListener(this);

        setDefaultConfig();



        return view;
    }

    void setDefaultConfig() {
        newLocale = "es";
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getActivity().recreate();
    }

    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        if (group == themeRadioGroup) {
            if (checkedId == R.id.settings_toggle_theme_whiteTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
            }
            if (checkedId == R.id.settings_toggle_theme_blackTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            }
            if (checkedId == R.id.settings_toggle_theme_systemTheme) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
            }
        }

        if (group == languageRadioGrouo) {
            LocaleList localeList = getResources().getConfiguration().getLocales();
            Locale currentLocale = localeList.get(0);

            if (checkedId == R.id.settings_toggle_language_spanish) {
                newLocale = "es";
            }
            if (checkedId == R.id.settings_toggle_language_english) {
                newLocale = "en";
            }
            if (checkedId == R.id.settings_toggle_language_galician) {
                newLocale = "gl";
            }

            if (!currentLocale.toString().equals(newLocale))
                setLocale(newLocale);
        }

    }

    @Override
    public void onClick(View v) {
        if (v == userGuideButton) {
            SharedPreferences settings = requireActivity().getSharedPreferences(PREFS_NAME, 0);
            settings.edit().putBoolean("my_first_time", true).apply(); // Cambia la variable a false
            getActivity().recreate();
        }
    }

}