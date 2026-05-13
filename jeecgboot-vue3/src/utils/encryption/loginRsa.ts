import { defHttp } from '/@/utils/http/axios';

export interface LoginRsaBundle {
  rsaKeyId: string;
  publicKey: string;
}

/** 匿名拉取服务端临时 RSA 公钥 */
export async function fetchLoginRsaPublicKey(): Promise<LoginRsaBundle> {
  const res = await defHttp.get<LoginRsaBundle>({ url: '/sys/loginRsaPublicKey' });
  if (!res?.rsaKeyId || !res?.publicKey) {
    throw new Error('获取登录公钥失败');
  }
  return res;
}

/** 使用 JSEncrypt（PKCS1）加密密码，与服务端 RSA/ECB/PKCS1Padding 一致 */
export async function encryptPasswordWithLoginRsa(plainPassword: string): Promise<{ password: string; rsaKeyId: string }> {
  const { rsaKeyId, publicKey } = await fetchLoginRsaPublicKey();
  const mod = await import('jsencrypt');
  const JSEncrypt = mod.default;
  const enc = new JSEncrypt();
  enc.setPublicKey(publicKey);
  const cipher = enc.encrypt(plainPassword);
  if (!cipher) {
    throw new Error('RSA 加密密码失败');
  }
  return { password: cipher, rsaKeyId };
}
