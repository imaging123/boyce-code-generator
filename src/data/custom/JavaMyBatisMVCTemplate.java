package data.custom;

import data.domain.JavaProjectInfo;
import lombok.extern.slf4j.Slf4j;

/**
 * Java Web MVC Template generating
 *
 * @author boyce - 01/14/2018
 */
@Slf4j
public class JavaMyBatisMVCTemplate extends MyBatisMVCTemplateBase {

    public JavaMyBatisMVCTemplate(JavaProjectInfo javaProjectInfo) {
        super(javaProjectInfo, "resources/template/java/");
    }
}
