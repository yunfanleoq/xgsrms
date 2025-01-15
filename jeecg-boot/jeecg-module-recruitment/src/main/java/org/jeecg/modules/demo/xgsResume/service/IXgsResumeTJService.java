package org.jeecg.modules.demo.xgsResume.service;

import org.jeecg.modules.demo.xgsResume.entity.XgsResumeWorks;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.demo.xgsResume.entity.XgsResumeTJ;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.demo.xgsResume.vo.XgsResumeBasePage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 基本信息-推荐
 * @Author: jeecg-boot
 * @Date:   2024-12-25
 * @Version: V1.0
 */
public interface IXgsResumeTJService extends IService<XgsResumeTJ> {

	/**
	 * 添加一对多
	 *
	 * @param xgsResumeTJ
	 * @param xgsResumeWorksList
	 * @param xgsResumeEdusList
	 * @param xgsResumeHomeList
	 */
	public void saveMain(XgsResumeTJ xgsResumeTJ,List<XgsResumeWorks> xgsResumeWorksList,List<XgsResumeEdus> xgsResumeEdusList,List<XgsResumeHome> xgsResumeHomeList) ;
	
	/**
	 * 修改一对多
	 *
	 * @param xgsResumeTJ
	 * @param xgsResumeWorksList
	 * @param xgsResumeEdusList
	 * @param xgsResumeHomeList
	 */
	public void updateMain(XgsResumeTJ xgsResumeTJ,List<XgsResumeWorks> xgsResumeWorksList,List<XgsResumeEdus> xgsResumeEdusList,List<XgsResumeHome> xgsResumeHomeList);
	
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

	/**
	 * 在线申请岗位保存
	 * @param xgsResumeBase
	 * @param xgsResumeWorksList
	 * @param xgsResumeEdusList
	 * @param xgsResumeHomeList
	 */
	void saveMainWithJob(XgsResumeBasePage xgsResumeBase, List<XgsResumeWorks> xgsResumeWorksList, List<XgsResumeEdus> xgsResumeEdusList, List<XgsResumeHome> xgsResumeHomeList);

}
