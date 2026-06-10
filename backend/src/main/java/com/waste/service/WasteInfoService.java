package com.waste.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.waste.common.PageQuery;
import com.waste.entity.WasteInfo;

import java.util.List;

public interface WasteInfoService extends IService<WasteInfo> {

    Page<WasteInfo> page(PageQuery query);

    boolean add(WasteInfo wasteInfo);

    boolean update(WasteInfo wasteInfo);

    boolean delete(Long id);

    WasteInfo getDetail(Long id);

    List<WasteInfo> listByCategoryId(Long categoryId);

    long countTotal();

    long countByStatus(Integer status);
}
