package com.visitor_x.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class DashboardResponse {

    private long totalVisitors;

    private long todayVisitors;
}