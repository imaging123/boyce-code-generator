package data.domain;

import lombok.Builder;
import lombok.Data;

/**
 * @author boyce - 10:15
 */
@Data
@Builder
public class Column {

    private String javaType;
    private String dbType;
    private String name;
    private String camelName;
    private String titleName;
    private String comment;
    private String imports;

}
