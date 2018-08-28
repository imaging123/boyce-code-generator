package data.core;

import com.fasterxml.jackson.core.type.TypeReference;
import data.dao.DataSourceRepository;
import data.util.JsonUtil;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static data.util.Constants.DB_SEPERATOR;

/**
 * @author boyce - 02/19/2018
 */
public class DataSourceManager {

    private static final ObservableList<String> DB_NAME_LIST = FXCollections.observableArrayList();
    /**
     * dbName -> dataSource
     */
    private static final Map<String, BoyceDataSource> DATA_SOURCE_MAP = new HashMap<>();

    static {
        //DATA_SOURCE_MAP = JsonUtil.parseJson(dataArray[0], ArrayList.class);

    }

    public static void remove(BoyceDataSource boyceDataSource) throws IOException {
        remove(boyceDataSource.getName());
    }

    public static void remove(String name) throws IOException {
        DATA_SOURCE_MAP.remove(name);
        if (DB_NAME_LIST.contains(name)) {
            DB_NAME_LIST.remove(name);
            DataSourceRepository.getInstance().store();
        }
    }

    public static void save(BoyceDataSource boyceDataSource) throws IOException {
        DATA_SOURCE_MAP.put(boyceDataSource.getName(), boyceDataSource);
        if (!DB_NAME_LIST.contains(boyceDataSource.getName())) {
            DB_NAME_LIST.add(boyceDataSource.getName());
            DataSourceRepository.getInstance().store();
        }
    }

    public static ObservableList<String> getObservableList() {
        return FXCollections.observableArrayList(DATA_SOURCE_MAP.keySet());
    }

    public static void addListener(ListChangeListener<String> changeListener) {
        DB_NAME_LIST.addListener(changeListener);
    }

    public static boolean containDb(String dbName) {
        return DB_NAME_LIST.contains(dbName);
    }

    public static BoyceDataSource getBoyceDataSource(String name) {
        return DATA_SOURCE_MAP.get(name);
    }

    public static byte[] encode() throws UnsupportedEncodingException {
        String listJson = JsonUtil.toJson(DB_NAME_LIST);
        String mapJson = JsonUtil.toJson(DATA_SOURCE_MAP);
        String data = listJson + DB_SEPERATOR + mapJson;
        return Base64.encodeBase64(data.getBytes("UTF-8"));
    }

    @SuppressWarnings("unchecked")
    public static void decode(byte[] storedData) throws IOException {
        String data = new String(Base64.decodeBase64(storedData), "UTF-8");
        String[] dataArray = StringUtils.split(data, DB_SEPERATOR);
        if (dataArray == null || dataArray.length < 2) {
            return;
        }
        DB_NAME_LIST.addAll(JsonUtil.parseJson(dataArray[0], List.class));
        DATA_SOURCE_MAP.putAll(JsonUtil.parseJson(dataArray[1], new TypeReference<Map<String, BoyceDataSource>>() {}));
    }

}
