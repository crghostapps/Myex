package lu.crghost.myex;

import android.app.Activity;
import android.support.v4.app.ListFragment;
import android.app.SearchManager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;

/**
 * Transactions list
 */
public class TransactionsFragment extends ListFragment implements AdapterView.OnItemLongClickListener {


    private static final String TAG = "TransactionsFragment";


    SimpleCursorAdapter mAdapter;
    String mSearchFilter;
    String selectedFilter;				// Selected Filter
    MyExApp app;

    SearchManager searchManager = null;
    SearchView searchView = null;

    private MyOnFragmentInteractionListener mListener;



    public static TransactionsFragment newInstance() {
        TransactionsFragment fragment = new TransactionsFragment();
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public TransactionsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyExApp) getActivity().getApplication();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transactions, container, false);

        Cursor c = getTransactions();
        getActivity().startManagingCursor(c);
        mAdapter = new TransactionsAdapter(
                view.getContext(),
                R.layout.fragment_transactions_item,
                c,
                new String[] {"description"},
                new int[] {android.R.id.text1}, app);
        setListAdapter(mAdapter);

        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getListView().setOnItemLongClickListener(this);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MyOnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        mListener.onFragmentInteractionEdit(Long.toString(id), MyOnFragmentInteractionListener.ACTION_EDIT_TRANSACTION);
        getListView().deferNotifyDataSetChanged();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onFragmentInteractionEdit(Long.toString(id), MyOnFragmentInteractionListener.ACTION_EDIT_TRANSACTION);
        getListView().deferNotifyDataSetChanged();
        return true;
    }


    /**
     * Manage data
     * @return
     */
    private Cursor getTransactions() {
        final String[] PROJECTION = new String[] {_ID,"description"};
        Uri uri = TransactionsProvider.CONTENT_URI;
        String selection = null;
        String[] selectionArgsArray = null;
        List<String> selectionArgs = new ArrayList<String>();
        if (mSearchFilter != null) {
            selection = "description LIKE ?";
            selectionArgs.add("%" + mSearchFilter + "%");
        }
        if (selection != null) {
            selectionArgsArray = new String[selectionArgs.size()];
            selectionArgs.toArray(selectionArgsArray);
        }
        return getActivity().managedQuery(uri,PROJECTION,selection,selectionArgsArray,null);
    }



}
