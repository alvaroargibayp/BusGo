package udc.psi.busgo.tabs;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import udc.psi.busgo.MainActivity;
import udc.psi.busgo.R;
import udc.psi.busgo.databinding.FragmentMapTabBinding;
import udc.psi.busgo.databinding.FragmentStopsTabBinding;
import udc.psi.busgo.widgets.StopsWidget;


public class MapTab extends Fragment implements View.OnClickListener {

    Button stop1Button;
    Button stop2Button;
    Button stop3Button;

    FragmentMapTabBinding binding;

    private static final String ACTION_UPDATE_WIDGET = "ACTION_UPDATE_WIDGET";




    public MapTab() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMapTabBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        stop1Button = binding.buttonParada1;
        stop1Button.setOnClickListener(this);

        stop2Button = binding.buttonParada2;
        stop2Button.setOnClickListener(this);

        stop3Button = binding.buttonParada3;
        stop3Button.setOnClickListener(this);


        return view;
    }

    void createWidget(String stopName) {
        AppWidgetManager mAppWidgetManager = requireActivity().getSystemService(AppWidgetManager.class);

        ComponentName myProvider = new ComponentName(requireActivity().getApplicationContext(), StopsWidget.class);

        Bundle b = new Bundle();
        b.putString("ggg", "ggg");

        //Comprueba si el lanzador sopporta la creación de widgets
        if (mAppWidgetManager.isRequestPinAppWidgetSupported()) {
            Intent pinnedWidgetCallbackIntent = new Intent(requireActivity().getApplicationContext(), StopsWidget.class);
            //pinnedWidgetCallbackIntent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            pinnedWidgetCallbackIntent.setAction(ACTION_UPDATE_WIDGET);

            pinnedWidgetCallbackIntent.putExtra("KEY_STOP_NAME", stopName);
            PendingIntent successCallback = PendingIntent.getBroadcast(requireActivity().getApplicationContext(), 0,
                    pinnedWidgetCallbackIntent, PendingIntent.FLAG_IMMUTABLE);


            mAppWidgetManager.requestPinAppWidget(myProvider, b, successCallback);

            requireActivity().getApplicationContext().sendBroadcast(pinnedWidgetCallbackIntent);

        }
    }

    @Override
    public void onClick(View v) {
        if (v == stop1Button) {
            createWidget("Campus elviña");
        }
        if (v == stop2Button) {
            createWidget("Paxariñas");
        }
        if (v == stop3Button) {
            createWidget("Antonio Insua Rivas, 53");
        }
    }
}