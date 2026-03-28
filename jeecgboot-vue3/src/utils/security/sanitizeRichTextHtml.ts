import xss from 'xss';
import { xssRichTextOptions } from './xssRichTextOptions';

/**
 * 富文本字段在 v-html 展示前调用，防止存储型 XSS。
 */
export function sanitizeRichTextHtml(html: string | null | undefined): string {
  if (html == null || html === '') {
    return '';
  }
  return xss(String(html), xssRichTextOptions);
}
