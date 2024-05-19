package udc.psi.busgo.tabs;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import udc.psi.busgo.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MapTab#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MapTab extends Fragment {
    private static final String GOOGLE_MAPS_URL = "https://openlayers.org/en/latest/examples/mobile-full-screen.html";

    WebView mapWebView;
    public MapTab() {
        // Required empty public constructor
    }

    public static MapTab newInstance(String param1, String param2) {
        MapTab fragment = new MapTab();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public void openWebView(String url) {
        mapWebView.setWebViewClient(new WebViewClient());
        mapWebView.loadUrl(url);

        WebSettings webSettings = mapWebView.getSettings();
        webSettings.setJavaScriptEnabled(true);
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map_tab, container, false);
        mapWebView = (WebView) view.findViewById(R.id.map_webview);

        openWebView(GOOGLE_MAPS_URL);

        return view;


    }
}