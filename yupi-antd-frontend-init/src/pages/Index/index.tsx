import { listGeneratorVoByPageUsingPost } from '@/services/backend/generatorController';
import { PageContainer, ProFormSelect, ProFormText, QueryFilter } from '@ant-design/pro-components';
import { history } from '@umijs/max';
import { Avatar, Card, Flex, Image, Input, List, message, Tabs, Tag, Typography } from 'antd';
import moment from 'moment';
import React, { useEffect, useState } from 'react';

/**
 * 初始化参数（不可改变）
 */
const INIT_PARAMS: PageRequest = {
  current: 1,
  pageSize: 4,
  sortField: 'createTime',
  sortOrder: 'descend',
};

const IndexPage: React.FC = () => {
  const [loading, setLoading] = useState<boolean>(false);

  const [total, setTotal] = useState<number>(0);

  const [dataList, setDataList] = useState<API.GeneratorVO[]>([]);

  // 搜索参数
  const [searchParams, setSearchParams] = useState<API.GeneratorQueryRequest>({
    ...INIT_PARAMS,
  });

  /**
   * 标签列表
   * @param tags
   */
  const tagListView = (tags?: string[]) => {
    if (!tags) {
      return <></>;
    }

    return (
      <div style={{ marginBottom: 16 }}>
        {tags.map((tag) => (
          <Tag key={tag}>{tag}</Tag>
        ))}
      </div>
    );
  };

  const doSearch = async () => {
    setLoading(true);
    try {
      const res = await listGeneratorVoByPageUsingPost({
        ...searchParams,
      });
      setDataList(res.data?.records ?? []);
      setTotal(Number(res.data?.total) ?? 0);
    } catch (error: any) {
      message.error('获取数据失败，' + error.message);
    }
    setLoading(false);
  };

  useEffect(() => {
    doSearch();
  }, [searchParams]);

  return (
    <PageContainer title={<></>}>
      <Flex justify="center">
        <Input.Search
          placeholder="请搜索生成器"
          allowClear
          enterButton="搜索"
          size="large"
          style={{
            width: '40vw',
            minWidth: '320',
          }}
          onChange={(e) => {
            searchParams.searchText = e.target.value;
          }}
          onSearch={(value: string) => {
            setSearchParams({
              ...INIT_PARAMS,
              searchText: value,
            });
          }}
        />
      </Flex>
      <div style={{ marginBottom: 16 }}></div>
      <Tabs
        size="large"
        defaultActiveKey="1"
        items={[
          {
            key: 'newest',
            label: '最新',
          },
          {
            key: 'recommend',
            label: '推荐',
          },
        ]}
        onChange={() => {}}
      />
      <QueryFilter
        span={12}
        labelWidth="auto"
        labelAlign="left"
        style={{ padding: '16px 0' }}
        onFinish={async (value: API.GeneratorQueryRequest) => {
          console.log(searchParams.searchText);
          setSearchParams({
            ...INIT_PARAMS,
            ...value,
            searchText: searchParams.searchText,
          });
        }}
        autoFocusFirstInput={false}
      >
        <ProFormSelect name="tags" label="标签" mode="tags" />
        <ProFormText name="name" label="名称" />
        <ProFormText name="description" label="描述" />
      </QueryFilter>
      <div style={{ marginBottom: 16 }}></div>
      <List<API.GeneratorVO>
        rowKey="id"
        loading={loading}
        grid={{
          gutter: 16,
          xs: 1,
          sm: 2,
          md: 3,
          lg: 3,
          xl: 4,
          xxl: 4,
        }}
        dataSource={dataList}
        pagination={{
          current: searchParams.current,
          pageSize: searchParams.pageSize,
          total,
          onChange: (current, pageSize) => {
            setSearchParams({
              ...searchParams,
              current,
              pageSize,
            });
          },
        }}
        renderItem={(data) => (
          <List.Item>
            <Card
              hoverable
              cover={<Image alt={data.name} src={data.picture} />}
              onClick={() => {
                history.push(`/generator/detail/${data.id}`);
              }}
            >
              <Card.Meta
                title={<a>{data.name}</a>}
                description={
                  <Typography.Paragraph
                    ellipsis={{
                      rows: 2,
                    }}
                    style={{
                      height: 44,
                    }}
                  >
                    {data.description}
                  </Typography.Paragraph>
                }
              />
              {tagListView(data.tags)}
              <Flex justify="space-between" align="center">
                <Typography.Paragraph
                  type="secondary"
                  style={{ fontSize: 12, margin: 0, padding: 0, display: 'flex' }}
                >
                  {moment(data.createTime).fromNow()}
                </Typography.Paragraph>
                <div>
                  <Avatar src={data.user?.userAvatar} />
                </div>
              </Flex>
            </Card>
          </List.Item>
        )}
      />
    </PageContainer>
  );
};

export default IndexPage;
