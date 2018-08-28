package data.domain;

import javafx.beans.property.SimpleStringProperty;
import javafx.util.StringConverter;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import static java.io.File.separator;

/**
 * @author boyce - 02/28/2018
 */
@AllArgsConstructor
public class JavaProjectInfo {

    private SimpleStringProperty groupId;
    private SimpleStringProperty author;
    private SimpleStringProperty tablePrefix;
    private SimpleStringProperty packagePath;

    public JavaProjectInfo() {
        this.groupId = new SimpleStringProperty();
        this.author = new SimpleStringProperty();
        this.packagePath = new SimpleStringProperty();
        this.tablePrefix = new SimpleStringProperty();
        this.packagePath.bindBidirectional(this.groupId, new StringConverter<String>() {
            @Override
            public String toString(String object) {
                if (object == null) {
                    return null;
                }
                return StringUtils.replaceAll(object, "\\.", separator).concat(separator);
            }

            @Override
            public String fromString(String string) {
                if (string == null) {
                    return null;
                }
                return StringUtils.replaceAll(string, separator, "\\.").substring(0, string.length() - 1);
            }
        });
    }

    public String getGroupId() {
        return groupId.get();
    }

    public void setGroupId(String groupId) {
        this.groupId.set(groupId);
    }

    public SimpleStringProperty groupIdProperty() {
        return groupId;
    }

    public String getAuthor() {
        return author.get();
    }

    public void setAuthor(String author) {
        this.author.set(author);
    }

    public SimpleStringProperty authorProperty() {
        return author;
    }

    public String getTablePrefix() {
        return tablePrefix.get();
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix.set(tablePrefix);
    }

    public SimpleStringProperty tablePrefixProperty() {
        return tablePrefix;
    }

    public String getPackagePath() {
        return packagePath.get();
    }

    public void setPackagePath(String packagePath) {
        this.packagePath.set(packagePath);
    }

    public SimpleStringProperty packagePathProperty() {
        return packagePath;
    }
}
