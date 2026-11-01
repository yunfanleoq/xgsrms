export const PASSWORD_POLICY_PATTERN = /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[^A-Za-z0-9]).{12,}$/;

export const PASSWORD_POLICY_MESSAGE = '密码至少12位，且须同时包含大写字母、小写字母、数字和特殊字符';

export const PWD_EXPIRED_KEY = 'PWD_EXPIRED__';

export function passwordPolicyRules(required = true) {
  return [
    {
      required,
      message: '请输入密码',
    },
    {
      pattern: PASSWORD_POLICY_PATTERN,
      message: PASSWORD_POLICY_MESSAGE,
    },
  ];
}

export function validatePasswordPolicy(value?: string) {
  if (!value) {
    return PASSWORD_POLICY_MESSAGE;
  }
  if (!PASSWORD_POLICY_PATTERN.test(value)) {
    return PASSWORD_POLICY_MESSAGE;
  }
  return null;
}
