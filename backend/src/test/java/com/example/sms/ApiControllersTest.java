package com.example.sms;

import com.example.sms.briefing.entity.Briefing;
import com.example.sms.briefing.service.BriefingService;
import com.example.sms.contact.entity.ContactEntity;
import com.example.sms.contact.service.ContactService;
import com.example.sms.group.entity.ContactGroup;
import com.example.sms.group.service.GroupService;
import com.example.sms.report.dto.ReportOverviewResponse;
import com.example.sms.report.service.ReportService;
import com.example.sms.smstask.entity.SmsTask;
import com.example.sms.smstask.service.SmsTaskService;
import com.example.sms.template.entity.Template;
import com.example.sms.template.service.TemplateService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = {
    com.example.sms.report.controller.ReportController.class,
    com.example.sms.contact.controller.ContactModuleController.class,
    com.example.sms.group.controller.GroupController.class,
    com.example.sms.template.controller.TemplateController.class,
    com.example.sms.smstask.controller.SmsTaskController.class,
    com.example.sms.briefing.controller.BriefingController.class
})
@AutoConfigureMockMvc(addFilters = false)
@Import(com.example.sms.config.CorsConfig.class)
@ContextConfiguration(classes = {
    com.example.sms.report.controller.ReportController.class,
    com.example.sms.contact.controller.ContactModuleController.class,
    com.example.sms.group.controller.GroupController.class,
    com.example.sms.template.controller.TemplateController.class,
    com.example.sms.smstask.controller.SmsTaskController.class,
    com.example.sms.briefing.controller.BriefingController.class,
    com.example.sms.config.CorsConfig.class
})
class ApiControllersTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ReportService reportService;
    @MockBean
    private ContactService contactService;
    @MockBean
    private GroupService groupService;
    @MockBean
    private TemplateService templateService;
    @MockBean
    private SmsTaskService smsTaskService;
    @MockBean
    private BriefingService briefingService;

    // ==================== Dashboard / Reports ====================

    @Test
    void shouldGetDashboard() throws Exception {
        when(reportService.overview()).thenReturn(new ReportOverviewResponse(1, 1, 1, 1, 1, List.of(), List.of(), List.of()));

        mockMvc.perform(get("/api/dashboard"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.totalContacts").value(1));
    }

    @Test
    void shouldGetReportsViaAliasRoute() throws Exception {
        when(reportService.overview()).thenReturn(new ReportOverviewResponse(0, 0, 0, 0, 0, List.of(), List.of(), List.of()));

        mockMvc.perform(get("/api/reports"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true));
    }

    // ==================== Contact ====================

    @Test
    void shouldSupportContactCrudEndpoints() throws Exception {
        ContactEntity contact = new ContactEntity(1L, "张三", "13800000000", "销售", "经理", "active", LocalDateTime.now(), LocalDateTime.now());
        when(contactService.listAll()).thenReturn(List.of(contact));
        when(contactService.getById(1L)).thenReturn(contact);
        when(contactService.create(any())).thenReturn(contact);
        when(contactService.update(any())).thenReturn(contact);
        when(contactService.delete(1L)).thenReturn(true);
        when(contactService.search("张")).thenReturn(List.of(contact));

        mockMvc.perform(get("/api/contacts")).andExpect(status().isOk()).andExpect(jsonPath("$.data.list[0].name").value("张三"));
        mockMvc.perform(get("/api/contacts/1")).andExpect(status().isOk()).andExpect(jsonPath("$.data.mobile").value("13800000000"));
        mockMvc.perform(post("/api/contacts").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"张三\",\"mobile\":\"13800000000\",\"department\":\"销售\",\"title\":\"经理\",\"status\":\"active\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.success").value(true));
        mockMvc.perform(put("/api/contacts/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"张三\",\"mobile\":\"13800000000\",\"department\":\"销售\",\"title\":\"经理\",\"status\":\"active\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(delete("/api/contacts/1")).andExpect(status().isOk()).andExpect(jsonPath("$.data").value(true));
        mockMvc.perform(get("/api/contacts/search").param("keyword", "张")).andExpect(status().isOk());
    }

    @Test
    void shouldReturn404ForNonExistentContact() throws Exception {
        when(contactService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/contacts/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false))
            .andExpect(jsonPath("$.message").value("联系人不存在"));
    }

    @Test
    void shouldGetContactsByDepartment() throws Exception {
        ContactEntity contact = new ContactEntity(1L, "张三", "13800000000", "销售", "经理", "active", LocalDateTime.now(), LocalDateTime.now());
        when(contactService.getByDepartment("销售")).thenReturn(List.of(contact));

        mockMvc.perform(get("/api/contacts/department/销售"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].department").value("销售"));
        // department endpoint returns plain list, not PageResult
    }

    @Test
    void shouldGetContactsByStatus() throws Exception {
        ContactEntity contact = new ContactEntity(1L, "张三", "13800000000", "销售", "经理", "active", LocalDateTime.now(), LocalDateTime.now());
        when(contactService.getByStatus("active")).thenReturn(List.of(contact));

        mockMvc.perform(get("/api/contacts/status/active"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data[0].status").value("active"));
        // status endpoint returns plain list, not PageResult
    }

    // ==================== Group ====================

    @Test
    void shouldSupportGroupCrudEndpoints() throws Exception {
        ContactGroup group = new ContactGroup(1L, "销售群", "销售部", 10, "销售,核心", LocalDateTime.now(), "启用", LocalDateTime.now(), LocalDateTime.now());
        when(groupService.listAll()).thenReturn(List.of(group));
        when(groupService.getById(1L)).thenReturn(group);
        when(groupService.create(any())).thenReturn(group);
        when(groupService.update(any())).thenReturn(group);
        when(groupService.delete(1L)).thenReturn(true);
        when(groupService.search("销售")).thenReturn(List.of(group));

        mockMvc.perform(get("/api/groups")).andExpect(status().isOk()).andExpect(jsonPath("$.data.list[0].name").value("销售群"));
        mockMvc.perform(get("/api/groups/1")).andExpect(status().isOk()).andExpect(jsonPath("$.data.ownerDept").value("销售部"));
        mockMvc.perform(post("/api/groups").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"销售群\",\"ownerDept\":\"销售部\",\"memberCount\":10,\"tags\":\"销售,核心\",\"status\":\"启用\"}"))
            .andExpect(status().isOk()).andExpect(jsonPath("$.data.name").value("销售群"));
        mockMvc.perform(put("/api/groups/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"销售群\",\"ownerDept\":\"销售部\",\"memberCount\":10,\"tags\":\"销售,核心\",\"status\":\"启用\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(delete("/api/groups/1")).andExpect(status().isOk()).andExpect(jsonPath("$.data").value(true));
        mockMvc.perform(get("/api/groups/search").param("keyword", "销售")).andExpect(status().isOk());
    }

    @Test
    void shouldReturn404ForNonExistentGroup() throws Exception {
        when(groupService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/groups/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== Template ====================

    @Test
    void shouldSupportTemplateCrudEndpoints() throws Exception {
        Template template = new Template(1L, "预警模板", "预警", "请注意安全", "启用中", "运营", null, LocalDateTime.now());
        when(templateService.listAll()).thenReturn(List.of(template));
        when(templateService.getById(1L)).thenReturn(template);
        when(templateService.create(any())).thenReturn(template);
        when(templateService.update(any())).thenReturn(template);
        when(templateService.delete(1L)).thenReturn(true);
        when(templateService.search("预警")).thenReturn(List.of(template));

        mockMvc.perform(get("/api/templates")).andExpect(status().isOk()).andExpect(jsonPath("$.data.list[0].name").value("预警模板"));
        mockMvc.perform(get("/api/templates/1")).andExpect(status().isOk()).andExpect(jsonPath("$.data.category").value("预警"));
        mockMvc.perform(post("/api/templates").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"预警模板\",\"category\":\"预警\",\"content\":\"请注意安全\",\"status\":\"启用中\",\"owner\":\"运营\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(put("/api/templates/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"预警模板\",\"category\":\"预警\",\"content\":\"请注意安全\",\"status\":\"启用中\",\"owner\":\"运营\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(delete("/api/templates/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/templates/search").param("keyword", "预警")).andExpect(status().isOk());
    }

    @Test
    void shouldReturn404ForNonExistentTemplate() throws Exception {
        when(templateService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/templates/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== SmsTask ====================

    @Test
    void shouldSupportTaskCrudEndpoints() throws Exception {
        SmsTask task = new SmsTask(1L, "暴雨提醒", "短信", LocalDateTime.now(), "待发送", 10, "张三", "—", LocalDateTime.now(), LocalDateTime.now());
        when(smsTaskService.listAll()).thenReturn(List.of(task));
        when(smsTaskService.getById(1L)).thenReturn(task);
        when(smsTaskService.create(any())).thenReturn(task);
        when(smsTaskService.update(any())).thenReturn(task);
        when(smsTaskService.delete(1L)).thenReturn(true);
        when(smsTaskService.search("暴雨")).thenReturn(List.of(task));

        mockMvc.perform(get("/api/tasks")).andExpect(status().isOk()).andExpect(jsonPath("$.data.list[0].title").value("暴雨提醒"));
        mockMvc.perform(get("/api/tasks/1")).andExpect(status().isOk()).andExpect(jsonPath("$.data.channel").value("短信"));
        mockMvc.perform(post("/api/tasks").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"暴雨提醒\",\"channel\":\"短信\",\"status\":\"待发送\",\"recipientCount\":10,\"creator\":\"张三\",\"successRate\":\"—\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(put("/api/tasks/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"暴雨提醒\",\"channel\":\"短信\",\"status\":\"待发送\",\"recipientCount\":10,\"creator\":\"张三\",\"successRate\":\"—\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(delete("/api/tasks/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/tasks/search").param("keyword", "暴雨")).andExpect(status().isOk());
    }

    @Test
    void shouldSupportSmsTasksAliasRoute() throws Exception {
        SmsTask task = new SmsTask(1L, "暴雨提醒", "短信", LocalDateTime.now(), "待发送", 10, "张三", "—", LocalDateTime.now(), LocalDateTime.now());
        when(smsTaskService.listAll()).thenReturn(List.of(task));

        mockMvc.perform(get("/api/sms-tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.list[0].title").value("暴雨提醒"));
    }

    @Test
    void shouldReturn404ForNonExistentTask() throws Exception {
        when(smsTaskService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/tasks/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== Briefing ====================

    @Test
    void shouldSupportBriefingCrudEndpoints() throws Exception {
        Briefing briefing = new Briefing(1L, "暴雨简讯", "请注意防汛", 1L, "待审核", "短信", "张三", "V1.0", "1,2", LocalDateTime.now(), "张三", LocalDateTime.now(), null, null, null, null, null);
        when(briefingService.listAll()).thenReturn(List.of(briefing));
        when(briefingService.getById(1L)).thenReturn(briefing);
        when(briefingService.create(any())).thenReturn(briefing);
        when(briefingService.update(any())).thenReturn(briefing);
        when(briefingService.delete(1L)).thenReturn(true);
        when(briefingService.search("暴雨")).thenReturn(List.of(briefing));

        mockMvc.perform(get("/api/briefings")).andExpect(status().isOk()).andExpect(jsonPath("$.data.list[0].title").value("暴雨简讯"));
        mockMvc.perform(get("/api/briefings/1")).andExpect(status().isOk()).andExpect(jsonPath("$.data.content").value("请注意防汛"));
        mockMvc.perform(post("/api/briefings").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"暴雨简讯\",\"content\":\"请注意防汛\",\"templateId\":1,\"status\":\"待审核\",\"channel\":\"短信\",\"author\":\"张三\",\"version\":\"V1.0\",\"audience\":\"1,2\",\"createdBy\":\"张三\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(put("/api/briefings/1").contentType(MediaType.APPLICATION_JSON)
                .content("{\"title\":\"暴雨简讯\",\"content\":\"请注意防汛\",\"templateId\":1,\"status\":\"待审核\",\"channel\":\"短信\",\"author\":\"张三\",\"version\":\"V1.0\",\"audience\":\"1,2\",\"createdBy\":\"张三\"}"))
            .andExpect(status().isOk());
        mockMvc.perform(delete("/api/briefings/1")).andExpect(status().isOk());
        mockMvc.perform(get("/api/briefings/search").param("keyword", "暴雨")).andExpect(status().isOk());
    }

    @Test
    void shouldReturn404ForNonExistentBriefing() throws Exception {
        when(briefingService.getById(999L)).thenReturn(null);

        mockMvc.perform(get("/api/briefings/999"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.success").value(false));
    }

    // ==================== Empty data scenarios ====================

    @Test
    void shouldReturnEmptyListWhenNoContacts() throws Exception {
        when(contactService.listAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/contacts"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.list").isArray())
            .andExpect(jsonPath("$.data.list").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoGroups() throws Exception {
        when(groupService.listAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/groups"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.list").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoTemplates() throws Exception {
        when(templateService.listAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/templates"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.list").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoTasks() throws Exception {
        when(smsTaskService.listAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/tasks"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.list").isEmpty());
    }

    @Test
    void shouldReturnEmptyListWhenNoBriefings() throws Exception {
        when(briefingService.listAll()).thenReturn(List.of());
        mockMvc.perform(get("/api/briefings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.list").isEmpty());
    }
}
