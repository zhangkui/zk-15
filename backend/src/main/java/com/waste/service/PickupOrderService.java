package com.waste.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.waste.common.PageQuery;
import com.waste.entity.PickupOrder;

public interface PickupOrderService extends IService<PickupOrder> {

    Page<PickupOrder> page(PageQuery query);

    boolean add(PickupOrder order);

    boolean update(PickupOrder order);

    boolean delete(Long id);

    PickupOrder getDetail(Long id);

    long countThisMonth();
}
