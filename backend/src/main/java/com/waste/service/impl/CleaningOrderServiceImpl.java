package com.waste.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.entity.CleaningOrder;
import com.waste.mapper.CleaningOrderMapper;
import com.waste.service.CleaningOrderService;
import org.springframework.stereotype.Service;

@Service
public class CleaningOrderServiceImpl extends ServiceImpl<CleaningOrderMapper, CleaningOrder> implements CleaningOrderService {
}
