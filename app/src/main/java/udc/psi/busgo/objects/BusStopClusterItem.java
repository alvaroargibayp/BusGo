package udc.psi.busgo.objects;

import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class BusStopClusterItem implements ClusterItem {
    private final LatLng position;
    private final String title;
    private final String snippet;
    public BusStopClusterItem(double lat, double lng, String title, String snippet) {
        position = new LatLng(lat, lng);
        this.title = title;
        this.snippet = snippet;
    }

    @Override
    public LatLng getPosition() {
        return position;
    }

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public String getSnippet() {
        return snippet;
    }

    @Nullable
    public Float getZIndex() {
        return 0f;
    }
}
