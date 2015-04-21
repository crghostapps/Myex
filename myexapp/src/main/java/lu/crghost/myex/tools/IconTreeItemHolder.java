package lu.crghost.myex.tools;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import com.unnamed.b.atv.model.TreeNode;
import lu.crghost.myex.R;

/**
 * An item in the costcenter treelist
 * https://github.com/bmelnychuk/AndroidTreeView
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {

    private static final String TAG = "IconTreeItemHolder";

    TextView textView;
    ImageButton btnToggle;
    Context context;
    int padding;

    public IconTreeItemHolder(Context context) {
        super(context);
        this.context = context;
        padding = context.getResources().getDimensionPixelSize(R.dimen.treeviewpadding);
    }

    @Override
    public View createNodeView(final TreeNode treeNode, final IconTreeItemHolder.IconTreeItem iconTreeItem) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.simple_tree_item, null, false);

        textView = (TextView) view.findViewById(R.id.node_value);
        String text = iconTreeItem.text + " (" + treeNode.getLevel() + ")";
        textView.setText(text);
        /*
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Log.d(TAG, "Click on node " + iconTreeItem.id);
                mClickListener.onIconTreeClick(iconTreeItem.id);
            }
        });
        textView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                mClickListener.onIconTreeLongClick(iconTreeItem.id);
                return false;
            }
        });
         */

        // Expand or collapse with imagebutton
        btnToggle = (ImageButton) view.findViewById(R.id.btnToggle);
        btnToggle.setPadding(padding * (treeNode.getLevel() - 1), 0, 0, 0);
        int iconres = R.drawable.ic_action_expand;
        if (treeNode.isLeaf()) {
            iconres = R.drawable.ic_action_leaf;
        } else {
            if (treeNode.isExpanded()) {
                iconres = R.drawable.ic_action_collapse;
            }
            if (treeNode.isSelected()) {
            }
        }
        btnToggle.setImageDrawable(context.getResources().getDrawable(iconres));
        btnToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!treeNode.isLeaf()) {
                    if (treeNode.isExpanded()) {
                        getTreeView().collapseNode(treeNode);
                        btnToggle.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_expand));
                    } else {
                        getTreeView().expandNode(treeNode);
                        btnToggle.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action_collapse));
                    }
                }
            }
        });



        // Root node
        if (treeNode.getLevel() == 1) {
            //view.findViewById(R.id.btn_delete).setVisibility(View.GONE);
        }

        return view;
    }

    @Override
    public void toggle(boolean active) {
        //arrowView.setIconText(context.getResources().getString(active ? R.string.ic_keyboard_arrow_down : R.string.ic_keyboard_arrow_right));
    }

    public static class IconTreeItem {
        public long id;
        public String text;

        public IconTreeItem(long id, String text) {
            this.id   = id;
            this.text = text;
        }
    }

}
