package com.waste.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@TableName("fee_record")
public class FeeRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String feeNo;

    private Long wasteId;

    private Long orderId;

    private Long categoryId;

    private BigDecimal baseFee;

    private BigDecimal quantityFee;

    private BigDecimal extraFee;

    private String extraFeeReason;

    private BigDecimal totalAmount;

    private String paymentMethod;

    private LocalDateTime paymentTime;

    private String payerName;

    private String invoiceNo;

    private String remark;

    private Integer status;

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
    private String orderNo;
}
