UPDATE xgs_positions
SET deleted = 'Y', update_time = NOW()
WHERE (deleted IS NULL OR deleted = '')
  AND status = '招聘中'
  AND (
    position_name LIKE '%测试%'
    OR position_name REGEXP '^[0-9]+$'
    OR approval_user LIKE '%测试账号%'
    OR id IN (
      '1877227680929280001','1894941281134215169','1900436504903589890',
      '1982685528983748610','1982685662144512001','1982694851717775362',
      '2036337312333438977','2036404815889358849',
      '2041340151807528961','2041342856684818433'
    )
  );
