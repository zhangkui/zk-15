CREATE DATABASE IF NOT EXISTS waste_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE waste_management;

CREATE TABLE IF NOT EXISTS waste_category (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    category_code VARCHAR(50) NOT NULL UNIQUE COMMENT '分类编码',
    category_name VARCHAR(100) NOT NULL COMMENT '分类名称',
    description TEXT COMMENT '分类说明',
    disposal_method VARCHAR(200) COMMENT '处理方式',
    unit_price DECIMAL(10, 2) DEFAULT 0.00 COMMENT '单价(元/单位)',
    unit VARCHAR(20) COMMENT '计量单位',
    sort_order INT DEFAULT 0 COMMENT '排序',
    status TINYINT DEFAULT 1 COMMENT '状态 0-禁用 1-启用',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='废弃物分类表';

CREATE TABLE IF NOT EXISTS waste_info (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    waste_no VARCHAR(32) NOT NULL UNIQUE COMMENT '废弃物编号',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    waste_name VARCHAR(200) NOT NULL COMMENT '废弃物名称',
    quantity DECIMAL(10, 2) NOT NULL COMMENT '数量',
    unit VARCHAR(20) COMMENT '计量单位',
    weight DECIMAL(10, 2) COMMENT '重量(kg)',
    description TEXT COMMENT '废弃物描述',
    photos VARCHAR(500) COMMENT '照片路径，多个用逗号分隔',
    renter_name VARCHAR(100) COMMENT '承租人姓名',
    renter_phone VARCHAR(20) COMMENT '承租人电话',
    address VARCHAR(500) COMMENT '房屋地址',
    check_out_date DATE COMMENT '退房日期',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待登记 1-已登记 2-已委托 3-清运中 4-已完成',
    created_by VARCHAR(100),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_waste_no (waste_no),
    INDEX idx_category_id (category_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='废弃物信息表';

CREATE TABLE IF NOT EXISTS pickup_order (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '委托单号',
    waste_id BIGINT NOT NULL COMMENT '废弃物ID',
    renter_name VARCHAR(100) COMMENT '承租人姓名',
    renter_phone VARCHAR(20) COMMENT '承租人电话',
    address VARCHAR(500) NOT NULL COMMENT '清运地址',
    contact_person VARCHAR(100) NOT NULL COMMENT '联系人',
    contact_phone VARCHAR(20) NOT NULL COMMENT '联系电话',
    appointment_date DATE NOT NULL COMMENT '预约日期',
    appointment_time VARCHAR(50) COMMENT '预约时间段',
    special_requirement TEXT COMMENT '特殊要求',
    collector_name VARCHAR(100) COMMENT '清运人员',
    collector_phone VARCHAR(20) COMMENT '清运人员电话',
    actual_pickup_time DATETIME COMMENT '实际清运时间',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待接单 1-已接单 2-清运中 3-已完成 4-已取消',
    cancel_reason VARCHAR(500) COMMENT '取消原因',
    created_by VARCHAR(100),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_order_no (order_no),
    INDEX idx_waste_id (waste_id),
    INDEX idx_status (status),
    INDEX idx_appointment_date (appointment_date)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清运委托单表';

CREATE TABLE IF NOT EXISTS fee_record (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fee_no VARCHAR(32) NOT NULL UNIQUE COMMENT '费用单号',
    waste_id BIGINT NOT NULL COMMENT '废弃物ID',
    order_id BIGINT COMMENT '委托单ID',
    category_id BIGINT COMMENT '分类ID',
    base_fee DECIMAL(10, 2) DEFAULT 0.00 COMMENT '基础费用',
    quantity_fee DECIMAL(10, 2) DEFAULT 0.00 COMMENT '按量费用',
    extra_fee DECIMAL(10, 2) DEFAULT 0.00 COMMENT '额外费用',
    extra_fee_reason VARCHAR(500) COMMENT '额外费用说明',
    total_amount DECIMAL(10, 2) NOT NULL COMMENT '总金额',
    payment_method VARCHAR(50) COMMENT '支付方式 微信-支付宝-现金-转账',
    payment_time DATETIME COMMENT '支付时间',
    payer_name VARCHAR(100) COMMENT '付款人',
    invoice_no VARCHAR(100) COMMENT '发票号',
    remark TEXT COMMENT '备注',
    status TINYINT DEFAULT 0 COMMENT '状态 0-待支付 1-已支付 2-已退款',
    created_by VARCHAR(100),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_fee_no (fee_no),
    INDEX idx_waste_id (waste_id),
    INDEX idx_order_id (order_id),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清运费用记录表';

CREATE TABLE IF NOT EXISTS disposal_voucher (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    voucher_no VARCHAR(32) NOT NULL UNIQUE COMMENT '凭证编号',
    waste_id BIGINT NOT NULL COMMENT '废弃物ID',
    order_id BIGINT COMMENT '委托单ID',
    fee_id BIGINT COMMENT '费用单ID',
    voucher_type VARCHAR(50) NOT NULL COMMENT '凭证类型 清运单-处置单-发票-其他',
    voucher_title VARCHAR(200) NOT NULL COMMENT '凭证标题',
    voucher_content TEXT COMMENT '凭证内容',
    file_path VARCHAR(500) COMMENT '文件路径',
    file_name VARCHAR(200) COMMENT '文件名',
    file_size BIGINT COMMENT '文件大小(字节)',
    disposal_unit VARCHAR(200) COMMENT '处理单位',
    disposal_person VARCHAR(100) COMMENT '处理人',
    disposal_time DATETIME COMMENT '处理时间',
    remark TEXT COMMENT '备注',
    created_by VARCHAR(100),
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    deleted TINYINT DEFAULT 0,
    INDEX idx_voucher_no (voucher_no),
    INDEX idx_waste_id (waste_id),
    INDEX idx_order_id (order_id),
    INDEX idx_voucher_type (voucher_type)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理凭证表';

CREATE TABLE IF NOT EXISTS operation_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    business_type VARCHAR(50) NOT NULL COMMENT '业务类型 废弃物-委托单-费用-凭证',
    business_id BIGINT NOT NULL COMMENT '业务ID',
    business_no VARCHAR(32) COMMENT '业务编号',
    operation_type VARCHAR(50) NOT NULL COMMENT '操作类型 创建-更新-删除-状态变更',
    operation_detail TEXT COMMENT '操作详情',
    operator VARCHAR(100) COMMENT '操作人',
    ip_address VARCHAR(50) COMMENT 'IP地址',
    created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_business (business_type, business_id),
    INDEX idx_created_time (created_time)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

INSERT INTO waste_category (category_code, category_name, description, disposal_method, unit_price, unit, sort_order) VALUES
('FURNITURE', '大件家具', '沙发、床、衣柜、餐桌、椅子等大型家具', '拆解回收/专业清运', 50.00, '件', 1),
('APPLIANCE', '家用电器', '冰箱、洗衣机、空调、电视等电器', '专业回收处理', 30.00, '件', 2),
('ELECTRONIC', '电子废弃物', '电脑、手机、充电器、电线等电子产品', '危废处理/资源回收', 20.00, 'kg', 3),
('KITCHEN', '厨房废弃物', '厨具、餐具、食材、调味品等', '分类投放/厨余处理', 10.00, 'kg', 4),
('CLOTHING', '衣物织物', '衣服、床上用品、窗帘、地毯等', '捐赠/回收/焚烧', 5.00, 'kg', 5),
('BOOKS', '书籍纸张', '书籍、文件、纸箱、报纸等', '回收再利用', 2.00, 'kg', 6),
('GLASS', '玻璃制品', '玻璃瓶、镜子、窗户玻璃等', '分类回收/专业清运', 15.00, 'kg', 7),
('METAL', '金属制品', '铁丝、钢管、五金工具等', '废品回收', 3.00, 'kg', 8),
('HAZARDOUS', '危险废弃物', '电池、灯管、药品、油漆等', '危废处理站专业处理', 100.00, 'kg', 9),
('OTHER', '其他废弃物', '建筑垃圾、装修垃圾、混杂垃圾等', '分类清运/填埋', 25.00, 'kg', 10);
