package com.popeye.orm.repository;

import com.popeye.orm.anotation.Column;
import com.popeye.orm.anotation.Id;
import com.popeye.orm.anotation.Table;
import com.popeye.orm.anotation.Transient;
import com.popeye.orm.common.Parameter;
import com.popeye.orm.common.*;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.*;
import java.sql.Date;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class CrudRepositoryImpl<E, Identity> extends DynamicRepositoryImpl implements CrudRepository<E, Identity> {
    private static final Logger logger = Logger.getLogger(CrudRepositoryImpl.class.getName());

    private Class<E> entityClass;
    private Class<Identity> identityClass;

    private Field idField;

    private String tableName;

    private Map<Field, String> columnNames = new LinkedHashMap<>();

    private boolean isReadOnly;

    private boolean isView;

    public CrudRepositoryImpl(Connection connection) {
        super(connection);
        this.loadEntityInfo();
    }

    /**
     * @USING_FOR: Turn on/off logging sql query
     * @DEFAULT_VALUE: false.
     */

    @Override
    public Optional<E> findById(Identity identity) {
        this.checkView();
        List<Parameter> idParameters = this.buildParameterFromId(identity);
        List<E> data = this.findAll(idParameters);
        if (data.isEmpty() || data.size() == 1) {
            return data.stream().findFirst();
        }

        logger.log(Level.WARNING, "FindById ERROR: find more than 1 record with id = " + identity.toString());
        return Optional.empty();
    }

    @Override
    public List<E> findAll() {
        return this.findAll(null, null, null);
    }

    @Override
    public List<E> findAllWithExcludeFields(List<String> ignoreFields) {
        return this.findAllWithExcludeFields(ignoreFields, null, null, null);
    }

    @Override
    public List<E> findAll(Parameter... parameters) {
        return this.findAll(Arrays.asList(parameters), null, null);
    }

    @Override
    public List<E> findAllWithExcludeFields(List<String> ignoreFields, Parameter... parameters) {
        return this.findAllWithExcludeFields(ignoreFields, Arrays.asList(parameters), null, null);
    }

    @Override
    public List<E> findAll(Sort... sorts) {
        return this.findAll(null, Arrays.asList(sorts), null);
    }

    @Override
    public List<E> findAll(Pagination pagination) {
        return this.findAll(null, null, pagination);
    }

    @Override
    public List<E> findAllWithExcludeFields(List<String> ignoreFields, Sort... sorts) {
        return this.findAllWithExcludeFields(ignoreFields, null, Arrays.asList(sorts), null);
    }

    @Override
    public List<E> findAll(List<Parameter> parameters) {
        return this.findAll(parameters, null, null);
    }

    @Override
    public List<E> findAllWithExcludeFields(List<String> ignoreFields, List<Parameter> parameters) {
        return this.findAllWithExcludeFields(ignoreFields, parameters, null, null);
    }

    @Override
    public List<E> findAll(List<Parameter> parameters, Pagination pagination) {
        return this.findAll(parameters, null, pagination);
    }

    @Override
    public List<E> findAllWithExcludeFields(List<String> ignoreFields, List<Parameter> parameters, Pagination pagination) {
        return this.findAllWithExcludeFields(ignoreFields, parameters, null, pagination);
    }

    @Override
    public List<E> findAll(List<Parameter> parameters, List<Sort> sorts) {
        return this.findAll(parameters, sorts, null);
    }

    @Override
    public List<E> findAllWithExcludeFields(List<String> ignoreFields, List<Parameter> parameters, List<Sort> sorts) {
        return this.findAllWithExcludeFields(ignoreFields, parameters, sorts, null);
    }

    @Override
    public List<E> findAll(List<Parameter> parameters, List<Sort> sorts, Pagination pagination) {
        this.checkView();
        String query = this.buildSelectQuery(parameters, sorts, pagination);
        List<E> result = new ArrayList<>();
        try (PreparedStatement statement = this.getConnection().prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            this.buildParameterForStatement(statement, parameters);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                E item = getEntity(rs);
                result.add(item);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<E> findAllWithExcludeFields(List<String> ignoreFields, List<Parameter> parameters, List<Sort> sorts, Pagination pagination) {
        this.checkView();
        String query = this.buildSelectQueryWithIgnoreFields(ignoreFields, parameters, sorts, pagination);
        List<E> result = new ArrayList<>();
        try (PreparedStatement statement = this.getConnection().prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            this.buildParameterForStatement(statement, parameters);
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                E item = getEntity(rs);
                result.add(item);
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }


    @Override
    public E insert(E entity) throws SQLException, IllegalAccessException {
        return this.insert(Collections.singletonList(entity)).stream().findFirst().orElse(entity);
    }

    @Override
    public List<E> insert(List<E> entities) throws SQLException, IllegalAccessException {
        this.checkUpsert();
        if (entities == null || entities.isEmpty()) {
            logger.warning("Skip insert because entities is null or is empty");
            return entities;
        }
        String query = this.buildInsertField();
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (E entity : entities) {
                List<Parameter> parameterForInsertStatement = this.getParameterInsertStatement(entity);
                this.buildParameterForStatement(statement, parameterForInsertStatement);
                statement.addBatch();
            }

            int[] countUpdateRows = statement.executeBatch();
            logger.info(String.format("Insert %d rows in table %s with results: %s", Arrays.stream(countUpdateRows).sum(), this.tableName, Arrays.toString(countUpdateRows)));
        } catch (SQLException | IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        return entities;
    }

    @Override
    public E update(E entity) throws SQLException, IllegalAccessException {
        List<Parameter> idParameters = this.buildIdParameterFromEntity(entity);
        return this.update(entity, idParameters);
    }

    @Override
    public E updateWithExcludeFields(E entity, List<String> ignoreFields) throws SQLException, IllegalAccessException {
        List<Parameter> idParameters = this.buildIdParameterFromEntity(entity);
        return this.updateWithExcludeFields(entity, ignoreFields, idParameters);
    }

    @Override
    public E update(E entity, Parameter... parameters) throws SQLException, IllegalAccessException {
        return this.update(entity, Arrays.asList(parameters));
    }

    @Override
    public E updateWithExcludeFields(E entity, List<String> ignoreFields, Parameter... parameters) throws SQLException, IllegalAccessException {
        return this.updateWithExcludeFields(entity, ignoreFields, Arrays.asList(parameters));
    }

    @Override
    public E update(E entity, List<Parameter> parameters) throws SQLException, IllegalAccessException {
        this.checkUpsert();
        String query = this.buildUpdateQuery(parameters);
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            List<Parameter> parameterForUpdateStatement = this.getParameterUpdateStatement(entity, parameters);
            this.buildParameterForStatement(statement, parameterForUpdateStatement);
            int countUpdateRows = statement.executeUpdate();
            logger.info(String.format("Update %d rows in table %s", countUpdateRows, this.tableName));
        } catch (SQLException | IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        return entity;
    }

    @Override
    public E updateWithExcludeFields(E entity, List<String> ignoreFields, List<Parameter> parameters) throws SQLException, IllegalAccessException {
        this.checkUpsert();
        String query = this.buildUpdateQueryWithExcludeFields(ignoreFields, parameters);
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            List<Parameter> parameterForUpdateStatement = this.getParameterUpdateStatementWithExcludeFields(entity, ignoreFields, parameters);
            this.buildParameterForStatement(statement, parameterForUpdateStatement);
            int countUpdateRows = statement.executeUpdate();
            logger.info(String.format("Update %d rows in table %s", countUpdateRows, this.tableName));
        } catch (SQLException | IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        return entity;
    }

    @Override
    public List<E> update(List<E> entities) throws SQLException, IllegalAccessException {
        this.checkUpsert();
        if (entities == null || entities.isEmpty()) {
            logger.warning("Skip update because entities is null or is empty");
            return entities;
        }
        String query = this.buildUpdateQuery(this.buildIdParameterFromEntity(entities.get(0)));
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (E entity : entities) {
                List<Parameter> idParameters = this.buildIdParameterFromEntity(entity);
                List<Parameter> parameterForUpdateStatement = this.getParameterUpdateStatement(entity, idParameters);
                this.buildParameterForStatement(statement, parameterForUpdateStatement);
                statement.addBatch();
            }

            int[] countUpdateRows = statement.executeBatch();
            logger.info(String.format("Update %d rows in table %s with result: %s", Arrays.stream(countUpdateRows).sum(), this.tableName, Arrays.toString(countUpdateRows)));
        } catch (SQLException | IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        return entities;
    }

    @Override
    public List<E> updateWithExcludeFields(List<E> entities, List<String> ignoreFields) throws SQLException, IllegalAccessException {
        this.checkUpsert();
        if (entities == null || entities.isEmpty()) {
            logger.warning("Skip updateWithExcludeFields because entities is null or is empty");
            return entities;
        }
        String query = this.buildUpdateQueryWithExcludeFields(ignoreFields, this.buildIdParameterFromEntity(entities.get(0)));
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (E entity : entities) {
                List<Parameter> idParameters = this.buildIdParameterFromEntity(entity);
                List<Parameter> parameterForUpdateStatement = this.getParameterUpdateStatementWithExcludeFields(entity, ignoreFields, idParameters);
                this.buildParameterForStatement(statement, parameterForUpdateStatement);
                statement.addBatch();
            }

            int[] countUpdateRows = statement.executeBatch();
            logger.info(String.format("Update %d rows in table %s with result: %s", Arrays.stream(countUpdateRows).sum(), this.tableName, Arrays.toString(countUpdateRows)));
        } catch (SQLException | IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        return entities;
    }

    @Override
    public E replace(E entity) throws SQLException, IllegalAccessException {
        return this.replace(Collections.singletonList(entity)).stream().findFirst().orElse(entity);
    }

    @Override
    public List<E> replace(List<E> entities) throws SQLException, IllegalAccessException {
        this.checkUpsert();
        if (entities == null || entities.isEmpty()) {
            logger.warning("Skip replace because entities is null or is empty");
            return entities;
        }
        String query = this.buildReplaceQuery();
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            for (E entity : entities) {
                List<Parameter> parameterForInsertStatement = this.getParameterInsertStatement(entity);
                this.buildParameterForStatement(statement, parameterForInsertStatement);
                statement.addBatch();
            }

            int[] countUpdateRows = statement.executeBatch();
            logger.info(String.format("Replace %d rows in table %s", Arrays.stream(countUpdateRows).sum(), this.tableName, Arrays.toString(countUpdateRows)));
        } catch (SQLException | IllegalAccessException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
            throw e;
        }

        return entities;
    }

    @Override
    public Boolean delete(E entity) {
        List<Parameter> idParameters = this.buildIdParameterFromEntity(entity);
        return this.delete(idParameters);
    }

    @Override
    public Boolean deleteAll(List<E> entities) {
        if (EmbeddedId.class.isAssignableFrom(this.identityClass)) {
            throw new RuntimeException("This function is not support delete multiple entities with ID of entity is EmbeddedId class");
        }

        if (entities == null || entities.isEmpty()) {
            logger.warning("Skip delete because entities is null or is empty");
            return false;
        }

        List<Parameter> parameters = new ArrayList<>();
        entities.forEach(entity -> parameters.addAll(this.buildIdParameterFromEntity(entity)));
        if (parameters.isEmpty()) {
            logger.warning("Can not delete entities because can not build parameter");
            return false;
        }
        Parameter idParameters = Parameter.of(parameters.get(0).getFieldName(), Operator.IN, parameters.stream().map(i -> "'" + i.getValue() + "'").collect(Collectors.joining()));
        return this.delete(idParameters);
    }

    @Override
    public Boolean deleteById(Identity identity) {
        List<Parameter> idParameters = this.buildParameterFromId(identity);
        return this.delete(idParameters);
    }

    @Override
    public Boolean delete(Parameter... parameters) {
        return this.delete(Arrays.asList(parameters));
    }

    @Override
    public Boolean delete(List<Parameter> parameters) {
        this.checkUpsert();
        String query = this.buildDeleteQuery(parameters);
        Connection connection = this.getConnection();
        try (PreparedStatement statement = connection.prepareStatement(query)) {
            this.buildParameterForStatement(statement, parameters);
            int countUpdateRows = statement.executeUpdate();
            logger.info(String.format("Deleted %d rows in table %s", countUpdateRows, this.tableName));
            return true;
        } catch (SQLException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return false;
    }

    @Override
    public Integer count() {
        return this.count(new ArrayList<>());
    }

    @Override
    public Integer count(Parameter... parameters) {
        return this.count(Arrays.asList(parameters));
    }

    @Override
    public Integer count(List<Parameter> parameters) {
        this.checkView();
        String query = this.buildCountQuery(parameters);
        Integer result = 0;
        try (PreparedStatement statement = this.getConnection().prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)) {
            this.buildParameterForStatement(statement, parameters);
            ResultSet rs = statement.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }

        return result;
    }

    @Override
    public List<E> findAllByCustomQuery(CustomQuery<List<E>> customQuery) {
        List<E> result = new ArrayList<>();
        Connection connection = getConnection();
        String query = customQuery.getQuery();
        List<Parameter> parameters = customQuery.getParameters();
        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            buildParameterForStatement(preparedStatement, parameters);
            ResultSet rs = preparedStatement.executeQuery();
            while (rs.next()) {
                E entity = getEntity(rs);
                result.add(entity);
            }
        } catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return result;
    }

    @Override
    public E findByCustomQuery(CustomQuery<E> customQuery) {
        Connection connection = getConnection();
        String query = customQuery.getQuery();
        List<Parameter> parameters = customQuery.getParameters();

        try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
            buildParameterForStatement(preparedStatement, parameters);
            ResultSet rs = preparedStatement.executeQuery();
            return getEntity(rs);
        } catch (SQLException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            logger.log(Level.SEVERE, e.getMessage(), e);
        }
        return customQuery.getResultData();
    }

    private List<Parameter> getParameterUpdateStatement(E entity, List<Parameter> parameters) throws IllegalAccessException {
        List<Parameter> result = getParametersFromEntity(entity);
        result.addAll(parameters);

        return result;
    }

    private List<Parameter> getParameterUpdateStatementWithExcludeFields(E entity, List<String> ignoreFields, List<Parameter> parameters) throws IllegalAccessException {
        List<Parameter> result = new LinkedList<>();
        Map<String, String> collect = ignoreFields.stream().collect(Collectors.toMap(Function.identity(), Function.identity(), (i1, i2) -> i1));
        List<Parameter> parametersFromEntity = getParametersFromEntity(entity);
        for (Parameter parameter : parametersFromEntity) {
            if (!collect.containsKey(parameter.getFieldName())) {
                result.add(parameter);
            }
        }
        result.addAll(parameters);

        return result;
    }

    private List<Parameter> getParameterInsertStatement(E entity) throws IllegalAccessException {
        return getParametersFromEntity(entity);
    }

    private List<Parameter> getParametersFromEntity(E entity) throws IllegalAccessException {
        List<Parameter> result = new LinkedList<>();
        for (Map.Entry<Field, String> entry : this.getColumnNames().entrySet()) {
            Field key = entry.getKey();
            String value = entry.getValue();
            key.setAccessible(true);
            if (this.idField.equals(key) && EmbeddedId.class.isAssignableFrom(this.identityClass)) {
                Field[] embeddedIds = this.identityClass.getDeclaredFields();
                Map<Field, String> fieldStringMap = loadColumnNames(embeddedIds);
                for (Map.Entry<Field, String> e : fieldStringMap.entrySet()) {
                    Field idColumnType = e.getKey();
                    idColumnType.setAccessible(true);
                    String idValue = e.getValue();
                    result.add(Parameter.of(idValue, idColumnType.get(key.get(entity))));
                }
            } else {
                result.add(Parameter.of(value, key.get(entity)));
            }
        }
        return result;
    }

    protected Map<Field, String> getColumnNames() {
        return this.columnNames;
    }

    private void loadEntityInfo() {
        Type genericSuperclass = this.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        this.entityClass = ((Class) actualTypeArguments[0]);
        this.identityClass = ((Class) actualTypeArguments[1]);
        Field[] declaredFields = this.entityClass.getDeclaredFields();
        this.tableName = this.loadTableName(this.entityClass);
        this.isReadOnly = this.loadIsReadOnlyFlag(this.entityClass);
        this.isView = this.loadIsViewFlag(this.entityClass);
        this.columnNames = this.loadColumnNames(declaredFields);
        this.idField = this.loadIdField(declaredFields, this.entityClass);
    }


    private String loadTableName(Class<E> entityClass) {
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        return tableAnnotation != null && StringUtils.isNotEmpty(tableAnnotation.name()) ? tableAnnotation.name().trim().replace(" ", "") : entityClass.getName();
    }

    private boolean loadIsReadOnlyFlag(Class<E> entityClass) {
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        return tableAnnotation == null || tableAnnotation.readOnly();
    }

    private boolean loadIsViewFlag(Class<E> entityClass) {
        Table tableAnnotation = entityClass.getAnnotation(Table.class);
        return tableAnnotation == null || tableAnnotation.isView();
    }

    private Map<Field, String> loadColumnNames(Field[] declaredFields) {
        Map<Field, String> columnNames = new LinkedHashMap<>();
        for (Field field : declaredFields) {
            AnnotatedType annotatedType = field.getAnnotatedType();
            Column annotation = annotatedType.getAnnotation(Column.class);
            String columnName = annotation != null && StringUtils.isNotEmpty(annotation.name()) ? annotation.name() : field.getName();
            if (annotatedType.getAnnotation(Transient.class) == null) {
                columnNames.put(field, columnName);
            }
        }

        return columnNames;
    }

    private Field loadIdField(Field[] declaredFields, Class<E> entityClass) {
        for (Field field : declaredFields) {
            AnnotatedType annotatedType = field.getAnnotatedType();
            Id idAnnotation = annotatedType.getAnnotation(Id.class);
            if (idAnnotation != null && annotatedType.getAnnotation(Transient.class) == null) {
                return field;
            }
        }

        throw new RuntimeException(String.format("LoadIdField ERROR: %s entity missing id field", entityClass.getName()));
    }

    private String buildSelectField(Map<String, String> ignoreFields) {
        StringBuilder result = new StringBuilder(" SELECT ");
        List<String> fieldName = new LinkedList<>();
        for (Map.Entry<Field, String> entry : this.getColumnNames().entrySet()) {
            Field key = entry.getKey();
            if (this.idField.equals(key) && EmbeddedId.class.isAssignableFrom(this.identityClass)) {
                Field[] embeddedIds = this.identityClass.getDeclaredFields();
                Map<Field, String> fieldStringMap = loadColumnNames(embeddedIds);
                for (Map.Entry<Field, String> e : fieldStringMap.entrySet()) {
                    String idValue = e.getValue();
                    if (ignoreFields.containsKey(idValue)) {
                        continue;
                    }
                    fieldName.add(String.format("%s.%s ", this.tableName, idValue));
                }
            } else {
                String value = entry.getValue();
                if (ignoreFields.containsKey(value)) {
                    continue;
                }
                fieldName.add(String.format("%s.%s ", this.tableName, value));
            }
        }
        result.append(String.join(",", fieldName));
        result.append(String.format("FROM %s %s ", this.tableName, this.tableName));

        return result.toString();
    }


    private String buildInsertField() {
        List<String> columnNames = getOnlyColumnNames();

        return String.format(" INSERT INTO %s (%s) VALUES (%s) ",
                this.tableName,
                String.join(", ", columnNames),
                columnNames.stream().map(i -> "?").collect(Collectors.joining(", "))
        );
    }

    private String buildReplaceQuery() {
        List<String> columnNames = getOnlyColumnNames();

        return String.format(" REPLACE INTO %s (%s) VALUES (%s) ",
                this.tableName,
                String.join(", ", columnNames),
                columnNames.stream().map(i -> "?").collect(Collectors.joining(", "))
        );
    }

    private List<String> getOnlyColumnNames() {
        List<String> columnNames = new LinkedList<>();
        for (Map.Entry<Field, String> e : this.getColumnNames().entrySet()) {
            Field key = e.getKey();
            String value = e.getValue();
            if (this.idField.equals(key) && EmbeddedId.class.isAssignableFrom(this.identityClass)) {
                Field[] embeddedIds = this.identityClass.getDeclaredFields();
                Map<Field, String> fieldStringMap = loadColumnNames(embeddedIds);
                for (Map.Entry<Field, String> entry : fieldStringMap.entrySet()) {
                    Field idColumnType = entry.getKey();
                    idColumnType.setAccessible(true);
                    String idValue = entry.getValue();
                    columnNames.add(idValue);
                }
            } else {
                columnNames.add(value);
            }
        }
        return columnNames;
    }

    private String buildUpdateField(Map<String, String> ignoreFieldMap) {
        StringBuilder result = new StringBuilder(String.format(" UPDATE %s SET ", this.tableName));
        List<String> onlyColumnNames = this.getOnlyColumnNames();
        List<String> fieldName = onlyColumnNames.stream()
                .filter(columnName -> !ignoreFieldMap.containsKey(columnName))
                .map(columnName -> String.format("%s.%s = ?", this.tableName, columnName))
                .collect(Collectors.toCollection(LinkedList::new));

        result.append(String.join(",", fieldName));

        return result.toString();
    }

    private String buildWhereCondition(List<Parameter> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "";
        }

        if (this.validateParameter(parameters)) {
            StringBuilder result = new StringBuilder(" WHERE ");
            List<String> items = new LinkedList<>();
            for (Parameter parameter : parameters) {
                items.add(String.format("%s %s ? ", parameter.getFieldName(), parameter.getOperator().value));
            }
            result.append(String.join(" AND ", items));

            return result.toString();
        }
        return "";
    }

    private boolean validateParameter(List<Parameter> parameters) {
        for (Parameter i : parameters) {
            validateField(i.getFieldName(), this.getColumnNames());
        }
        return true;
    }

    private String buildOrderBy(List<Sort> sorts) {
        if (sorts == null || sorts.isEmpty()) {
            return "";
        }

        if (this.validateSort(sorts)) {
            StringBuilder result = new StringBuilder(" ORDER BY ");
            List<String> items = new LinkedList<>();
            for (Sort sort : sorts) {
                items.add(String.format("%s %s ", sort.getFieldName(), sort.getType().value));
            }
            result.append(String.join(",", items));

            return result.toString();
        }

        return "";
    }

    private boolean validateSort(List<Sort> sorts) {
        for (Sort sort : sorts) {
            validateField(sort.getFieldName(), this.getColumnNames());
        }
        return true;
    }

    private void validateField(String fieldName, Map<Field, String> columnNames) {
        boolean isExists = false;
        for (Map.Entry<Field, String> entry : columnNames.entrySet()) {
            Field key = entry.getKey();
            String value = entry.getValue();
            if (this.idField.equals(key) && EmbeddedId.class.isAssignableFrom(this.identityClass)) {
                Field[] embeddedIds = this.identityClass.getDeclaredFields();
                Map<Field, String> fieldStringMap = loadColumnNames(embeddedIds);
                for (Map.Entry<Field, String> e : fieldStringMap.entrySet()) {
                    String idValue = e.getValue();
                    if (idValue.equals(fieldName)) {
                        isExists = true;
                        break;
                    }
                }
            } else {
                if (value.equals(fieldName)) {
                    isExists = true;
                    break;
                }
            }
        }
        if (!isExists) {
            throw new RuntimeException(String.format("Invalid field name: %s", fieldName));
        }
    }

    private String buildPagination(Pagination pagination) {
        if (pagination == null) {
            return "";
        }

        return String.format(" LIMIT %d OFFSET %d", pagination.getSize(), pagination.getOffset());
    }

    private String buildSelectQuery(List<Parameter> parameters, List<Sort> sorts, Pagination pagination) {
        return this.buildSelectField(new HashMap<>())
                + this.buildWhereCondition(parameters)
                + this.buildOrderBy(sorts)
                + this.buildPagination(pagination);
    }

    private String buildSelectQueryWithIgnoreFields(List<String> ignoreFields, List<Parameter> parameters, List<Sort> sorts, Pagination pagination) {
        Map<String, String> fieldMap = ignoreFields.stream().collect(Collectors.toMap(Function.identity(), Function.identity(), (i1, i2) -> i1));

        return this.buildSelectField(fieldMap)
                + this.buildWhereCondition(parameters == null ? new ArrayList<>() : parameters.stream().filter(i -> !fieldMap.containsKey(i.getFieldName())).collect(Collectors.toList()))
                + this.buildOrderBy(sorts == null ? new ArrayList<>() : sorts.stream().filter(i -> !fieldMap.containsKey(i.getFieldName())).collect(Collectors.toList()))
                + this.buildPagination(pagination);
    }

    private String buildCountQuery(List<Parameter> parameters) {
        return String.format("SELECT COUNT(*) FROM %s %s %s", this.tableName, this.tableName, this.buildWhereCondition(parameters));
    }

    private String buildUpdateQuery(List<Parameter> parameters) {
        return this.buildUpdateField(new HashMap<>()) + this.buildWhereCondition(parameters);
    }

    private String buildUpdateQueryWithExcludeFields(List<String> ignoreFields, List<Parameter> parameters) {
        Map<String, String> ignoreFieldMap = ignoreFields.stream().collect(Collectors.toMap(Function.identity(), Function.identity(), (i1, i2) -> i1));
        return this.buildUpdateField(ignoreFieldMap) + this.buildWhereCondition(parameters);
    }

    private String buildUpdateQuery(Parameter... parameters) {
        return this.buildUpdateQuery(Arrays.asList(parameters));
    }

    private String buildDeleteQuery(List<Parameter> parameters) {
        return String.format("DELETE FROM %s ", this.tableName) + this.buildWhereCondition(parameters);
    }

    private String buildDeleteQuery(Parameter... parameters) {
        return this.buildDeleteQuery(Arrays.asList(parameters));
    }

    private Identity getIdValue(E entity) {
        try {
            this.idField.setAccessible(true);
            return (Identity) this.idField.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean validateField(String fieldName, Object data) {
        if (data instanceof String || data instanceof EmbeddedId || ClassUtils.isPrimitiveOrWrapper(data.getClass())) {
            return true;
        }

        logger.log(Level.WARNING, "Incorrect data for fieldName: " + fieldName);
        return false;
    }

    private List<Parameter> buildIdParameterFromEntity(E entity) {
        Identity id = this.getIdValue(entity);
        return this.buildParameterFromId(id);
    }

    private List<Parameter> buildParameterFromId(Identity identity) {
        if (identity instanceof String || ClassUtils.isPrimitiveOrWrapper(identity.getClass())) {
            return Collections.singletonList(Parameter.of(this.getColumnNames().get(this.idField), String.valueOf(identity)));
        }

        try {
            if (identity instanceof EmbeddedId) {
                Field[] idFields = identity.getClass().getDeclaredFields();
                Map<Field, String> idColumns = this.loadColumnNames(idFields);
                List<Parameter> result = new ArrayList<>();
                for (Map.Entry<Field, String> entry : idColumns.entrySet()) {
                    Field key = entry.getKey();
                    key.setAccessible(true);
                    String idName = entry.getValue();
                    result.add(Parameter.of(idName, String.valueOf(key.get(identity))));
                }
                return result;
            }
        } catch (IllegalAccessException e) {
            logger.log(Level.SEVERE, "buildParameterFromId errors: {0}", e);
            throw new RuntimeException("Can not build buildParameterFromId", e);
        }

        throw new RuntimeException("Incorrect identity type");
    }

    private E getEntity(ResultSet rs) throws InstantiationException, IllegalAccessException, SQLException, NoSuchMethodException, InvocationTargetException {
        Constructor<E> entityConstructor = this.entityClass.getDeclaredConstructor();
        entityConstructor.setAccessible(true);
        E item = entityConstructor.newInstance();
        Map<String, String> columnFromResultSet = this.getColumnByResultSet(rs);
        for (Map.Entry<Field, String> entry : this.getColumnNames().entrySet()) {
            Field field = entry.getKey();
            field.setAccessible(true);
            if (this.idField.equals(field) && EmbeddedId.class.isAssignableFrom(this.identityClass)) {
                Constructor<Identity> identityConstructor = this.identityClass.getDeclaredConstructor();
                Identity identity = identityConstructor.newInstance();
                Field[] embeddedIds = this.identityClass.getDeclaredFields();
                Map<Field, String> fieldStringMap = this.loadColumnNames(embeddedIds);
                for (Map.Entry<Field, String> e : fieldStringMap.entrySet()) {
                    Field idColumnType = e.getKey();
                    String idValue = e.getValue();
                    idColumnType.setAccessible(true);
                    idColumnType.set(identity, rs.getObject(idValue));
                }
                field.set(item, identity);
            } else if (columnFromResultSet.containsKey(entry.getValue())) {
                String columnName = entry.getValue();
                Object value = rs.getObject(columnName);
                if (value instanceof Timestamp && LocalDateTime.class == field.getType()) {
                    field.set(item, ((Timestamp) value).toLocalDateTime());
                } else if (value instanceof Date && LocalDate.class == field.getType()) {
                    field.set(item, ((Date) value).toLocalDate());
                } else {
                    field.set(item, value);
                }
            }
        }
        return item;
    }


    private void checkUpsert() {
        if (this.isReadOnly || this.isView) {
            throw new RuntimeException("Can not update, insert or delete because this table or view is read only");
        }
    }

    private void checkView() {
        if (this.isView) {
            throw new RuntimeException("This function can not use because entity is view");
        }
    }
}
