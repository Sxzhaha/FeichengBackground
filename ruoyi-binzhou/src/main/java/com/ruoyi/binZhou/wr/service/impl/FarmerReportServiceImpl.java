package com.ruoyi.binZhou.wr.service.impl;

import com.ruoyi.binZhou.wr.domain.FarmerReportDTO;
import com.ruoyi.binZhou.wr.service.IFarmerReportService;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class FarmerReportServiceImpl implements IFarmerReportService {

    // 实际开发中注入你已有的各个 Mapper
    // @Autowired private IotWarningMapper iotMapper;
    // @Autowired private PestWarningMapper pestMapper;
    // @Autowired private PhenologyMapper phenologyMapper;

    @Override
    public FarmerReportDTO generateReportData(Long userId, String role) {
        FarmerReportDTO report = new FarmerReportDTO();

        // 1. 根据角色切分数据颗粒度和文案风格
        if ("farmer".equals(role)) {
            report.setTitle("智能大棚 - 个人农事精细化周报");
            report.setTargetUser("王大能 (精细化种植农户)");
            report.setSummary("【当前处于：枣树幼果膨大期】本周您的地块触发环境预警 3 次。AI 建议：下午2点密切注意棚内高温，及时开启灌溉。");

            // 农户看具体明细 (真实开发时用：iotMapper.selectByUserId(userId))
            report.setIotAlerts(getMockFarmerIotData());
            report.setPestAlerts(getMockFarmerPestData());

            List<String> ai = new ArrayList<>();
            ai.add("【物候期提示】当前正值幼果期，对水分极敏感，请保持土壤湿度在65%以上。");
            ai.add("【病虫害预警】检测到绿盲蝽上升趋势，傍晚推荐喷洒吡虫啉。");
            report.setAiSuggestions(ai);

        } else if ("county".equals(role)) {
            report.setTitle("沾化区 - 枣树区域农情调度简报");
            report.setTargetUser("张局长 (县级管理员)");
            report.setSummary("【全县物候统筹】本周全县共统计物联网预警142次，虫情设备捕获标本同比上升12%。下辖[下洼镇]缺水风险最高，需防范局部干旱。");

            // 县级看区域汇总 (真实开发时用：iotMapper.selectCountByCounty(areaCode))
            report.setIotAlerts(getMockCountyIotData());
            report.setPestAlerts(getMockCountyPestData());

            List<String> ai = new ArrayList<>();
            ai.add("【防灾调度建议】下洼镇、大高镇多处地块气温逼近39℃触发严重红警，建议启动区域节水灌溉联动。");
            ai.add("【全县统防统治】下周全县将迎来绿盲蝽孵化高峰，建议发布全县飞防技术指导通知。");
            report.setAiSuggestions(ai);

        } else if ("city".equals(role)) {
            report.setTitle("滨州市 - 枣产业宏观物联网监测年/月报");
            report.setTargetUser("刘市长 (市级指挥官)");
            report.setSummary("【全市宏观大盘】本统计周期内，全市枣树主产区运行宏观平稳。全市高温预警、虫情趋势线均处于历年安全包络线内，预期总产量稳定。");

            // 市级看全市大盘对比数据
            report.setIotAlerts(getMockCityIotData());
            report.setPestAlerts(getMockCityPestData());

            List<String> ai = new ArrayList<>();
            ai.add("【产业决策参考】沾化区信息化覆盖率及设备在线率达98%，建议作为数字化种植示范区进行推广。");
            ai.add("【气象灾害预警】气象局预测中旬有局部冰雹，请督导各县区做好防雹网布设检查工作。");
            report.setAiSuggestions(ai);
        }

        return report;
    }

    @Override
    public void executeScheduledPush(String frequency) {
        System.out.println("====== [若依Quartz定时任务启动] 正在扫描推送频率为: " + frequency + " 的用户 ======");
        // 1. 查询 binzhou_report_push_config 表中所有符合该频率且状态正常的配置
        // List<PushConfig> configs = pushConfigMapper.selectByFrequency(frequency);
        // 2. 循环遍历每个用户，调用上面的 generateReportData(config.getUserId(), config.getUserRole())
        // 3. 根据 push_channel 的类型（短信/系统通知/微信），将报表内容发送出去
        System.out.println("====== 定时报表推送成功分发完毕 ======");
    }

    // --- 以下为适配多角色的 Mock 数据生成器，结构完全契合前端表格 ---
    private List<Map<String, Object>> getMockFarmerIotData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> d = new HashMap<>(); d.put("time", "07-02 14:00"); d.put("type", "棚内超温"); d.put("value", "38.5℃"); d.put("level", "严重"); list.add(d);
        return list;
    }
    private List<Map<String, Object>> getMockFarmerPestData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> d = new HashMap<>(); d.put("time", "07-01 08:00"); d.put("name", "绿盲蝽"); d.put("count", 12); d.put("trend", "上升"); list.add(d);
        return list;
    }
    private List<Map<String, Object>> getMockCountyIotData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> d = new HashMap<>(); d.put("time", "下洼镇基地"); d.put("type", "土壤重度缺水"); d.put("value", "涉及32个棚"); d.put("level", "严重"); list.add(d);
        Map<String, Object> d2 = new HashMap<>(); d2.put("time", "大高镇基地"); d2.put("type", "空气高温"); d2.put("value", "涉及15个棚"); d2.put("level", "中度"); list.add(d2);
        return list;
    }
    private List<Map<String, Object>> getMockCountyPestData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> d = new HashMap<>(); d.put("time", "全县汇总"); d.put("name", "红蜘蛛"); d.put("count", "测报灯平均28只/台"); d.put("trend", "上升"); list.add(d);
        return list;
    }
    private List<Map<String, Object>> getMockCityIotData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> d = new HashMap<>(); d.put("time", "沾化区总计"); d.put("type", "环境高温预警"); d.put("value", "高发频次: 45次"); d.put("level", "良好"); list.add(d);
        Map<String, Object> d2 = new HashMap<>(); d2.put("time", "无棣县总计"); d2.put("type", "环境高温预警"); d2.put("value", "高发频次: 12次"); d2.put("level", "安全"); list.add(d2);
        return list;
    }
    private List<Map<String, Object>> getMockCityPestData() {
        List<Map<String, Object>> list = new ArrayList<>();
        Map<String, Object> d = new HashMap<>(); d.put("time", "全市横向对比"); d.put("name", "绿盲蝽主要重灾区"); d.put("count", "沾化区(占比78%)"); d.put("trend", "严峻"); list.add(d);
        return list;
    }
}