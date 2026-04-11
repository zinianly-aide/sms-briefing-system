package com.example.sms.audit.mapper;

import com.example.sms.audit.entity.OperationLog;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface OperationLogMapper {

    @Select("SELECT * FROM operation_log ORDER BY id DESC")
    List<OperationLog> selectAll();

    @Insert("INSERT INTO operation_log (module, action, operator, detail, ip, created_at) VALUES (#{module}, #{action}, #{operator}, #{detail}, #{ip}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(OperationLog log);

    @Select("SELECT * FROM operation_log WHERE module = #{module} ORDER BY id DESC LIMIT 100")
    List<OperationLog> selectByModule(@Param("module") String module);

    @Select("SELECT * FROM operation_log WHERE operator = #{operator} ORDER BY id DESC LIMIT 100")
    List<OperationLog> selectByOperator(@Param("operator") String operator);
}
