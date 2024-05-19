package udc.psi.busgo.tabs;

import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.os.LocaleListCompat;
import androidx.fragment.app.Fragment;

import android.os.LocaleList;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import java.util.Locale;

import udc.psi.busgo.R;
import udc.psi.busgo.databinding.FragmentSettingsTabBinding;
import udc.psi.busgo.databinding.FragmentStopsTabBinding;

public class SettingsTab extends Fragment implements RadioGroup.OnCheckedChangeListener {

    FragmentSettingsTabBinding fragmentSettingsTabBinding;

    private RadioGroup themeRadioGroup;

    private RadioGroup languageRadioGrouo;

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

        return view;
    }

    public void setLocale(String lang) {
        Locale myLocale = new Locale(lang);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        getActivity().recreate();
        /*Intent refresh = new Intent(this, AndroidLocalize.class);
        finish();
        startActivity(refresh);*/
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

            String newLocale = "";
            if (checkedId == R.id.settings_toggle_language_spanish) {
                newLocale = "es";
            }
            if (checkedId == R.id.settings_toggle_language_english) {
                newLocale = "en";
            }

            if (!currentLocale.toString().equals(newLocale))
                setLocale(newLocale);
        }

    }
}