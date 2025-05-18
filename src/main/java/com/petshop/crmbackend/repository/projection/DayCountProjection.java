package com.petshop.crmbackend.repository.projection;

/**
 * 每日统计投影，
 * 对应 DTO 中的 DayCount
 */
public interface DayCountProjection {
    String getDay();
    long   getCount();
}
