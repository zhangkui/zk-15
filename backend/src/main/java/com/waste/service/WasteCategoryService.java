package com.waste.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.waste.common.PageQuery;
import com.waste.entity.WasteCategory;

import java.util.List;

public interface WasteCategoryService extends IService<WasteCategory> {

    Page<WasteCategory> page(PageQuery query);

    boolean add(WasteCategory category);

    boolean update(WasteCategory category);

    boolean delete(Long id);

    WasteCategory getById(Long id);

    List<WasteCategory> listAll();
}
