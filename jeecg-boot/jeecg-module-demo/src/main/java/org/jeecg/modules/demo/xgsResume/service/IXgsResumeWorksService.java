package org.jeecg.modules.demo.xgsResume.service;

import org.jeecg.modules.demo.xgsResume.entity.XgsResumeWorks;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 工作经历
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
public interface IXgsResumeWorksService extends IService<XgsResumeWorks> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<XgsResumeWorks>
	 */
	public List<XgsResumeWorks> selectByMainId(String mainId);
}
