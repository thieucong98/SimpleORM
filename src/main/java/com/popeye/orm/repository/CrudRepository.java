package com.popeye.orm.repository;

import com.popeye.orm.common.CustomQuery;
import com.popeye.orm.common.Pagination;
import com.popeye.orm.common.Parameter;
import com.popeye.orm.common.Sort;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<E, Identity> extends DynamicRepository {

    Optional<E> findById(Identity id);

    List<E> findAll();

    List<E> findAllWithExcludeFields(List<String> fields);

    List<E> findAll(Parameter... parameters);

    List<E> findAllWithExcludeFields(List<String> fields, Parameter... parameters);

    List<E> findAll(Sort... sorts);

    List<E> findAll(Pagination pagination);

    List<E> findAllWithExcludeFields(List<String> fields, Sort... sorts);

    List<E> findAll(List<Parameter> parameters);

    List<E> findAllWithExcludeFields(List<String> fields, List<Parameter> parameters);

    List<E> findAll(List<Parameter> parameters, List<Sort> sorts);

    List<E> findAllWithExcludeFields(List<String> fields, List<Parameter> parameters, List<Sort> sorts);

    List<E> findAll(List<Parameter> parameters, Pagination pagination);

    List<E> findAllWithExcludeFields(List<String> fields, List<Parameter> parameters, Pagination pagination);

    List<E> findAll(List<Parameter> parameters, List<Sort> sorts, Pagination pagination);

    List<E> findAllWithExcludeFields(List<String> fields, List<Parameter> parameters, List<Sort> sorts, Pagination pagination);

    E insert(E entity) throws SQLException, IllegalAccessException;

    List<E> insert(List<E> entities) throws SQLException, IllegalAccessException;

    E update(E entity) throws SQLException, IllegalAccessException;

    E updateWithExcludeFields(E entity, List<String> ignoreFields) throws SQLException, IllegalAccessException;

    E update(E entity, Parameter... parameters) throws SQLException, IllegalAccessException;

    E updateWithExcludeFields(E entity, List<String> ignoreFields, Parameter... parameters) throws SQLException, IllegalAccessException;

    E update(E entity, List<Parameter> parameters) throws SQLException, IllegalAccessException;

    E updateWithExcludeFields(E entity, List<String> ignoreFields, List<Parameter> parameters) throws SQLException, IllegalAccessException;

    List<E> update(List<E> entities) throws SQLException, IllegalAccessException;

    List<E> updateWithExcludeFields(List<E> entities, List<String> ignoreFields) throws SQLException, IllegalAccessException;

    E replace(E entity) throws SQLException, IllegalAccessException;

    List<E> replace(List<E> entities) throws SQLException, IllegalAccessException;

    Boolean delete(E entity);

    Boolean deleteAll(List<E> entities);

    Boolean deleteById(Identity id);


    Boolean delete(Parameter... parameters);

    Boolean delete(List<Parameter> parameters);

    Integer count();

    Integer count(Parameter... parameters);

    Integer count(List<Parameter> parameters);

    List<E> findAllByCustomQuery(CustomQuery<List<E>> customQuery);

    E findByCustomQuery(CustomQuery<E> customQuery);

}
