package com.popeye.orm.repository;

import com.popeye.orm.common.Parameter;

import java.util.List;
import java.util.Map;

public interface DynamicRepository {

    List<Map<String, Object>> findAllWithDynamicObject(String query);

    List<Map<String, Object>> findAllWithDynamicObject(String query, Parameter... parameters);

    List<Map<String, Object>> findAllWithDynamicObject(String query, List<Parameter> parameters);

}
