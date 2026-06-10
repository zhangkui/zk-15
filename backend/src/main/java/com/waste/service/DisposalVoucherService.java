package com.waste.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.waste.common.PageQuery;
import com.waste.entity.DisposalVoucher;

public interface DisposalVoucherService extends IService<DisposalVoucher> {

    Page<DisposalVoucher> page(PageQuery query);

    boolean add(DisposalVoucher voucher);

    boolean update(DisposalVoucher voucher);

    boolean delete(Long id);

    DisposalVoucher getDetail(Long id);
}
