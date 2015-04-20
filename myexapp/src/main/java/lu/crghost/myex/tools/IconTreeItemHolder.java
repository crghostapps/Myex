package lu.crghost.myex.tools;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import com.unnamed.b.atv.model.TreeNode;
import lu.crghost.myex.R;

/**
 * An item in the costcenter treelist
 * https://github.com/bmelnychuk/AndroidTreeView
 */
public class IconTreeItemHolder extends TreeNode.BaseNodeViewHolder<IconTreeItemHolder.IconTreeItem> {

    TextView textView;
    int padding;

    public IconTreeItemHolder(Context context) {
        super(context);
        padding = context.getResources().getDimensionPixelSize(R.dimen.treeviewpadding);
    }

    @Override
    public View createNodeView(final TreeNode treeNode, IconTreeItemHolder.IconTreeItem iconTreeItem) {
        final LayoutInflater inflater = LayoutInflater.from(context);
        final View view = inflater.inflate(R.layout.simple_tree_item, null, false);
        textView = (TextView) view.findViewById(R.id.node_value);

        String prefix = "+ ";
        if (treeNode.isLeaf()) {
            prefix = ". ";
        } else {
            if (treeNode.isExpanded()) {
                prefix = "- ";
            }
            if (treeNode.isSelected()) {
                prefix = "> ";
            }
        }

        String text = prefix + iconTreeItem.text + " (" + treeNode.getLevel() + ")";
        textView.setText(text);
        textView.setPadding(padding * (treeNode.getLevel() - 1),0,0,0);



        /*
        view.findViewById(R.id.btntoggle).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TreeNode newFolder = new TreeNode(new IconTreeItemHolder.IconTreeItem(R.string.ccstexpand, "New ?"));
                getTreeView().addNode(treeNode, newFolder);
            }
        });
        */


        //view.findViewById(R.id.btn_delete).setOnClickListener(new View.OnClickListener() {
        //    public void onClick(View v) {
        //        getTreeView().removeNode(node);
        //    }
        //});

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
        public int icon;
        public String text;

        public IconTreeItem(int icon, String text) {
            this.icon = icon;
            this.text = text;
        }
    }
}
