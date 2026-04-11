package com.example.sms.contact.mapper;

import com.example.sms.contact.entity.ContactEntity;
import org.apache.ibatis.annotations.*;
import java.util.List;

@Mapper
public interface ContactMapper {
    
    // 查询所有联系人
    @Select("SELECT * FROM contact ORDER BY id DESC")
    List<ContactEntity> selectAll();
    
    // 根据ID查询联系人
    ContactEntity selectById(@Param("id") Long id);
    
    // 根据手机号查询联系人
    ContactEntity selectByMobile(@Param("mobile") String mobile);
    
    // 插入联系人
    @Insert("INSERT INTO contact (name, mobile, department, title, status, created_at, updated_at) " +
            "VALUES (#{name}, #{mobile}, #{department}, #{title}, #{status}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ContactEntity contact);
    
    // 更新联系人
    @Update("UPDATE contact SET name = #{name}, mobile = #{mobile}, department = #{department}, " +
            "title = #{title}, status = #{status}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(ContactEntity contact);
    
    // 删除联系人
    @Delete("DELETE FROM contact WHERE id = #{id}")
    int deleteById(@Param("id") Long id);
    
    // 根据部门查询联系人
    @Select("SELECT * FROM contact WHERE department = #{department} ORDER BY id DESC")
    List<ContactEntity> selectByDepartment(@Param("department") String department);
    
    // 根据状态查询联系人
    @Select("SELECT * FROM contact WHERE status = #{status} ORDER BY id DESC")
    List<ContactEntity> selectByStatus(@Param("status") String status);
    
    // 模糊查询（姓名或手机号）
    @Select("SELECT * FROM contact WHERE name LIKE CONCAT('%', #{keyword}, '%') OR mobile LIKE CONCAT('%', #{keyword}, '%')")
    List<ContactEntity> searchByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM contact ORDER BY id DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<ContactEntity> selectPage(@Param("pageSize") int pageSize, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM contact")
    int count();
}
