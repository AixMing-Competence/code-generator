package com.aixming.web.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import cn.hutool.json.JSONUtil;
import com.aixming.maker.generator.GenerateTemplate;
import com.aixming.maker.generator.ZipGenerator;
import com.aixming.maker.meta.Meta;
import com.aixming.maker.meta.MetaVolidator;
import com.aixming.web.annotation.AuthCheck;
import com.aixming.web.common.BaseResponse;
import com.aixming.web.common.DeleteRequest;
import com.aixming.web.common.ErrorCode;
import com.aixming.web.common.ResultUtils;
import com.aixming.web.constant.UserConstant;
import com.aixming.web.exception.BusinessException;
import com.aixming.web.exception.ThrowUtils;
import com.aixming.web.manager.CosManager;
import com.aixming.web.model.dto.generator.*;
import com.aixming.web.model.entity.Generator;
import com.aixming.web.model.entity.User;
import com.aixming.web.model.vo.GeneratorVO;
import com.aixming.web.service.GeneratorService;
import com.aixming.web.service.UserService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.qcloud.cos.model.COSObject;
import com.qcloud.cos.model.COSObjectInputStream;
import com.qcloud.cos.utils.IOUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * 代码生成器接口
 *
 * @author AixMing
 */
@RestController
@RequestMapping("/generator")
@Slf4j
public class GeneratorController {

    @Resource
    private GeneratorService generatorService;

    @Resource
    private UserService userService;

    @Resource
    private CosManager cosManager;

    // region 增删改查

    /**
     * 创建代码生成器
     *
     * @param generatorAddRequest
     * @param request
     * @return
     */
    @PostMapping("/add")
    public BaseResponse<Long> addGenerator(@RequestBody GeneratorAddRequest generatorAddRequest, HttpServletRequest request) {
        ThrowUtils.throwIf(generatorAddRequest == null, ErrorCode.PARAMS_ERROR);
        // 在此处将实体类和 DTO 进行转换
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorAddRequest, generator);
        generator.setTags(JSONUtil.toJsonStr(generatorAddRequest.getTags()));
        generator.setFileConfig(JSONUtil.toJsonStr(generatorAddRequest.getFileConfig()));
        generator.setModelConfig(JSONUtil.toJsonStr(generatorAddRequest.getModelConfig()));
        // 数据校验
        generatorService.validGenerator(generator, true);
        // 填充默认值
        User loginUser = userService.getLoginUser(request);
        generator.setUserId(loginUser.getId());
        generator.setStatus(0);
        // 写入数据库
        boolean result = generatorService.save(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        // 返回新写入的数据 id
        long newGeneratorId = generator.getId();
        return ResultUtils.success(newGeneratorId);
    }

