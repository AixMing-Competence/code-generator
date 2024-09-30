import { COS_HOST } from '@/constants';
import AuthorInfo from '@/pages/Generator/Detail/components/AuthorInfo';
import FileConfig from '@/pages/Generator/Detail/components/FileConfig';
import ModelConfig from '@/pages/Generator/Detail/components/ModelConfig';
import {
  downloadGeneratorByIdUsingGet,
  getGeneratorVoByIdUsingGet,
} from '@/services/backend/generatorController';
import { useParams } from '@@/exports';
import { DownloadOutlined, EditOutlined } from '@ant-design/icons';
import { PageContainer, ProCard } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import { Button, Col, Image, message, Row, Space, Tabs, Tag, Typography } from 'antd';
import { saveAs } from 'file-saver';
import moment from 'moment';
import React, { useEffect, useState } from 'react';
import { Link } from 'umi';

/**
 * 生成器详情页面
 * @constructor
 */
const GeneratorDetailPage: React.FC = () => {
  const { id } = useParams();
  const [data, setData] = useState<API.GeneratorVO>({});
  const [loading, setLoading] = useState<boolean>(false);
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};
  const isMy = currentUser?.id === data.userId;

  /**
   * 标签列表
   * @param tags
   */
  const tagsListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }
    return (
      <div>
        {tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    );
  };

  /**
   * 加载数据
   */
  const loadData = async () => {
    if (!id) {
      return;
    }
    setLoading(true);
    try {
      const res = await getGeneratorVoByIdUsingGet({ id: id as any });
      if (res.data) {
        setData(res.data ?? {});
      }
    } catch (error: any) {
      message.error('加载数据失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    if (!id) {
      return;
    }
    loadData();
  }, [id]);

  /**
   * 下载按钮
   */
  const downloadButton = data.distPath && currentUser && (
    <Button
      icon={<DownloadOutlined />}
      onClick={async () => {
        const blob = await downloadGeneratorByIdUsingGet(
          { id: id as any },
          { responseType: 'blob' },
        );
        const fullPath = COS_HOST + data.distPath;
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
      }}
    >
      下载
    </Button>
  );

  /**
   * 编辑按钮
   */
  const editButton = isMy && (
    <Link to={`/generator/update?id=${id}`}>
      <Button icon={<EditOutlined />}>编辑</Button>
    </Link>
  );

  return (
    <PageContainer title={<></>} loading={loading}>
      <ProCard>
        <Row justify="space-between" gutter={[32, 32]}>
          <Col flex="auto">
            <Space size="large" align="center">
              <Typography.Title level={4}>{data.name}</Typography.Title>
              {tagsListView(data.tags)}
            </Space>
            <Typography.Paragraph>{data.description}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">
              创建时间：{moment(data.createTime).format('YYYY-MM-DD HH:mm:ss')}
            </Typography.Paragraph>
            <Typography.Paragraph type="secondary">基础包：{data.basePackage}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">版本：{data.version}</Typography.Paragraph>
            <Typography.Paragraph type="secondary">作者：{data.author}</Typography.Paragraph>
            <div style={{ marginBottom: '24px' }}></div>
            <Space size="middle">
              <Link to={`/generator/use/${data.id}`}>
                <Button type="primary">立即使用</Button>
              </Link>
              {downloadButton}
              {editButton}
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={data.picture} />
          </Col>
        </Row>
      </ProCard>
      <div style={{ marginBottom: '24px' }}></div>
      <ProCard>
        <Tabs
          size="large"
          defaultActiveKey="fileConfig"
          items={[
            {
              key: 'fileConfig',
              label: '文件配置',
              children: <FileConfig data={data} />,
            },
            {
              key: 'modelConfig',
              label: '模型配置',
              children: <ModelConfig data={data} />,
            },
            {
              key: 'userInfo',
              label: '作者信息',
              children: <AuthorInfo data={data} />,
            },
          ]}
          onChange={() => {}}
        />
      </ProCard>
    </PageContainer>
  );
};
export default GeneratorDetailPage;
