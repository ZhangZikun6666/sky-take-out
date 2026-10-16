package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id查询对应套餐的id
     * */

    List<Long> getSetmealIDsByDishIds(List<Long> dishIds);

    void insertBatch(List<SetmealDish> setmealDishes);

    List<SetmealDish> getDishesById(Long id);

    @Delete("delete from setmeal_dish where setmeal_id = #{id}")
    void deleteBySetmealId(Long id);



}
