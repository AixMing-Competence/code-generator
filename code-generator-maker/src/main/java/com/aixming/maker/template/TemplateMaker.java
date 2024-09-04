package com.aixming.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aixming.maker.enums.FileGenerateTypeEnum;
import com.aixming.maker.enums.FileTypeEnum;
import com.aixming.maker.meta.Meta;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 *
 * @author AixMing
 * @since 2024-09-02 20:26:53
 */
public class TemplateMaker {

    private static long makeTemplate(String originProjectPath, String inputFilePath, Meta newMeta, Meta.ModelConfig.ModelInfo modelInfo, String searchStr, Long id) {
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 源项目路径
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";

        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            // 复制目录，创建工作空间
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 一、输入信息

        // 要挖坑的项目根目录
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath));
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        // 制作文件模板
        File inputFile = new File(sourceRootPath + File.separator + inputFilePath);
        ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        if (inputFile.isDirectory()) {
            for (File file : FileUtil.loopFiles(inputFile)) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        } else {
            Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(modelInfo, searchStr, sourceRootPath, inputFile);
            newFileInfoList.add(fileInfo);
        }

        // 三、生成配置文件（meta.json）
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        if (FileUtil.exist(metaOutputPath)) {
            // 1、追加配置参数
            Meta oldMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);

            List<Meta.FileConfig.FileInfo> fileInfoList = oldMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);

            List<Meta.ModelConfig.ModelInfo> modelInfoList = oldMeta.getModelConfig().getModels();
            modelInfoList.add(modelInfo);

            oldMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            oldMeta.getModelConfig().setModels(distinctModels(modelInfoList));

            // 2、输出元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(oldMeta), metaOutputPath);

        } else {
            // 1、构造配置参数对象

            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);

            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.add(modelInfo);

            // 2、输出元信息文件
            FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);
        }

        return id;
    }

    /**
     * 制作模板文件
     *
     * @param modelInfo
     * @param searchStr
     * @param sourceRootPath 尽量少用，可能是文件路径也可能是url等等，避免以后频繁修改
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(Meta.ModelConfig.ModelInfo modelInfo, String searchStr, String sourceRootPath, File inputFile) {
        // 要挖坑的文件（注意一定要是相对路径）
        String inputFileAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\","/");
        
        String inputFilePath = inputFileAbsolutePath.replace(sourceRootPath + "/", "");
        String outputFilePath = inputFilePath + ".ftl";

        // 二、替换字符串，生成模板文件
        String outputFileAbsolutePath = inputFile.getAbsolutePath() + ".ftl";
        String fileContent;

        // 如果已有模板文件，表示已经挖过坑，则在模板文件的基础上再挖坑
        if (FileUtil.exist(outputFileAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(outputFileAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(inputFileAbsolutePath);
        }

        // 替换源文件内容
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, searchStr, replacement);

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(inputFilePath);
        fileInfo.setOutputPath(outputFilePath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        
        // 如果文件内容没有被替换（挖坑），则静态生成
        if(fileContent.equals(newFileContent)){
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            fileInfo.setOutputPath(inputFilePath);
        }else{
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            // 输出模板文件
            FileUtil.writeUtf8String(newFileContent, outputFileAbsolutePath);
        }
        
        return fileInfo;
    }

    /**
     * 文件去重
     *
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(fileInfoList.stream()
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, item -> item, (e, r) -> r))
                .values());
        return newFileInfoList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(modelInfoList.stream()
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, item -> item, (e, r) -> r))
                .values());
        return newModelInfoList;
    }

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "springboot-init";
        String name = "springboot-init";
        String description = "springboot项目模板";
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);
        String inputFilePath = "src/main/java/com/aixming/springbootinit";

        // 第一次挖坑
        // Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        // modelInfo.setFieldName("outputText");
        // modelInfo.setType("String");
        // modelInfo.setDefaultValue("sum");

        // 第二次挖坑
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        // String searchStr = "sum";
        String searchStr = "BaseResponse";
        long id = makeTemplate(originProjectPath, inputFilePath, meta, modelInfo, searchStr, null);
        System.out.println(id);

    }
    
}
