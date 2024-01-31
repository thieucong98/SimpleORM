package com.popeye.orm;

import com.popeye.orm.common.Parameter;
import com.popeye.orm.entity.ExampleEntity;
import com.popeye.orm.entity.TestId;
import com.popeye.orm.repository.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.*;

public class DemoCrudExample3Application {
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

        DynamicRepository dynamicRepository = new DynamicRepositoryImpl(connection);

        List<Map<String, Object>> allWithDynamicObject = dynamicRepository.findAllWithDynamicObject(
                "SELECT * FROM demo_orm demo_orm  WHERE code_name = ?  ORDER BY code_id DESC  LIMIT 20 OFFSET 0",
                Parameter.of("code_name", "asdkl")
        );

        List<Map<String, Object>> allWithDynamicObject2 = dynamicRepository.findAllWithDynamicObject(
                "SELECT * FROM demo_orm demo_orm   ORDER BY code_id DESC  LIMIT 20 OFFSET 0"
        );
        connection.close();
    }

}
