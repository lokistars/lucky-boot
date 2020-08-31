package com.lucky.platform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lucky.platform.entity.ModelPage;

import java.util.List;

/**
 * @author Nuany
 */
public interface ModelPageMapper extends BaseMapper<ModelPage> {
    List<ModelPage> selectList();
}
