package com.loogingko.ncjd.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.loogingko.ncjd.constant.Constants;
import com.loogingko.ncjd.mapper.ManualCategoryMapper;
import com.loogingko.ncjd.mapper.ManualItemMapper;
import com.loogingko.ncjd.model.bo.R;
import com.loogingko.ncjd.model.entity.ManualCategoryDO;
import com.loogingko.ncjd.model.entity.ManualItemDO;
import com.loogingko.ncjd.model.dto.ManualDTO;
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
        LambdaQueryWrapper<ManualCategoryDO> categoryWrapper = new LambdaQueryWrapper<>();
        categoryWrapper.and(wrapper -> wrapper
                        .eq(ManualCategoryDO::getUserId, userId)
                        .or()
                        .eq(ManualCategoryDO::getUserId, Constants.PUBLIC_USERID))
                .orderByAsc(ManualCategoryDO::getSort);

        List<ManualCategoryDO> mcList = categoryMapper.selectList(categoryWrapper);

        if (CollUtil.isEmpty(mcList)) {
            return R.succ(Collections.emptyList());
        }

        // 2. 批量查询所有分类下的条目（解决N+1问题）
        List<String> cateIds = mcList.stream()
                .map(ManualCategoryDO::getId)
                .collect(Collectors.toList());

        LambdaQueryWrapper<ManualItemDO> itemWrapper = new LambdaQueryWrapper<>();
        itemWrapper.in(ManualItemDO::getCateId, cateIds)
                .and(wrapper -> wrapper
                        .eq(ManualItemDO::getUserId, userId)
                        .or()
                        .eq(ManualItemDO::getUserId, Constants.PUBLIC_USERID))
                .orderByAsc(ManualItemDO::getSort);

        List<ManualItemDO> miList = itemMapper.selectList(itemWrapper);

        // 3. 将条目按分类ID分组
        Map<String, List<ManualItemDO>> itemMap = miList.stream()
                .collect(Collectors.groupingBy(ManualItemDO::getCateId));

        // 4. 组装结果
        List<ManualDTO> voList = mcList.stream().map(category -> {
            ManualDTO vo = BeanUtil.copyProperties(category, ManualDTO.class);
            vo.setItems(itemMap.getOrDefault(category.getId(), Collections.emptyList()));
            return vo;
        }).collect(Collectors.toList());

        return R.succ(voList);
    }
}