package controller;

import controller.handler.DbTreeViewMouseHandler;
import data.core.DataSourceManager;
import data.core.GeneratingContentInfo;
import data.custom.BoyceMyBatisMVCTemplate;
import data.custom.JavaMyBatisMVCTemplate;
import data.custom.MyBatisMVCTemplate;
import data.custom.MyBatisMVCTemplateBase;
import data.custom.ShardingMyBatisMVCTemplate;
import data.dao.DataSourceRepository;
import data.domain.JavaProjectInfo;
import data.domain.MyBatisTemplateItem;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.ListChangeListener;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBoxTreeItem;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.control.cell.CheckBoxTreeCell;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static data.domain.MyBatisTemplateNames.EXAMPLE;
import static data.domain.MyBatisTemplateNames.MYBATIS_XML;
import static data.domain.MyBatisTemplateNames.POJO;
import static data.domain.MyBatisTemplateNames.REPOSITORY;
import static data.domain.MyBatisTemplateNames.SERVICE;
import static data.domain.MyBatisTemplateNames.SERVICE_IML;
import static view.tool.WindowUtil.showInfoDialog;

/**
 * @author boyce - 02/14/2018
 */
@Slf4j
public class AppController {
    /**
     * 存放模板的列表
     */
    public ListView<String> templateItemsComboBox;
    private List<MyBatisTemplateItem> myBatisTemplateItems;

    /**
     * Control UI
     */
    public BorderPane javaTemplatePane;
    public BorderPane appPane;
    public BorderPane dataSourceTree;
    public TextField groupIdField;
    public TextField authorField;
    public TextField tablePrefixField;
    public ProgressBar generateProgressBar;
    public Label progressDesc;
    public Button buttonGenerate;
    private static final Map<String, Class> templateTypeMap = new HashMap<>();
    private TreeView<String> dbTreeView;

    /**
     * 右键菜单
     */
    private ContextMenu dbContextMenu;

    /**
     * Data
     */
    private JavaProjectInfo javaProjectInfo;

    static {
        templateTypeMap.put("58coin", MyBatisMVCTemplate.class);
        templateTypeMap.put("java", JavaMyBatisMVCTemplate.class);
    }

    private DataSourceInfoController dataSourceInfoController;
    private Stage datasourceStage;
    private List<String> selectedTemplateNameList = new ArrayList<>();

    public ChoiceBox<String> templateType;
    private MyBatisMVCTemplateBase templateManager;

    /**
     * 添加新的数据源连接
     */
    public void createDataSourceInfo() {
        showDataSourceInfoDialog();
    }

    private void showDataSourceInfoDialog() {
        this.datasourceStage.showAndWait();
    }

    /**
     * 生成具体的文件
     */
    @FXML
    public void generate() {
        this.buttonGenerate.setDisable(true);
        // 遍历并统计选中的表
        TreeItem<String> root = this.dbTreeView.getRoot();
        List<GeneratingContentInfo> generatingContentInfoList = new ArrayList<>();
        for (TreeItem<String> dataSourceNameItem : root.getChildren()) {
            CheckBoxTreeItem<String> dataSourceNameCheckbox = (CheckBoxTreeItem<String>) dataSourceNameItem;
            for (TreeItem<String> dbNameItem : dataSourceNameCheckbox.getChildren()) {
                GeneratingContentInfo generatingContentInfo =
                    new GeneratingContentInfo(dbNameItem.getValue(),
                        DataSourceManager.getBoyceDataSource(dataSourceNameCheckbox.getValue()));
                for (TreeItem<String> tableNameItem : dbNameItem.getChildren()) {
                    CheckBoxTreeItem<String> child = (CheckBoxTreeItem<String>) tableNameItem;
                    if (child.isSelected()) {
                        generatingContentInfo.addTableName(child.getValue());
                    }
                }
                if (generatingContentInfo.getTableNameList().isEmpty()) {
                    continue;
                }
                generatingContentInfoList.add(generatingContentInfo);
            }
        }
        for (MyBatisTemplateItem myBatisTemplateItem : this.myBatisTemplateItems) {
            if (myBatisTemplateItem.isSelected()) {
                this.selectedTemplateNameList.add(myBatisTemplateItem.getName());
            }
        }

        this.templateManager = getMyBatisMVCTemplate(this.javaProjectInfo);
        Task task = this.templateManager.generate(generatingContentInfoList, this.selectedTemplateNameList);
        if (this.generateProgressBar.progressProperty().isBound()) {
            this.generateProgressBar.progressProperty().unbind();
        }
        this.generateProgressBar.setProgress(0D);
        this.generateProgressBar.progressProperty().bind(task.progressProperty());
        Thread generateTask = new Thread(task, "Generate task");
        task.messageProperty().addListener((observable, oldValue, newValue) -> {
            if ("Finished".equals(newValue)) {
                buttonGenerate.setDisable(false);
            }
            progressDesc.setText(newValue);
        });
        generateTask.start();
    }

