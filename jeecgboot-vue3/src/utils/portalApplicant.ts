/** 前台首页。没有管理端角色的账号登录后只停在这里。 */
export const PORTAL_HOME = '/home';

export function isPortalPath(path?: string) {
  if (!path) return false;
  const pure = path.split('?')[0];
  return pure === PORTAL_HOME || pure.startsWith(`${PORTAL_HOME}/`);
}

export function isPortalApplicantHome(homePath?: string | null) {
  return homePath === PORTAL_HOME;
}
