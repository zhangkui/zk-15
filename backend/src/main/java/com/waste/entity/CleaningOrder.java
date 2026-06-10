package com.waste.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("cleaning_order")
public class CleaningOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("order_no")
    private String orderNo;

    @TableField("waste_item_id")
    private Long wasteItemId;

    @TableField("service_type")
    private String serviceType;

    @TableField("appointment_time")
    private LocalDateTime appointmentTime;

    private String address;

    @TableField("contact_name")
    private String contactName;

    @TableField("contact_phone")
    private String contactPhone;

    @TableField("cleaner_name")
    private String cleanerName;

    @TableField("cleaner_phone")
    private String cleanerPhone;

    private String status;

    @TableField("complete_time")
    private LocalDateTime completeTime;

    private String remark;

    @TableField(value = "create_time", fill = FieldFill.INSERT)
    private LocalDateTime createTime;

    @TableField(value = "update_time", fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updateTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String wasteItemName;
}
