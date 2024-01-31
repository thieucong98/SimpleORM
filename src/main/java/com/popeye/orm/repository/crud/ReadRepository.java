package com.popeye.orm.repository.crud;

import com.popeye.orm.common.CustomQuery;
import com.popeye.orm.common.Pagination;
import com.popeye.orm.common.Parameter;
import com.popeye.orm.common.Sort;

import java.util.List;
import java.util.Optional;

public interface ReadRepository<Entity, Identity> {
    Optional<Entity> findById(Identity id);

    List<Entity> findAll();

    List<Entity> findAllWithExcludeFields(List<String> fields);

    List<Entity> findAll(Parameter... parameters);

    List<Entity> findAllWithExcludeFields(List<String> fields, Parameter... parameters);

    List<Entity> findAll(Sort... sorts);

    List<Entity> findAll(Pagination pagination);

    List<Entity> findAllWithExcludeFields(List<String> fields, Sort... sorts);

    List<Entity> findAll(List<Parameter> parameters);

    List<Entity> findAllWithExcludeFields(List<String> fields, List<Parameter> parameters);

    List<Entity> findAll(List<Parameter> parameters, List<Sort> sorts);

    List<Entity> findAllWithExcludeFields(List<String> fields, List<Parameter> parameters, List<Sort> sorts);

    List<Entity> findAll(List<Parameter> parameters, Pagination pagination);

    List<Entity> findAllWithExcludeFields(List<String> fields, List<Parameter> parameters, Pagination pagination);

    List<Entity> findAll(List<Parameter> parameters, List<Sort> sorts, Pagination pagination);

    List<Entity> findAllWithExcludeFields(List<String> fields, List<Parameter> parameters, List<Sort> sorts, Pagination pagination);

    List<Entity> findAllByCustomQuery(CustomQuery<List<Entity>> customQuery);

    Entity findByCustomQuery(CustomQuery<Entity> customQuery);

    Integer count();

    Integer count(Parameter... parameters);

    Integer count(List<Parameter> parameters);
}
