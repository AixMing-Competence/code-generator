package com.aixming.web.job;

import cn.hutool.core.util.StrUtil;
import com.aixming.web.manager.CosManager;
import com.aixming.web.mapper.GeneratorMapper;
import com.aixming.web.model.entity.Generator;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 清理对象存储空间
 * <p>
 * 开发步骤：
 * 1、任务开发：在Spring Bean实例中，开发Job方法；
 * 2、注解配置：为Job方法添加注解 "@XxlJob(value="自定义jobhandler名称", init = "JobHandler初始化方法", destroy = "JobHandler销毁方法")"，注解value值对应的是调度中心新建任务的JobHandler属性的值。
 * 3、执行日志：需要通过 "XxlJobHelper.log" 打印执行日志；
 * 4、任务结果：默认任务结果为 "成功" 状态，不需要主动设置；如有诉求，比如设置任务结果为失败，可以通过 "XxlJobHelper.handleFail/handleSuccess" 自主设置任务结果；
 *
 * @author xuxueli 2019-12-11 21:52:51
 */
@Component
@Slf4j
public class ClearCosJobHandler {

    @Resource
    private CosManager cosManager;

    @Resource
    private GeneratorMapper generatorMapper;

    /**
     * 每天执行清理无用文件
     *
     * @throws Exception
     */
    @XxlJob("clearCosJobHandler")
    public void clearCosJobHandler() throws Exception {
        log.info("clearCosJobHandler start");
        // 删除用户上传的模板文件（generator_make_template）
        cosManager.deleteDir("/generator_make_template/");

        // 删除已经被删除的代码生成器的产物包文件（generator_dist）
        List<Generator> generatorList = generatorMapper.listDeletedGenerator();
        Set<String> keySet = generatorList.stream().map(generator -> generator.getDistPath())
                .filter(StrUtil::isNotBlank)
                // 移除 '/' 前缀
                .map(distPath -> distPath.substring(1))
                .collect(Collectors.toSet());
        cosManager.deleteObjects(new ArrayList<>(keySet));
        log.info("clearCosJobHandler end");
    }

}
