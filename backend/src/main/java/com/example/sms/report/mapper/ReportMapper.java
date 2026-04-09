package com.example.sms.report.mapper; import com.example.sms.report.dto.ReportOverviewResponse; import org.apache.ibatis.annotations.Mapper;
@Mapper public interface ReportMapper{ ReportOverviewResponse selectOverview(); }
