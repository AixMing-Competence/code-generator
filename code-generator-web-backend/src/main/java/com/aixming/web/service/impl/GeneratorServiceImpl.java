package com.aixming.web.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import com.aixming.web.common.ErrorCode;
import com.aixming.web.constant.CommonConstant;
import com.aixming.web.exception.BusinessException;
import com.aixming.web.exception.ThrowUtils;
import com.aixming.web.mapper.GeneratorMapper;
import com.aixming.web.model.dto.generator.GeneratorQueryRequest;
import com.aixming.web.model.entity.Generator;
import com.aixming.web.model.entity.User;
import com.aixming.web.model.vo.GeneratorVO;
import com.aixming.web.model.vo.UserVO;
import com.aixming.web.service.GeneratorService;
import com.aixming.web.service.UserService;
import com.aixming.web.utils.SqlUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 代码生成器服务实现
 *
 * @author AixMing
 */
@Service
@Slf4j
public class GeneratorServiceImpl extends ServiceImpl<GeneratorMapper, Generator> implements GeneratorService {

    @Resource
    private UserService userService;

    /**
     * 校验数据
     *
     * @param generator
     * @param add       对创建的数据进行校验
     */
    @Override
    public void validGenerator(Generator generator, boolean add) {
        ThrowUtils.throwIf(generator == null, ErrorCode.PARAMS_ERROR);
        // 从对象中取值
        String name = generator.getName();
        String description = generator.getDescription();

        // 创建数据时，参数不能为空
        if (add) {
            ThrowUtils.throwIf(StrUtil.hasBlank(name, description), ErrorCode.PARAMS_ERROR);
        }
        // 修改数据时，有参数则校验
        if (StrUtil.isNotBlank(name) && name.length() > 80) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "名称不能过长");
        }
        if (StrUtil.isNotBlank(description) && description.length() > 256) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR, "描述不能过长");
        }
    }

    /**
     * 获取查询条件
     *
     * @param generatorQueryRequest
     * @return
     */
    @Override
    public QueryWrapper<Generator> getQueryWrapper(GeneratorQueryRequest generatorQueryRequest) {
        QueryWrapper<Generator> queryWrapper = new QueryWrapper<>();
        if (generatorQueryRequest == null) {
            return queryWrapper;
        }
        // 从对象中取值
        Long id = generatorQueryRequest.getId();
        String name = generatorQueryRequest.getName();
        String description = generatorQueryRequest.getDescription();
        String basePackage = generatorQueryRequest.getBasePackage();
        String version = generatorQueryRequest.getVersion();
        String author = generatorQueryRequest.getAuthor();
        List<String> tags = generatorQueryRequest.getTags();
        String distPath = generatorQueryRequest.getDistPath();
        Long userId = generatorQueryRequest.getUserId();
        Integer status = generatorQueryRequest.getStatus();
        Long notId = generatorQueryRequest.getNotId();
        String searchText = generatorQueryRequest.getSearchText();
        String sortField = generatorQueryRequest.getSortField();
        String sortOrder = generatorQueryRequest.getSortOrder();

        // 补充需要的查询条件
        // 从多字段中搜索
        if (StringUtils.isNotBlank(searchText)) {
            // 需要拼接查询条件
            queryWrapper.and(qw -> qw.like("name", searchText).or().like("description", searchText));
        }
        // todo 修复拼接逻辑异常
        // 模糊查询
        queryWrapper.like(StringUtils.isNotBlank(name), "name", name);
        queryWrapper.like(StringUtils.isNotBlank(description), "description", description);
        queryWrapper.like(StringUtils.isNotBlank(basePackage), "basePackage", basePackage);
        queryWrapper.like(StringUtils.isNotBlank(version), "version", version);
        queryWrapper.like(StringUtils.isNotBlank(author), "author", author);
        queryWrapper.like(StringUtils.isNotBlank(distPath), "distPath", distPath);
        // JSON 数组查询
        if (CollUtil.isNotEmpty(tags)) {
            for (String tag : tags) {
                queryWrapper.like("tags", "\"" + tag + "\"");
            }
        }
        // 精确查询
        queryWrapper.ne(ObjectUtils.isNotEmpty(notId), "id", notId);
        queryWrapper.eq(ObjectUtils.isNotEmpty(id), "id", id);
        queryWrapper.eq(ObjectUtils.isNotEmpty(status), "status", status);
        queryWrapper.eq(ObjectUtils.isNotEmpty(userId), "userId", userId);
        // 排序规则
        queryWrapper.orderBy(SqlUtils.validSortField(sortField),
                sortOrder.equals(CommonConstant.SORT_ORDER_ASC),
                sortField);
        return queryWrapper;

    }

    /**
     * 获取代码生成器封装
     *
     * @param generator
     * @param request
     * @return
     */
    @Override
    public GeneratorVO getGeneratorVO(Generator generator, HttpServletRequest request) {
        // 对象转封装类
        GeneratorVO generatorVO = GeneratorVO.objToVo(generator);

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Long userId = generator.getUserId();
        User user = null;
        if (userId != null && userId > 0) {
            user = userService.getById(userId);
        }
        UserVO userVO = userService.getUserVO(user);
        generatorVO.setUser(userVO);
        // endregion

        return generatorVO;
    }

    /**
     * 分页获取代码生成器封装
     *
     * @param generatorPage
     * @param request
     * @return
     */
    @Override
    public Page<GeneratorVO> getGeneratorVOPage(Page<Generator> generatorPage, HttpServletRequest request) {
        List<Generator> generatorList = generatorPage.getRecords();
        Page<GeneratorVO> generatorVOPage = new Page<>(generatorPage.getCurrent(), generatorPage.getSize(), generatorPage.getTotal());
        if (CollUtil.isEmpty(generatorList)) {
            return generatorVOPage;
        }
        // 对象列表 => 封装对象列表
        List<GeneratorVO> generatorVOList = generatorList.stream().map(generator -> {
            return GeneratorVO.objToVo(generator);
        }).collect(Collectors.toList());

        // 可以根据需要为封装对象补充值，不需要的内容可以删除
        // region 可选
        // 1. 关联查询用户信息
        Set<Long> userIdSet = generatorList.stream().map(Generator::getUserId).collect(Collectors.toSet());
        Map<Long, List<User>> userIdUserListMap = userService.listByIds(userIdSet).stream()
                .collect(Collectors.groupingBy(User::getId));
        // 填充信息
        generatorVOList.forEach(generatorVO -> {
            Long userId = generatorVO.getUserId();
            User user = null;
            if (userIdUserListMap.containsKey(userId)) {
                user = userIdUserListMap.get(userId).get(0);
            }
            generatorVO.setUser(userService.getUserVO(user));
        });
        // endregion

        generatorVOPage.setRecords(generatorVOList);
        return generatorVOPage;
    }

}
