package com.popeye.orm.repository;

import com.popeye.orm.repository.crud.CreateRepository;
import com.popeye.orm.repository.crud.DeleteRepository;
import com.popeye.orm.repository.crud.ReadRepository;
import com.popeye.orm.repository.crud.UpdateRepository;

public interface CrudRepository<Entity, Identity> extends DynamicRepository, CreateRepository<Entity>, ReadRepository<Entity, Identity>, UpdateRepository<Entity>, DeleteRepository<Entity, Identity> {
}
