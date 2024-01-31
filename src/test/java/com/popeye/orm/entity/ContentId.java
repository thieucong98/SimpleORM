/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.popeye.orm.entity;

import com.popeye.orm.anotation.Column;
import com.popeye.orm.common.EmbeddedId;

import java.io.Serializable;

public class ContentId extends EmbeddedId implements Serializable {

    @Column(name = "system_id")
    private String systemId;
    @Column(name = "path_full")
    private String _fullPath;
    @Column(name = "name")
    private String name;

    private ContentId(@Column(name = "system_id") String systemId, @Column(name = "path_full") String _fullPath, @Column(name = "name") String name) {
        this.systemId = systemId;
        this._fullPath = _fullPath;
        this.name = name;
    }

    public ContentId() {
    }

    public static ContentId of(@Column(name = "system_id") String systemId, @Column(name = "path_full") String _fullPath, @Column(name = "name") String name) {
        return new ContentId(systemId, _fullPath, name);
    }
}
