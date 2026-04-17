package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.example.springboot.entity.TagType;

import java.util.List;

public interface TagTypeService {

    /**
     * 分页查询标签
     */
    Page<TagType> getTagTypesByPage(String name, Integer currentPage, Integer size);

    /**
     * 获取所有启用的标签
     */
    List<TagType> getAllEnabledTags();

    /**
     * 根据ID获取标签
     */
    TagType getById(Long id);

    /**
     * 新增标签
     */
    void addTagType(TagType tagType);

    /**
     * 更新标签
     */
    void updateTagType(Long id, TagType tagType);

    /**
     * 删除标签
     */
    void deleteTagType(Long id);

    /**
     * 批量删除标签
     */
    void batchDeleteTagTypes(List<Long> ids);
}
