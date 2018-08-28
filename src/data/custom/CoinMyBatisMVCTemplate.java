package data.custom;

import data.domain.JavaProjectInfo;

/**
 * @author boyce - 2018/3/31.
 */
public class CoinMyBatisMVCTemplate extends MyBatisMVCTemplateBase {
    public CoinMyBatisMVCTemplate(JavaProjectInfo javaProjectInfo) {
        super(javaProjectInfo, "resources/template/coin/");
    }
}
