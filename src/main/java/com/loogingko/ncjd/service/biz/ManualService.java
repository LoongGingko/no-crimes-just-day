package com.loogingko.ncjd.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.loogingko.ncjd.constant.Constants;
import com.loogingko.ncjd.mapper.ManualCategoryMapper;
import com.loogingko.ncjd.mapper.ManualItemMapper;
import com.loogingko.ncjd.model.bo.R;
import com.loogingko.ncjd.model.entity.ManualCategory;
import com.loogingko.ncjd.model.entity.ManualItem;
import com.loogingko.ncjd.model.vo.ManualVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ManualService {

    private final ManualCategoryMapper categoryMapper;
    private final ManualItemMapper itemMapper;

    public R getManualList(String userId) {
        
        // 1. 一次性查询所有分类（包括用户自己的和公共的）
        LambdaQueryWrapper<ManualCategory> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.and(wrapper -> wrapper
                        .eq(ManualCategory::getUserId, userId)
                        .or()
                        .eq(ManualCategory::getUserId, Constants.PUBLIC_USERID))
                .orderByAsc(ManualCategory::getSort);

        List<ManualCategory> mcList = categoryMapper.selectList(categoryWrapper);

        if (CollUtil.isEmpty(mcList)) {
            return R.succ(Collections.emptyList());
        }

        // 2. 批量查询所有分类下的条目（解决N+1问题）
        List<String> cateIds = mcList.stream()
                .map(ManualCategory::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<ManualItem> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(ManualItem::getCateId, cateIds)
                .and(wrapper -> wrapper
                        .eq(ManualItem::getUserId, userId)
                        .or()
                        .eq(ManualItem::getUserId, Constants.PUBLIC_USERID))
                .orderByAsc(ManualItem::getSort);

        List<ManualItem> miList = itemMapper.selectList(itemWrapper);

        // 3. 将条目按分类ID分组
        Map<String, List<ManualItem>> itemMap = miList.stream()
                .collect(Collectors.groupingBy(ManualItem::getCateId));

        // 4. 组装结果
        List<ManualVO> voList = mcList.stream().map(category -> {
            ManualVO vo = BeanUtil.copyProperties(category, ManualVO.class);
            vo.setItems(itemMap.getOrDefault(category.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        return R.succ(voList);
    }
}