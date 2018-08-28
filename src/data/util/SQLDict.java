package data.util;

/**
 * @author boyce - 01/14/2018
 */
public class SQLDict {

    public static final String LOAD_ALL_SCHEMA = "SELECT SCHEMA_NAME FROM SCHEMATA";
    public static final String LOAD_COLUMN = "SELECT COLUMN_NAME, DATA_TYPE, COLUMN_COMMENT FROM COLUMNS WHERE " +
        "TABLE_NAME = ? AND TABLE_SCHEMA = ?";
    public static final String LOAD_KEY_COLUMN = "SELECT COLUMN_NAME FROM KEY_COLUMN_USAGE WHERE TABLE_NAME = ? and CONSTRAINT_NAME = 'PRIMARY'";
    public static final String LOAD_TABLE = "SELECT TABLE_COMMENT, TABLE_NAME FROM TABLES WHERE TABLE_SCHEMA = ?";
}
