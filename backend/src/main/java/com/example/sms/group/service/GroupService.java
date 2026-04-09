package com.example.sms.group.service;

import com.example.sms.group.entity.ContactGroup;

import java.util.List;

public interface GroupService {
    List<ContactGroup> listAll();

    ContactGroup getById(Long id);

    ContactGroup create(ContactGroup group);

    ContactGroup update(ContactGroup group);

    boolean delete(Long id);

    List<ContactGroup> search(String keyword);
}
