package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import cn.hutool.json.JSONUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author AixMing
 */
@Data
@CommandLine.Command(name = "json-generate", description = "读取 json 文件生成代码", mixinStandardHelpOptions = true)
public class JsonGenerateCommand implements Callable<Integer> {
    
    /**
     * 是否开启接口文档功能
     */
    @CommandLine.Option(names = {"-f","--file"}, description = "json 文件路径", arity = "0..1", interactive = true, echo = true)
    private String filePath;
    
    @Override
    public Integer call() throws Exception {
        // 读取 json 文件，转化为数据模型
        String jsonStr = FileUtil.readUtf8String(filePath);

        DataModel dataModel = JSONUtil.toBean(jsonStr, DataModel.class);
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
