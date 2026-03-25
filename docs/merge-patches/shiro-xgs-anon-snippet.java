// 已废弃：整段 /xgsHome/**、/xgsJournalism/** 等 anon 会导致匿名访问管理端全量列表。
// 现改为 Shiro 仅保留 /sys/dict/getDictItems/* 等必要 anon，招聘门户通过各接口 @IgnoreAuth + JwtFilter 白名单 + 控制器内数据范围与脱敏。
// 合并升级时请勿再恢复下列通配 anon：
// filterChainDefinitionMap.put("/xgsHome/**", "anon");
// filterChainDefinitionMap.put("/xgsJournalism/**", "anon");
// filterChainDefinitionMap.put("/xgsIntroduce/**", "anon");
// filterChainDefinitionMap.put("/xgsContactUs/**", "anon");
// filterChainDefinitionMap.put("/positions/xgsPositions/list", "anon");
// filterChainDefinitionMap.put("/positions/xgsPositions/getDeptList", "anon");
