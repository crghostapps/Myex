package lu.crghost.myex;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import lu.crghost.myex.tools.IconTreeItemHolder;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;


/**
 * Costcenters treelist
 */
public class CostcentersFragment extends Fragment {


    private static final String TAG = "CostcentersFragment";
    MyExApp app;
    private MyOnFragmentInteractionListener mListener;

    AndroidTreeView tView;

    public static CostcentersFragment newInstance() {
        CostcentersFragment fragment = new CostcentersFragment();
        return fragment;
    }

    public CostcentersFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_costcenters, container, false);
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.ccstcontainer);

        TreeNode root = TreeNode.root();
        TreeNode parent = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ccstexpand, "RootNode"));
        TreeNode child0 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ccstexpand, "Child0"));
        TreeNode child1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ccstexpand, "Child1"));
        parent.addChildren(child0, child1);
        root.addChild(parent);
        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        containerView.addView(tView.getView());

        return rootView;
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

}
