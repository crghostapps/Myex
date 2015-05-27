package lu.crghost.myex;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.unnamed.b.atv.model.TreeNode;
import com.unnamed.b.atv.view.AndroidTreeView;
import lu.crghost.myex.models.Costcenter;
import lu.crghost.myex.tools.IconTreeItemHolder;
import lu.crghost.myex.tools.MyOnFragmentInteractionListener;

import java.util.ArrayList;
import java.util.List;


/**
 * Costcenters treelist
 */
public class CostcentersFragment extends Fragment implements IconTreeItemHolder.IconItemClickListener {


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
        app = (MyExApp) getActivity().getApplication();
        //debug();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_costcenters, container, false);
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.ccstcontainer);

        TreeNode root = TreeNode.root();
        List<Costcenter> roots = app.getDataManager().getCostcenters("parent_id is null",new String[]{});
        for (Costcenter cc : roots) {
            IconTreeItemHolder myHolder = new IconTreeItemHolder(this.getActivity(),this);
            TreeNode parent = new TreeNode(new IconTreeItemHolder.IconTreeItem(cc.getId(),cc.getName())).setViewHolder(myHolder);
            //Log.d(TAG,"parent="+cc.getName() );
            List<TreeNode> childs = getChilds(cc.getId());
            if (childs != null) parent.addChildren(childs);
            root.addChildren(parent);
        }
        tView = new AndroidTreeView(getActivity(), root);
        containerView.addView(tView.getView());
        tView.expandAll();
        return rootView;
    }

    private List<TreeNode> getChilds(long parent_id) {
        List<Costcenter> childs = app.getDataManager().getCostcenters("parent_id=?", new String[]{Long.toString(parent_id)});
        List<TreeNode> childtrees = null;
        if (childs != null) {
            childtrees = new ArrayList<TreeNode>();
            for (Costcenter costcenter : childs) {
                IconTreeItemHolder myHolder = new IconTreeItemHolder(this.getActivity(),this);
                TreeNode child = new TreeNode(new IconTreeItemHolder.IconTreeItem(costcenter.getId(),costcenter.getName())).setViewHolder(myHolder);
                //Log.d(TAG,"child="+costcenter.getName());
                List<TreeNode> childchilds = getChilds(costcenter.getId());
                if (childchilds != null) child.addChildren(childchilds);
                childtrees.add(child);
            }
        }
        return childtrees;
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

    private void debug() {
        List<Costcenter> data = app.getDataManager().getCostcenters(null,null);
        for (Costcenter cc:data) {
            Log.d(TAG,"id="+cc.getId()+ " name=" + cc.getName() + " parent_id=" + cc.getParent_id());
        }
    }


    @Override
    public void onIconItemClick(long costcenter_id) {
        if (null != mListener) {
            mListener.onFragmentInteractionNewTransaction(null,Long.toString(costcenter_id),null);
        }
    }

    @Override
    public void onIconItemLongClick(long costcenter_id) {
        if (null != mListener) {
            mListener.onFragmentInteractionEdit(Long.toString(costcenter_id), MyOnFragmentInteractionListener.ACTION_EDIT_COSTCENTER);
        }
    }
}
