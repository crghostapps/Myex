package lu.crghost.myex;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager.LoaderCallbacks;
import android.app.SearchManager;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Transactions list
 */
public class TransactionsFragment extends Fragment implements LoaderCallbacks<Cursor> {


    private static final String TAG = "TransactionsFragment";

    SimpleCursorAdapter mAdapter;
    ListView mListView;
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

        // Click on list
        mListView = (ListView) view.findViewById(android.R.id.list);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));
        mListView.setClickable(true);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            public void onItemClick(AdapterView<?> arg0, View arg1, int position, long id) {
                if (null != mListener) {
                    mListener.onFragmentInteractionEdit(Long.toString(id), MyOnFragmentInteractionListener.ACTION_EDIT_TRANSACTION);
                    //getActivity().getLoaderManager().restartLoader(0, null, TransactionsFragment.this);
                }

            }

        });

        // Fill the list
        /*
        mAdapter = new TransactionsAdapter(
                view.getContext(),
                R.layout.fragment_transactions_item,
                null,
                new String[] {"description"},
                new int[] {R.id.item_trans_description}, 0
        );*/
        mAdapter = new SimpleCursorAdapter(view.getContext(),android.R.layout.simple_list_item_1,null,new String[] {"description"},
                new int[] {R.id.item_trans_description}, 0);
        mListView.setAdapter(mAdapter);
        getActivity().getSupportLoaderManager().initLoader(0,null,this);

        Log.d(TAG,"--------------------------Adaptercount="+ mAdapter.getCount());

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (MyOnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        Uri uri = TransactionsProvider.CONTENT_URI;
        CursorLoader mLoader = new CursorLoader(this.getActivity(), uri, null, null, null, null);
        String selection = null;
        List<String> selectionArgs = new ArrayList<String>();
        if (mSearchFilter != null) {
            selection = "description LIKE ?";
            selectionArgs.add("%" + mSearchFilter + "%");
        }
        if (selection != null) {
            final String[] SELECTION_ARGS = new String[selectionArgs.size()];
            selectionArgs.toArray(SELECTION_ARGS);
            mLoader.setSelection(selection);
            mLoader.setSelectionArgs(SELECTION_ARGS);
        }
        Log.d(TAG,"----------------------Loader oncreate--------------------------");
        return mLoader;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }


}
