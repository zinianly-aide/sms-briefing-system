package com.example.sms.group.mapper;

import com.example.sms.group.entity.ContactGroup;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMapper {
    @Select("SELECT * FROM contact_group ORDER BY id DESC")
    List<ContactGroup> selectAll();

    @Select("SELECT * FROM contact_group WHERE id = #{id}")
    ContactGroup selectById(@Param("id") Long id);

    @Insert("INSERT INTO contact_group (name, owner_dept, member_count, tags, last_sync_time, created_at, updated_at) VALUES (#{name}, #{ownerDept}, #{memberCount}, #{tags}, #{lastSyncTime}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(ContactGroup group);

    @Update("UPDATE contact_group SET name = #{name}, owner_dept = #{ownerDept}, member_count = #{memberCount}, tags = #{tags}, last_sync_time = #{lastSyncTime}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(ContactGroup group);

    @Delete("DELETE FROM contact_group WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM contact_group WHERE name LIKE CONCAT('%', #{keyword}, '%') OR owner_dept LIKE CONCAT('%', #{keyword}, '%')")
    List<ContactGroup> search(@Param("keyword") String keyword);

    @Select("SELECT * FROM contact_group WHERE name LIKE CONCAT('%', #{keyword}, '%') OR owner_dept LIKE CONCAT('%', #{keyword}, '%') ORDER BY id DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<ContactGroup> searchPage(@Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM contact_group WHERE name LIKE CONCAT('%', #{keyword}, '%') OR owner_dept LIKE CONCAT('%', #{keyword}, '%')")
    int countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM contact_group ORDER BY id DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<ContactGroup> selectPage(@Param("pageSize") int pageSize, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM contact_group")
    int count();

    @Select("SELECT COUNT(*) FROM contact_group WHERE status = #{status}")
    int countByStatus(@Param("status") String status);
}
