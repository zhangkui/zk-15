package com.waste.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.entity.WasteCategory;
import com.waste.mapper.WasteCategoryMapper;
import com.waste.service.WasteCategoryService;
import org.springframework.stereotype.Service;

@Service
public class WasteCategoryServiceImpl extends ServiceImpl<WasteCategoryMapper, WasteCategory> implements WasteCategoryService {
}
