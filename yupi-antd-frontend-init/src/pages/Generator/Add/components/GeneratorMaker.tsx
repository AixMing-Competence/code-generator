import FileUploader from '@/components/FileUploader';
import { GENERATOR_MAKE_TEMPLATE } from '@/constants';
import { makeGeneratorUsingPost } from '@/services/backend/generatorController';
import { ProFormInstance, ProFormItem } from '@ant-design/pro-components';
import { ProForm } from '@ant-design/pro-form';
import { Collapse, message } from 'antd';
import { saveAs } from 'file-saver';
import { useRef, useState } from 'react';

interface Props {
  meta: any;
}

export default (props: Props) => {
  const { meta } = props;
  const formRef = useRef<ProFormInstance>();
  const [making, setMaking] = useState<boolean>(false);

  /**
   * 提交表单
   * @param values
   */
  const doSubmit = async (values: API.GeneratorMakeRequest) => {
    setMaking(true);
    // 数据转换
    if (!meta.name) {
      message.error('请填写名称');
      return;
    }

    const zipFilePath = values.zipFilePath;
    if (!zipFilePath || zipFilePath.length < 1) {
      message.error('请上传模板文件路径');
      return;
    }

    // @ts-ignore
    values.zipFilePath = zipFilePath[0].response;

    try {
      const blob = await makeGeneratorUsingPost(
        {
          meta,
          zipFilePath: values.zipFilePath,
        },
        {
          responseType: 'blob',
        },
      );
      saveAs(blob, meta.name + '.zip');
    } catch (error: any) {
      message.error('制作失败，' + error.message);
    }
    setMaking(false);
  };

  /**
   * 表单视图
   */
  const formView = (
    <ProForm
      formRef={formRef}
      submitter={{
        searchConfig: {
          submitText: '制作',
        },
        resetButtonProps: {
          hidden: true,
        },
      }}
      loading={making}
      onFinish={doSubmit}
    >
      <ProFormItem label="模板文件" name="zipFilePath">
        <FileUploader
          biz={GENERATOR_MAKE_TEMPLATE}
          description="请上传压缩包，打包时注意不要添加最外层目录！"
        />
      </ProFormItem>
    </ProForm>
  );

  return (
    <>
      <Collapse
        items={[
          {
            key: 'meker',
            label: '生成器制作工具',
            children: formView,
          },
        ]}
      />
    </>
  );
};
