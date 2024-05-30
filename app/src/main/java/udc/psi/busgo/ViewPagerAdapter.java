package udc.psi.busgo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.List;

import udc.psi.busgo.objects.Line;
import udc.psi.busgo.tabs.HomeTab;
import udc.psi.busgo.tabs.LinesTab;
import udc.psi.busgo.tabs.MapFragment;
import udc.psi.busgo.tabs.MapTab;
import udc.psi.busgo.tabs.SettingsTab;
import udc.psi.busgo.tabs.StopsTab;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private static final String TAG = "_TAG View Pager Adapter";

    LineDetail lineDetail;
    StopDetail stopDetail;
    LinesTab.LineDetailSelection detailSelection;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
        lineDetail = new LineDetail();
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position) {
            case 0:
                return new MapFragment();
            case 1:
                return new StopsTab();
            case 2:
                return new LinesTab();
            case 3:
                return new SettingsTab();
            case 4:
                return lineDetail;
            case 5:
                return stopDetail;
            default:
                return new MapFragment();
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
