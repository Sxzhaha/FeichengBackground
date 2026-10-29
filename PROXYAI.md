# ProxyAI 项目指令

## 语言要求

除非我明确要求使用英文，否则所有回答、计划、总结、风险说明、修改说明、命令解释都必须使用简体中文。

## 操作要求

1. 默认只分析项目，不要修改代码。
2. 修改代码前必须先用中文列出修改计划，并等待我确认。
3. 执行命令前必须用中文说明命令目的、是否会修改文件、可能影响。
4. 不要自动删除、重命名或大范围重构文件。
5. 不要修改 application.yml、application-druid.yml、数据库连接、Redis、MinIO、密钥等敏感配置，除非我明确要求。
6. 如果只是检查项目，可以读取文件、分析依赖、检查路由、接口、实体类、Mapper、XML 是否一致。
7. 发现问题后先列出问题清单、涉及文件、风险等级和建议修改方式。
8. 对 Spring Boot 项目，保持 Controller、Service、ServiceImpl、Mapper、Mapper.xml、Entity/Domain 分层结构。
9. 对 Vue 项目，接口统一放在 src/api，页面组件放在 src/views，路由放在 src/router。
10. 所有最终结论必须用中文输出。