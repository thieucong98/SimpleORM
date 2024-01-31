package com.popeye.orm.repository;

import com.popeye.orm.common.Parameter;

import java.lang.reflect.InvocationTargetException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DynamicRepositoryImpl implements DynamicRepository {
    private static final Logger logger = Logger.getLogger(DynamicRepositoryImpl.class.getName());
    protected static boolean SHOW_QUERY = false;
    protected final Connection connection;

    public DynamicRepositoryImpl(Connection connection) {
        this.connection = connection;
    }

    protected static void logQuery(Statement statement) {
        if (SHOW_QUERY) {
            logger.info(statement.toString());
        }
    }

    public static void setShowQuery(boolean showQuery) {
        SHOW_QUERY = showQuery;
    }

    @Override
    public List<Map<String, Object>> findAllWithDynamicObject(String query) {
        return this.findAllWithDynamicObject(query, new ArrayList<>());
    }

    @Override
    public List<Map<String, Object>> findAllWithDynamicObject(String query, Parameter... parameters) {
        return this.findAllWithDynamicObject(query, Arrays.asList(parameters));
    }

    @Override
    public List<Map<String, Object>> findAllWithDynamicObject(String query, List<Parameter> parameters) {
        List<Map<String, Object>> result = new ArrayList<>();
        Connection connection = getConnection();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            buildParameterForStatement(preparedStatement, parameters);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                Map<String, Object> record = getDynamicObject(rs);
                result.add(record);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    private Map<String, Object> getDynamicObject(ResultSet rs) throws InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException, InvocationTargetException {
        Map<String, Object> record = new LinkedHashMap<>();
        Map<String, String> columnFromResultSet = this.getColumnByResultSet(rs);
        for (Map.Entry<String, String> entry : columnFromResultSet.entrySet()) {
            record.put(entry.getKey(), rs.getObject(entry.getKey()));
        }
        return record;
    }

    protected Map<String, String> getColumnByResultSet(ResultSet rs) throws SQLException {
        Map<String, String> result = new HashMap<>();
        int columnCount = rs.getMetaData().getColumnCount();
        for (int i = 1; i <= columnCount; i++) {
            String columnName = rs.getMetaData().getColumnLabel(i);
            result.put(columnName, columnName);
        }
        return result;
    }

    protected synchronized Connection getConnection() {
        return this.connection;
    }

    protected synchronized void buildParameterForStatement(PreparedStatement statement, List<Parameter> parameters) throws SQLException {
        if (parameters == null || parameters.isEmpty()) {
            logQuery(statement);
            return;
        }

        AtomicInteger atomicInteger = new AtomicInteger(1);
        for (Parameter parameter : parameters) {
            statement.setObject(atomicInteger.getAndIncrement(), parameter.getValue());
        }

        logQuery(statement);
    }
}
