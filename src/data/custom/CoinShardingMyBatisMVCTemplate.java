package data.custom;

import data.domain.JavaProjectInfo;

/**
 * @author boyce - 2018/4/4.
 */
public class CoinShardingMyBatisMVCTemplate extends MyBatisMVCTemplateBase {
    public CoinShardingMyBatisMVCTemplate(JavaProjectInfo javaProjectInfo) {
        super(javaProjectInfo, "resources/template/coin_sharding/");
    }
}
