package controller.handler;

import data.core.BoyceDataSource;
import data.core.DataSourceManager;
import data.domain.Schema;
import data.domain.Table;
import javafx.event.EventHandler;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;

import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static view.tool.WindowUtil.showInfoDialog;

/**
 * @author boyce - 02/22/2018
 */
public class DbTreeViewMouseHandler implements EventHandler<MouseEvent> {

    private TreeView<String> dbTreeView;

    private Map<String, Boolean> nodeDataLoadMap = new HashMap<>();

    public DbTreeViewMouseHandler(TreeView<String> dbTreeView) {
        this.dbTreeView = dbTreeView;
    }

    @Override
    public void handle(MouseEvent value) {

        // 双击连接数据库
        if (value.getClickCount() == 2) {
            if (value.getTarget() instanceof CheckBoxTreeCell) {
                TreeItem<String> targetItem = ((CheckBoxTreeCell) value.getTarget()).getTreeItem();
                Boolean loaded = nodeDataLoadMap.get(targetItem.getValue());
                if (loaded == null) {
                    if (loadTreeItem(targetItem)) {
                        nodeDataLoadMap.put(targetItem.getValue(), Boolean.TRUE);
                        targetItem.setExpanded(true);
                    }
                } else {
                    if (targetItem.isExpanded()) {
                        targetItem.setExpanded(true);
                    } else {
                        targetItem.setExpanded(false);
                    }
                }
            }
        }
    }

    private boolean loadTableList(TreeItem<String> dbNode) {
        TreeItem<String> parentNode = dbNode.getParent();
        BoyceDataSource dataSource =
            DataSourceManager.getBoyceDataSource(parentNode
                .getValue());
        try {
            List<Table> tableList = dataSource.loadAllTable
                (dbNode.getValue());
            for (Table table : tableList) {
                dbNode.getChildren().add(new CheckBoxTreeItem<>(table.getName()));
            }
            return true;
        } catch (SQLException e) {
            showInfoDialog(e.getMessage());
            return false;
        }
    }

    private boolean loadSchemaList(TreeItem<String> dbNode) {
        BoyceDataSource dataSource = DataSourceManager
            .getBoyceDataSource(dbNode.getValue());
        try {
            List<Schema> schemaList = dataSource.loadAllSchema();
            for (Schema schema : schemaList) {
                dbNode.getChildren().add(new CheckBoxTreeItem<>
                    (schema.getName()));
            }
            return true;
        } catch (SQLException e) {
            showInfoDialog(e.getMessage());
            return false;
        }
    }

    @SuppressWarnings("unchecked")
    private boolean loadTreeItem(TreeItem<String> dbNode) {

        int level = this.dbTreeView.getTreeItemLevel(dbNode);
        // dao level
        if (level == 1) {
            return loadSchemaList(dbNode);
        }
        // table level
        if (level == 2) {
            return loadTableList(dbNode);
        }
        return false;
    }
}
