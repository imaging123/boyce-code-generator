package data.core;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/**
 * 生成boyceTemplate时要用的数据
 *
 * @author boyce - 02/28/2018
 */
@Data
public class GeneratingContentInfo {
    private String dbName;
    private BoyceDataSource boyceDataSource;
    private List<String> tableNameList;

    public GeneratingContentInfo(String dbName, BoyceDataSource boyceDataSource) {
        this.dbName = dbName;
        this.boyceDataSource = boyceDataSource;
        this.tableNameList = new ArrayList<>();
    }

    public void addTableName(String name) {
        this.tableNameList.add(name);
    }
}
