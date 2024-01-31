package com.popeye.orm;

import com.mysql.cj.jdbc.JdbcConnection;
import com.popeye.orm.common.CustomQuery;
import com.popeye.orm.common.Pagination;
import com.popeye.orm.common.Parameter;
import com.popeye.orm.common.Sort;
import com.popeye.orm.entity.Example2Entity;
import com.popeye.orm.repository.CrudRepository;
import com.popeye.orm.repository.CrudRepositoryImpl;
import com.popeye.orm.repository.Example2Repository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DemoCrudExample2Application {
    public static void main(String[] args) throws SQLException, ClassNotFoundException, IllegalAccessException {
        CrudRepositoryImpl.setShowQuery(true);

        Connection connection = ConnectionPool.getConnection();
        CrudRepository<Example2Entity, String> crudRepository = new Example2Repository(connection);

        List<Example2Entity> testFindAll = crudRepository.findAll();
        Optional<Example2Entity> byId = crudRepository.findById("6");
        List<Example2Entity> testFindByParameter = crudRepository.findAll(
                Parameter.of("code_id", "1"),
                Parameter.of("code_name", "科目名")
        );
        List<Parameter> parameters = Arrays.asList(
//            Parameter.of("code_id", "22204"),
                Parameter.of("code_name", "asdkl")
        );
        List<Example2Entity> testFindByParameterWithPagination = crudRepository.findAll(
                parameters,
                Pagination.of(1, 20)
        );
        List<Example2Entity> testFindByParameterWithDefaultSortAndPagination = crudRepository.findAll(
                parameters,
                Arrays.asList(Sort.of("code_id", Sort.Type.DESC)),
                Pagination.of(1, 20)
        );
        List<Example2Entity> testFindByParameterWithCustomSortAndPagination = crudRepository.findAll(
//            parameters,
                new ArrayList<>(),
                Arrays.asList(Sort.of("item_key", Sort.Type.DESC), Sort.of("code_id", Sort.Type.DESC)),
                Pagination.of(1, 20)
        );

        Integer countAll = crudRepository.count();
        Integer countAllWithCondition = crudRepository.count(Parameter.of("code_id", "1"));
        Integer countAllWithMultipleConditions = crudRepository.count(
                Parameter.of("code_id", "2"),
                Parameter.of("item_key", "AC")
        );

        Example2Entity insert = crudRepository.insert(new Example2Entity("110we123", "asdkl", "taggggg", ""));
        List<Example2Entity> insert1 = crudRepository.insert(
                Arrays.asList(
                        new Example2Entity("9191e23", "asdkl", "taggggg", ""),
                        new Example2Entity("911e23", "asdkl", "taggggg", "")
                )
        );

        Example2Entity replace = crudRepository.replace(new Example2Entity("3324111", "asdkl", "taggggg", ""));
        List<Example2Entity> replace1 = crudRepository.replace(
                Arrays.asList(
                        new Example2Entity("332341114", "asdkl", "taggggg", "test1"),
                        new Example2Entity("02031114", "asdkl", "taggggg", "test1")
                )
        );

        Example2Entity update = crudRepository.update(new Example2Entity("34", "asdkl", "taggggg", "test 234234 lfakj"));
        List<Example2Entity> update1 = crudRepository.update(
                Arrays.asList(
                        new Example2Entity("33234111", "asdkl", "taggggg", "te234234 st1"),
                        new Example2Entity("0203111", "asdkl", "taggggg", "tes234234t1")
                )
        );

        Boolean delete = crudRepository.delete(new Example2Entity("3324111", "asdkl", "taggggg", ""));
        Boolean codeId = crudRepository.delete(Parameter.of("code_id", 5));

        List<Example2Entity> allByCustomQuery = crudRepository.findAllByCustomQuery(CustomQuery.of("SELECT demo_orm.code_id ,demo_orm.code_name ,demo_orm.item_key ,demo_orm.item_value_ja ,demo_orm.item_value_en ,demo_orm.parent_code_id ,demo_orm.parent_item_key FROM demo_orm demo_orm  WHERE code_name = 'asdkl'  ORDER BY code_id DESC  LIMIT 20 OFFSET 0"));
        List<Example2Entity> allByCustomQuery2 = crudRepository.findAllByCustomQuery(
                CustomQuery.of(
                        "SELECT demo_orm.code_id ,demo_orm.code_name ,demo_orm.item_key ,demo_orm.item_value_ja ,demo_orm.item_value_en ,demo_orm.parent_code_id ,demo_orm.parent_item_key FROM demo_orm demo_orm  WHERE code_name = ?  ORDER BY code_id DESC  LIMIT 20 OFFSET 0",
                        Parameter.of("code_name", "asdkl")
                ));

        connection.close();
    }

}
