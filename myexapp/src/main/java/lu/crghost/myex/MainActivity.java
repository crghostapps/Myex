package lu.crghost.myex;

import android.app.ActionBar;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.*;
import android.widget.SearchView;


public class MainActivity extends FragmentActivity implements ActionBar.TabListener,
        AccountsFragment.OnFragmentInteractionListener,
        CostcentersFragment.OnFragmentInteractionListener,
        DebtorsFragment.OnFragmentInteractionListener,
        TransactionsFragment.OnFragmentInteractionListener {

    private static final int TABITEM_ACCOUNTS = 0;
    private static final int TABITEM_COSTCENTERS = 1;
    private static final int TABITEM_DEBTORS = 2;
    private static final int TABITEM_TRANSACTIONS = 3;

    MyExApp app;
    SearchManager searchManager = null;
    SearchView searchView = null;
    String mSearchFilter;

    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        app = (MyExApp) getApplication();

        // Create the adapter that will return a fragment for each of the three primary sections of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Configure action bar
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowTitleEnabled(false);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the user swipes between sections.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                actionBar.setSelectedNavigationItem(position);
                actionBar.setTitle(mAppSectionsPagerAdapter.getPageTitle(position));
            }
        });

        // Add tabs to action bar
        ActionBar.Tab tab = actionBar.newTab()
                .setText(R.string.accounts_title)
                .setTabListener(this);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.costcenters_title)
                .setTabListener(this);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.debtors_title)
                .setTabListener(this);
        actionBar.addTab(tab);

        tab = actionBar.newTab()
                .setText(R.string.transactions_title)
                .setTabListener(this);
        actionBar.addTab(tab);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(false); // Do not iconify the widget; expand it by default
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            public boolean onQueryTextChange(String newText) {
                // Called when the action bar search text has changed.  Update
                // the search filter, and restart the loader to do a new query
                // with this filter.
                String newFilter = !TextUtils.isEmpty(newText) ? newText : null;
                // Don't do anything if the filter hasn't actually changed.
                // Prevents restarting the loader when restoring state.
                if (mSearchFilter == null && newFilter == null) {
                    return true;
                }
                if (mSearchFilter != null && mSearchFilter.equals(newFilter)) {
                    return true;
                }
                mSearchFilter = newFilter;
                //getLoaderManager().restartLoader(0, null, PricesActivity.this);
                return true;
            }
        });

        // Back-button on search view
        searchView.setOnQueryTextFocusChangeListener(new View.OnFocusChangeListener() {
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    searchView.setQuery("", false);
                }
            }
        });

        return true;
    }

    /**
     * Menu/actionbar selections
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int current_item = mViewPager.getCurrentItem();
        switch (item.getItemId()) {
            case R.id.action_settings:
                Intent settings = new Intent(this,SettingsActivity.class);
                startActivity(settings);
                return true;
            case R.id.action_add:
                switch (current_item) {
                    case TABITEM_ACCOUNTS:
                    case TABITEM_COSTCENTERS:
                    case TABITEM_DEBTORS:
                        Intent debtorsedit = new Intent(this,DebtorsEditActivity.class);
                        debtorsedit.putExtra("id",0L);
                        startActivity(debtorsedit);
                    case TABITEM_TRANSACTIONS:
                    default:
                }
                return true;
            case R.id.action_search:
                switch (current_item) {
                    case TABITEM_ACCOUNTS:
                    case TABITEM_COSTCENTERS:
                    case TABITEM_DEBTORS:
                    case TABITEM_TRANSACTIONS:
                    default:
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        // When the given tab is selected, switch to the corresponding page in the ViewPager.
        mViewPager.setCurrentItem(tab.getPosition());
        setTitle(tab.getText());
    }

    @Override
    public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentInteraction(String id) {
        int current_item = mViewPager.getCurrentItem();
        switch (current_item) {
            case TABITEM_DEBTORS:
                long lid = Long.parseLong(id);
                Intent debtorsedit = new Intent(this,DebtorsEditActivity.class);
                debtorsedit.putExtra("id",lid);
                startActivity(debtorsedit);
                break;
        }

    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentPagerAdapter {

        public AppSectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int i) {
            switch (i) {
                case TABITEM_ACCOUNTS:
                    return new AccountsFragment();
                case TABITEM_COSTCENTERS:
                    return new CostcentersFragment();
                case TABITEM_DEBTORS:
                    return new DebtorsFragment();
                case TABITEM_TRANSACTIONS:
                    return new TransactionsFragment();
                default:
                    return new AccountsFragment();
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            CharSequence cs = "";
            switch (position) {
                case TABITEM_ACCOUNTS:
                    cs = MyExApp.getContext().getResources().getString(R.string.accounts_title);
                    break;
                case TABITEM_COSTCENTERS:
                    cs = MyExApp.getContext().getResources().getString(R.string.costcenters_title);
                    break;
                case TABITEM_DEBTORS:
                    cs = MyExApp.getContext().getResources().getString(R.string.debtors_title);
                    break;
                case TABITEM_TRANSACTIONS:
                    cs = MyExApp.getContext().getResources().getString(R.string.transactions_title);
                    break;
                default:
                    cs = MyExApp.getContext().getResources().getString(R.string.accounts_title);
            }
            return cs;
        }
    } // class AppSectionsPagerAdapter



}
