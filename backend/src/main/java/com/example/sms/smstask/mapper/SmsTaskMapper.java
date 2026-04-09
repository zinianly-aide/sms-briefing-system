package com.example.sms.smstask.mapper;

import com.example.sms.smstask.entity.SmsTask;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SmsTaskMapper {
    List<SmsTask> selectAll();

    @Select("SELECT * FROM send_task WHERE id = #{id}")
    SmsTask selectById(@Param("id") Long id);

    @Insert("INSERT INTO send_task (title, channel, planned_send_time, status, recipient_count, creator, success_rate, created_at, updated_at) VALUES (#{title}, #{channel}, #{plannedSendTime}, #{status}, #{recipientCount}, #{creator}, #{successRate}, #{createdAt}, #{updatedAt})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SmsTask task);

    @Update("UPDATE send_task SET title = #{title}, channel = #{channel}, planned_send_time = #{plannedSendTime}, status = #{status}, recipient_count = #{recipientCount}, creator = #{creator}, success_rate = #{successRate}, updated_at = #{updatedAt} WHERE id = #{id}")
    int update(SmsTask task);

    @Delete("DELETE FROM send_task WHERE id = #{id}")
    int deleteById(@Param("id") Long id);

    @Select("SELECT * FROM send_task WHERE title LIKE CONCAT('%', #{keyword}, '%') OR creator LIKE CONCAT('%', #{keyword}, '%')")
    List<SmsTask> search(@Param("keyword") String keyword);
}
