package com.loogingko.ncjd.service.biz;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.bean.copier.CopyOptions;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.loogingko.ncjd.constant.Constants;
import com.loogingko.ncjd.mapper.MemoMapper;
import com.loogingko.ncjd.model.bo.R;
import com.loogingko.ncjd.model.dto.MemoListReq;
import com.loogingko.ncjd.model.dto.MemoSaveReq;
import com.loogingko.ncjd.model.entity.MemoItemDO;
import com.loogingko.ncjd.service.auth.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemoService extends ServiceImpl<MemoMapper, MemoItemDO> {
    
    private final JwtService jwtService;

    /**
     * 预加载标签等数据
     * @author LiuRunYu 2026-06-25
     */
    public R pre() {
        String userId = jwtService.getCurrentUserId();

        // 所有未删除笔记的标签
        List<MemoItemDO> memoDOs = lambdaQuery()
                .eq(Constants.PUBLIC_USERID.equals(userId), MemoItemDO::getUserId, Constants.PUBLIC_USERID)
                .eq(!Constants.PUBLIC_USERID.equals(userId), MemoItemDO::getUserId, userId)
                .isNull(MemoItemDO::getDeletedAt)
                .select(MemoItemDO::getTags)
                .list();

        // 提取并去重
        Set<String> tagSet = memoDOs.stream()
                .map(MemoItemDO::getTags)
                .filter(StrUtil::isNotBlank)
                .flatMap(tags -> Arrays.stream(tags.split(",")))
                .filter(StrUtil::isNotBlank)
                .collect(Collectors.toSet());
        return R.succ(new ArrayList<>(tagSet));
    }

    /**
     * 抓数据
     * @author LiuRunYu 2026-06-25
     */
    public R listByUser(MemoListReq req) {
        String userId = jwtService.getCurrentUserId();
        
        List<MemoItemDO> memoDOs = lambdaQuery()
                .eq(Constants.PUBLIC_USERID.equals(userId), MemoItemDO::getUserId, Constants.PUBLIC_USERID)
                .eq(!Constants.PUBLIC_USERID.equals(userId), MemoItemDO::getUserId, userId)
                .isNull(req.getDelFlag() != null && req.getDelFlag() == 0, MemoItemDO::getDeletedAt)
                .isNotNull(req.getDelFlag() != null && req.getDelFlag() == 1, MemoItemDO::getDeletedAt)
                .like(StrUtil.isNotBlank(req.getKeyword()), MemoItemDO::getPlainText, req.getKeyword())
                .and(CollUtil.isNotEmpty(req.getTags()),
                        w -> {
                            for (String tag : req.getTags()) {
                                w.like(MemoItemDO::getTags, "," + tag + ",");
                            }
                        })
                .ge(StrUtil.isNotBlank(req.getDateStart()), MemoItemDO::getCreateAt, req.getDateStart())
                .le(StrUtil.isNotBlank(req.getDateEnd()), MemoItemDO::getCreateAt, req.getDateEnd())
                .orderByDesc(MemoItemDO::getCreateAt) // 创建时间降序，防止混乱
                .list();
        return R.succ(memoDOs);
    }

    /**
     * 新增/更新笔记
     * @author LiuRunYu 2026-06-25
     */
    public R saveOrUpdate(MemoSaveReq req) {
        String userId = jwtService.getCurrentUserId();
        if (Constants.PUBLIC_USERID.equals(userId)) {
            return R.fail("公共用户无操作权限");
        }

        if (StrUtil.isBlank(req.getId())) {
            // 新增
            MemoItemDO memo = new MemoItemDO();
            memo.setUserId(userId);
            BeanUtil.copyProperties(req, memo, CopyOptions.create().ignoreNullValue().setIgnoreProperties("id", "tags"));
            memo.setTags(transTag(req.getTags()));
            save(memo);
            return R.succ(memo);
        } else {
            // 更新
            MemoItemDO memo = getById(req.getId());
            if (memo == null) {
                return R.fail("笔记不存在");
            }
            if (!userId.equals(memo.getUserId())) {
                return R.fail("无权修改他人笔记");
            }
            BeanUtil.copyProperties(req, memo, CopyOptions.create().ignoreNullValue().setIgnoreProperties("id", "tags"));
            memo.setTags(transTag(req.getTags()));
            updateById(memo);
            return R.succ(memo);
        }
    }

    /**
     * 删除笔记
     * @author LiuRunYu 2026-06-25
     */
    public R deleteById(String id) {
        String userId = jwtService.getCurrentUserId();
        if (Constants.PUBLIC_USERID.equals(userId)) {
            return R.fail("公共用户无操作权限");
        }
        MemoItemDO memo = getById(id);
        if (memo == null) {
            return R.fail("笔记不存在");
        }
        if (!userId.equals(memo.getUserId())) {
            return R.fail("无权删除他人笔记");
        }
        memo.setDeletedAt(LocalDateTime.now());
        updateById(memo);
        return R.succ(null);
    }

    /**
     * 转换Tag格式
     */
    private String transTag(List<String> tags) {
        if (CollUtil.isEmpty(tags)) {
            return null;
        }
        return "," + String.join(",", tags) + ",";
    }
}