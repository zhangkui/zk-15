package com.waste.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.entity.DisposalVoucher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface DisposalVoucherMapper extends BaseMapper<DisposalVoucher> {

    @Select("SELECT v.*, w.waste_name, o.order_no FROM disposal_voucher v " +
            "LEFT JOIN waste_info w ON v.waste_id = w.id " +
            "LEFT JOIN pickup_order o ON v.order_id = o.id " +
            "WHERE v.deleted = 0 " +
            "AND (#{keyword} IS NULL OR v.voucher_no LIKE CONCAT('%', #{keyword}, '%') OR v.voucher_title LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND (#{status} IS NULL OR v.voucher_type = #{voucherType}) " +
            "ORDER BY v.created_time DESC")
    IPage<DisposalVoucher> selectPageWithRelations(Page<DisposalVoucher> page,
                                                   @Param("keyword") String keyword,
                                                   @Param("voucherType") String voucherType);
}