    /**
     * 删除代码生成器
     *
     * @param deleteRequest
     * @param request
     * @return
     */
    @PostMapping("/delete")
    public BaseResponse<Boolean> deleteGenerator(@RequestBody DeleteRequest deleteRequest, HttpServletRequest request) {
        if (deleteRequest == null || deleteRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        User user = userService.getLoginUser(request);
        long id = deleteRequest.getId();
        // 判断是否存在
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可删除
        if (!oldGenerator.getUserId().equals(user.getId()) && !userService.isAdmin(request)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = generatorService.removeById(id);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 更新代码生成器（仅管理员可用）
     *
     * @param generatorUpdateRequest
     * @return
     */
    @PostMapping("/update")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Boolean> updateGenerator(@RequestBody GeneratorUpdateRequest generatorUpdateRequest) {
        if (generatorUpdateRequest == null || generatorUpdateRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorUpdateRequest, generator);
        generator.setTags(JSONUtil.toJsonStr(generatorUpdateRequest.getTags()));
        generator.setFileConfig(JSONUtil.toJsonStr(generatorUpdateRequest.getFileConfig()));
        generator.setModelConfig(JSONUtil.toJsonStr(generatorUpdateRequest.getModelConfig()));
        // 数据校验
        generatorService.validGenerator(generator, false);
        // 判断是否存在
        long id = generatorUpdateRequest.getId();
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 操作数据库
        boolean result = generatorService.updateById(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    /**
     * 根据 id 获取代码生成器（封装类）
     *
     * @param id
     * @return
     */
    @GetMapping("/get/vo")
    public BaseResponse<GeneratorVO> getGeneratorVOById(long id, HttpServletRequest request) {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Generator generator = generatorService.getById(id);
        ThrowUtils.throwIf(generator == null, ErrorCode.NOT_FOUND_ERROR);
        // 获取封装类
        return ResultUtils.success(generatorService.getGeneratorVO(generator, request));
    }

    /**
     * 分页获取代码生成器列表（仅管理员可用）
     *
     * @param generatorQueryRequest
     * @return
     */
    @PostMapping("/list/page")
    @AuthCheck(mustRole = UserConstant.ADMIN_ROLE)
    public BaseResponse<Page<Generator>> listGeneratorByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 查询数据库
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        return ResultUtils.success(generatorPage);
    }

    /**
     * 分页获取代码生成器列表（封装类）
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                 HttpServletRequest request) {
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        // 获取封装类
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    /**
     * 分页获取当前登录用户创建的代码生成器列表
     *
     * @param generatorQueryRequest
     * @param request
     * @return
     */
    @PostMapping("/my/list/page/vo")
    public BaseResponse<Page<GeneratorVO>> listMyGeneratorVOByPage(@RequestBody GeneratorQueryRequest generatorQueryRequest,
                                                                   HttpServletRequest request) {
        ThrowUtils.throwIf(generatorQueryRequest == null, ErrorCode.PARAMS_ERROR);
        // 补充查询条件，只查询当前登录用户的数据
        User loginUser = userService.getLoginUser(request);
        generatorQueryRequest.setUserId(loginUser.getId());
        long current = generatorQueryRequest.getCurrent();
        long size = generatorQueryRequest.getPageSize();
        // 限制爬虫
        ThrowUtils.throwIf(size > 20, ErrorCode.PARAMS_ERROR);
        // 查询数据库
        Page<Generator> generatorPage = generatorService.page(new Page<>(current, size),
                generatorService.getQueryWrapper(generatorQueryRequest));
        // 获取封装类
        return ResultUtils.success(generatorService.getGeneratorVOPage(generatorPage, request));
    }

    /**
     * 编辑代码生成器（给用户使用）
     *
     * @param generatorEditRequest
     * @param request
     * @return
     */
    @PostMapping("/edit")
    public BaseResponse<Boolean> editGenerator(@RequestBody GeneratorEditRequest generatorEditRequest, HttpServletRequest request) {
        if (generatorEditRequest == null || generatorEditRequest.getId() <= 0) {
            throw new BusinessException(ErrorCode.PARAMS_ERROR);
        }
        // 在此处将实体类和 DTO 进行转换
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorEditRequest, generator);
        generator.setTags(JSONUtil.toJsonStr(generatorEditRequest.getTags()));
        generator.setFileConfig(JSONUtil.toJsonStr(generatorEditRequest.getFileConfig()));
        generator.setModelConfig(JSONUtil.toJsonStr(generatorEditRequest.getModelConfig()));
        // 数据校验
        generatorService.validGenerator(generator, false);
        User loginUser = userService.getLoginUser(request);
        // 判断是否存在
        long id = generatorEditRequest.getId();
        Generator oldGenerator = generatorService.getById(id);
        ThrowUtils.throwIf(oldGenerator == null, ErrorCode.NOT_FOUND_ERROR);
        // 仅本人或管理员可编辑
        if (!oldGenerator.getUserId().equals(loginUser.getId()) && !userService.isAdmin(loginUser)) {
            throw new BusinessException(ErrorCode.NO_AUTH_ERROR);
        }
        // 操作数据库
        boolean result = generatorService.updateById(generator);
        ThrowUtils.throwIf(!result, ErrorCode.OPERATION_ERROR);
        return ResultUtils.success(true);
    }

    // endregion

    @GetMapping("/download")
    public void downloadGeneratorById(long id, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        // 判断用户是否登录，未登录会直接抛出异常
        User loginUser = userService.getLoginUser(request);

        Generator generator = generatorService.getById(id);
        ThrowUtils.throwIf(generator == null, ErrorCode.NOT_FOUND_ERROR);

        String filePath = generator.getDistPath();
        ThrowUtils.throwIf(StrUtil.isBlank(filePath), ErrorCode.NOT_FOUND_ERROR, "文件不存在");

        // 追踪事件
        log.info("用户 {} 下载了 {}", loginUser, filePath);

        // 下载
        COSObjectInputStream cosObjectInputStream = null;
        try {
            COSObject cosobject = cosManager.getObject(filePath);
            cosObjectInputStream = cosobject.getObjectContent();

            byte[] bytes = IOUtils.toByteArray(cosObjectInputStream);
            response.setContentType("application/octet-stream;charset=UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + filePath);

            response.getOutputStream().write(bytes);
            response.getOutputStream().flush();
        } catch (IOException e) {
            log.error("file download error, filepath = " + filePath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载失败");
        } finally {
            if (cosObjectInputStream != null) {
                cosObjectInputStream.close();
            }
        }
    }

    /**
     * 使用代码生成器
     *
     * @param generatorUseRequest
     * @param request
     * @param response
     * @throws IOException
     */
    @PostMapping("/use")
    public void useGenerator(@RequestBody GeneratorUseRequest generatorUseRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ThrowUtils.throwIf(generatorUseRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取用户输入的参数
        Long id = generatorUseRequest.getId();
        Map<String, Object> dataModel = generatorUseRequest.getDataModel();

        ThrowUtils.throwIf(id <= 0, ErrorCode.PARAMS_ERROR);
        Generator generator = generatorService.getById(id);
        ThrowUtils.throwIf(generator == null, ErrorCode.NOT_FOUND_ERROR);

        // 生成器的存储路径
        String distPath = generator.getDistPath();
        ThrowUtils.throwIf(StrUtil.isBlank(distPath), ErrorCode.NOT_FOUND_ERROR, "产物包不存在");

        // 用户是否登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId = {} 使用了生成器 id = {}", loginUser.getId(), id);

        // 从对象存储下载压缩包
        String projectPath = System.getProperty("user.dir");
        // 定义独立的工作空间
        String tempDirPath = String.format("%s/.temp/use/%s", projectPath, id);
        String zipFilePath = tempDirPath + File.separator + "dist.zip";
        if (!FileUtil.exist(zipFilePath)) {
            FileUtil.touch(zipFilePath);
        }

        try {
            cosManager.download(distPath, zipFilePath);
        } catch (InterruptedException e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "生成器下载失败");
        }

        // 解压压缩包，得到脚本文件
        File unzipDistDir = ZipUtil.unzip(zipFilePath, StandardCharsets.UTF_8);

        // 将用户输入的参数写入 json 文件
        String dataModelFilePath = tempDirPath + File.separator + "dataModel.json";
        String jsonStr = JSONUtil.toJsonStr(dataModel);
        FileUtil.writeUtf8String(jsonStr, dataModelFilePath);

        // 执行脚本
        // 找到脚本文件
        // windows 下脚本文件后缀为 .bat
        File scriptFile = FileUtil.loopFiles(unzipDistDir, 2, null)
                .stream()
                .filter(file -> file.isFile() && "generator.bat".equals(file.getName()))
                .findFirst()
                .orElseThrow(RuntimeException::new);

        // 修改可执行权限
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(scriptFile.toPath(), permissions);
        } catch (Exception e) {
            // ignore
        }

        File scriptDir = scriptFile.getParentFile();

        String scriptFileAbsolutePath = scriptFile.getAbsolutePath();
        String[] commands = {scriptFileAbsolutePath, "json-generate", "--file=" + dataModelFilePath};
        ProcessBuilder processBuilder = new ProcessBuilder(commands);
        processBuilder.directory(scriptDir);

        try {
            Process process = processBuilder.start();
            // 读取命令的输出
            InputStream inputStream = process.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
            }
            // 等待命令执行完成
            int exitCode = process.waitFor();
            System.out.println("命令执行结束，退出吗：" + exitCode);
            if (exitCode != 0) {
                throw new RuntimeException();
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "执行生成器脚本错误");
        }

        // 压缩得到的生成结果，将结果返回给前端
        String generatedPath = scriptDir + File.separator + "generated";
        String resultPath = tempDirPath + File.separator + "result.zip";
        File resultFile = ZipUtil.zip(generatedPath, resultPath);

        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + resultFile.getName());
        Files.copy(resultFile.toPath(), response.getOutputStream());

        // 清理文件，异步处理提高性能
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });
    }

