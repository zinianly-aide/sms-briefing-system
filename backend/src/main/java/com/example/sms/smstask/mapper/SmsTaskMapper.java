package com.example.sms.smstask.mapper;
import com.example.sms.smstask.entity.SmsTask; import org.apache.ibatis.annotations.Mapper; import java.util.List;
@Mapper public interface SmsTaskMapper{ List<SmsTask> selectAll(); }
