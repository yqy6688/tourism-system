package org.example.springboot.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.example.springboot.entity.Carousel;
import org.example.springboot.mapper.CarouselMapper;
import org.example.springboot.service.CarouselService;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class CarouselServiceImpl extends ServiceImpl<CarouselMapper, Carousel> implements CarouselService {

    @Override
    public Page<Carousel> getCarouselPage(Integer currentPage, Integer size) {
        Page<Carousel> page = new Page<>(currentPage, size);
        QueryWrapper<Carousel> wrapper = new QueryWrapper<>();
        wrapper.orderByDesc("create_time");
        return baseMapper.selectPage(page, wrapper);
    }

    @Override
    public boolean updateStatus(Long id, Integer status) {
        Carousel carousel = new Carousel();
        carousel.setId(id);
        carousel.setStatus(status);
        return updateById(carousel);
    }
}