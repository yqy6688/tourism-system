package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.example.springboot.entity.TagType;
import org.example.springboot.exception.ServiceException;
import org.example.springboot.mapper.TagTypeMapper;
import org.example.springboot.service.TagTypeService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class TagTypeServiceImpl extends ServiceImpl<TagTypeMapper, TagType> implements TagTypeService {

    @Override
    public Page<TagType> getTagTypesByPage(String name, Integer currentPage, Integer size) {
        LambdaQueryWrapper<TagType> queryWrapper = new LambdaQueryWrapper<>();
        
        // 按名称模糊查询
        if (StringUtils.hasText(name)) {
            queryWrapper.like(TagType::getName, name);
        }
        
        // 按排序顺序升序排列
        queryWrapper.orderByAsc(TagType::getSortOrder);
        
        return page(new Page<>(currentPage, size), queryWrapper);
    }

    @Override
    public List<TagType> getAllEnabledTags() {
        LambdaQueryWrapper<TagType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TagType::getStatus, 1)
                .orderByAsc(TagType::getSortOrder);
        return list(queryWrapper);
    }

    @Override
    public TagType getById(Long id) {
        TagType tagType = super.getById(id);
        if (tagType == null) {
            throw new ServiceException("标签不存在");
        }
        return tagType;
    }

    @Override
    public void addTagType(TagType tagType) {
        // 检查标签名称是否已存在
        LambdaQueryWrapper<TagType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(TagType::getName, tagType.getName());
        if (count(queryWrapper) > 0) {
            throw new ServiceException("标签名称已存在");
        }
        
        // 设置默认值
        if (tagType.getStatus() == null) {
            tagType.setStatus(1);
        }
        if (tagType.getSortOrder() == null) {
            tagType.setSortOrder(0);
        }
        
        save(tagType);
    }

    @Override
    public void updateTagType(Long id, TagType tagType) {
        TagType existingTag = getById(id);
        if (existingTag == null) {
            throw new ServiceException("标签不存在");
        }
        
        // 检查标签名称是否与其他标签重复
        if (!existingTag.getName().equals(tagType.getName())) {
            LambdaQueryWrapper<TagType> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(TagType::getName, tagType.getName())
                    .ne(TagType::getId, id);
            if (count(queryWrapper) > 0) {
                throw new ServiceException("标签名称已存在");
            }
        }
        
        tagType.setId(id);
        updateById(tagType);
    }

    @Override
    public void deleteTagType(Long id) {
        TagType tagType = getById(id);
        if (tagType == null) {
            throw new ServiceException("标签不存在");
        }
        removeById(id);
    }

    @Override
    public void batchDeleteTagTypes(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }
        removeByIds(ids);
    }
}
