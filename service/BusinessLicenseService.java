package org.example.springboot.service;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.example.springboot.entity.BusinessLicense;
import org.example.springboot.entity.User;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.BusinessLicenseMapper;
import org.example.springboot.mapper.UserMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 营业资格证服务
 */
@Service
public class BusinessLicenseService {
    private static final Logger logger = LoggerFactory.getLogger(BusinessLicenseService.class);
    
    @Resource
    private BusinessLicenseMapper businessLicenseMapper;
    
    @Resource
    private UserMapper userMapper;
    
    /**
     * 保存营业资格证
     */
    public void saveLicense(BusinessLicense license) {
        // 检查用户是否存在
        User user = userMapper.selectById(license.getUserId());
        if (user == null) {
            throw new ServiceException("用户不存在");
        }
        
        // 检查用户是否是商家
        if (!"MERCHANT".equals(user.getRoleCode())) {
            throw new ServiceException("只有商家用户才能提交营业资格证");
        }
        
        // 检查是否已经存在营业资格证
        BusinessLicense existingLicense = businessLicenseMapper.selectOne(
            new LambdaQueryWrapper<BusinessLicense>().eq(BusinessLicense::getUserId, license.getUserId())
        );
        
        if (existingLicense != null) {
            // 更新现有资格证（重新提交审核）
            license.setId(existingLicense.getId());
            license.setCreateTime(existingLicense.getCreateTime());
            // 重置状态为待审核，清空审核意见和时间
            license.setStatus("PENDING");
            license.setReviewComment(null);
            license.setReviewTime(null);
            if (businessLicenseMapper.updateById(license) <= 0) {
                throw new ServiceException("更新营业资格证失败");
            }
        } else {
            // 创建新资格证
            license.setStatus("PENDING");
            if (businessLicenseMapper.insert(license) <= 0) {
                throw new ServiceException("保存营业资格证失败");
            }
        }
    }
    
    /**
     * 根据用户ID获取营业资格证
     */
    public BusinessLicense getLicenseByUserId(Long userId) {
        return businessLicenseMapper.selectOne(
            new LambdaQueryWrapper<BusinessLicense>().eq(BusinessLicense::getUserId, userId)
        );
    }
    
    /**
     * 分页获取营业资格证列表
     */
    public Page<BusinessLicense> getLicensePage(String status, Integer currentPage, Integer size) {
        LambdaQueryWrapper<BusinessLicense> queryWrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.isNotBlank(status)) {
            queryWrapper.eq(BusinessLicense::getStatus, status);
        }
        
        return businessLicenseMapper.selectPage(new Page<>(currentPage, size), queryWrapper);
    }
    
    /**
     * 审核营业资格证
     */
    public void reviewLicense(Long id, String status, String comment) {
        BusinessLicense license = businessLicenseMapper.selectById(id);
        if (license == null) {
            throw new ServiceException("营业资格证不存在");
        }
        
        license.setStatus(status);
        license.setReviewComment(comment);
        license.setReviewTime(LocalDateTime.now());
        
        if (businessLicenseMapper.updateById(license) <= 0) {
            throw new ServiceException("审核营业资格证失败");
        }
    }
    
    /**
     * 根据ID获取营业资格证
     */
    public BusinessLicense getLicenseById(Long id) {
        return businessLicenseMapper.selectById(id);
    }
}