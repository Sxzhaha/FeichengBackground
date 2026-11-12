package com.ruoyi.binZhou.common;

/**
 * 肥城桃项目的业务默认值。
 *
 * <p>历史项目中不同作物的默认值散落在各模块，容易产生跨作物数据。
 * 新代码统一从此处读取；需要支持多作物时再升级为数据库或配置中心配置。</p>
 */
public final class FeichengBusinessConstants
{
    public static final String REGION_NAME = "肥城";
    public static final String CROP_NAME = "肥城桃";
    public static final String CROP_ALIAS = "桃树";

    private FeichengBusinessConstants()
    {
    }
}
