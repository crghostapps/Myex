package lu.crghost.myex.activities;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import lu.crghost.myex.MyExApp;
import lu.crghost.myex.R;
import lu.crghost.myex.models.Debtor;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;


/**
 * Debtors fragment
 */
public class DebtorsFragment extends Fragment implements AbsListView.OnItemClickListener, AbsListView.OnItemLongClickListener {

    private static final String TAG = "DebtorsFragment";
    MyExApp app;
    private MyOnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    public static DebtorsFragment newInstance() {
        DebtorsFragment fragment = new DebtorsFragment();
        return fragment;
    }

    public DebtorsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        app = (MyExApp) getActivity().getApplication();
        mAdapter = new DebtorsAdapter(app,getActivity(),app.getDataManager().getDebtors(null, null));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_debtors, container, false);

        // Set the adapter
        mListView = (AbsListView) view.findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        mListView.setOnItemLongClickListener(this);
        mListView.setEmptyView(view.findViewById(android.R.id.empty));

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
            Debtor debtor = (Debtor) mAdapter.getItem(position);
            mListener.onFragmentInteractionNewTransaction(null,null,debtor.getIdAsString(), debtor.getName());
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            Debtor debtor = (Debtor) mAdapter.getItem(position);
            mListener.onFragmentInteractionEdit(debtor.getIdAsString(),MyOnFragmentInteractionListener.ACTION_EDIT_DEBTOR);
        }
        return false;
    }


    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
    }



}
