package com.waste.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.waste.entity.PickupOrder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface PickupOrderMapper extends BaseMapper<PickupOrder> {

    @Select("SELECT o.*, w.waste_name, w.waste_no FROM pickup_order o " +
            "LEFT JOIN waste_info w ON o.waste_id = w.id " +
            "WHERE o.deleted = 0 " +
            "AND (#{keyword} IS NULL OR o.order_no LIKE CONCAT('%', #{keyword}, '%') OR o.contact_person LIKE CONCAT('%', #{keyword}, '%')) " +
            "AND (#{status} IS NULL OR o.status = #{status}) " +
            "ORDER BY o.created_time DESC")
    IPage<PickupOrder> selectPageWithWaste(Page<PickupOrder> page,
                                           @Param("keyword") String keyword,
                                           @Param("status") Integer status);
}
