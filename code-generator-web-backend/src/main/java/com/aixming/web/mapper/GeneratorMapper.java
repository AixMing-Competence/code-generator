package com.aixming.web.mapper;

import com.aixming.web.model.entity.Generator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @Entity generator.domain.Generator
 */
public interface GeneratorMapper extends BaseMapper<Generator> {
    @Select("select id,distPath from generator where isDelete = 1")
    List<Generator> listDeletedGenerator();
}
