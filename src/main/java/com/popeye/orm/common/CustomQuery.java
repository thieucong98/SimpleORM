package com.popeye.orm.common;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class CustomQuery<R> {
    private String query;
    private List<Parameter> parameters;

    private R resultData;

    private CustomQuery(String query, List<Parameter> parameters) {
        this.query = query;
        this.parameters = parameters;
    }

    public static <R> CustomQuery<R> of(String query) {
        return new CustomQuery<>(query, new ArrayList<>());
    }

    public static <R> CustomQuery<R> of(String query, List<Parameter> parameters) {
        return new CustomQuery<>(query, parameters);
    }

    public static <I, R> CustomQuery<R> of(String query, Parameter parameters) {
        return of(query, Collections.singletonList(parameters));
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public List<Parameter> getParameters() {
        return parameters;
    }

    public void setParameters(List<Parameter> parameters) {
        this.parameters = parameters;
    }

    public R getResultData() {
        return resultData;
    }

    public CustomQuery<R> updateResult(R data) {
        this.resultData = data;
        return this;
    }

    public void buildPrepareStatementParameters(PreparedStatement preparedStatement) throws SQLException {
        if (this.parameters == null || this.parameters.isEmpty()) {
            return;
        }

        AtomicInteger counter = new AtomicInteger(1);
        for (Parameter parameter : this.parameters) {
            preparedStatement.setObject(counter.getAndIncrement(), parameter.getValue());
        }
    }

}
