package udc.psi.busgo.userGuide;

import android.graphics.Point;
import android.graphics.Rect;
import android.view.View;

import com.github.amlcurran.showcaseview.targets.Target;

public class CustomViewTarget implements Target {
    private final View view;
    private final int padding;

    public CustomViewTarget(View view, int padding) {
        this.view = view;
        this.padding = padding;
    }

    @Override
    public Point getPoint() {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Point(location[0] + view.getWidth() / 2, location[1] + view.getHeight() / 2);
    }

    public Rect getBounds() {
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        return new Rect(location[0] - padding, location[1] - padding, location[0] + view.getWidth() + padding, location[1] + view.getHeight() + padding);
    }
}