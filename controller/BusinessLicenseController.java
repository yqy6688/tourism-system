package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.BusinessLicense;
import org.example.springboot.entity.User;
import org.example.springboot.service.BusinessLicenseService;
import org.example.springboot.service.UserService;
import org.springframework.web.bind.annotation.*;

/**
 * 营业资格证控制器
 */
@RestController
@RequestMapping("/business-license")
@Tag(name = "营业资格证管理", description = "营业资格证相关接口")
public class BusinessLicenseController {
    
    @Resource
    private BusinessLicenseService businessLicenseService;
    @Resource
    private UserService userService;
    
    /**
     * 保存营业资格证
     */
    @PostMapping("/save")
    @Operation(summary = "保存营业资格证", description = "保存或更新营业资格证信息")
    public Result<?> saveLicense(@RequestBody BusinessLicense license) {
        businessLicenseService.saveLicense(license);
        return Result.success("保存成功");
    }
    
    /**
     * 根据用户ID获取营业资格证
     */
    @GetMapping("/user/{userId}")
    @Operation(summary = "根据用户ID获取营业资格证", description = "获取指定用户的营业资格证信息")
    public Result<BusinessLicense> getLicenseByUserId(@PathVariable Long userId) {
        BusinessLicense license = businessLicenseService.getLicenseByUserId(userId);
        return Result.success(license);
    }
    
    /**
     * 分页获取营业资格证列表
     */
    @GetMapping("/page")
    @Operation(summary = "分页获取营业资格证列表", description = "分页获取营业资格证列表，支持状态筛选")
    public Result<?> getLicensePage(
        @RequestParam(required = false) String status,
        @RequestParam(defaultValue = "1") Integer currentPage,
        @RequestParam(defaultValue = "10") Integer size
    ) {
        Page<BusinessLicense> licensePage = businessLicenseService.getLicensePage(status, currentPage, size);
        for (BusinessLicense license : licensePage.getRecords()) {
            User userById = userService.getUserById(license.getUserId());
            license.setUser(userById);
        }

        return Result.success(licensePage);
    }
    
    /**
     * 审核营业资格证
     */
    @PutMapping("/review/{id}")
    @Operation(summary = "审核营业资格证", description = "审核营业资格证，设置状态和意见")
    public Result<?> reviewLicense(
        @PathVariable Long id,
        @RequestParam String status,
        @RequestParam(required = false) String comment
    ) {
        businessLicenseService.reviewLicense(id, status, comment);
        return Result.success("审核成功");
    }
    
    /**
     * 根据ID获取营业资格证
     */
    @GetMapping("/{id}")
    @Operation(summary = "根据ID获取营业资格证", description = "根据ID获取营业资格证详细信息")
    public Result<BusinessLicense> getLicenseById(@PathVariable Long id) {
        BusinessLicense license = businessLicenseService.getLicenseById(id);
        return Result.success(license);
    }
}