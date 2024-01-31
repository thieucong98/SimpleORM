package com.popeye.orm.repository.crud;

import com.popeye.orm.common.Parameter;

import java.util.List;

public interface DeleteRepository<Entity, Identity> {
    Boolean delete(Entity entity);

    Boolean deleteAll(List<Entity> entities);

    Boolean deleteById(Identity id);


    Boolean delete(Parameter... parameters);

    Boolean delete(List<Parameter> parameters);
}
