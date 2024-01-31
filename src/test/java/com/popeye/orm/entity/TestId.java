package com.popeye.orm.entity;

import com.popeye.orm.anotation.Column;
import com.popeye.orm.common.EmbeddedId;
import lombok.Data;


@Data
public class TestId extends EmbeddedId {
    @Column(name = "code_id")
    private String codeId;

    @Column(name = "code_name")
    private String codeName;

    @Column(name = "item_key")
    private String itemKey;

    public TestId() {
    }

    private TestId(@Column(name = "code_id") String codeId, @Column(name = "code_name") String codeName, @Column(name = "item_key") String itemKey) {
        this.codeId = codeId;
        this.codeName = codeName;
        this.itemKey = itemKey;
    }

    public static TestId of(String codeId, String codeName, String itemKey) {
        return new TestId(codeId, codeName, itemKey);
    }

}
