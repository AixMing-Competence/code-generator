import { COS_HOST } from '@/constants';
import {
  getGeneratorVoByIdUsingGet,
  useGeneratorUsingPost,
} from '@/services/backend/generatorController';
import { useParams } from '@@/exports';
import { DownloadOutlined } from '@ant-design/icons';
import { PageContainer, ProCard } from '@ant-design/pro-components';
import { useModel } from '@umijs/max';
import {
  Button,
  Col,
  Collapse,
  Form,
  Image,
  Input,
  message,
  Row,
  Space,
  Tag,
  Typography,
} from 'antd';
import { saveAs } from 'file-saver';
import React, { useEffect, useState } from 'react';
import { Link } from 'umi';

/**
 * 生成器详情页面
 * @constructor
 */
const GeneratorDetailPage: React.FC = () => {
  const [form] = Form.useForm();
  const { id } = useParams();
  const [data, setData] = useState<API.GeneratorVO>({});
  const [loading, setLoading] = useState<boolean>(false);
  const [downloading, setDownloading] = useState<boolean>(false);
  const { initialState } = useModel('@@initialState');
  const { currentUser } = initialState ?? {};

  const models = data.modelConfig?.models;

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
      type="primary"
      icon={<DownloadOutlined />}
      loading={downloading}
      onClick={async () => {
        setDownloading(true);
        const values = form.getFieldsValue();
        // eslint-disable-next-line react-hooks/rules-of-hooks
        const blob = await useGeneratorUsingPost(
          { id: id as any, dataModel: values },
          { responseType: 'blob' },
        );
        const fullPath = COS_HOST + data.distPath;
        saveAs(blob, fullPath.substring(fullPath.lastIndexOf('/') + 1));
        setDownloading(false);
      }}
    >
      生成代码
    </Button>
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
            <div style={{ marginBottom: '24px' }}></div>
            <Form form={form}>
              {models?.map((model, index) => {
                if (model.groupKey) {
                  if (!model.models) {
                    return <></>;
                  }
                  return (
                    <>
                      <Collapse
                        key={index}
                        items={[
                          {
                            key: index,
                            label: model.groupName + '（分组）',
                            children: model.models.map((subModel, index) => {
                              return (
                                <Form.Item
                                  label={subModel.fieldName}
                                  key={index}
                                  // @ts-ignore
                                  name={[model.groupKey, subModel.fieldName]}
                                >
                                  <Input placeholder={subModel.description} />
                                </Form.Item>
                              );
                            }),
                          },
                        ]}
                        bordered={false}
                        defaultActiveKey={[index]}
                      />
                      <div style={{ marginBottom: 24 }}></div>
                    </>
                  );
                }
                return (
                  <Form.Item label={model.fieldName} key={index} name={model.fieldName}>
                    <Input placeholder={model.description} />
                  </Form.Item>
                );
              })}
            </Form>
            <Space size="middle">
              {downloadButton}
              <Link to={`/generator/detail/${id}`}>
                <Button>查看详情</Button>
              </Link>
            </Space>
          </Col>
          <Col flex="320px">
            <Image src={data.picture} />
          </Col>
        </Row>
      </ProCard>
    </PageContainer>
  );
};
export default GeneratorDetailPage;
