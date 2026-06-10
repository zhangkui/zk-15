package com.waste.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("operation_log")
public class OperationLog {

    @TableId(type = IdType.AUTO)
    private Long id;

    private String businessType;

    private Long businessId;

    private String businessNo;

    private String operationType;

    private String operationDetail;

    private String operator;

    private String ipAddress;

    private LocalDateTime createdTime;
}
