package com.popeye.orm.repository.crud;

import java.sql.SQLException;
import java.util.List;

public interface CreateRepository<Entity> {
    Entity insert(Entity entity) throws SQLException, IllegalAccessException;

    List<Entity> insert(List<Entity> entities) throws SQLException, IllegalAccessException;
}
