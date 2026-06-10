CREATE DATABASE IF NOT EXISTS waste_management DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE waste_management;

DROP TABLE IF EXISTS archive;
DROP TABLE IF EXISTS certificate;
DROP TABLE IF EXISTS cleaning_fee;
DROP TABLE IF EXISTS cleaning_order;
DROP TABLE IF EXISTS waste_item;
DROP TABLE IF EXISTS waste_category;

CREATE TABLE waste_category (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    name VARCHAR(50) NOT NULL COMMENT '分类名称',
    code VARCHAR(20) NOT NULL COMMENT '分类编码',
    description TEXT COMMENT '分类说明',
    icon VARCHAR(255) COMMENT '图标',
    sort_order INT DEFAULT 0 COMMENT '排序',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_code (code)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='废弃物分类表';

CREATE TABLE waste_item (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    category_id BIGINT NOT NULL COMMENT '分类ID',
    name VARCHAR(100) NOT NULL COMMENT '废弃物名称',
    description TEXT COMMENT '描述',
    quantity INT DEFAULT 1 COMMENT '数量',
    unit VARCHAR(20) DEFAULT '件' COMMENT '单位',
    estimated_weight DECIMAL(10,2) COMMENT '预估重量(kg)',
    address VARCHAR(255) COMMENT '地址',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    photos JSON COMMENT '照片URL列表',
    status VARCHAR(20) DEFAULT '待处理' COMMENT '状态',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_category_id (category_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='废弃物信息登记表';

CREATE TABLE cleaning_order (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_no VARCHAR(30) NOT NULL COMMENT '委托单号',
    waste_item_id BIGINT NOT NULL COMMENT '废弃物ID',
    service_type VARCHAR(20) DEFAULT '上门清运' COMMENT '服务类型',
    appointment_time DATETIME COMMENT '预约时间',
    address VARCHAR(255) COMMENT '清运地址',
    contact_name VARCHAR(50) COMMENT '联系人',
    contact_phone VARCHAR(20) COMMENT '联系电话',
    cleaner_name VARCHAR(50) COMMENT '清运人员',
    cleaner_phone VARCHAR(20) COMMENT '清运人员电话',
    status VARCHAR(20) DEFAULT '待确认' COMMENT '状态',
    complete_time DATETIME COMMENT '完成时间',
    remark TEXT COMMENT '备注',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    UNIQUE KEY uk_order_no (order_no),
    KEY idx_waste_item_id (waste_item_id),
    KEY idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清运委托单表';

CREATE TABLE cleaning_fee (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '委托单ID',
    fee_type VARCHAR(20) NOT NULL COMMENT '费用类型',
    amount DECIMAL(10,2) NOT NULL COMMENT '金额',
    description VARCHAR(255) COMMENT '费用说明',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='清运费用记录表';

CREATE TABLE certificate (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '委托单ID',
    certificate_type VARCHAR(20) NOT NULL COMMENT '凭证类型',
    file_url VARCHAR(500) NOT NULL COMMENT '文件URL',
    file_name VARCHAR(255) COMMENT '文件名',
    file_type VARCHAR(20) COMMENT '文件类型',
    file_size BIGINT COMMENT '文件大小(字节)',
    description TEXT COMMENT '描述',
    upload_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '上传时间',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    deleted TINYINT DEFAULT 0 COMMENT '逻辑删除',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='处理凭证表';

CREATE TABLE archive (
    id BIGINT NOT NULL AUTO_INCREMENT COMMENT '主键',
    order_id BIGINT NOT NULL COMMENT '委托单ID',
    archive_type VARCHAR(20) NOT NULL COMMENT '留档类型',
    content TEXT COMMENT '留档内容',
    operator VARCHAR(50) COMMENT '操作人',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    PRIMARY KEY (id),
    KEY idx_order_id (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='留档记录表';

INSERT INTO waste_category (name, code, description, icon, sort_order) VALUES
('可回收物', 'RECYCLABLE', '适宜回收利用和资源化利用的生活废弃物，包括废纸、废塑料、废玻璃、废金属、废织物等', 'recycle', 1),
('有害垃圾', 'HAZARDOUS', '对人体健康或者自然环境造成直接或者潜在危害的废弃物，包括废电池、废灯管、废药品、废油漆及其容器等', 'hazardous', 2),
('厨余垃圾', 'KITCHEN', '易腐烂的、含有机质的生活废弃物，包括食材废料、剩饭剩菜、过期食品、瓜皮果核等', 'kitchen', 3),
('其他垃圾', 'OTHER', '除可回收物、有害垃圾、厨余垃圾外的其他生活废弃物，包括砖瓦陶瓷、卫生纸、烟蒂等', 'other', 4),
('大件废弃物', 'BULKY', '体积较大、整体性强，需要拆分再处理的废弃物品，包括废旧家具、废旧家电、废旧床垫等', 'bulky', 5),
('装修废弃物', 'RENOVATION', '房屋装修过程中产生的废弃物，包括废木材、废砖瓦、废涂料、废管材等', 'renovation', 6);
