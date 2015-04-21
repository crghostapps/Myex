package lu.crghost.myex;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
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
        app = (MyExApp) getActivity().getApplication();
        debug();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_costcenters, container, false);
        ViewGroup containerView = (ViewGroup) rootView.findViewById(R.id.ccstcontainer);

        /*
        TreeNode root = TreeNode.root();
        TreeNode parent = new TreeNode(new IconTreeItemHolder.IconTreeItem(0, "RootNode"));
        TreeNode child0 = new TreeNode(new IconTreeItemHolder.IconTreeItem(0, "Child0"));
        TreeNode child1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(0, "Child1"));
        TreeNode parentB = new TreeNode(new IconTreeItemHolder.IconTreeItem(0, "RootNode B"));
        TreeNode childB0 = new TreeNode(new IconTreeItemHolder.IconTreeItem(0, "ChildB0"));
        TreeNode childB1 = new TreeNode(new IconTreeItemHolder.IconTreeItem(0, "ChildB1"));
        parentB.addChildren(childB0,childB1);
        parent.addChildren(child0, child1, parentB);

        root.addChild(parent);

        */

        TreeNode root = TreeNode.root();
        List<Costcenter> roots = app.getDataManager().getCostcenters("parent_id=?",new String[]{"0"});
        for (Costcenter cc : roots) {
            TreeNode parent = new TreeNode(new IconTreeItemHolder.IconTreeItem(cc.getId(),cc.getName()));
            Log.d(TAG,"parent="+cc.getName() );
            List<TreeNode> childs = getChilds(cc.getId());
            if (childs != null) parent.addChildren(childs);
            root.addChildren(parent);
        }
        tView = new AndroidTreeView(getActivity(), root);
        tView.setDefaultViewHolder(IconTreeItemHolder.class);
        tView.setDefaultNodeClickListener(new TreeNode.TreeNodeClickListener() {
            @Override
            public void onClick(TreeNode treeNode, Object o) {
                Log.d(TAG,"------------------------------------click in tree--------------------------------------------");
            }
        });
        containerView.addView(tView.getView());

        return rootView;
    }

    private List<TreeNode> getChilds(long parent_id) {
        List<Costcenter> childs = app.getDataManager().getCostcenters("parent_id=?", new String[]{Long.toString(parent_id)});
        List<TreeNode> childtrees = null;
        if (childs != null) {
            childtrees = new ArrayList<TreeNode>();
            for (Costcenter costcenter : childs) {
                TreeNode child = new TreeNode(new IconTreeItemHolder.IconTreeItem(costcenter.getId(),costcenter.getName()));
                Log.d(TAG,"child="+costcenter.getName());
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


}
