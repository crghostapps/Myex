package lu.crghost.myex.activities;

import android.app.Activity;
import android.database.Cursor;
import android.support.v4.app.Fragment;
import android.app.SearchManager;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.tools.MyOnFragmentFilterListener;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;
import java.util.ArrayList;
import java.util.List;

import static android.provider.BaseColumns._ID;

/**
 * Transactions list
 */
public class TransactionsFragment extends Fragment implements AbsListView.OnItemClickListener, AdapterView.OnItemLongClickListener,
        MyOnFragmentFilterListener,
        android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {


    private static final String TAG = "TransactionsFragment";


    SimpleCursorAdapter mAdapter;
    ListView mListView;
    String mSearchFilter;
    String mSelectedFilter;				// Selected Filter
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

        // Click on list
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        mListView.setClickable(true);

        mAdapter = new TransactionsAdapter(
                view.getContext(),
                R.layout.fragment_transactions_item,
                null,
                new String[] {"description"},
                new int[] {android.R.id.text1}, app);
        mListView.setAdapter(mAdapter);
        getLoaderManager().initLoader(0, null, this);

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
            throw new ClassCastException(activity.toString() + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onFragmentInteractionEdit(Long.toString(id), MyOnFragmentInteractionListener.ACTION_EDIT_TRANSACTION);
        mListView.deferNotifyDataSetChanged();
    }


    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        mListener.onFragmentInteractionEdit(Long.toString(id), MyOnFragmentInteractionListener.ACTION_EDIT_TRANSACTION);
        mListView.deferNotifyDataSetChanged();
        return true;
    }

    @Override
    public void onSearch(String search) {
        mSearchFilter = search;
        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onFilter(String filter) {
        mSelectedFilter = filter;
        getLoaderManager().restartLoader(0, null, this);
    }



    /*******************************************************************************************************************
     *
     * Implementation of LoaderCallbacks<Cursor>
     *
     *******************************************************************************************************************/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TransactionsProvider.CONTENT_URI;
        CursorLoader mLoader = new CursorLoader(this.getActivity(),uri,null,null,null,null);
        String selection = null;
        List<String> selectionArgs = new ArrayList<String>();
        if (mSearchFilter != null) {
            selection = "description LIKE ?";
            selectionArgs.add("%" + mSearchFilter + "%");
        }
        if (mSelectedFilter != null) {
            if (selection==null) {
                selection = "account_id=?";
            } else {
                selection = " AND account_id=?";
            }
            selectionArgs.add(mSelectedFilter);
        }

        if (selection != null) {
            final String[] SELECTION_ARGS = new String[selectionArgs.size()];
            selectionArgs.toArray(SELECTION_ARGS);
            mLoader.setSelection(selection);
            mLoader.setSelectionArgs(SELECTION_ARGS);
        }
        mListView.getEmptyView().setVisibility(View.GONE);
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        final View emptyView = mListView.getEmptyView();
        emptyView.setVisibility(View.VISIBLE);
        mAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }



}
