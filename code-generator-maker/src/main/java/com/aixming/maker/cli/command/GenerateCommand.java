package com.aixming.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import com.aixming.maker.generator.file.FileGenerator;
import com.aixming.maker.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author AixMing
 * @since 2024-08-23 18:43:13
 */
@Data
@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

    /**
     * 是否循环（开关）
     */
    @CommandLine.Option(names = {"-l", "--loop"}, description = "是否循环", arity = "0..1", interactive = true, echo = true)
    private boolean loop;

    /**
     * 作者（填充值）
     */
    @CommandLine.Option(names = {"-a", "--author"}, description = "作者", arity = "0..1", interactive = true, echo = true)
    private String author;

    /**
     * 输出信息
     */
    @CommandLine.Option(names = {"-o", "--outputText"}, description = "输出信息", arity = "0..1", interactive = true, echo = true)
    private String outputText;

    @Override
    public Integer call() throws Exception {
        DataModel mainTemplateConfig = new DataModel();
        BeanUtil.copyProperties(this, mainTemplateConfig);
        FileGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
