package com.waste.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("waste_category")
public class WasteCategory {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String categoryCode;

    private String categoryName;

    private String description;

    private String disposalMethod;

    private BigDecimal unitPrice;

    private String unit;

    private Integer sortOrder;

    private Integer status;

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    @TableLogic
    private Integer deleted;
}
