package com.popeye.orm;

import com.popeye.orm.common.Pagination;
import com.popeye.orm.common.Parameter;
import com.popeye.orm.common.Sort;
import com.popeye.orm.entity.Content;
import com.popeye.orm.entity.ContentId;
import com.popeye.orm.repository.ContentRepository;
import com.popeye.orm.repository.CrudRepository;
import com.popeye.orm.repository.CrudRepositoryImpl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DemoCrudContentApplication {
    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        CrudRepositoryImpl.setShowQuery(true);
        Connection connection = ConnectionPool.getConnection();

        CrudRepository<Content, ContentId> crudRepository = new ContentRepository(connection);

        List<Content> testFindAll = crudRepository.findAll();
        Optional<Content> byId = crudRepository.findById(ContentId.of("6", "専攻イニシャルコード", "ACM"));
        List<Content> testFindByParameter = crudRepository.findAll(Parameter.of("system_id", "1000043202"), Parameter.of("name", "RDID"));
        List<Parameter> parameters = Arrays.asList(
//            Parameter.of("system_id", "22204"),
                Parameter.of("system_id", "1000043202"));
        List<Content> testFindByParameterWithPagination = crudRepository.findAll(parameters, Pagination.of(1, 20));
        List<Content> testFindByParameterWithDefaultSortAndPagination = crudRepository.findAll(parameters, Arrays.asList(Sort.of("system_id", Sort.Type.DESC)), Pagination.of(1, 20));
        List<Content> testFindByParameterWithCustomSortAndPagination = crudRepository.findAll(
//            parameters,
                new ArrayList<>(), Arrays.asList(Sort.of("name", Sort.Type.DESC), Sort.of("system_id", Sort.Type.DESC)), Pagination.of(1, 20));

        Integer countAll = crudRepository.count();
        Integer countAllWithCondition = crudRepository.count(Parameter.of("system_id", "1000043202"));
        Integer countAllWithMultipleConditions = crudRepository.count(Parameter.of("system_id", "1000043202"), Parameter.of("name", "RDID"));

        connection.close();
    }

}
