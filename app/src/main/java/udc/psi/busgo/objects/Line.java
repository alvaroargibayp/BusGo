package udc.psi.busgo.objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Line implements Parcelable {

    private static final String TAG = "_TAG Line";
    private String color, destination, name, origin;
    private int id;

    public Line(String color, String destination, String name, String origin, int id) {
        this.color = color;
        this.destination = destination;
        this.name = name;
        this.origin = origin;
        this.id = id;
    }

    protected Line(Parcel in) {
        color = in.readString();
        destination = in.readString();
        name = in.readString();
        origin = in.readString();
        id = in.readInt();
    }

    public static final Creator<Line> CREATOR = new Creator<Line>() {


        @Override
        public Line createFromParcel(Parcel in) {
            return new Line(in);
        }

        @Override
        public Line[] newArray(int size) {
            return new Line[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeString(color);
        dest.writeString(destination);
        dest.writeString(name);
        dest.writeString(origin);
        dest.writeInt(id);
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
