package com.example.sms.template.mapper;

import com.example.sms.template.entity.Template;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface TemplateMapper {
    @Select("SELECT * FROM briefing_template ORDER BY id DESC")
    List<Template> selectAll();

    @Select("SELECT * FROM briefing_template WHERE id = #{id}")
    Template selectById(@Param("id") Long id);

    @Insert("INSERT INTO briefing_template (name, category, content, status, owner, default_group_ids, updated_at) VALUES (#{name}, #{category}, #{content}, #{status}, #{owner}, #{defaultGroupIds}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(Template template);

    @Update("UPDATE briefing_template SET name = #{name}, category = #{category}, content = #{content}, status = #{status}, owner = #{owner}, default_group_ids = #{defaultGroupIds}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(Template template);

    @Delete("DELETE FROM briefing_template WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM briefing_template WHERE name LIKE CONCAT('%', #{keyword}, '%') OR category LIKE CONCAT('%', #{keyword}, '%')")
    List<Template> search(@Param("keyword") String keyword);

    @Select("SELECT * FROM briefing_template WHERE name LIKE CONCAT('%', #{keyword}, '%') OR category LIKE CONCAT('%', #{keyword}, '%') ORDER BY id DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<Template> searchPage(@Param("keyword") String keyword, @Param("pageSize") int pageSize, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM briefing_template WHERE name LIKE CONCAT('%', #{keyword}, '%') OR category LIKE CONCAT('%', #{keyword}, '%')")
    int countByKeyword(@Param("keyword") String keyword);

    @Select("SELECT * FROM briefing_template ORDER BY id DESC LIMIT #{pageSize} OFFSET #{offset}")
    List<Template> selectPage(@Param("pageSize") int pageSize, @Param("offset") int offset);

    @Select("SELECT COUNT(*) FROM briefing_template")
    int count();
}
