package lu.crghost.myex.activities;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentTransaction;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.v4.app.*;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;
import android.view.*;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.tools.MyOnFragmentFilterListener;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;

import java.util.List;
import java.util.Map;


public class MainFragment extends FragmentActivity implements ActionBar.TabListener,
        MyOnFragmentInteractionListener {

    private static final String TAG = "MainActivity";

    public static final int TABITEM_ACCOUNTS = 0;
    public static final int TABITEM_COSTCENTERS = 1;
    public static final int TABITEM_DEBTORS = 2;
    public static final int TABITEM_TRANSACTIONS = 3;
    public static final int NEW_TRANSACTION = 10;

    MyExApp app;
    static Menu menu;
    Map<String, String> accountsmap = null;
    SearchManager searchManager = null;
    SearchView searchView = null;
    String mSearchFilter;
    String mSelectedFilter;

    int backpressed = 0;

    int actuelFragmentPosition = 0;
    AppSectionsPagerAdapter mAppSectionsPagerAdapter;
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mainfragment);
        Log.i(TAG, "------------------------CREATE------------------------------------");
        app = (MyExApp) getApplication();

        // Create the adapter that will return a fragment for each of the three primary sections of the app.
        mAppSectionsPagerAdapter = new AppSectionsPagerAdapter(getSupportFragmentManager());

        // Configure action bar
        final ActionBar actionBar = getActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        //actionBar.setDisplayShowTitleEnabled(false);

        // Set up the ViewPager, attaching the adapter and setting up a listener for when the user swipes between sections.
        actuelFragmentPosition = 0;
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mAppSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int position) {
                // When swiping between different app sections, select the corresponding tab.
                actionBar.setSelectedNavigationItem(position);
                actionBar.setTitle(mAppSectionsPagerAdapter.getPageTitle(position));
                actuelFragmentPosition = position;
                if (position==TABITEM_TRANSACTIONS) {
                    showFilter();
                } else {
                    hideFilter();
                }
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
    protected void onResume() {
        super.onResume();
        backpressed = 0;
    }

    @Override
    public void onBackPressed() {
        backpressed++;
        if (backpressed < 2) {
            Toast.makeText(this,R.string.backpress, Toast.LENGTH_SHORT).show();
        } else {
            finish();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_mainfragment, menu);
        this.menu = menu;
        hideFilter();

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
                if (actuelFragmentPosition==TABITEM_ACCOUNTS
                        || actuelFragmentPosition==TABITEM_DEBTORS
                        || actuelFragmentPosition==TABITEM_TRANSACTIONS) {
                    MyOnFragmentFilterListener filter = (MyOnFragmentFilterListener) mAppSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
                    if (filter != null) filter.onSearch(mSearchFilter);
                    else Log.d(TAG,"Filter is null !!!!!!!!!");
                }

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

    public static void showFilter() {
        if (menu!=null) {
            MenuItem mi = menu.getItem(0);
            mi.setVisible(true);
        }
    }

    public static void hideFilter() {
        if (menu!=null) {
            MenuItem mi = menu.getItem(0);
            mi.setVisible(false);
        }
    }

    /**
     * Menu/actionbar selections
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int current_item = mViewPager.getCurrentItem();
        backpressed = 0;
        switch (item.getItemId()) {
            case R.id.action_exit:
                finish();
                return true;
            case R.id.action_about:
                action_about();
                return true;
            case R.id.action_settings:
                Intent settings = new Intent(this,SettingsActivity.class);
                startActivity(settings);
                return true;
            case R.id.action_add:
                switch (current_item) {
                    case TABITEM_ACCOUNTS:
                        Intent accountedit = new Intent(this,AccountsEditActivity.class);
                        accountedit.putExtra("id", 0L);
                        startActivityForResult(accountedit, TABITEM_ACCOUNTS);
                        return true;
                    case TABITEM_COSTCENTERS:
                        Intent costcenteredit = new Intent(this,CostcentersEditActivity.class);
                        costcenteredit.putExtra("id", 0L);
                        startActivityForResult(costcenteredit, TABITEM_COSTCENTERS);
                        return true;
                    case TABITEM_DEBTORS:
                        Intent debtorsedit = new Intent(this,DebtorsEditActivity.class);
                        debtorsedit.putExtra("id", 0L);
                        startActivityForResult(debtorsedit, TABITEM_DEBTORS);
                        return true;
                    case TABITEM_TRANSACTIONS:
                        Intent transactionedit = new Intent(this,TransactionsEditActivity.class);
                        transactionedit.putExtra("id", 0L);
                        startActivityForResult(transactionedit, TABITEM_TRANSACTIONS);
                        return true;
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
            case R.id.action_filter:
                switch (current_item) {
                    case TABITEM_ACCOUNTS:
                    case TABITEM_COSTCENTERS:
                    case TABITEM_DEBTORS:
                    case TABITEM_TRANSACTIONS:
                        if (accountsmap==null) accountsmap = app.getDataManager().getAccountsNamesMap(null,null);
                        String[] items = accountsmap.values().toArray(new String[0]);
                        AlertDialog.Builder builder = new AlertDialog.Builder(this);
                        String title = getResources().getString(R.string.filter_accounts);
                        builder.setTitle(title);
                        builder.setItems(items, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String[] ids = accountsmap.keySet().toArray(new String[0]);
                                String id = ids[which];
                                mSelectedFilter = id;
                                MyOnFragmentFilterListener filter = (MyOnFragmentFilterListener) mAppSectionsPagerAdapter.getRegisteredFragment(mViewPager.getCurrentItem());
                                if (filter != null) filter.onFilter(mSelectedFilter);
                                else Log.d(TAG,"Filter is null !!!!!!!!!");
                            }
                        });
                        builder.show();
                    default:
                }
                return true;
            case R.id.action_export:
                Intent iexport = new Intent(this,ExportActivity.class);
                startActivity(iexport);
                return true;
            case R.id.action_import:
                Intent iimport = new Intent(this,ImportActivity.class);
                startActivity(iimport);
                return true;
            case R.id.action_basicdata:
                Intent imeasures = new Intent(this,MeasuresActivity.class);
                startActivity(imeasures);
                return true;
            case R.id.action_stats:
                Intent istats = new Intent(this, StatsActivity.class);
                startActivity(istats);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Show about dialog
     */
    private void action_about() {
        LayoutInflater li = LayoutInflater.from(this);
        View prompt = li.inflate(R.layout.prompt_about, null);
        TextView txtversion = (TextView) prompt.findViewById(R.id.txtVersion);
        PackageInfo pinfo;
        try {
            pinfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            //int versionNumber = pinfo.versionCode;
            txtversion.setText(pinfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            txtversion.setText("???");
        }
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);
        dialog.setView(prompt);
        dialog.setTitle(getResources().getString(R.string.menu_about));
        dialog.setIcon(android.R.drawable.ic_dialog_info);
        dialog.setPositiveButton(R.string.btn_ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
        backpressed = 0;
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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (resultCode==RESULT_OK) {
            Log.d(TAG,"----------------------RELOAD DATA ---------------------");
            mViewPager.getAdapter().notifyDataSetChanged();
        }

        switch (requestCode) {
            case TABITEM_DEBTORS:

                break;
        }
    }

    @Override
    public void onFragmentInteractionEdit(String id, int action) {
        backpressed = 0;
        switch (action){
            case MyOnFragmentInteractionListener.ACTION_EDIT_ACCOUNT:
                long account_id = Long.parseLong(id);
                Intent accountsedit = new Intent(this,AccountsEditActivity.class);
                accountsedit.putExtra("id", account_id);
                startActivityForResult(accountsedit, TABITEM_ACCOUNTS);
                break;
            case MyOnFragmentInteractionListener.ACTION_EDIT_DEBTOR:
                long debtor_id = Long.parseLong(id);
                Intent debtorsedit = new Intent(this,DebtorsEditActivity.class);
                debtorsedit.putExtra("id", debtor_id);
                startActivityForResult(debtorsedit, TABITEM_DEBTORS);
                break;
            case MyOnFragmentInteractionListener.ACTION_EDIT_COSTCENTER:
                long costcenter_id = Long.parseLong(id);
                Intent costcenteredit = new Intent(this,CostcentersEditActivity.class);
                costcenteredit.putExtra("id", costcenter_id);
                startActivityForResult(costcenteredit, TABITEM_COSTCENTERS);
                break;
            case MyOnFragmentInteractionListener.ACTION_EDIT_TRANSACTION:
                long transaction_id = Long.parseLong(id);
                Intent transactionedit = new Intent(this,TransactionsEditActivity.class);
                transactionedit.putExtra("id", transaction_id);
                startActivityForResult(transactionedit, TABITEM_TRANSACTIONS);
                break;
        }
    }

    @Override
    public void onFragmentInteractionNewTransaction(String account_id, String costcenter_id, String debtor_id, String description) {
        backpressed = 0;
        Intent newtrans = new Intent(this,TransactionsEditActivity.class);
        newtrans.putExtra("id",0L);
        if (account_id!=null)    newtrans.putExtra("account_id", Long.parseLong(account_id));
        if (debtor_id!=null)     newtrans.putExtra("debtor_id",Long.parseLong(debtor_id));
        if (costcenter_id!=null) newtrans.putExtra("costcenter_id",Long.parseLong(costcenter_id));
        if (description!=null)   newtrans.putExtra("description", description);
        startActivityForResult(newtrans,NEW_TRANSACTION);
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to one of the primary
     * sections of the app.
     */
    public static class AppSectionsPagerAdapter extends FragmentStatePagerAdapter {

        SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
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

        // Reattachs fragment (and reloads data)
        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
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

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            Fragment fragment = (Fragment) super.instantiateItem(container, position);
            registeredFragments.put(position, fragment);
            return fragment;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            registeredFragments.remove(position);
            super.destroyItem(container, position, object);
        }

        public Fragment getRegisteredFragment(int position) {
            return registeredFragments.get(position);
        }

    } // class AppSectionsPagerAdapter



}
