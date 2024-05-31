package udc.psi.busgo.objects;

import com.google.android.gms.maps.model.MarkerOptions;

// Clase auxiliar para mantener el marcador y su distancia
public class MarkerConDistancia {
    public MarkerOptions marker;
    public double distancia;

    public MarkerConDistancia(MarkerOptions marker, double distancia) {
        this.marker = marker;
        this.distancia = distancia;
    }
}