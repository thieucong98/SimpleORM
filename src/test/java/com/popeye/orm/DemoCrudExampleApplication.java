package com.popeye.orm;

import com.mysql.cj.jdbc.JdbcConnection;
import com.popeye.orm.common.CustomQuery;
import com.popeye.orm.common.Pagination;
import com.popeye.orm.common.Parameter;
import com.popeye.orm.common.Sort;
import com.popeye.orm.entity.ExampleEntity;
import com.popeye.orm.entity.TestId;
import com.popeye.orm.repository.CrudRepository;
import com.popeye.orm.repository.CrudRepositoryImpl;
import com.popeye.orm.repository.ExampleRepository;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class DemoCrudExampleApplication {
    /**
     * @TABLE_TEST: <pre>
     *     create table demo_orm
     *     (
     *         code_id                char(10)                           not null,
     *         code_name              varchar(128)                       not null,
     *         item_key               varchar(64)                        not null,
     *         item_value_ja          varchar(256)                       null,
     *         item_value_en          varchar(256)                       null,
     *         item_value_pair        varchar(255)                       null,
     *         parent_code_id         char(5)                            null,
     *         parent_item_key        varchar(128)                       null,
     *         parent_item_key_backup varchar(45)                        null,
     *         parent_item_key_new    varchar(45)                        null,
     *         created_timestamp      datetime default CURRENT_TIMESTAMP null,
     *         updated_timestamp      datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
     *         primary key (code_id, item_key, code_name)
     *     )
     *         charset = utf8mb4;
     * </pre>
     * @DATA_TEST: <pre>
     *     INSERT INTO hyoka_system.demo_orm (code_id, code_name, item_key, item_value_ja, item_value_en, item_value_pair, parent_code_id, parent_item_key, parent_item_key_backup, parent_item_key_new, created_timestamp, updated_timestamp) VALUES ('0203', 'asdkl', 'taggggg', null, '', null, null, null, null, null, '2023-02-06 16:04:20', '2023-02-06 16:04:20');
     *     INSERT INTO hyoka_system.demo_orm (code_id, code_name, item_key, item_value_ja, item_value_en, item_value_pair, parent_code_id, parent_item_key, parent_item_key_backup, parent_item_key_new, created_timestamp, updated_timestamp) VALUES ('1', '専攻イニシャルコード', 'AB', '建築・環境デザイン学科', '', null, null, null, null, null, '2022-12-15 16:26:21', '2023-02-06 15:32:07');
     *     INSERT INTO hyoka_system.demo_orm (code_id, code_name, item_key, item_value_ja, item_value_en, item_value_pair, parent_code_id, parent_item_key, parent_item_key_backup, parent_item_key_new, created_timestamp, updated_timestamp) VALUES ('2', '専攻イニシャルコード', 'AC', '工業化学科', '', null, null, null, null, null, '2022-12-15 16:26:21', '2023-02-06 15:32:07');
     * </pre>
     */

    public static void main(String[] args) throws SQLException, ClassNotFoundException, IllegalAccessException {

        CrudRepositoryImpl.setShowQuery(true);
        Connection connection = ConnectionPool.getConnection();

        CrudRepository<ExampleEntity, TestId> crudRepository = new ExampleRepository(connection);

        List<ExampleEntity> testFindAll = crudRepository.findAll();
        Optional<ExampleEntity> byId = crudRepository.findById(TestId.of("6", "専攻イニシャルコード", "ACM"));
        List<ExampleEntity> testFindByParameter = crudRepository.findAll(
                Parameter.of("code_id", "1"),
                Parameter.of("code_name", "科目名")
        );
        List<Parameter> parameters = Arrays.asList(
//            Parameter.of("code_id", "22204"),
                Parameter.of("code_name", "asdkl")
        );
        List<ExampleEntity> testFindByParameterWithPagination = crudRepository.findAll(
                parameters,
                Pagination.of(1, 20)
        );
        List<ExampleEntity> testFindByParameterWithDefaultSortAndPagination = crudRepository.findAll(
                parameters,
                Arrays.asList(Sort.of("code_id", Sort.Type.DESC)),
                Pagination.of(1, 20)
        );
        List<ExampleEntity> testFindByParameterWithCustomSortAndPagination = crudRepository.findAll(
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

        ExampleEntity insert = crudRepository.insert(new ExampleEntity(TestId.of("110123", "asdkl", "taggggg"), ""));
        List<ExampleEntity> insert1 = crudRepository.insert(
                Arrays.asList(
                        new ExampleEntity(TestId.of("919123", "asdkl", "taggggg"), ""),
                        new ExampleEntity(TestId.of("91123", "asdkl", "taggggg"), "")
                )
        );

        ExampleEntity replace = crudRepository.replace(new ExampleEntity(TestId.of("3324111", "asdkl", "taggggg"), ""));
        List<ExampleEntity> replace1 = crudRepository.replace(
                Arrays.asList(
                        new ExampleEntity(TestId.of("332341114", "asdkl", "taggggg"), "test1"),
                        new ExampleEntity(TestId.of("02031114", "asdkl", "taggggg"), "test1")
                )
        );

        ExampleEntity update = crudRepository.update(new ExampleEntity(TestId.of("34", "asdkl", "taggggg"), "test 234234 lfakj"));
        List<ExampleEntity> update1 = crudRepository.update(
                Arrays.asList(
                        new ExampleEntity(TestId.of("33234111", "asdkl", "taggggg"), "te234234 st1"),
                        new ExampleEntity(TestId.of("0203111", "asdkl", "taggggg"), "tes234234t1")
                )
        );

        Boolean delete = crudRepository.delete(new ExampleEntity(TestId.of("3324111", "asdkl", "taggggg"), ""));
        Boolean codeId = crudRepository.delete(Parameter.of("code_id", 5));

        List<ExampleEntity> allByCustomQuery = crudRepository.findAllByCustomQuery(CustomQuery.of("SELECT demo_orm.code_id ,demo_orm.code_name ,demo_orm.item_key ,demo_orm.item_value_ja ,demo_orm.item_value_en ,demo_orm.parent_code_id ,demo_orm.parent_item_key FROM demo_orm demo_orm  WHERE code_name = 'asdkl'  ORDER BY code_id DESC  LIMIT 20 OFFSET 0"));
        List<ExampleEntity> allByCustomQuery2 = crudRepository.findAllByCustomQuery(
                CustomQuery.of(
                        "SELECT demo_orm.code_id ,demo_orm.code_name ,demo_orm.item_key ,demo_orm.item_value_ja ,demo_orm.item_value_en ,demo_orm.parent_code_id ,demo_orm.parent_item_key FROM demo_orm demo_orm  WHERE code_name = ?  ORDER BY code_id DESC  LIMIT 20 OFFSET 0",
                        Parameter.of("code_name", "asdkl")
                ));

        connection.close();
    }

}
