package data.util;

import java.util.HashMap;
import java.util.Map;

/**
 * A javaType and package map util class
 *
 * @author boyce - 01/14/2018
 */
public class DbJavaMapUtil {
    private static final Map<String, String> DB_JAVA_MAP = new HashMap<>();
    private static final Map<String, String> JAVA_IMPORT_MAP = new HashMap<>();
    private static final Map<String, String> MYBATIS_DB_TYPE_MAP = new HashMap<>();

    static {
        DB_JAVA_MAP.put("bit", "Boolean");
        MYBATIS_DB_TYPE_MAP.put("bit", "BIT");
        JAVA_IMPORT_MAP.put("Boolean", "java.lang.Boolean");

        DB_JAVA_MAP.put("bigint", "Long");
        MYBATIS_DB_TYPE_MAP.put("bigint", "BIGINT");
        JAVA_IMPORT_MAP.put("Long", "java.lang.Long");

        DB_JAVA_MAP.put("mediumint", "Integer");
        MYBATIS_DB_TYPE_MAP.put("mediumint", "MEDIUMINT");
        DB_JAVA_MAP.put("int", "Integer");
        MYBATIS_DB_TYPE_MAP.put("int", "INTEGER");
        JAVA_IMPORT_MAP.put("Integer", "java.lang.Integer");

        DB_JAVA_MAP.put("tinyint", "Byte");
        MYBATIS_DB_TYPE_MAP.put("tinyint", "TINYINT");
        JAVA_IMPORT_MAP.put("Byte", "java.lang.Byte");

        DB_JAVA_MAP.put("smallint", "Short");
        MYBATIS_DB_TYPE_MAP.put("smallint", "SMALLINT");
        JAVA_IMPORT_MAP.put("Short", "java.lang.Short");

        DB_JAVA_MAP.put("decimal", "BigDecimal");
        MYBATIS_DB_TYPE_MAP.put("decimal", "DECIMAL");
        JAVA_IMPORT_MAP.put("BigDecimal", "java.math.BigDecimal");

        DB_JAVA_MAP.put("double", "Double");
        MYBATIS_DB_TYPE_MAP.put("double", "DOUBLE");
        JAVA_IMPORT_MAP.put("Double", "java.lang.Double");

        DB_JAVA_MAP.put("float", "Float");
        MYBATIS_DB_TYPE_MAP.put("float", "FLOAT");
        JAVA_IMPORT_MAP.put("Float", "java.lang.Float");

        DB_JAVA_MAP.put("char", "String");
        MYBATIS_DB_TYPE_MAP.put("char", "CHAR");
        DB_JAVA_MAP.put("varchar", "String");
        MYBATIS_DB_TYPE_MAP.put("varchar", "VARCHAR");
        DB_JAVA_MAP.put("longtext", "String");
        MYBATIS_DB_TYPE_MAP.put("longtext", "VARCHAR");
        JAVA_IMPORT_MAP.put("String", "java.lang.String");

        DB_JAVA_MAP.put("timestamp", "Date");
        MYBATIS_DB_TYPE_MAP.put("timestamp", "TIMESTAMP");
        DB_JAVA_MAP.put("datetime", "Date");
        MYBATIS_DB_TYPE_MAP.put("datetime", "DATETIME");
        JAVA_IMPORT_MAP.put("Date", "java.util.Date");
    }

    public static String getJavaType(String dbType) {
        return DB_JAVA_MAP.get(dbType);
    }

    public static String getMyBatisDbType(String dbType) {
        return MYBATIS_DB_TYPE_MAP.get(dbType);
    }

    public static boolean containImport(String javaType) {
        return JAVA_IMPORT_MAP.containsKey(javaType);
    }

    public static String getImport(String javaType) {
        return JAVA_IMPORT_MAP.get(javaType);
    }
}
