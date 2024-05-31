package udc.psi.busgo.objects;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.annotation.NonNull;

public class Route implements Parcelable {
    private Line line;
    private Stop originStop;
    private Stop destinationStop;

    public Route(Line line, Stop originStop, Stop destinationStop) {
        this.line = line;
        this.originStop = originStop;
        this.destinationStop = destinationStop;
    }


    protected Route(Parcel in) {

    }

    public static final Creator<Route> CREATOR = new Creator<Route>() {
        @Override
        public Route createFromParcel(Parcel in) {
            return new Route(in);
        }

        @Override
        public Route[] newArray(int size) {
            return new Route[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(@NonNull Parcel dest, int flags) {
    }

    public void setLine(Line line) {
        this.line = line;
    }

    public void setOriginStop(Stop originStop) {
        this.originStop = originStop;
    }

    public void setDestinationStop(Stop destinationStop) {
        this.destinationStop = destinationStop;
    }

    public Line getLine() {
        return line;
    }

    public Stop getOriginStop() {
        return originStop;
    }

    public Stop getDestinationStop() {
        return destinationStop;
    }
}
