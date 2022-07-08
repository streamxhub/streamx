package com.streamxhub.streamx;

import org.junit.Test;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 一些简单的测试
 */
public class RegExpTest {

    /**
     * 不区分大小写，将所有内容作为一行进行匹配，.会匹配换行符，注意：\s可以匹配任何空白字符，包括换行符
     */
    public static final int DEFAULT_PATTERN_FLAGS = Pattern.CASE_INSENSITIVE | Pattern.DOTALL;

    /**
     * CREATE CATALOG catalog_name WITH (key1=val1, key2=val2, ...)<br>
     * 示例：create catalog hive_catalog with('name' = 'my_hive', 'conf' = '/home/hive/conf')
     */
    private static final Pattern CREATE_HIVE_CATALOG = Pattern.compile("CREATE\\s+CATALOG\\s+.+", DEFAULT_PATTERN_FLAGS);

    @Test
    public void testCreateHiveCatalog() {
        String str = "create catalog hive with (\n" +
                "    'type' = 'hive',\n" +
                "    'hadoop-conf-dir' = 'D:\\IDEAWorkspace\\work\\baishan\\log\\data-max\\src\\main\\resources',\n" +
                "    'hive-conf-dir' = 'D:\\IDEAWorkspace\\work\\baishan\\log\\data-max\\src\\main\\resources'\n" +
                ")";
        Matcher matcher = CREATE_HIVE_CATALOG.matcher(str);
        System.out.println(matcher.matches());
    }

    /**
     * CREATE [TEMPORARY|TEMPORARY SYSTEM] FUNCTION [IF NOT EXISTS] [catalog_name.][db_name.]function_name AS identifier [LANGUAGE JAVA|SCALA|PYTHON]<br>
     * 示例：create function test_fun as com.flink.testFun
     */
    private static final Pattern CREATE_FUNCTION = Pattern.compile("CREATE\\s+(TEMPORARY\\s+|TEMPORARY\\s+SYSTEM\\s+|)FUNCTION\\s+(IF NOT EXISTS\\s+|)([A-Za-z]+[A-Za-z\\d.\\-_]+)\\s+AS\\s+([A-Za-z].+)\\s+LANGUAGE\\s+(JAVA|SCALA|PYTHON)\\s*", DEFAULT_PATTERN_FLAGS);

    @Test
    public void testCreateFunction() {
        String str = "create   function if not exists hive.get_json_value as com.flink.function.JsonValueFunction language java";
        Matcher matcher = CREATE_FUNCTION.matcher(str);
        System.out.println(matcher.matches());
    }

    /**
     * USE [catalog_name.]database_name
     */
    private static final Pattern USE_DATABASE = Pattern.compile("USE\\s+(?!(CATALOG|MODULES)).*", DEFAULT_PATTERN_FLAGS);

    @Test
    public void testUseDatabase() {
        String str = "use modul.a ";
        Matcher matcher = USE_DATABASE.matcher(str);
        System.out.println(matcher.matches());
    }

    /**
     * SHOW [USER] FUNCTIONS
     */
    private static final Pattern SHOW_FUNCTIONS = Pattern.compile("SHOW\\s+(USER\\s+|)FUNCTIONS", DEFAULT_PATTERN_FLAGS);

    @Test
    public void testShowFunction() {
        String str = "show user functions";
        Matcher matcher = SHOW_FUNCTIONS.matcher(str);
        System.out.println(matcher.matches());
    }


}
