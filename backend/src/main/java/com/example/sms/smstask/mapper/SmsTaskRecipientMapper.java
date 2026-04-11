package com.example.sms.smstask.mapper;

import com.example.sms.smstask.entity.SmsTaskRecipient;
import org.apache.ibatis.annotations.*;

import java.util.List;

@Mapper
public interface SmsTaskRecipientMapper {

    @Select("SELECT * FROM sms_task_recipient WHERE task_id = #{taskId} ORDER BY id ASC")
    List<SmsTaskRecipient> selectByTaskId(@Param("taskId") Long taskId);

    @Insert("INSERT INTO sms_task_recipient (task_id, contact_id, mobile, name, status, sent_at, error_msg) " +
            "VALUES (#{taskId}, #{contactId}, #{mobile}, #{name}, #{status}, #{sentAt}, #{errorMsg})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    int insert(SmsTaskRecipient recipient);

    @Update("UPDATE sms_task_recipient SET status = #{status}, sent_at = #{sentAt}, error_msg = #{errorMsg} WHERE id = #{id}")
    int updateStatus(SmsTaskRecipient recipient);

    @Select("SELECT COUNT(*) FROM sms_task_recipient WHERE task_id = #{taskId} AND status = 'SUCCESS'")
    int countSuccess(@Param("taskId") Long taskId);

    @Select("SELECT COUNT(*) FROM sms_task_recipient WHERE task_id = #{taskId}")
    int countByTaskId(@Param("taskId") Long taskId);
}
