package udc.psi.busgo;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.google.android.gms.maps.MapFragment;

import udc.psi.busgo.tabs.HomeTab;
import udc.psi.busgo.tabs.LinesTab;
import udc.psi.busgo.tabs.MapTab;
import udc.psi.busgo.tabs.SettingsTab;
import udc.psi.busgo.tabs.StopsTab;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "_TAG View Pager Adapter";

    LineDetail lineDetail;
    StopDetail stopDetail;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        lineDetail = new LineDetail();
        stopDetail = new StopDetail();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                Log.d(TAG, "Creacion de tab 0");
                return new MapTab();
            case 1:
                Log.d(TAG, "Creacion de tab 1");
                return new StopsTab();
            case 2:
                Log.d(TAG, "Creacion de tab 2");
                return new LinesTab();
            case 3:
                Log.d(TAG, "Creacion de tab 3");
                return new SettingsTab();
            case 4:
                Log.d(TAG, "Creacion de tab 4");
                return lineDetail;
            case 5:
                Log.d(TAG, "Creacion de tab 5");
                return stopDetail;
            default:
                Log.d(TAG, "Creacion de tab default");
                return new MapTab();
        }
    }


    public void setLineDetail(LineDetail lineDetail){
        this.lineDetail = lineDetail;
        notifyItemChanged(5);
    }

    public void setStopDetail(StopDetail stopDetail){
        this.stopDetail = stopDetail;
        notifyItemChanged(6);
    }
    @Override
    public int getItemCount() {
        return 6;
    }

}
