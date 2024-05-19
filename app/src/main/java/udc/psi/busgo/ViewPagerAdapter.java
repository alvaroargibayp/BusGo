package udc.psi.busgo;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import udc.psi.busgo.tabs.HomeTab;
import udc.psi.busgo.tabs.LinesTab;
import udc.psi.busgo.tabs.MapFragment;
import udc.psi.busgo.tabs.MapTab;
import udc.psi.busgo.tabs.SettingsTab;
import udc.psi.busgo.tabs.StopsTab;

public class ViewPagerAdapter extends FragmentStateAdapter {

    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity) {
        super(fragmentActivity);
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        switch (position){
            case 0:
                return new MapFragment();
            case 1:
                return new StopsTab();
            case 2:
                return new HomeTab();
            case 3:
                return new LinesTab();
            case 4:
                return new SettingsTab();
            default:
                return new HomeTab();
        }
    }

    @Override
    public int getItemCount() {
        return 5;
    }

}
