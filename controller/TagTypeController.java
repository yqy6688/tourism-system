package org.example.springboot.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import org.example.springboot.common.Result;
import org.example.springboot.entity.TagType;
import org.example.springboot.service.TagTypeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "标签管理接口")
@RestController
@RequestMapping("/tag")
public class TagTypeController {
    private static final Logger LOGGER = LoggerFactory.getLogger(TagTypeController.class);
    
    @Resource
    private TagTypeService tagTypeService;

    @Operation(summary = "分页查询标签")
    @GetMapping("/page")
    public Result<?> getTagTypesByPage(
            @RequestParam(required = false) String name,
            @RequestParam(defaultValue = "1") Integer currentPage,
            @RequestParam(defaultValue = "10") Integer size) {
        Page<TagType> page = tagTypeService.getTagTypesByPage(name, currentPage, size);
        return Result.success(page);
    }

    @Operation(summary = "获取所有启用的标签")
    @GetMapping("/all")
    public Result<?> getAllEnabledTags() {
        List<TagType> list = tagTypeService.getAllEnabledTags();
        return Result.success(list);
    }

    @Operation(summary = "根据ID获取标签")
    @GetMapping("/{id}")
    public Result<?> getById(@PathVariable Long id) {
        TagType tagType = tagTypeService.getById(id);
        return Result.success(tagType);
    }

    @Operation(summary = "新增标签")
    @PostMapping("/add")
    public Result<?> addTagType(@RequestBody TagType tagType) {
        tagTypeService.addTagType(tagType);
        return Result.success("新增成功");
    }

    @Operation(summary = "更新标签")
    @PutMapping("/{id}")
    public Result<?> updateTagType(@PathVariable Long id, @RequestBody TagType tagType) {
        tagTypeService.updateTagType(id, tagType);
        return Result.success("更新成功");
    }

    @Operation(summary = "删除标签")
    @DeleteMapping("/delete/{id}")
    public Result<?> deleteTagType(@PathVariable Long id) {
        tagTypeService.deleteTagType(id);
        return Result.success("删除成功");
    }

    @Operation(summary = "批量删除标签")
    @DeleteMapping("/batch-delete")
    public Result<?> batchDeleteTagTypes(@RequestBody List<Long> ids) {
        tagTypeService.batchDeleteTagTypes(ids);
        return Result.success("批量删除成功");
    }
}
