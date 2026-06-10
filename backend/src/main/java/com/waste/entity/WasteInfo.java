package com.waste.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@TableName("waste_info")
public class WasteInfo {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String wasteNo;

    private Long categoryId;

    private String wasteName;

    private BigDecimal quantity;

    private String unit;

    private BigDecimal weight;

    private String description;

    private String photos;

    private String renterName;

    private String renterPhone;

    private String address;

    private LocalDate checkOutDate;

    private Integer status;

    private String createdBy;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;

    @TableField(exist = false)
    private String categoryName;

    @TableField(exist = false)
    private String categoryCode;
}
