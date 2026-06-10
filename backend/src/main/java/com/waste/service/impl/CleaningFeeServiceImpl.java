package com.waste.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.entity.CleaningFee;
import com.waste.mapper.CleaningFeeMapper;
import com.waste.service.CleaningFeeService;
import org.springframework.stereotype.Service;

@Service
public class CleaningFeeServiceImpl extends ServiceImpl<CleaningFeeMapper, CleaningFee> implements CleaningFeeService {
}
