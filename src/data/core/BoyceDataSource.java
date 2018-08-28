package data.core;

import com.fasterxml.jackson.annotation.JsonIgnore;
import data.domain.Column;
import data.domain.Schema;
import data.domain.Table;
import data.util.DbJavaMapUtil;
import javafx.beans.property.SimpleStringProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;

import java.sql.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import static com.google.common.base.CaseFormat.*;
import static data.util.Constants.*;
import static data.util.SQLDict.*;

/**
 * @author boyce - 10:19
 */
@Data
@Builder
@AllArgsConstructor
@EqualsAndHashCode(of = {"name"})
@Slf4j
public class BoyceDataSource implements Cloneable {
    private final static String PROTOCOL = "jdbc:mysql://";
    private final static String SCHEMA_TABLE = "/information_schema";
    private SimpleStringProperty ip;
    private SimpleStringProperty port;
    private SimpleStringProperty name;
    private SimpleStringProperty driver;
    private SimpleStringProperty user;
    @JsonIgnore
    private Connection connection;
    private boolean ready = false;
    private SimpleStringProperty password;

    public BoyceDataSource() {
        this.ip = new SimpleStringProperty();
        this.port = new SimpleStringProperty();
        this.name = new SimpleStringProperty();
        this.driver = new SimpleStringProperty();
        this.user = new SimpleStringProperty();
        this.password = new SimpleStringProperty();
    }

    @JsonIgnore
    public String getUrl() {
        return PROTOCOL.concat(this.ip.getValue()).concat(":").concat(this.port.getValue()).concat
            (SCHEMA_TABLE);
    }

    public void setUrl(String v) {

    }

    private void checkParameters() {
        if (ready) {
            return;
        }

        try {
            Class.forName(driver.get());
        } catch (ClassNotFoundException e) {
            log.error("", e);
        }
        ready = true;
    }

    private void checkConnectionAvailable() throws SQLException {
        if (this.connection == null) {
            this.connection = DriverManager.getConnection(getUrl(), this.user.get(), this.password.get());
        }
    }

    public boolean testConnection() {
        try {
            DriverManager.getConnection(getUrl(), this.user.get(), password.get());
            return true;
        } catch (SQLException e) {
            log.error("", e);
            return false;
        }
    }

    /**
     * 加载要生成的table
     *
     * @param dbName        数据库名
     * @param tableNameList 要生成的tableName列表
     * @return tableList
     */
    public List<Table> loadTable(String dbName, List<String> tableNameList) throws SQLException {
        checkConnectionAvailable();
        List<Table> allTableList = loadAllTable(dbName);
        // 不需要生成的table不记录
        Iterator<Table> tableIterator = allTableList.iterator();
        List<Table> willRemovedList = new ArrayList<>();
        while (tableIterator.hasNext()) {
            Table table = tableIterator.next();
            if (!tableNameList.contains(table.getName())) {
                willRemovedList.add(table);
            }
        }
        allTableList.removeAll(willRemovedList);
        return allTableList;
    }

    public List<Column> loadColumn(Table table, String dbSchema) throws SQLException {
        checkConnectionAvailable();
        String primaryColumnName = loadPrimaryColumnName(table.getName());
        List<Column> columnList = new LinkedList<>();
        PreparedStatement pt = this.connection.prepareStatement(LOAD_COLUMN);
        pt.setString(1, table.getName());
        pt.setString(2, dbSchema);
        ResultSet rs = pt.executeQuery();
        while (rs.next()) {
            String columnName = rs.getString(COLUMN_NAME);
            String columnComment = rs.getString(COLUMN_COMMENT);
            String dbType = rs.getString(DATA_TYPE);
            String javaType = DbJavaMapUtil.getJavaType(dbType);
            Column col = Column.builder()
                .camelName(UPPER_UNDERSCORE.to(LOWER_CAMEL, columnName))
                .titleName(UPPER_UNDERSCORE.to(UPPER_CAMEL, columnName))
                .name(columnName)
                .comment(columnComment)
                .dbType(DbJavaMapUtil.getMyBatisDbType(dbType))
                .javaType(javaType)
                .imports(DbJavaMapUtil.getImport(javaType))
                .build();
            if (primaryColumnName.equals(columnName)) {
                table.setPrimaryColumn(col);
            } else {
                columnList.add(col);
            }
        }
        return columnList;
    }

    private String loadPrimaryColumnName(String tableName) throws SQLException {
        checkConnectionAvailable();
        PreparedStatement pt = this.connection.prepareStatement(LOAD_KEY_COLUMN);
        pt.setString(1, tableName);
        ResultSet rs = pt.executeQuery();
        rs.next();
        return rs.getString(COLUMN_NAME);
    }

    public List<Schema> loadAllSchema() throws SQLException {
        checkConnectionAvailable();
        PreparedStatement pt = this.connection.prepareStatement(LOAD_ALL_SCHEMA);
        ResultSet rs = pt.executeQuery();

        List<Schema> schemaList = new ArrayList<>();
        while (rs.next()) {
            String schemaName = rs.getString(SCHEMA_NAME);
            schemaList.add(new Schema(schemaName));
        }
        return schemaList;
    }

    public List<Table> loadAllTable(String dbName) throws SQLException {
        checkConnectionAvailable();
        List<Table> tableList = new LinkedList<>();
        PreparedStatement pt = this.connection.prepareStatement(LOAD_TABLE);
        pt.setString(1, dbName);
        ResultSet rs = pt.executeQuery();
        while (rs.next()) {
            String tableName = rs.getString(TABLE_NAME);
            String tableComment = rs.getString(TABLE_COMMENT);
            tableList.add(Table.builder()
                .name(tableName)
                .comment(tableComment)
                .build());
        }
        return tableList;
    }

    /**
     * getter setter
     */
    public String getName() {
        return name.get();
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public SimpleStringProperty nameProperty() {
        return name;
    }

    public String getIp() {
        return ip.get();
    }

    public void setIp(String ip) {
        this.ip.set(ip);
    }

    public SimpleStringProperty ipProperty() {
        return ip;
    }

    public String getPort() {
        return port.get();
    }

    public void setPort(String port) {
        this.port.set(port);
    }

    public SimpleStringProperty portProperty() {
        return port;
    }

    public String getDriver() {
        return driver.get();
    }

    public void setDriver(String driver) {
        this.driver.set(driver);
    }

    public SimpleStringProperty driverProperty() {
        return driver;
    }

    public String getUser() {
        return user.get();
    }

    public void setUser(String user) {
        this.user.set(user);
    }

    public SimpleStringProperty userProperty() {
        return user;
    }

    public String getPassword() {
        return password.get();
    }

    public void setPassword(String password) {
        this.password.set(password);
    }

    public SimpleStringProperty passwordProperty() {
        return password;
    }

    @Override
    public BoyceDataSource clone() throws CloneNotSupportedException {
        return (BoyceDataSource) super.clone();
    }
}
