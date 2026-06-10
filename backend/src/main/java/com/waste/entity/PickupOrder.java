package com.waste.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("pickup_order")
public class PickupOrder {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String orderNo;

    private Long wasteId;

    private String renterName;

    private String renterPhone;

    private String address;

    private String contactPerson;

    private String contactPhone;

    private LocalDate appointmentDate;

    private String appointmentTime;

    private String specialRequirement;

    private String collectorName;

    private String collectorPhone;

    private LocalDateTime actualPickupTime;

    private Integer status;

    private String cancelReason;

    private String createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String wasteName;

    @TableField(exist = false)
    private String wasteNo;
}
