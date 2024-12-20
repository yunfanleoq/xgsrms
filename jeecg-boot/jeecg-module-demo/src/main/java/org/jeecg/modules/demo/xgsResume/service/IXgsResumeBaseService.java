package org.jeecg.modules.demo.xgsResume.service;

import org.jeecg.modules.demo.xgsResume.entity.XgsResumeWorks;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeBase;
import com.baomidou.mybatisplus.extension.service.IService;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 基本信息
 * @Author: jeecg-boot
 * @Date:   2024-12-20
 * @Version: V1.0
 */
public interface IXgsResumeBaseService extends IService<XgsResumeBase> {

	/**
	 * 添加一对多
	 *
	 * @param xgsResumeBase
	 * @param xgsResumeWorksList
	 * @param xgsResumeEdusList
	 * @param xgsResumeHomeList
	 */
	public void saveMain(XgsResumeBase xgsResumeBase,List<XgsResumeWorks> xgsResumeWorksList,List<XgsResumeEdus> xgsResumeEdusList,List<XgsResumeHome> xgsResumeHomeList) ;
	
	/**
	 * 修改一对多
	 *
	 * @param xgsResumeBase
	 * @param xgsResumeWorksList
	 * @param xgsResumeEdusList
	 * @param xgsResumeHomeList
	 */
	public void updateMain(XgsResumeBase xgsResumeBase,List<XgsResumeWorks> xgsResumeWorksList,List<XgsResumeEdus> xgsResumeEdusList,List<XgsResumeHome> xgsResumeHomeList);
	
	/**
	 * 删除一对多
	 *
	 * @param id
	 */
	public void delMain (String id);
	
	/**
	 * 批量删除一对多
	 *
	 * @param idList
	 */
	public void delBatchMain (Collection<? extends Serializable> idList);
	
}
