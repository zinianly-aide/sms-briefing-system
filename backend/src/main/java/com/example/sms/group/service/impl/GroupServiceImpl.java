package com.example.sms.group.service.impl;
import com.example.sms.group.entity.ContactGroup; import com.example.sms.group.service.GroupService; import org.springframework.stereotype.Service; import java.util.List;
@Service public class GroupServiceImpl implements GroupService { public List<ContactGroup> list(){ return List.of(new ContactGroup(11L,"全员通讯录",1260)); } }
