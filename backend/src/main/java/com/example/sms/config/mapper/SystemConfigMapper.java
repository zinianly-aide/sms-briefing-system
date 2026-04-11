package com.example.sms.config.mapper;

import com.example.sms.config.entity.SystemConfig;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SystemConfigMapper {

    @Select("SELECT * FROM system_config ORDER BY id ASC")
    List<SystemConfig> selectAll();

    @Select("SELECT * FROM system_config WHERE id = #{id}")
    SystemConfig selectById(@Param("id") Long id);

    @Select("SELECT * FROM system_config WHERE config_key = #{key}")
    SystemConfig selectByKey(@Param("key") String key);

    @Insert("INSERT INTO system_config (config_key, config_value, config_desc, updated_at) VALUES (#{configKey}, #{configValue}, #{configDesc}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SystemConfig config);

    @Update("UPDATE system_config SET config_value = #{configValue}, config_desc = #{configDesc}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(SystemConfig config);

    @Delete("DELETE FROM system_config WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
}
