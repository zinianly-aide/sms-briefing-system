package com.example.sms.auth.mapper;

import com.example.sms.auth.entity.User;
import org.apache.ibatis.annotations.*;

@Mapper
public interface UserMapper {

    @Select("SELECT * FROM user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    @Insert("INSERT INTO user (username, password, display_name, role, created_at) VALUES (#{username}, #{password}, #{displayName}, #{role}, #{createdAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(User user);
}
