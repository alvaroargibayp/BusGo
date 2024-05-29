package udc.psi.busgo.objects;

import android.content.Context;
import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;

public class BusStopClusterRenderer extends DefaultClusterRenderer<BusStopClusterItem> {

    public BusStopClusterRenderer(Context context, GoogleMap map, ClusterManager<BusStopClusterItem> clusterManager) {
        super(context, map, clusterManager);
    }

    @Override
    protected int getColor(int clusterSize) {
        return Color.MAGENTA;// Return any color you want here. You can base it on clusterSize.
    }

    @Override
    protected void onBeforeClusterItemRendered(BusStopClusterItem item, MarkerOptions markerOptions) {
        super.onBeforeClusterItemRendered(item, markerOptions);
        // Cambiar el color del marcador a violeta
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_VIOLET));
    }
}