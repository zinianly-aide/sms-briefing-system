package com.example.sms.contact.service.impl;
import com.example.sms.contact.entity.ContactEntity; import com.example.sms.contact.service.ContactService; import org.springframework.stereotype.Service; import java.util.List;
@Service public class ContactServiceImpl implements ContactService { public List<ContactEntity> list(){ return List.of(new ContactEntity(1001L,"张晓晨","13800001111")); } }
