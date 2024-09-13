package com.aixming.web.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.aixming.web.model.dto.generator.GeneratorQueryRequest;
import com.aixming.web.model.entity.Generator;
import com.aixming.web.model.vo.GeneratorVO;

import javax.servlet.http.HttpServletRequest;

/**
 * 代码生成器服务
 *
 * @author AixMing
 */
public interface GeneratorService extends IService<Generator> {

    /**
     * 校验数据
     *
     * @param generator
     * @param add 对创建的数据进行校验
     */
    void validGenerator(Generator generator, boolean add);

    /**
     * 获取查询条件
     *
     * @param generatorQueryRequest
     * @return
     */
    QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest);
    
    /**
     * 获取代码生成器封装
     *
     * @param generator
     * @param request
     * @return
     */
    GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request);

    /**
     * 分页获取代码生成器封装
     *
     * @param generatorPage
     * @param request
     * @return
     */
    Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request);
}
