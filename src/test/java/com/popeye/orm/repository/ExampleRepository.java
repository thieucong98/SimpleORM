package com.popeye.orm.repository;


import com.popeye.orm.entity.ExampleEntity;
import com.popeye.orm.entity.TestId;

import java.sql.Connection;

public class ExampleRepository extends CrudRepositoryImpl<ExampleEntity, TestId> {
    public ExampleRepository(Connection connection) {
        super(connection);
    }
}
