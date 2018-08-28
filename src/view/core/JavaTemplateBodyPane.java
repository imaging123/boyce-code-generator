package view.core;

import data.core.BoyceDataSource;
import data.core.JavaBoyceTemplate;
import data.custom.JavaMyBatisMVCTemplate;
import data.domain.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.sql.SQLException;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

import static data.util.Constants.*;

/**
 * @author boyce - 2018/1/16.
 */
public class JavaTemplateBodyPane extends BorderPane implements Observer {
    //private final LeftPane leftPane;
    //private final RightPane rightPane;
    private final JavaBoyceTemplate javaBoyceTemplate = new JavaBoyceTemplate();
    //private final JavaMyBatisMVCTemplate templateManager;

    public JavaTemplateBodyPane() {
        super();
        // data
        //this.templateManager = new JavaMyBatisMVCTemplate();

        // UI
        //this.leftPane = new LeftPane(templateManager);
        //this.rightPane = new RightPane(templateManager);
        this.setPadding(new Insets(0, 20, 20, 0));

        //this.setLeft(leftPane);
        //this.setCenter(rightPane);

    }

    @Override
    public void update(Observable o, Object arg) {
    }

    private static class LeftPane extends BorderPane {

        private static final Pane EMPTY_PANE = new EmptyPane();

        private JavaMyBatisMVCTemplate templateManager;

        public LeftPane(JavaMyBatisMVCTemplate templateManager) {
            // Data
            this.templateManager = templateManager;

            // View
            MenuBar menuBar = new MenuBar();
            menuBar.setMinWidth(300);

            Menu datasource = new Menu("数据源");

            MenuItem addDb = new MenuItem("account");

            addDb.setOnAction(action -> {
                try {
                    initDataSource(null);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
                //showDbTree();
            });

            datasource.getItems().add(addDb);

            menuBar.getMenus().add(datasource);

            this.setTop(menuBar);
            this.setCenter(EMPTY_PANE);
        }

        private void initDataSource(BoyceDataSource dataSource) throws SQLException {
            //templateManager.initDatasource();
            //templateManager.loadTableList("account");
            List<Table> tableList = templateManager.getTableList();
            showDbTree(tableList);
        }

        private void showDbTree(List<Table> tableList) {

            CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>("Db");
            rootItem.setExpanded(false);

            TreeView<String> tree = new TreeView<>(rootItem);
            tree.setEditable(false);

            tree.setCellFactory(CheckBoxTreeCell.forTreeView());
            for (Table table : tableList) {
                CheckBoxTreeItem<String> checkBoxTreeItem = new CheckBoxTreeItem<>(table.getName());
                rootItem.getChildren().add(checkBoxTreeItem);
            }

            tree.setRoot(rootItem);
            tree.setShowRoot(true);

            this.setCenter(tree);
        }

        private static class EmptyPane extends BorderPane {
            private EmptyPane() {
                Label centerIntro = new Label(
                    "DB Table Tree");
                centerIntro.setFont(Font.font(18.0));
                this.setCenter(centerIntro);
            }
        }

    }

    private static class RightPane extends GridPane {
        private JavaMyBatisMVCTemplate templateManager;

        public RightPane(JavaMyBatisMVCTemplate templateManager) {
            super();

            // data
            this.templateManager = templateManager;

            // UI
            this.setHgap(10);
            this.setVgap(10);

            Label groupIdLabel = new Label(GROUP_ID);
            this.add(groupIdLabel, 0, 0);

            TextField groupIdInput = new TextField();
            this.add(groupIdInput, 1, 0);

            Label authorLabel = new Label(AUTHOR);
            this.add(authorLabel, 0, 1);

            TextField authorInput = new TextField();
            this.add(authorInput, 1, 1);

            Label tablePrefixLabel = new Label(TABLE_PREFIX);
            this.add(tablePrefixLabel, 0, 2);

            TextField tablePrefixInput = new TextField();
            this.add(tablePrefixInput, 1, 2);

            ObservableList<String> options = FXCollections.observableArrayList("MyBatisCRUD");
            ComboBox<String> templateNameList = new ComboBox<>(options);
            templateNameList.setPromptText("Template Kit Name");
            templateNameList.setValue("MyBatisCRUD");
            this.add(templateNameList, 2, 0);

            ListView<String> customTemplateList = new ListView<>();
            ObservableList<String> items = FXCollections.observableArrayList(
                "POJO", "Repository", "Service", "ServiceImpl", "MyBatisXml");
            customTemplateList.setItems(items);
            customTemplateList.setOrientation(Orientation.VERTICAL);
            this.add(customTemplateList, 2, 1, 1, 5);

            Button generate = new Button(GENERATE);
            this.add(generate, 2, 6);

            generate.setOnMouseClicked(click -> {
                //try {
                String author = authorInput.getText();
                String groupId = groupIdInput.getText();
                //this.templateManager.generate(author, groupId);
                //} catch (SQLException e) {
                //    e.printStackTrace();
                //}
            });

        }
    }
}
