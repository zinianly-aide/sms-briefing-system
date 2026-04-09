package com.example.sms.report.service.impl;
import com.example.sms.report.dto.ReportOverviewResponse; import com.example.sms.report.service.ReportService; import org.springframework.stereotype.Service;
@Service public class ReportServiceImpl implements ReportService { public ReportOverviewResponse overview(){ return new ReportOverviewResponse(1,1); } }
