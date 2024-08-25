import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;

/**
 * @author Duzeming
 * @since 2024-08-21 17:28:01
 */
public class FreeMarkerTest {
    
    @Test
    public void test() throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        configuration.setDirectoryForTemplateLoading(new File("src/main/resources/templates"));
        configuration.setDefaultEncoding("utf-8");

        Template template = configuration.getTemplate("myweb.html.ftl");

        // 准备数据
        HashMap<String, Object> dataModel = new HashMap<>();

        FileWriter out = new FileWriter("myweb.html.ftl");
        template.process(dataModel,out);
        out.close();
    }
}
