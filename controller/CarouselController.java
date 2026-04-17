package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.Carousel;
import org.example.springboot.service.CarouselService;
import org.springframework.web.bind.annotation.*;

@Tag(name = "轮播图管理")
@RestController
@RequestMapping("/carousel")
public class CarouselController {

    @Resource
    private CarouselService carouselService;

    @Operation(summary = "获取轮播图列表")
    @GetMapping("/page")
    public Result<Page<Carousel>> getCarouselPage(
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        try {
            Page<Carousel> page = carouselService.getCarouselPage(currentPage, size);
            return Result.success(page);
        } catch (Exception e) {
            return Result.error("获取轮播图列表失败");
        }
    }

    @Operation(summary = "添加轮播图")
    @PostMapping
    public Result<String> addCarousel(@RequestBody Carousel carousel) {
        try {
            boolean success = carouselService.save(carousel);
            if (success) {
                return Result.success("添加成功");
            } else {
                return Result.error("添加失败");
            }
        } catch (Exception e) {
            return Result.error("添加失败");
        }
    }

    @Operation(summary = "编辑轮播图")
    @PutMapping
    public Result<String> updateCarousel(@RequestBody Carousel carousel) {
        try {
            boolean success = carouselService.updateById(carousel);
            if (success) {
                return Result.success("编辑成功");
            } else {
                return Result.error("编辑失败");
            }
        } catch (Exception e) {
            return Result.error("编辑失败");
        }
    }

    @Operation(summary = "删除轮播图")
    @DeleteMapping("/{id}")
    public Result<String> deleteCarousel(@PathVariable Long id) {
        try {
            boolean success = carouselService.removeById(id);
            if (success) {
                return Result.success("删除成功");
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败");
        }
    }

    @Operation(summary = "修改轮播图状态")
    @PutMapping("/status/{id}")
    public Result<String> updateCarouselStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            boolean success = carouselService.updateStatus(id, status);
            if (success) {
                return Result.success("状态修改成功");
            } else {
                return Result.error("状态修改失败");
            }
        } catch (Exception e) {
            return Result.error("状态修改失败");
        }
    }
}