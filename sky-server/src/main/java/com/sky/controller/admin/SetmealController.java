package com.sky.controller.admin;

import com.sky.constant.StatusConstant;
import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.entity.Setmeal;
import com.sky.mapper.SetmealMapper;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;
import com.sky.vo.SetmealVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
@Api("套餐相关接口")
@RequestMapping("/admin/setmeal")
public class SetmealController {
    @Autowired
    private SetmealService setmealService;
    @Autowired
    private SetmealMapper setmealMapper;


    @PostMapping
    @ApiOperation("增加套餐操作")
    public Result save(@RequestBody SetmealDTO setmealDTO){
        log.info("增加套餐{}",setmealDTO);
        setmealService.save(setmealDTO);
        return Result.success();
    }

    @GetMapping("/page")
    @ApiOperation("套餐分页查询")
    public Result<PageResult> pageQuery(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("进行套餐分页查询{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQuery(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    @GetMapping("/{id}")
    @ApiOperation("通过id查询套餐")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("通过ID{}查询套餐",id);
        SetmealVO setmealVO = setmealService.getById(id);
        return Result.success(setmealVO);
    }

    @PostMapping("/status/{status}")
    @ApiOperation("套餐起售或者停售")
    public Result openOrClose(@PathVariable Integer status,Long id){
        log.info("对套餐{}进行套餐停售或起售{}" ,id,status);
        setmealService.openOrClose(status,id);
        return Result.success();
    }

    @PutMapping
    @ApiOperation("套餐修改")
    public Result setmealUpdate(@RequestBody SetmealDTO setmealDTO){
        log.info("修改套餐{}",setmealDTO);
        setmealService.setmealUpdate(setmealDTO);
        return Result.success();
    }

    @DeleteMapping
    @ApiOperation("套餐删除")
    public Result setmealBatchDelete(@RequestParam List<Long> ids){
        log.info("删除套餐{}",ids);
        setmealService.deleteBatch(ids);
        return Result.success();
    }
}
