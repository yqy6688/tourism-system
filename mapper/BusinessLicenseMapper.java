package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.example.springboot.entity.BusinessLicense;

/**
 * 营业资格证Mapper
 */
@Mapper
public interface BusinessLicenseMapper extends BaseMapper<BusinessLicense> {
}