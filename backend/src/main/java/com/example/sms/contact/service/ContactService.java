package com.example.sms.contact.service;

import com.example.sms.common.dto.PageResult;
import com.example.sms.contact.entity.ContactEntity;
import java.util.List;

public interface ContactService {

    PageResult<ContactEntity> listPaged(int page, int pageSize);

    List<ContactEntity> listAll();
    
    /**
     * 根据ID获取联系人
     */
    ContactEntity getById(Long id);
    
    /**
     * 根据手机号获取联系人
     */
    ContactEntity getByMobile(String mobile);
    
    /**
     * 创建联系人
     */
    ContactEntity create(ContactEntity contact);
    
    /**
     * 更新联系人
     */
    ContactEntity update(ContactEntity contact);
    
    /**
     * 删除联系人
     */
    boolean delete(Long id);
    
    /**
     * 根据部门查询联系人
     */
    List<ContactEntity> getByDepartment(String department);
    
    /**
     * 根据状态查询联系人
     */
    List<ContactEntity> getByStatus(String status);
    
    /**
     * 搜索联系人（姓名或手机号模糊匹配）
     */
    List<ContactEntity> search(String keyword);

    PageResult<ContactEntity> searchPaged(String keyword, int page, int pageSize);

    /**
     * 批量导入联系人（CSV），返回 {success: 成功数, fail: 失败数, errors: 错误明细}
     */
    java.util.Map<String, Object> importCsv(byte[] csvBytes);

    /**
     * 导出所有联系人为CSV字节
     */
    byte[] exportCsv();
}
