package in.squill.squilloffice;

import android.graphics.Color;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;

import in.squill.squilloffice.data.util.BottomNavigationViewHelper;
import in.squill.squilloffice.fragments.BaseFragment;
import in.squill.squilloffice.fragments.BookmarkFragment;
import in.squill.squilloffice.fragments.DiscoverFragment;
import in.squill.squilloffice.fragments.TrendingFragment;
import in.squill.squilloffice.service.NetworkUtil;

public class LandingPageActivity extends AbstractAppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BaseFragment trendingFragment;
    private BaseFragment discoverFragment;
    private Fragment specialFragment;

    private Fragment activeFragment;

    private boolean networkConnected = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bottom_navigation);

        networkConnected = NetworkUtil.checkConnectivity(LandingPageActivity.this);

        trendingFragment = new TrendingFragment();
        loadFragment(trendingFragment);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        BottomNavigationViewHelper.disableShiftMode(navigation);
        navigation.setOnNavigationItemSelectedListener(this);

    }

    private boolean loadFragment(Fragment fragment) {
        if (fragment != null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, fragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_tending:
                if(trendingFragment == null) {
                    trendingFragment = new TrendingFragment();
                }
                activeFragment = trendingFragment;
                break;

            case R.id.navigation_article:
                if(discoverFragment == null) {
                    discoverFragment = new DiscoverFragment();
                }
                activeFragment = discoverFragment;
                break;

            case R.id.navigation_bookmark:
                if(specialFragment == null) {
                    specialFragment = new BookmarkFragment();
                }
                activeFragment = specialFragment;
                break;
        }

        return loadFragment(activeFragment);
    }

    private Snackbar err;

    private void fireNetworkStateChange(boolean isPrevConnected, boolean isNowConnected) {
        if(activeFragment != null && (activeFragment instanceof BaseFragment)) {
            ((BaseFragment)activeFragment).onNetworkStateChange(isPrevConnected, isNowConnected);
        }
    }

    @Override
    public void onNetworkConnect() {
        if(this.err != null && this.err.isShown()) {
            this.err.dismiss();
        }
        if(networkConnected)
            return;
        networkConnected = true;
        fireNetworkStateChange(false, true);
    }

    @Override
    public void onNetworkDisconnect() {
        if(!networkConnected)
            return;
        networkConnected = false;
        View mainLayout = findViewById(R.id.fragment_container);
        if(mainLayout != null && (this.err == null || !this.err.isShown())) {
            this.err = Snackbar.make(mainLayout, "Internet Connection Lost",
                    Snackbar.LENGTH_LONG);
            View errView = this.err.getView();
            CoordinatorLayout.LayoutParams params =(CoordinatorLayout.LayoutParams)errView.getLayoutParams();
            params.gravity = Gravity.TOP;
            errView.setLayoutParams(params);
            errView.setBackgroundColor(Color.RED);
            this.err.show();
        }
        fireNetworkStateChange(true, false);
    }
}