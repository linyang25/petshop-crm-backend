package com.petshop.crmbackend.repository.projection;


/**
 * 通用的“label + value” 投影接口，
 * 对应 DTO 中的 LabelValue
 */
public interface LabelValueProjection {
    String getLabel();
    long   getValue();
}
