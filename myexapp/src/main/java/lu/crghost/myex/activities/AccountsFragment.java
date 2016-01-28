package lu.crghost.myex.activities;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.models.Account;
import lu.crghost.myex.tools.MyOnFragmentFilterListener;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;

/**
 * Accounts list
 */
public class AccountsFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener, MyOnFragmentFilterListener {

    private static final String TAG = "AccountsFragment";

    MyExApp app;
    private MyOnFragmentInteractionListener mListener;
    boolean usegps;

    Account selected_account;
    String  selected_debtorid;
    String  selected_costcenterid;
    String  selected_description;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ArrayAdapter mAdapter;

    public static AccountsFragment newInstance() {
        AccountsFragment fragment = new AccountsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public AccountsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyExApp) getActivity().getApplication();
        mAdapter = new AccountsAdapter(app,getActivity(),app.getDataManager().getAccounts(null,null));
        usegps = app.getPrefs().getBoolean("localisation",false);
        //if (usegps) app.refreshLocation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_accounts, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MyOnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement MyOnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            selected_account = (Account) mAdapter.getItem(position);
            selected_costcenterid = null;
            selected_debtorid     = null;
            selected_description  = null;
            String message        = null;
            mListener.onFragmentInteractionNewTransaction(selected_account.getIdAsString(), String.valueOf(selected_account.getCostcenter_id()), null, null);
            if (message==null) {

            } else {

            }
        }
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            selected_account = (Account) mAdapter.getItem(position);
            mListener.onFragmentInteractionEdit(selected_account.getIdAsString(), MyOnFragmentInteractionListener.ACTION_EDIT_ACCOUNT);
        }
        return false;
    }

    @Override
    public void onSearch(String search) {
        mAdapter.getFilter().filter(search);
    }

    @Override
    public void onFilter(String filter) {
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }



}
