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
    LinesTab.DetailSelection detailSelection;

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity, LinesTab.DetailSelection detailSelection) {
        super(fragmentActivity);
        this.detailSelection = detailSelection;
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
                return new HomeTab();
            case 3:
                LinesTab linesTab = new LinesTab();
                linesTab.setDetailSelection(detailSelection);
                return linesTab;
            case 4:
                return new SettingsTab();
            case 5:
                return lineDetail;
            default:
                return new HomeTab();
        }
    }


    public void setLineDetail(LineDetail lineDetail){
        this.lineDetail = lineDetail;
        notifyItemChanged(5);
    }
    @Override
    public int getItemCount() {
        return 6;
    }

}
