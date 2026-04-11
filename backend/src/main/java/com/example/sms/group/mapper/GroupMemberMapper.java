package com.example.sms.group.mapper;

import com.example.sms.group.entity.GroupMember;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface GroupMemberMapper {

    @Select("SELECT gm.*, c.name AS contactName, c.mobile AS contactMobile, c.department AS contactDepartment " +
            "FROM group_member gm LEFT JOIN contact c ON gm.contact_id = c.id " +
            "WHERE gm.group_id = #{groupId} ORDER BY gm.id ASC")
    @Results({
        @Result(property = "groupId", column = "group_id"),
        @Result(property = "contactId", column = "contact_id"),
        @Result(property = "joinedAt", column = "joined_at"),
        @Result(property = "contactName", column = "contactName"),
        @Result(property = "contactMobile", column = "contactMobile"),
        @Result(property = "contactDepartment", column = "contactDepartment")
    })
    List<GroupMember> selectByGroupId(@Param("groupId") Long groupId);

    @Insert("INSERT INTO group_member (group_id, contact_id, role, joined_at) VALUES (#{groupId}, #{contactId}, #{role}, #{joinedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(GroupMember member);

    @Delete("DELETE FROM group_member WHERE group_id = #{groupId} AND contact_id = #{contactId}")
    int delete(@Param("groupId") Long groupId, @Param("contactId") Long contactId);

    @Select("SELECT COUNT(*) FROM group_member WHERE group_id = #{groupId}")
    int countByGroupId(@Param("groupId") Long groupId);
}
