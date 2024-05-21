package udc.psi.busgo.objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Stop implements Parcelable{

    private int[] coords, osmcoords;
    private Line[] lines;
    private String name;
    private int id;
    private long osmid;
    private boolean[] properties;

    public Stop(int[] coords , int id, Line[] lines, String name, int[] osmcoords, long osmid, boolean[] properties) {
        this.coords = coords;
        this.lines = lines;
        this.name = name;
        this.osmcoords = osmcoords;
        this.osmid = osmid;
        this.properties = properties;
        this.id = id;
    }

    // Sin Lines, ni Properties, ni Osmcoords
    public Stop(int[] coords , int id, String name, long osmid) {
        this.coords = coords;
        this.name = name;
        this.osmid = osmid;
        this.id = id;
    }

    protected Stop(Parcel in) {
        coords = in.createIntArray();
        id = in.readInt();
        lines = in.createTypedArray(Line.CREATOR);
        name = in.readString();
        osmcoords = in.createIntArray();
        properties = in.createBooleanArray();
    }

    public static final Creator<Stop> CREATOR = new Creator<Stop>() {


        @Override
        public Stop createFromParcel(Parcel in) {
            return new Stop(in);
        }

        @Override
        public Stop[] newArray(int size) {
            return new Stop[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeIntArray(coords);
        dest.writeInt(id);
        dest.writeTypedArray(lines, flags);
        dest.writeString(name);
        dest.writeIntArray(osmcoords);
        dest.writeLong(osmid);
        dest.writeBooleanArray(properties);
    }

    public int[] getCoords() {
        return coords;
    }

    public int getId() {
        return id;
    }

    public long getOsmid() {
        return osmid;
    }

    public boolean[] getProperties() {
        return properties;
    }

    public int[] getOsmcoords() {
        return osmcoords;
    }

    public Line[] getLines() {
        return lines;
    }

    public String getName() {
        return name;
    }
}
