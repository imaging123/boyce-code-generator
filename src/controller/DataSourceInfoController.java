package controller;

import data.core.BoyceDataSource;
import data.core.DataSourceManager;
import javafx.event.ActionEvent;
import javafx.scene.control.*;
import javafx.stage.Stage;
import net.sf.cglib.beans.BeanCopier;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.apache.commons.lang3.StringUtils.EMPTY;
import static view.tool.WindowUtil.showInfoDialog;

/**
 * @author boyce - 02/17/2018
 */
public class DataSourceInfoController {
    private static final Map<String, String> DRIVER_MAP = new HashMap<>();
    private static final Map<String, String> PORT_MAP = new HashMap<>();

    private static BeanCopier beanCopier = BeanCopier.create(BoyceDataSource.class, BoyceDataSource.class, false);

    static {
        DRIVER_MAP.put("MySQL", "com.mysql.cj.jdbc.Driver");
        PORT_MAP.put("MySQL", "3306");
        DRIVER_MAP.put("SQLServer", "");
        DRIVER_MAP.put("Oracle", "");
    }

    /**
     * 数据库类型
     */
    public ChoiceBox<String> dataSourceType;
    public Button saveButton;
    public TextField ip;
    public TextField port;
    public TextField user;
    public PasswordField password;
    public TextField name;
    /**
     * 当前数据源的相关信息
     */
    private BoyceDataSource boyceDataSource;

    private Stage stage;

    /**
     * 测试连接是否可用
     */
    public void testConnection() {
        if (!checkParameters()) {
            return;
        }
        if (this.boyceDataSource.testConnection()) {
            showInfoDialog("连接成功!");
        } else {
            showInfoDialog("连接失败!");
        }
    }

    /**
     * 检查数据的合法性
     *
     * @return boolean
     */
    private boolean checkParameters() {
        if (StringUtils.isEmpty(ip.getText())) {
            showInfoDialog("IP required!");
            return false;
        }
        if (StringUtils.isEmpty(name.getText())) {
            showInfoDialog("Name required!");
            return false;
        }
        if (DataSourceManager.containDb(name.getText())) {
            showInfoDialog("Db Name already exists!");
            return false;
        }
        return true;
    }

    /**
     * 保存数据源的数据到文件中
     *
     * @param actionEvent click
     */
    public void saveDataSource(ActionEvent actionEvent) throws IOException, CloneNotSupportedException {
        checkParameters();
        BoyceDataSource boyceDataSource = new BoyceDataSource();
        beanCopier.copy(this.boyceDataSource, boyceDataSource, null);
        DataSourceManager.save(boyceDataSource);
        this.clear();
        this.initDefaultValue();
        this.stage.close();
    }

    public void initialize() {
        this.boyceDataSource = new BoyceDataSource();
        bindDataSourceInfoBean();
        this.port.setTextFormatter(new TextFormatter<Integer>(change -> {
            if (!StringUtils.isNumeric(change.getText())
                || change.getControlText().length() >= 5) {
                change.setText(EMPTY);
            }
            return change;
        }));
    }

    /**
     * 控件的数据和数据结构做绑定
     */
    private void bindDataSourceInfoBean() {
        this.name.textProperty().bindBidirectional(boyceDataSource.nameProperty());
        this.port.textProperty().bindBidirectional(boyceDataSource.portProperty());
        this.ip.textProperty().bindBidirectional(boyceDataSource.ipProperty());
        this.user.textProperty().bindBidirectional(boyceDataSource.userProperty());
        this.password.textProperty().bindBidirectional(boyceDataSource.passwordProperty());
    }

    /**
     * 数据库类型切换时，端口也跟着修改
     *
     * @param actionEvent action
     */
    @SuppressWarnings("unchecked")
    public void onChangeType(ActionEvent actionEvent) {
        String type = ((ChoiceBox<String>) actionEvent.getTarget()).getValue();
        boyceDataSource.setPort(PORT_MAP.get(type));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * 清空用户写入的数据，为复用做准备
     */
    public void clear() {
        this.port.clear();
        this.ip.clear();
        this.user.clear();
        this.password.clear();
    }

    /**
     * 设置UI中的初始值
     */
    public void initDefaultValue() {
        String defaultPort = PORT_MAP.get(this.dataSourceType.getValue());
        this.boyceDataSource.setIp("localhost");
        this.boyceDataSource.setPort(defaultPort);
        this.boyceDataSource.setUser("root");
        this.boyceDataSource.setDriver("MySQL");
    }
}
