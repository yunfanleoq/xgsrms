package org.jeecg.modules.recruitment.xgsResume.service;

import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeHome;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 家庭状况
 * @Author: Liuyl
 * @Date:   2024-12-25
 * @Version: V1.0
 */
public interface IXgsResumeHomeService extends IService<XgsResumeHome> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<XgsResumeHome>
	 */
	public List<XgsResumeHome> selectByMainId(String mainId);
}
