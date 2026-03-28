package org.jeecg.modules.recruitment.positions.util;

import org.jeecg.modules.recruitment.positions.entity.XgsPositions;
import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;

/**
 * 招聘岗位入库前 HTML 净化，防止富文本字段存储型 XSS。
 */
public final class XgsPositionInputSanitizer {

	private XgsPositionInputSanitizer() {}

	public static void sanitize(XgsPositions entity) {
		if (entity == null) {
			return;
		}
		entity.setResearchDirection(cleanPlain(entity.getResearchDirection()));
		entity.setXlxw(cleanPlain(entity.getXlxw()));
		entity.setProfessional(cleanPlain(entity.getProfessional()));
		entity.setWorkYears(cleanPlain(entity.getWorkYears()));
		entity.setDuty(cleanRichText(entity.getDuty()));
		entity.setMemo(cleanRichText(entity.getMemo()));
	}

	private static String cleanPlain(String value) {
		if (value == null) {
			return null;
		}
		return Jsoup.clean(value, Whitelist.none());
	}

	/** 与前端 xss 富文本白名单能力大致对齐，允许常见排版与表格、图片 */
	private static String cleanRichText(String value) {
		if (value == null) {
			return null;
		}
		return Jsoup.clean(value, Whitelist.relaxed());
	}
}
