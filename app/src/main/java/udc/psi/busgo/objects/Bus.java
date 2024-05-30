package udc.psi.busgo.objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Bus implements Parcelable {
    private int busId, stopId, state, direction;
    private double distance;

    // state es 0 == Parado, 1 == yendo a la siguiente parada
    public Bus(int busId, int stopId, double distance, int state, int direction ){
        this.busId = busId;
        this.stopId = stopId;
        this.distance = distance;
        this.state = state;
        this.direction = direction;
    }

    protected Bus(Parcel in) {
        busId = in.readInt();
        stopId = in.readInt();
        distance = in.readDouble();
        state = in.readInt();
        direction = in.readInt();
    }

    public static final Creator<Bus> CREATOR = new Creator<Bus>() {
        @Override
        public Bus createFromParcel(Parcel in) {
            return new Bus(in);
        }

        @Override
        public Bus[] newArray(int size) {
            return new Bus[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
        dest.writeInt(busId);
        dest.writeInt(stopId);
        dest.writeDouble(distance);
        dest.writeInt(state);
        dest.writeInt(direction);
    }

    public int getBusId() {
        return busId;
    }

    public void setBusId(int busId) {
        this.busId = busId;
    }

    public int getStopId() {
        return stopId;
    }

    public void setStopId(int stopId) {
        this.stopId = stopId;
    }

    public int getstate() {
        return state;
    }

    public void setstate(int state) {
        this.state = state;
    }

    public int getdirection() {
        return direction;
    }

    public void setdirection(int direction) {
        this.direction = direction;
    }

    public double getdistance() {
        return distance;
    }

    public void setdistance(double distance) {
        this.distance = distance;
    }
}