    /**
     * 制作代码生成器
     *
     * @param generatorMakeRequest
     * @param request
     * @param response
     */
    @PostMapping("/make")
    public void makeGenerator(@RequestBody GeneratorMakeRequest generatorMakeRequest, HttpServletRequest request, HttpServletResponse response) throws IOException {
        ThrowUtils.throwIf(generatorMakeRequest == null, ErrorCode.PARAMS_ERROR);
        // 获取输入参数
        Meta meta = generatorMakeRequest.getMeta();
        String zipFilePath = generatorMakeRequest.getZipFilePath();

        // 用户是否登录
        User loginUser = userService.getLoginUser(request);
        log.info("userId = {} 制作了生成器", loginUser.getId());

        // 创建独立的工作空间
        String projectPath = System.getProperty("user.dir");
        long id = IdUtil.getSnowflakeNextId();
        String tempDirPath = String.format("%s/.temp/make/%s", projectPath, id);
        String localZipFilePath = tempDirPath + File.separator + "project.zip";

        if (!FileUtil.exist(localZipFilePath)) {
            FileUtil.touch(localZipFilePath);
        }

        // 下载压缩包到本地
        try {
            cosManager.download(zipFilePath, localZipFilePath);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "下载文件失败");
        }

        // 解压得到项目模板文件
        File unZipDistDir = ZipUtil.unzip(localZipFilePath);

