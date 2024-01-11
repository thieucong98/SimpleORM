/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.popeye.orm.entity;

import com.popeye.orm.anotation.Column;
import com.popeye.orm.anotation.Id;
import com.popeye.orm.anotation.Table;
import lombok.Data;

import java.io.Serializable;


/**
 * CREATE TABLE `content` (
 *   `system_id` varchar(50) NOT NULL,
 *   `path_full` varchar(256) NOT NULL,
 *   `name` varchar(128) NOT NULL,
 *   `rd_id` varchar(50) NOT NULL,
 *   `path_level_1` varchar(128) DEFAULT NULL,
 *   `path_level_2` varchar(128) DEFAULT NULL,
 *   `path_level_3` varchar(128) DEFAULT NULL,
 *   `Path_level_4` varchar(128) DEFAULT NULL,
 *   `data` longtext,
 *   PRIMARY KEY (`system_id`,`path_full`,`name`)
 * ) ENGINE=InnoDB DEFAULT CHARSET=utf8 ROW_FORMAT=DYNAMIC
 * */
@Data
@Table(name = "content")
public class Content implements Serializable {

    @Id
    private ContentId id;
    @Column(name = "rd_id")
    private String _rdId;

    @Column(name = "path_level_1")
    private String _group_1;
    @Column(name = "path_level_2")

    private String _group_2;
    @Column(name = "path_level_3")

    private String _group_3;
    @Column(name = "Path_level_4")

    private String _group_4;

    @Column(name = "data")

    private String _value;

    public Content() {
    }

    public Content(@Id ContentId id) {
        this.id = id;
    }

    public Content(@Id ContentId id, @Column(name = "rd_id") String _rdId, @Column(name = "path_level_1") String _group_1, @Column(name = "path_level_2") String _group_2, @Column(name = "path_level_3") String _group_3, @Column(name = "Path_level_4") String _group_4, @Column(name = "data") String _value) {
        this.id = id;
        this._rdId = _rdId;
        this._group_1 = _group_1;
        this._group_2 = _group_2;
        this._group_3 = _group_3;
        this._group_4 = _group_4;
        this._value = _value;
    }
}
