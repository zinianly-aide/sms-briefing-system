package com.example.sms.briefing.service.impl;
import com.example.sms.briefing.entity.Briefing; import com.example.sms.briefing.service.BriefingService; import org.springframework.stereotype.Service; import java.util.List;
@Service public class BriefingServiceImpl implements BriefingService { public List<Briefing> list(){ return List.of(new Briefing(1L,"台风预警简讯","请各单位做好防台准备")); } }
