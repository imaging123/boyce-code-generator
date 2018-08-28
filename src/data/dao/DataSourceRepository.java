package data.dao;

import data.core.DataSourceManager;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

import static view.tool.WindowUtil.showInfoDialog;

/**
 * Store datasource information
 *
 * @author boyce - 02/21/2018
 */
public class DataSourceRepository {
    private static final File DATASOURCE_DB = new File("./data/datasource.bcg");
    private static volatile DataSourceRepository instance;

    static {
        if (!DATASOURCE_DB.exists()) {
            try {
                if (!DATASOURCE_DB.createNewFile()) {
                }
            } catch (IOException e) {
                e.printStackTrace();
                showInfoDialog(e.getMessage());
            }
        }
    }

    private DataSourceRepository() {

    }

    public static DataSourceRepository getInstance() {
        if (instance == null) {
            synchronized (DataSourceRepository.class) {
                if (instance == null) {
                    instance = new DataSourceRepository();
                }
            }
        }
        return instance;
    }

    public void store() throws IOException {
        byte[] data = DataSourceManager.encode();
        FileUtils.writeByteArrayToFile(DATASOURCE_DB, data);
    }

    public void read() throws IOException {
        byte[] data = FileUtils.readFileToByteArray(DATASOURCE_DB);
        DataSourceManager.decode(data);
    }

}
