package org.example.springboot.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.example.springboot.entity.Carousel;

public interface CarouselService extends IService<Carousel> {
    
    Page<Carousel> getCarouselPage(Integer currentPage, Integer size);
    
    boolean updateStatus(Long id, Integer status);
}