    public void initialize() throws IOException {
        // UI settings
        prepared();

        datasourceSettings();

        loadDbData();

        loadDataSourceInfoStage();

        bindJavaProjectInfo();

        loadTemplateList();

        // 右键菜单
        this.dbContextMenu = new ContextMenu();
        MenuItem item = new MenuItem("删除");
        this.dbContextMenu.getItems().add(item);
        item.setOnAction(handler -> {
            TreeItem<String> selectedItem = this.dbTreeView.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                try {
                    DataSourceManager.remove(selectedItem.getValue());
                } catch (IOException e) {
                    log.error("", e);
                    showInfoDialog("删除失败！");
                }
            }
        });
        this.dbTreeView.addEventHandler(new EventType<>("other"), (EventHandler<MouseEvent>) event -> {
            if (event.getTarget() != dbTreeView) {
                dbContextMenu.hide();
            }
        });
        this.dbTreeView.setOnContextMenuRequested(event
            -> this.dbContextMenu.show(this.dbTreeView, event.getScreenX(), event.getScreenY()));
    }

    /**
     * 设置初始的模板
     * 1. POJO
     * 2. REPOSITORY
     * 3. EXAMPLE
     * 4. SERVICE
     * 5. SERVICE_IMPL
     */
    private void loadTemplateList() {

        // 默认选中
        final boolean defaultChecked = true;
        MyBatisTemplateItem pojoItem = new MyBatisTemplateItem(defaultChecked, POJO);
        MyBatisTemplateItem repositoryItem = new MyBatisTemplateItem(defaultChecked, REPOSITORY);
        MyBatisTemplateItem mybatisItem = new MyBatisTemplateItem(defaultChecked, MYBATIS_XML);
        MyBatisTemplateItem exampleItem = new MyBatisTemplateItem(defaultChecked, EXAMPLE);
        MyBatisTemplateItem serviceItem = new MyBatisTemplateItem(defaultChecked, SERVICE);
        MyBatisTemplateItem serviceImplItem = new MyBatisTemplateItem(defaultChecked, SERVICE_IML);

        this.myBatisTemplateItems = new ArrayList<>();
        this.myBatisTemplateItems.add(pojoItem);
        this.myBatisTemplateItems.add(repositoryItem);
        this.myBatisTemplateItems.add(mybatisItem);
        this.myBatisTemplateItems.add(exampleItem);
        this.myBatisTemplateItems.add(serviceItem);
        this.myBatisTemplateItems.add(serviceImplItem);

        this.templateItemsComboBox.setCellFactory(CheckBoxListCell.forListView
            (param -> {
                    BooleanProperty booleanProperty = new SimpleBooleanProperty(true);
                    switch (param) {
                        case POJO:
                            booleanProperty.bindBidirectional(pojoItem.selectedProperty());
                            break;
                        case REPOSITORY:
                            booleanProperty.bindBidirectional(repositoryItem.selectedProperty());
                            break;
                        case MYBATIS_XML:
                            booleanProperty.bindBidirectional(mybatisItem.selectedProperty());
                            break;
                        case EXAMPLE:
                            booleanProperty.bindBidirectional(exampleItem.selectedProperty());
                            break;
                        case SERVICE:
                            booleanProperty.bindBidirectional(serviceItem.selectedProperty());
                            break;
                        case SERVICE_IML:
                            booleanProperty.bindBidirectional(serviceImplItem.selectedProperty());
                            break;
                        default:
                    }
                    return booleanProperty;
                }
            ));
        this.templateItemsComboBox.getItems().add(pojoItem.getName());
        this.templateItemsComboBox.getItems().add(repositoryItem.getName());
        this.templateItemsComboBox.getItems().add(mybatisItem.getName());
        this.templateItemsComboBox.getItems().add(exampleItem.getName());
        this.templateItemsComboBox.getItems().add(serviceItem.getName());
        this.templateItemsComboBox.getItems().add(serviceImplItem.getName());
    }

    /**
     * 绑定groupId, author, tablePrefix对应的数据结构
     */
    private void bindJavaProjectInfo() {
        this.javaProjectInfo = new JavaProjectInfo();
        this.groupIdField.textProperty().bindBidirectional(this.javaProjectInfo.groupIdProperty());
        this.javaProjectInfo.authorProperty().bindBidirectional(this.authorField.textProperty());
        this.javaProjectInfo.tablePrefixProperty().bindBidirectional(this.tablePrefixField.textProperty());
    }

    /**
     * 加载数据源弹窗
     */
    private void loadDataSourceInfoStage() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("../window/datasource_info.fxml"));
        Parent root = loader.load();
        this.dataSourceInfoController = loader.getController();
        this.datasourceStage = new Stage();
        this.dataSourceInfoController.setStage(this.datasourceStage);
        this.dataSourceInfoController.initDefaultValue();
        this.datasourceStage.setScene(new Scene(root, 296D, 506D));
        this.datasourceStage.<ActionEvent>addEventFilter(new EventType<>("close"), event -> {
        });
        this.dataSourceTree.setCenter(this.dbTreeView);
    }

    /**
     * 相关属性初始化
     */
    private void prepared() throws IOException {
        // 先显示emptyPane
        this.appPane.setCenter(javaTemplatePane);

        this.dbTreeView = new TreeView<>();
        CheckBoxTreeItem<String> rootItem = new CheckBoxTreeItem<>();
        this.dbTreeView.setRoot(rootItem);
        this.dbTreeView.setShowRoot(false);
        this.dbTreeView.setCellFactory(CheckBoxTreeCell.forTreeView());

        // 连接对应的DB，并显示所有的数据库名
        this.dbTreeView.setOnMouseClicked(new DbTreeViewMouseHandler(AppController.this.dbTreeView));

        this.generateProgressBar.setProgress(0D);
    }

    private void loadDbData() throws IOException {
        // load before data
        // 1. 读取数据
        // 2. render 只需要显示schema即可
        DataSourceRepository.getInstance().read();
    }

    private void datasourceSettings() {

        // 当数据库相关数据变化时，window也跟着改变
        DataSourceManager.addListener(new ListChangeListener<String>() {

            private void addDbTreeItem(List<String> added) {
                if (!added.isEmpty()) {
                    for (String dbName : added) {
                        CheckBoxTreeItem<String> item = new CheckBoxTreeItem<>(dbName);
                        dbTreeView.getRoot().getChildren().add(item);
                    }
                }
            }

            @Override
            @SuppressWarnings("unchecked")
            public void onChanged(Change<? extends String> c) {
                if (!c.next()) {
                    return;
                }
                List<String> added = (List<String>) c.getAddedSubList();
                addDbTreeItem(added);

                List<String> removed = (List<String>) c.getRemoved();
                if (!removed.isEmpty()) {
                    Iterator<TreeItem<String>> dbNameIterator = dbTreeView.getRoot().getChildren().iterator();
                    while (dbNameIterator.hasNext()) {
                        TreeItem<String> existDBName = dbNameIterator.next();
                        if (removed.contains(existDBName.getValue())) {
                            dbTreeView.getRoot().getChildren().remove(existDBName);
                            break;
                        }
                    }
                }
            }
        });
    }

    private MyBatisMVCTemplateBase getMyBatisMVCTemplate(JavaProjectInfo javaProjectInfo) {
        switch (this.templateType.getValue()) {
            case "bhex":
                return new MyBatisMVCTemplate(javaProjectInfo);
            case "bhex_shard":
                return new ShardingMyBatisMVCTemplate(javaProjectInfo);
            case "java":
                return new JavaMyBatisMVCTemplate(javaProjectInfo);
            default:
                return new BoyceMyBatisMVCTemplate(javaProjectInfo);
        }
    }

}
