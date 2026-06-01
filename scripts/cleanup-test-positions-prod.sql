-- PROJ-005：生产环境清理门户可见的测试岗位（执行前请先跑 SELECT 预览）
-- 用法（在服务器或本机连生产库）：
--   mysql -u... -p xgsrms < scripts/cleanup-test-positions-prod.sql
-- 逻辑：软删除 deleted='Y'，/list 与门户已过滤 deleted IS NULL

USE xgsrms;

-- 1) 预览将下架的记录
SELECT id, position_name, status, approval_user, telphone, email, dept
FROM xgs_positions
WHERE (deleted IS NULL OR deleted = '')
  AND status = '招聘中'
  AND (
    position_name LIKE '%测试%'
    OR position_name REGEXP '^[0-9]+$'
    OR approval_user LIKE '%测试账号%'
    OR id IN (
      '1877227680929280001',
      '1894941281134215169',
      '1900436504903589890',
      '1982685528983748610',
      '2036337312333438977',
      '2036404815889358849',
      '2041340151807528961',
      '2041342856684818433'
    )
  );

-- 2) 确认无误后执行下架（取消下面注释）
/*
UPDATE xgs_positions
SET deleted = 'Y',
    update_time = NOW()
WHERE (deleted IS NULL OR deleted = '')
  AND status = '招聘中'
  AND (
    position_name LIKE '%测试%'
    OR position_name REGEXP '^[0-9]+$'
    OR approval_user LIKE '%测试账号%'
    OR id IN (
      '1877227680929280001',
      '1894941281134215169',
      '1900436504903589890',
      '1982685528983748610',
      '2036337312333438977',
      '2036404815889358849',
      '2041340151807528961',
      '2041342856684818433'
    )
  );
*/
