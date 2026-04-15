package com.example.sms.briefing.mapper;

import com.example.sms.briefing.entity.Briefing;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface BriefingMapper {
    @Select("SELECT * FROM briefing ORDER BY id DESC")
    List<Briefing> selectAll();

    @Select("SELECT * FROM briefing WHERE id = #{id}")
    Briefing selectById(@Param("id") Long id);

    @Insert("INSERT INTO briefing (title, content, template_id, status, channel, author, version, audience, updated_at, created_by, created_at, disaster_type, disaster_level, content_part2, remark, legacy_payload) VALUES (#{title}, #{content}, #{templateId}, #{status}, #{channel}, #{author}, #{version}, #{audience}, #{updatedAt}, #{createdBy}, #{createdAt}, #{disasterType}, #{disasterLevel}, #{contentPart2}, #{remark}, #{legacyPayload})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Briefing briefing);

    @Update("UPDATE briefing SET title = #{title}, content = #{content}, template_id = #{templateId}, status = #{status}, channel = #{channel}, author = #{author}, version = #{version}, audience = #{audience}, updated_at = #{updatedAt}, created_by = #{createdBy}, created_at = #{createdAt}, disaster_type = #{disasterType}, disaster_level = #{disasterLevel}, content_part2 = #{contentPart2}, remark = #{remark}, legacy_payload = #{legacyPayload} WHERE id = #{id}")
    int update(Briefing briefing);

    @Delete("DELETE FROM briefing WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM briefing WHERE title LIKE CONCAT('%', #{keyword}, '%') OR author LIKE CONCAT('%', #{keyword}, '%')")
    List<Briefing> search(@Param("keyword") String keyword);

    @Select("SELECT * FROM briefing WHERE title LIKE CONCAT('%', #{keyword}, '%') OR author LIKE CONCAT('%', #{keyword}, '%') ORDER BY id DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<Briefing> searchPage(@Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM briefing WHERE title LIKE CONCAT('%', #{keyword}, '%') OR author LIKE CONCAT('%', #{keyword}, '%')")
    int countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM briefing ORDER BY id DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<Briefing> selectPage(@Param("pageSize") int pageSize, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM briefing")
    int count();
}
