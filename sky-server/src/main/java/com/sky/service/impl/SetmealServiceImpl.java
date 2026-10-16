package com.sky.service.impl;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.constant.MessageConstant;
import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Dish;
import com.sky.entity.Setmeal;
import com.sky.entity.SetmealDish;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.mapper.CategoryMapper;
import com.sky.mapper.SetmealDishMapper;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import org.aspectj.apache.bcel.ExceptionConstants;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SetmealServiceImpl implements SetmealService {
    @Autowired
    private SetmealMapper setmealMapper;
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealDishMapper setmealDishMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    /**
     * 新增套餐
     * @param setmealDTO
     * */
    @Override
    public void save(SetmealDTO setmealDTO) {
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        //前端在设置套餐下面所拥有的菜品时 不会给为每一条设置他们归属的套餐 需要单独设置
        List<SetmealDish> setmealDishes = setmealDTO.getSetmealDishes();
        setmealMapper.insert(setmeal);
        Long Id = setmeal.getId();
        setmealDishes.forEach(setmealDish -> {
            setmealDish.setSetmealId(Id);
        });
        setmealDishMapper.insertBatch(setmealDishes);
    }
    /**
     * 通过id获取套餐信息
     * @param id
     * */
    @Override
    @Transactional
    public SetmealVO getById(Long id) {
        Setmeal setmeal = setmealMapper.getById(id);
        SetmealVO setmealVO = new SetmealVO();
        String categoryName = categoryMapper.getNameById(setmeal.getCategoryId());

        BeanUtils.copyProperties(setmeal,setmealVO);

        setmealVO.setCategoryName(categoryName);

        List<SetmealDish> list = setmealDishMapper.getDishesById(setmeal.getId());
        setmealVO.setSetmealDishes(list);

        return setmealVO;
    }
    /**
     * 分页查询套餐
     * @param setmealPageQueryDTO
     * */
    @Override
    public PageResult pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        PageHelper.startPage(setmealPageQueryDTO.getPage(),setmealPageQueryDTO.getPageSize());
        Page <SetmealVO> page = setmealMapper.pageQuery(setmealPageQueryDTO);
        return new PageResult(page.getTotal(),page.getResult());
    }

    @Override
    public void openOrClose(Integer status, Long id) {
        Setmeal setmeal =new Setmeal();
        setmeal.setStatus(status);
        setmeal.setId(id);
        setmealMapper.update(setmeal);
    }

    @Override
    public void setmealUpdate(SetmealDTO setmealDTO) {
        //先删除原有的套餐拥有的菜品
        setmealDishMapper.deleteBySetmealId(setmealDTO.getId());
        Setmeal setmeal = new Setmeal();
        BeanUtils.copyProperties(setmealDTO,setmeal);
        List<SetmealDish> dishes = setmealDTO.getSetmealDishes();
        if(dishes != null && !dishes.isEmpty()) {
            dishes.forEach(setmealDish -> {
                setmealDish.setSetmealId(setmeal.getId());
            });
            setmealDishMapper.insertBatch(dishes);
        }
        setmealMapper.update(setmeal);
    }

    @Override
    @Transactional
    public void deleteBatch(List<Long> ids) {
        if(ids == null||ids.isEmpty()) return ;
        ids.forEach(id->{
            Setmeal setmeal = setmealMapper.getById(id);
            if(setmeal.getStatus() == StatusConstant.ENABLE){
                throw new DeletionNotAllowedException(MessageConstant.DISH_ON_SALE);
            }
            setmealDishMapper.deleteBySetmealId(id);
        });
        setmealMapper.deleteBatchById(ids);
    }
}
