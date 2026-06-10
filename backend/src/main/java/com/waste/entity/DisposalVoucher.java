package com.waste.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("disposal_voucher")
public class DisposalVoucher {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String voucherNo;

    private Long wasteId;

    private Long orderId;

    private Long feeId;

    private String voucherType;

    private String voucherTitle;

    private String voucherContent;

    private String filePath;

    private String fileName;

    private Long fileSize;

    private String disposalUnit;

    private String disposalPerson;

    private LocalDateTime disposalTime;

    private String remark;

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
