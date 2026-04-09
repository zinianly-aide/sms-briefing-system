package com.example.sms.group.mapper;
import com.example.sms.group.entity.ContactGroup; import org.apache.ibatis.annotations.Mapper; import java.util.List;
@Mapper public interface GroupMapper{ List<ContactGroup> selectAll(); }
