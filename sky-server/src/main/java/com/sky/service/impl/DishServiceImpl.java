package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.DishDTO;
import com.sky.dto.DishPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.DishFlavor;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.DishFlavorMapper;
import com.sky.mapper.DishMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.result.PageResult;
import com.sky.service.DishService;
import com.sky.vo.DishOverViewVO;
import com.sky.vo.DishVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class DishServiceImpl implements DishService {
    @Autowired
    private DishMapper dishMapper;
    @Autowired
    private DishFlavorMapper dishFlavorMapper;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    /**
     * 新增菜品和对应的口味
     * @Param dishDTO
     * */
    @Transactional //事务注解 确保里面的每一部分都成功才返回 否则不返回
    public void saveWithFlavor(DishDTO dishDTO) {
        //把DTO中的数据拷贝到dish对象里面
        Dish dish = new Dish();
        BeanUtils.copyProperties(dishDTO,dish);
        //向菜品表插入数据
        dishMapper.insert(dish);
        Long dishId = dish.getId();
        //向口味表插入多条数据
        List<DishFlavor> flavors = dishDTO.getFlavors();
        if(flavors != null && !flavors.isEmpty()){
            flavors.forEach(dishFlavor -> {
                dishFlavor.setDishId(dishId);
            });
            dishFlavorMapper.insertBatch(flavors);
        }
    }

    @Override
    public PageResult pageQuery(DishPageQueryDTO dishPageQueryDTO) {
        PageHelper.startPage(dishPageQueryDTO.getPage(),dishPageQueryDTO.getPageSize());
        Page<DishVO> page = dishMapper.pageQuery(dishPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    @Transactional //事务注解
    public void deleteBatch(List<Long> ids) {
        //判断当前菜品是否能够删除 是否存在起售中的菜品
        for (Long id : ids) {
            Dish dish = dishMapper.getById(id);
            if(dish.getStatus() == StatusConstant.ENABLE){
                //当前菜品处于起售中
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
        }
        //判断菜品是否被套餐关联
        List<Long> setmealIds = setmealDishMapper.getSetmealIDsByDishIds(ids);
        if(setmealIds != null && !setmealIds.isEmpty()){
            //当前菜品被套餐关联了 不能删除
            throw new DeletionNotAllowedException(MessageConstant.DISH_BE_RELATED_BY_SETMEAL);

        }
        //删除表中的菜品数据
//            for(Long id: ids){
//                dishMapper.deleteById(id);
//                //删除菜品的口味数据
//                dishFlavorMapper.deleteById(id);
//            }
        //删除部分可以优化 直接根据集合进行删除
        dishMapper.deleteById(ids);
        dishFlavorMapper.deleteById(ids);
    }
}
