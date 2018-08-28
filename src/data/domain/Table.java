package data.domain;

import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static com.google.common.base.CaseFormat.UPPER_UNDERSCORE;

/**
 * @author boyce - 10:15
 */
@Builder
public class Table {

    @Getter
    private String name;
    @Getter
    @Setter
    private String comment;
    @Setter
    @Getter
    private String titleName;
    @Setter
    @Getter
    private String camelName;
    @Setter
    @Getter
    private List<Column> columnList;
    @Setter
    @Getter
    private Column primaryColumn;
    @Getter
    private String tablePrefix;
    @Setter
    @Getter
    private String noPrefixName;

    @Override
    public boolean equals(Object o) {
        if (this == o) { return true; }
        if (!(o instanceof Table)) { return false; }
        if (!super.equals(o)) { return false; }

        Table table = (Table)o;

        return name.equals(table.name);
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    public void setName(String name) {
        this.name = name;
        calculateCamelTitleName();
    }

    public void setTablePrefix(String tablePrefix) {
        this.tablePrefix = tablePrefix;
        calculateCamelTitleName();
    }

    private void calculateCamelTitleName() {
        if (!StringUtils.isEmpty(this.tablePrefix) && this.name.startsWith(this.tablePrefix)) {
            this.noPrefixName = this.name.substring(this.tablePrefix.length());
        } else {
            this.noPrefixName = this.name;
        }
        this.titleName = UPPER_UNDERSCORE.to(UPPER_CAMEL, this.noPrefixName);
        this.camelName = UPPER_UNDERSCORE.to(LOWER_CAMEL, this.noPrefixName);
    }
}
