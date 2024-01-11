package com.popeye.orm.repository;

import com.popeye.orm.entity.Content;
import com.popeye.orm.entity.ContentId;

import java.sql.Connection;

public class ContentRepository extends CrudRepositoryImpl<Content, ContentId> {
    public ContentRepository(Connection connection) {
        super(connection);
    }
}
