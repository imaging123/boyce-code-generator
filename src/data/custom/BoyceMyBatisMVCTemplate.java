package data.custom;

import data.domain.JavaProjectInfo;

public class BoyceMyBatisMVCTemplate extends MyBatisMVCTemplateBase {
    public BoyceMyBatisMVCTemplate(JavaProjectInfo javaProjectInfo) {
        super(javaProjectInfo, "resources/template/boyce/");
    }
}
