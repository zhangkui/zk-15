package com.waste.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.waste.entity.WasteItem;
import com.waste.mapper.WasteItemMapper;
import com.waste.service.WasteItemService;
import org.springframework.stereotype.Service;

@Service
public class WasteItemServiceImpl extends ServiceImpl<WasteItemMapper, WasteItem> implements WasteItemService {
}
