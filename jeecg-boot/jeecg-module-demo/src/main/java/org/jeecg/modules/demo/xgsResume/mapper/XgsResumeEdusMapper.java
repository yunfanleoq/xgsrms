package org.jeecg.modules.demo.xgsResume.mapper;

import java.util.List;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;

/**
 * @Description: 教育经历
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
public interface XgsResumeEdusMapper extends BaseMapper<XgsResumeEdus> {

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
   * @return List<XgsResumeEdus>
   */
	public List<XgsResumeEdus> selectByMainId(@Param("mainId") String mainId);
}
