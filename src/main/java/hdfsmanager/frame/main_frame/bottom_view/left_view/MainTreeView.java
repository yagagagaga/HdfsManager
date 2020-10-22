package hdfsmanager.frame.main_frame.bottom_view.left_view;

import java.awt.*;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;

import org.apache.hadoop.fs.FileStatus;

import hdfsmanager.util.GuiUtil;

class MainTreeView extends JTree {

    MainTreeView() {

        super(new DefaultMutableTreeNode("/"));

        initTreeCellRenderer();
        initLookAndFeel();
    }

    private void initTreeCellRenderer() {
        TreeCellRenderer renderer = createTreeCellRenderer();
        setCellRenderer(renderer);
    }

    private void initLookAndFeel() {
        GuiUtil.initLookAndFeel();
    }

    @SuppressWarnings("all")
    private DefaultTreeCellRenderer createTreeCellRenderer() {
        return new DefaultTreeCellRenderer() {

            {
                setLeafIcon(GuiUtil.getSystemIcon(null, true));
                setOpenIcon(GuiUtil.getSystemIcon(null, true));
            }

            private Icon generateLeafIcon(Object value) {
                String str = value.toString();
                if (str.indexOf('.') == 0 || str.equals("")) {
                    return new ImageIcon("images/nullNode.gif");
                } else {
                    return GuiUtil.getSystemIcon(value.toString(), false);
                }
            }

            @Override
            public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row, boolean hasFocus) {
                Component defaultRet = super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
                if (value instanceof DefaultMutableTreeNode) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
                    Object object = node.getUserObject();
                    if (object instanceof FileStatus) {
                        setText(((FileStatus) object).getPath().getName());
                    }
                }
                if (leaf) {
                    Icon icon = generateLeafIcon(value);
                    setIcon(icon);
                }
                return defaultRet;
            }
        };
    }
}
