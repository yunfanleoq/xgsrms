package org.jeecg.modules.demo.xgsResume.service;

import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import com.baomidou.mybatisplus.extension.service.IService;
import java.util.List;

/**
 * @Description: 教育经历
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
public interface IXgsResumeEdusService extends IService<XgsResumeEdus> {

	/**
	 * 通过主表id查询子表数据
	 *
	 * @param mainId 主表id
	 * @return List<XgsResumeEdus>
	 */
	public List<XgsResumeEdus> selectByMainId(String mainId);
}
