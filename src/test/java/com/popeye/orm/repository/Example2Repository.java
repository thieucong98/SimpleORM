package com.popeye.orm.repository;


import com.popeye.orm.entity.Example2Entity;

import java.sql.Connection;

public class Example2Repository extends CrudRepositoryImpl<Example2Entity, String> {
    public Example2Repository(Connection connection) {
        super(connection);
    }
}
