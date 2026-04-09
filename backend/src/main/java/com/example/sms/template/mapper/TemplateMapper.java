package com.example.sms.template.mapper;
import com.example.sms.template.entity.Template; import org.apache.ibatis.annotations.Mapper; import java.util.List;
@Mapper public interface TemplateMapper{ List<Template> selectAll(); }
