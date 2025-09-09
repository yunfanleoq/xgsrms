package org.jeecg.modules.recruitment.xgsResume.service;

import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeWorks;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeEdus;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeHome;
import org.jeecg.modules.recruitment.xgsResume.entity.XgsResumeFG;
import com.baomidou.mybatisplus.extension.service.IService;
import org.jeecg.modules.recruitment.xgsResume.vo.XgsResumeBasePage;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;

/**
 * @Description: 基本信息-副高
 * @Author: Liuyl
 * @Date:   2024-12-25
 * @Version: V1.0
 */
public interface IXgsResumeFGService extends IService<XgsResumeFG> {

	/**
	 * 添加一对多
	 *
	 * @param xgsResumeFG
	 * @param xgsResumeWorksList
	 * @param xgsResumeEdusList
	 * @param xgsResumeHomeList
	 */
	public void saveMain(XgsResumeFG xgsResumeFG,List<XgsResumeWorks> xgsResumeWorksList,List<XgsResumeEdus> xgsResumeEdusList,List<XgsResumeHome> xgsResumeHomeList) ;
	
	/**
	 * 修改一对多
	 *
	 * @param xgsResumeFG
	 * @param xgsResumeWorksList
	 * @param xgsResumeEdusList
	 * @param xgsResumeHomeList
	 */
	public void updateMain(XgsResumeFG xgsResumeFG,List<XgsResumeWorks> xgsResumeWorksList,List<XgsResumeEdus> xgsResumeEdusList,List<XgsResumeHome> xgsResumeHomeList);
	
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