        // 构造 meta 对象和文件输出路径
        String sourceRootPath = unZipDistDir.getAbsolutePath();
        meta.getFileConfig().setSourceRootPath(sourceRootPath);
        // 校验和处理默认值
        MetaVolidator.doVolidateAndFill(meta);
        String outputRootPath = tempDirPath + "/generated/" + meta.getName();

        // 调用 maker 方法生成生成器
        GenerateTemplate generator = new ZipGenerator();

        // 处理 models
        meta.getModelConfig().getModels().stream().forEach(modelInfo -> {
            // 没有分组，并且 type 为 boolean
            if (StrUtil.isBlank(modelInfo.getGroupKey()) && modelInfo.getType().equals("boolean") && modelInfo.getDefaultValue() instanceof String) {
                // 将运行时类型转化为 boolean
                modelInfo.setDefaultValue(Boolean.parseBoolean((String) modelInfo.getDefaultValue()));
            }
        });
        
        try {
            generator.doGenerate(meta, outputRootPath);
        } catch (Exception e) {
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "制作生成器失败");
        }

        // 下载制作好的生成器压缩包
        String suffix = "-dist.zip";
        String zipFileName = meta.getName() + suffix;
        String distZipFilePath = outputRootPath + suffix;
        response.setContentType("application/octet-stream;charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=" + zipFileName);
        Files.copy(Paths.get(distZipFilePath), response.getOutputStream());

        // 清理工作空间文件
        CompletableFuture.runAsync(() -> {
            FileUtil.del(tempDirPath);
        });
    }
    
}
