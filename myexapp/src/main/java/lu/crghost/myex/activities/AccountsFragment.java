package lu.crghost.myex.activities;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import lu.crghost.cralib3.tools.Formats;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.models.Account;
import lu.crghost.myex.models.Costcenter;
import lu.crghost.myex.models.Debtor;
import lu.crghost.myex.models.Transaction;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;

import java.io.StringBufferInputStream;
import java.util.List;

/**
 * Accounts list
 */
public class AccountsFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

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
    private ListAdapter mAdapter;

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
            //if (usegps) message   = findNearBy(selected_account);
            if (message==null) {
                mListener.onFragmentInteractionNewTransaction(selected_account.getIdAsString(), String.valueOf(selected_account.getCostcenter_id()), null, null);
            } else {
                final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.transactions_nearby_title);
                builder.setMessage(message);
                builder.setPositiveButton(R.string.btn_yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onFragmentInteractionNewTransaction(selected_account.getIdAsString(), selected_costcenterid, selected_debtorid, selected_description);
                    }
                });
                builder.setNegativeButton(R.string.btn_no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mListener.onFragmentInteractionNewTransaction(selected_account.getIdAsString(), String.valueOf(selected_account.getCostcenter_id()), null, null);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
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

    /**
     * Find near by locations
     * @param account
     */
    private String findNearBy(Account account) {
        String dlgmessage = null;
        Location lastlocation = app.getLastKnownLocation();
        Transaction near_transaction = null;
        float distance_transaction = Float.MAX_VALUE;
        List<Transaction> transactions = app.getDataManager().getTransactions("latitude <> 0 and longitude <> 0 and account_id=?",new String[] {account.getIdAsString()});
        for (Transaction t : transactions) {
            float distance = lastlocation.distanceTo(t.getLocation());
            if (distance < distance_transaction) {
                distance_transaction = distance;
                near_transaction = t;
            }
        }

        Debtor near_debtor = null;
        float distance_debtor = Float.MAX_VALUE;
        List<Debtor> debtors = app.getDataManager().getDebtors("latitude <> 0 and longitude <> 0",null);
        for (Debtor d : debtors) {
            float distance = lastlocation.distanceTo(d.getLocation());
            if (distance < distance_debtor) {
                distance_debtor = distance;
                near_debtor = d;
            }
        }

        float maxdistence = 100;
        if (distance_transaction < maxdistence && distance_transaction < distance_debtor) {
            dlgmessage = getResources().getString(R.string.transactions_nearby_msg_t) + "\n"
                    + Formats.frDateFormat.format(near_transaction.getDateAmount_at()) + " "
                    + near_transaction.getDescription();
            selected_debtorid     = String.valueOf(near_transaction.getDebtor_id());
            selected_costcenterid = String.valueOf(near_transaction.getCostcenter_id());
            selected_description  = near_transaction.getDescription();
        } else if (distance_debtor < maxdistence) {
            dlgmessage = getResources().getString(R.string.transactions_nearby_msg_d) + "\n"
                    + near_debtor.getName();
            selected_debtorid = near_debtor.getIdAsString();
        }

        return dlgmessage;

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
