package org.jeecg.modules.recruitment.xgsResume.mapper;

import java.util.List;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeWorks;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 工作经历
 * @Author: Liuyl
 * @Date:   2024-12-25
 * @Version: V1.0
 */
public interface XgsResumeWorksMapper extends BaseMapper<XgsResumeWorks> {

	/**
	 * 通过主表id删除子表数据
	 *
	 * @param mainId 主表id
	 * @return boolean
	 */
	public boolean deleteByMainId(@Param("mainId") String mainId);

  /**
   * 通过主表id查询子表数据
   *
   * @param mainId 主表id
   * @return List<XgsResumeWorks>
   */
	public List<XgsResumeWorks> selectByMainId(@Param("mainId") String mainId);
}
