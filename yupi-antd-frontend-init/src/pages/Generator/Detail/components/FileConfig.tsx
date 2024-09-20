import { InfoCircleOutlined } from '@ant-design/icons';
import { Descriptions, DescriptionsProps, Divider } from 'antd';
import React from 'react';

interface Props {
  data: API.GeneratorVO;
}

/**
 * 文件配置信息
 * @param props
 * @constructor
 */
const FileConfig: React.FC<Props> = (props) => {
  const { data } = props;

  const fileConfig = data.fileConfig;

  if (!fileConfig) {
    return <></>;
  }

  const fileListView = (files?: API.FileInfo[]) => {
    if (!files) {
      return <></>;
    }

    return (
      <>
        {files.map((file, index) => {
          if (file.groupKey) {
            const groupFileItems: DescriptionsProps['items'] = [
              {
                key: 'groupKey',
                label: '分组Key',
                children: <p>{file.groupKey}</p>,
              },
              {
                key: 'groupName',
                label: '分组名',
                children: <p>{file.groupName}</p>,
              },
              {
                key: 'condition',
                label: '条件',
                children: <p>{file.condition}</p>,
              },
              {
                key: 'files',
                label: '组内文件',
                children: <p>{fileListView(file.files)}</p>,
              },
            ];

            return (
              <Descriptions key={index} column={1} title={file.groupName} items={groupFileItems} />
            );
          }

          const fileItems: DescriptionsProps['items'] = [
            {
              key: 'inputPath',
              label: '输入路径',
              children: <p>{file.inputPath}</p>,
            },
            {
              key: 'outputPath',
              label: '输出路径',
              children: <p>{file.outputPath}</p>,
            },
            {
              key: 'type',
              label: '文件类别',
              children: <p>{file.type}</p>,
            },
            {
              key: 'generateType',
              label: '文件生成类别',
              children: <p>{file.generateType}</p>,
            },
          ];

          return (
            <>
              <Descriptions key={index} column={2} items={fileItems} />
              <Divider />
            </>
          );
        })}
      </>
    );
  };

  /**
   * 文件配置基本信息
   */
  const baseFileItems: DescriptionsProps['items'] = [
    {
      key: 'inputRootPath',
      label: '输入根路径',
      children: <p>{fileConfig.inputRootPath}</p>,
    },
    {
      key: 'outputRootPath',
      label: '输出根路径',
      children: <p>{fileConfig.outputRootPath}</p>,
    },
    {
      key: 'outputRootPath',
      label: '项目根路径',
      children: <p>{fileConfig.sourceRootPath}</p>,
    },
    {
      key: 'type',
      label: '文件类别',
      children: <p>{fileConfig.type}</p>,
    },
  ];

  return (
    <div>
      <Descriptions
        title={
          <>
            <InfoCircleOutlined /> 基本信息
          </>
        }
        column={2}
        items={baseFileItems}
      />
      <div style={{ marginBottom: 16 }}></div>
      <Descriptions
        title={
          <>
            <InfoCircleOutlined /> 文件列表
          </>
        }
        column={2}
      />
      {fileListView(fileConfig.files)}
    </div>
  );
};

export default FileConfig;
