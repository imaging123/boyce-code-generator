package data.custom;

import data.domain.JavaProjectInfo;

/**
 * @author boyce - 2018/3/31.
 */
public class MyBatisMVCTemplate extends MyBatisMVCTemplateBase {
    public MyBatisMVCTemplate(JavaProjectInfo javaProjectInfo) {
        super(javaProjectInfo, "resources/template/coin/");
    }
}
