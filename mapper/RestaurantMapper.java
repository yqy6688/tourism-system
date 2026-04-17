package org.example.springboot.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.example.springboot.entity.Restaurant;

import java.util.List;

/**
 * 餐饮信息Mapper接口
 */
@Mapper
public interface RestaurantMapper extends BaseMapper<Restaurant> {
    
    /**
     * 获取所有餐饮类型
     */
    @Select("SELECT DISTINCT type FROM restaurant WHERE status = 'OPEN' ORDER BY type")
    List<String> selectAllTypes();
    
    /**
     * 分页查询餐饮列表（带关联景点信息）
     */
    @Select("<script>" +
            "SELECT r.*, s.name as scenicName " +
            "FROM restaurant r " +
            "LEFT JOIN scenic_spot s ON r.scenic_id = s.id " +
            "WHERE r.status = 'OPEN' " +
            "<if test='name != null and name != \"\"'>" +
            "   AND r.name LIKE CONCAT('%', #{name}, '%')" +
            "</if>" +
            "<if test='type != null and type != \"\"'>" +
            "   AND r.type = #{type}" +
            "</if>" +
            "<if test='scenicId != null'>" +
            "   AND r.scenic_id = #{scenicId}" +
            "</if>" +
            "<if test='minPrice != null and minPrice != \"\"'>" +
            "   AND (SUBSTRING_INDEX(r.price_range, '-', 1) &gt;= #{minPrice})" +
            "</if>" +
            "<if test='maxPrice != null and maxPrice != \"\"'>" +
            "   AND (SUBSTRING_INDEX(r.price_range, '-', -1) &lt;= #{maxPrice})" +
            "</if>" +
            "<if test='minRating != null and minRating != 0'>" +
            "   AND r.average_rating &gt;= #{minRating}" +
            "</if>" +
            "<if test='sortBy != null and sortBy != \"\"'>" +
            "   <choose>" +
            "       <when test='sortBy == \"price_asc\"'>" +
            "           ORDER BY SUBSTRING_INDEX(r.price_range, '-', 1) ASC" +
            "       </when>" +
            "       <when test='sortBy == \"price_desc\"'>" +
            "           ORDER BY SUBSTRING_INDEX(r.price_range, '-', 1) DESC" +
            "       </when>" +
            "       <when test='sortBy == \"rating_desc\"'>" +
            "           ORDER BY r.average_rating DESC" +
            "       </when>" +
            "       <otherwise>" +
            "           ORDER BY r.create_time DESC" +
            "       </otherwise>" +
            "   </choose>" +
            "</if>" +
            "<if test='sortBy == null or sortBy == \"\"'>" +
            "   ORDER BY r.create_time DESC" +
            "</if>" +
            "</script>")
    List<Restaurant> selectRestaurantPage(@Param("name") String name,
                                         @Param("type") String type,
                                         @Param("scenicId") Integer scenicId,
                                         @Param("minPrice") String minPrice,
                                         @Param("maxPrice") String maxPrice,
                                         @Param("minRating") Double minRating,
                                         @Param("sortBy") String sortBy);
}