package udc.psi.busgo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import udc.psi.busgo.tabs.HomeTab;
import udc.psi.busgo.tabs.LinesTab;
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
                return new MapTab();
            case 1:
                return new StopsTab();
            case 2:
                return new HomeTab();
            case 3:
                return new LinesTab();
            case 4:
                return new SettingsTab();
            case 5:
                return lineDetail;
            case 6:
                return stopDetail;
            default:
                return new HomeTab();
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
        return 7;
    }

}
