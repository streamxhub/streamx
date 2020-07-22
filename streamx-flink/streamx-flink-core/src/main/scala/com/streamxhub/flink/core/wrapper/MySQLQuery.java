package com.streamxhub.flink.core.wrapper;

import java.io.Serializable;


/**
 * @author benjobs
 */
public class MySQLQuery implements Serializable {
    private String table;

    /**
     * 偏移字段
     */
    private String field;
    /**
     * 偏移字段的值,可以是时间戳,或者自增ID,必须是单调递增(可以相等)参考该值往后拉取数据.
     */
    private String offset;

    /**
     * 条件连接操作符
     */
    private String option;

    /**
     * 采用fetch模式拉取,每次拉取的大小
     */
    private Integer fetchSize = 2000;
    private Integer size = 0;
    private String lastOffset;

    public MySQLQuery() {
    }

    public MySQLQuery(String table, String field, String offset, String option) {
        this.table = table;
        this.field = field;
        this.offset = offset;
        this.option = option;
    }

    public boolean isEmpty() {
        return this.size == 0;
    }

    public String getSQL() {
        return String.format("SELECT * FROM %s WHERE %s %s '%s' ORDER BY %s ASC", table, field, option, offset, field);
    }

    public String getTable() {
        return table;
    }

    public void setTable(String table) {
        this.table = table;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }

    public String getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = offset;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
    }

    public Integer getFetchSize() {
        return fetchSize;
    }

    public void setFetchSize(Integer fetchSize) {
        this.fetchSize = fetchSize;
    }

    public Integer getSize() {
        return size;
    }

    public void setSize(Integer size) {
        this.size = size;
    }

    public String getLastOffset() {
        return lastOffset;
    }

    public void setLastOffset(String lastOffset) {
        this.lastOffset = lastOffset;
    }
}
