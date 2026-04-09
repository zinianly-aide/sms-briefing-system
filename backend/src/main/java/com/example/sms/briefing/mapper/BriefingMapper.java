package com.example.sms.briefing.mapper;
import com.example.sms.briefing.entity.Briefing;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;
@Mapper public interface BriefingMapper{ List<Briefing> selectAll(); }
