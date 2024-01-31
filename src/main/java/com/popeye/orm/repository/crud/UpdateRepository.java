package com.popeye.orm.repository.crud;

import com.popeye.orm.common.Parameter;

import java.sql.SQLException;
import java.util.List;

public interface UpdateRepository<Entity> {
    Entity update(Entity entity) throws SQLException, IllegalAccessException;

    Entity updateWithExcludeFields(Entity entity, List<String> ignoreFields) throws SQLException, IllegalAccessException;

    Entity update(Entity entity, Parameter... parameters) throws SQLException, IllegalAccessException;

    Entity updateWithExcludeFields(Entity entity, List<String> ignoreFields, Parameter... parameters) throws SQLException, IllegalAccessException;

    Entity update(Entity entity, List<Parameter> parameters) throws SQLException, IllegalAccessException;

    Entity updateWithExcludeFields(Entity entity, List<String> ignoreFields, List<Parameter> parameters) throws SQLException, IllegalAccessException;

    List<Entity> update(List<Entity> entities) throws SQLException, IllegalAccessException;

    List<Entity> updateWithExcludeFields(List<Entity> entities, List<String> ignoreFields) throws SQLException, IllegalAccessException;

    Entity replace(Entity entity) throws SQLException, IllegalAccessException;

    List<Entity> replace(List<Entity> entities) throws SQLException, IllegalAccessException;
}
