package org.jeecg.modules.recruitment.security;

import org.jeecg.modules.recruitment.positions.entity.XgsPositions;
import org.jeecg.modules.recruitment.xgsContactUs.entity.XgsContactUs;
import org.jeecg.modules.recruitment.xgsHome.entity.XgsHome;
import org.jeecg.modules.recruitment.xgsIntroduce.entity.XgsIntroduce;
import org.jeecg.modules.recruitment.xgsJournalism.entity.XgsJournalism;

import java.util.List;

/**
 * 门户匿名/访客场景下脱敏：不返回后台审计字段。
 */
public final class RecruitmentPublicDataSanitizer {

	private RecruitmentPublicDataSanitizer() {
	}

	public static void stripXgsHome(XgsHome e) {
		if (e == null) {
			return;
		}
		e.setCreateBy(null);
		e.setUpdateBy(null);
		e.setSysOrgCode(null);
		e.setCreateTime(null);
		e.setUpdateTime(null);
	}

	public static void stripXgsHomeList(List<XgsHome> list) {
		if (list == null) {
			return;
		}
		for (XgsHome e : list) {
			stripXgsHome(e);
		}
	}

	public static void stripXgsJournalism(XgsJournalism e) {
		if (e == null) {
			return;
		}
		e.setCreateBy(null);
		e.setUpdateBy(null);
		e.setSysOrgCode(null);
		e.setCreateTime(null);
		e.setUpdateTime(null);
	}

	public static void stripXgsJournalismList(List<XgsJournalism> list) {
		if (list == null) {
			return;
		}
		for (XgsJournalism e : list) {
			stripXgsJournalism(e);
		}
	}

	public static void stripXgsIntroduce(XgsIntroduce e) {
		if (e == null) {
			return;
		}
		e.setCreateBy(null);
		e.setUpdateBy(null);
		e.setSysOrgCode(null);
		e.setCreateTime(null);
		e.setUpdateTime(null);
	}

	public static void stripXgsIntroduceList(List<XgsIntroduce> list) {
		if (list == null) {
			return;
		}
		for (XgsIntroduce e : list) {
			stripXgsIntroduce(e);
		}
	}

	public static void stripXgsContactUs(XgsContactUs e) {
		if (e == null) {
			return;
		}
		e.setCreateBy(null);
		e.setUpdateBy(null);
		e.setSysOrgCode(null);
		e.setCreateTime(null);
		e.setUpdateTime(null);
	}

	public static void stripXgsContactUsList(List<XgsContactUs> list) {
		if (list == null) {
			return;
		}
		for (XgsContactUs e : list) {
			stripXgsContactUs(e);
		}
	}

	public static void stripXgsPositions(XgsPositions e) {
		if (e == null) {
			return;
		}
		e.setCreateBy(null);
		e.setUpdateBy(null);
		e.setSysOrgCode(null);
		e.setCreateTime(null);
		e.setUpdateTime(null);
	}

	public static void stripXgsPositionsList(List<XgsPositions> list) {
		if (list == null) {
			return;
		}
		for (XgsPositions e : list) {
			stripXgsPositions(e);
		}
	}
}
