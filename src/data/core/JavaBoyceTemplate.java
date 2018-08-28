package data.core;

import data.domain.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.jtwig.JtwigModel;
import org.jtwig.JtwigTemplate;

import java.sql.SQLException;

import static java.io.File.separator;

/**
 * @author boyce - 01/14/2018
 */
@Slf4j
@AllArgsConstructor
@Builder
@Data
public class JavaBoyceTemplate extends BoyceTemplate {

    private String name;
    private BoyceDataSource dataSource;
    private String location;
    private JtwigModel model;
    private String dbName;
    private Table table;
    private String outputDir;
    private String packagePath;
    private String suffix;

    public JavaBoyceTemplate() {
        super();
    }

    @Override
    protected JtwigModel prepareModel() {
        return this.model;
    }

    @Override
    protected Table prepareTable() {
        return this.table;
    }

    @Override
    protected JtwigTemplate loadTemplate() {
        return JtwigTemplate.classpathTemplate(this.location);
    }

    @Override
    protected Table fillTable(Table nowTable) {
        try {
            nowTable.setColumnList(this.dataSource.loadColumn(nowTable, this.dbName));
        } catch (SQLException e) {
            log.error("", e);
        }
        return nowTable;
    }

    @Override
    protected String getFileName(Table table) {
        return this.outputDir + separator + this.packagePath + table.getTitleName() + this.name + this.suffix;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof JavaBoyceTemplate)) { return false; }

        JavaBoyceTemplate that = (JavaBoyceTemplate)o;

        return location.equals(that.location) && table.equals(that.table);
    }

    @Override
    public int hashCode() {
        int result = location.hashCode();
        result = 31 * result + table.hashCode();
        return result;
    }

}
