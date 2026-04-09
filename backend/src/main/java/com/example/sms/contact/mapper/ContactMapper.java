package com.example.sms.contact.mapper;
import com.example.sms.contact.entity.ContactEntity; import org.apache.ibatis.annotations.Mapper; import java.util.List;
@Mapper public interface ContactMapper{ List<ContactEntity> selectAll(); }
