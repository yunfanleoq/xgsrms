<template>
  <div class="category-content">
    <div class="content-wrapper">
      <div class="intro-section">
        <p>
          中国科学院信息工程研究所为员工提供清晰的职业发展路径，建立了科学合理的岗位分类体系。根据岗位性质和职责特点，设置了三大执行系列：<strong>自然科学系列</strong>、<strong>工程技术系列</strong>和<strong>职员系列</strong>，每个系列下设多种岗位类型和类别，为不同专业背景和职业目标的员工提供清晰的发展通道。
        </p>
      </div>

      <!-- 岗位体系总览图 -->
      <div class="system-overview">
        <h3>岗位发展体系总览</h3>
        <div class="overview-diagram">
          <div class="series-column natural-science">
            <div class="series-header">自然科学系列</div>
            <div class="category-group">
              <div class="position-types">
                <div class="position-type">基础科学研究</div>
                <div class="position-type">技术科学研究</div>
                <div class="position-type">教学与研究</div>
              </div>
            </div>
          </div>

          <div class="series-column engineering">
            <div class="series-header">工程技术系列</div>
            <div class="category-group">
              <div class="position-types">
                <div class="position-type">工程技术研究</div>
                <div class="position-type">科技支撑保障</div>
              </div>
            </div>
          </div>

          <div class="series-column staff">
            <div class="series-header">职员系列</div>
            <div class="category-group">
              <div class="position-types">
                <div class="position-type">管理岗位</div>
                <div class="position-type">科研助理</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 自然科学系列 -->
      <div class="path-section natural-science-bg">
        <h3>一、自然科学系列（科技类岗位）</h3>
        <p class="series-description">
          自然科学系列主要面向从事科学研究和技术创新的专业人员，涵盖基础科学研究、技术科学研究、教学与研究、工程技术研究等多个领域。该系列强调学术创新能力和科研成果产出，为科研人员提供从初级到高级的完整职业发展通道。
        </p>
        
        <!-- 四种岗位类型 -->
        <div class="position-category">
          <h4>1. 基础科学研究</h4>
          <div class="path-diagram">
            <div class="path-node level-assistant">助理研究员</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-associate">副研究员</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-senior">研究员</div>
          </div>
          <p class="path-description">
            主要从事通过网络空间安全相关领域中的安全现象揭示客观规律为主要目的的探索性研究工作。围绕世界科技前沿，探索开拓新的知识领域，促进学科发展。对网络空间安全的基本科学数据、资料和信息系统进行考察、采集、鉴定，开展理论方法创新工作的岗位。具备原始创新和基础研究能力。
          </p>
        </div>

        <div class="position-category">
          <h4>2. 技术科学研究</h4>
          <div class="path-diagram">
            <div class="path-node level-assistant">助理研究员</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-associate">副研究员</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-senior">研究员</div>
          </div>
          <p class="path-description">
            主要从事网络空间安全相关领域应用技术研究工作。围绕国家战略需求、国民经济主战场，以应用转化为导向，系统地研究和发展相关领域的关键技术，发明新技术、新方法，推动技术创新的岗位。具备以重大应用目标导向，凝炼科学问题和技术问题的能力。
          </p>
        </div>

        <div class="position-category">
          <h4>3. 教学与研究</h4>
          <div class="path-diagram">
            <div class="path-node level-assistant">助理研究员</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-associate">副研究员</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-senior">研究员</div>
          </div>
          <p class="path-description">
            主要从事网络空间安全相关领域的研究生、本科生教育教学和科学研究工作。落实科教融合战略目标，围绕学科发展和培养体系建设，开展课程体系、教育教学方法以及基础科学研究。具备网络空间安全学科课程体系优化和原始创新、基础研究的能力。
          </p>
        </div>
      </div>

      <!-- 工程技术系列 -->
      <div class="path-section engineering-bg">
        <h3>二、工程技术系列</h3>
        <p class="series-description">
          工程技术系列主要面向提供科研技术支撑和保障服务的专业技术人员，包括科技支撑保障和科研助理等岗位。该系列强调专业技能和服务质量，为技术支撑人员提供专业化的职业发展路径。
        </p>

        
        <div class="position-category">
          <h4>1. 工程技术研究</h4>
          <div class="path-diagram">
            <div class="path-node level-junior">助理工程师</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-assistant">工程师</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-associate">副高级专业技术岗位</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-senior">正高级专业技术岗位</div>
          </div>
          <p class="path-description">
            主要从事利用应用研究的成果和现有的知识与技术，策划、承担、组织、参与本领域重大工程项目的工作。围绕国家战略需求、产业需求，组织行业发展规划，开展技术攻关，突破关键核心技术的岗位。具备技术创新和集成创新能力。
          </p>
        </div>

        <div class="position-category">
          <h4>2. 科技支撑保障</h4>
          <div class="path-diagram">
            <div class="path-node level-junior">助理工程师</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-assistant">工程师</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-associate">副高级专业技术岗位</div>
            <div class="path-arrow">→</div>
            <div class="path-node level-senior">正高级专业技术岗位</div>
          </div>
          <p class="path-description">
            支撑类岗位，即科技支撑保障岗位，主要是指从事围绕国家和行业重大需求，开展工程技术项目集成建设、重要系统业务支撑和运维支撑工作。具备业务体系规划、解决技术难题能力。以及从事战略研究、科学传播、图书资料、学报编辑等工作的岗位。
          </p>
        </div>

        <div class="position-category">
          <h4>3. 科研助理</h4>
          <p class="series-description">
            协助科研人员开展研究工作，包括数据收集、实验操作、文献整理等，通过工作经验积累和专业能力提升实现职业发展。
          </p>
          <p class="path-description" style="text-align: center;">
            <img src="../../../assets/images/gangwei-3.png">
          </p>
        </div>
      </div>

      <!-- 职员系列 -->
      <div class="path-section staff-bg">
        <h3>三、职员系列（管理类岗位）</h3>
        <p class="series-description">
          职员系列主要面向从事行政管理、人力资源、财务、综合事务等管理工作的人员。该系列强调管理能力和综合素质，为管理人员提供从基层到高层的完整职业发展通道。
        </p>

        <!-- 岗位职级 -->
        <div class="position-category" style="text-align: center;">
          <img src="../../../assets/images/gangwei-5.png">
        </div>
      </div>

      <!-- 发展支持措施 -->
      <div class="support-section">
        <h3>职业发展支持措施</h3>
        <div class="support-cards">
          <div class="support-card">
            <div class="support-icon">📋</div>
            <div class="support-title">明确晋升标准</div>
            <div class="support-desc">制定清晰的岗位晋升标准和考核体系，让员工明确发展目标和要求</div>
          </div>
          <div class="support-card">
            <div class="support-icon">🎓</div>
            <div class="support-title">专业培训支持</div>
            <div class="support-desc">提供各类专业培训、学术交流和继续教育机会，助力员工能力提升</div>
          </div>
          <div class="support-card">
            <div class="support-icon">👥</div>
            <div class="support-title">导师指导制度</div>
            <div class="support-desc">为青年员工配备资深导师，提供职业规划指导和专业技能传授</div>
          </div>
          <div class="support-card">
            <div class="support-icon">🏆</div>
            <div class="support-title">绩效激励机制</div>
            <div class="support-desc">建立科学的绩效评价和激励机制，优秀表现获得晋升和奖励机会</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="gangweifazhanlu">
  // 这里可以添加页面特定的逻辑
</script>

<style scoped>
  .category-content {
    padding: 20px;
  }

  h2 {
    color: #3d54a7;
    margin-bottom: 25px;
    font-size: 24px;
    font-weight: bold;
  }

  h3 {
    color: #3d54a7;
    margin: 30px 0 15px;
    font-size: 20px;
    font-weight: bold;
    padding-left: 10px;
    border-left: 4px solid #3d54a7;
  }

  h4 {
    color: #2c3e80;
    margin: 20px 0 12px;
    font-size: 17px;
    font-weight: bold;
  }

  .content-wrapper {
    line-height: 1.8;
    font-size: 16px;
  }

  .intro-section {
    margin-bottom: 35px;
    padding: 25px;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    border-radius: 12px;
    border-left: 5px solid #3d54a7;
  }

  .intro-section p {
    margin: 0;
    color: #333;
  }

  /* 岗位体系总览 */
  .system-overview {
    margin: 40px 0;
    padding: 30px;
    background: white;
    border-radius: 15px;
    box-shadow: 0 4px 20px rgba(0, 0, 0, 0.1);
  }

  .system-overview h3 {
    text-align: center;
    margin-bottom: 30px;
    font-size: 22px;
    border: none;
    padding: 0;
  }

  .overview-diagram {
    display: flex;
    justify-content: space-around;
    gap: 20px;
    flex-wrap: wrap;
  }

  .series-column {
    flex: 1;
    min-width: 280px;
    border-radius: 12px;
    padding: 20px;
    box-shadow: 0 3px 15px rgba(0, 0, 0, 0.1);
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }

  .series-column:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.15);
  }

  .series-column.natural-science {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  }

  .series-column.engineering {
    background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
  }

  .series-column.staff {
    background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
  }

  .series-header {
    color: white;
    font-size: 20px;
    font-weight: bold;
    text-align: center;
    margin-bottom: 20px;
    padding-bottom: 15px;
    border-bottom: 2px solid rgba(255, 255, 255, 0.3);
  }

  .category-group {
    background: rgba(255, 255, 255, 0.95);
    border-radius: 8px;
    padding: 15px;
  }

  .category-title {
    font-size: 16px;
    font-weight: bold;
    color: #333;
    margin-bottom: 15px;
    text-align: center;
    padding: 8px;
    background: rgba(61, 84, 167, 0.1);
    border-radius: 6px;
  }

  .position-types {
    display: flex;
    flex-direction: column;
    gap: 10px;
  }

  .position-type {
    background: white;
    padding: 10px 15px;
    border-radius: 6px;
    text-align: center;
    font-size: 14px;
    color: #555;
    border: 2px solid rgba(61, 84, 167, 0.2);
    transition: all 0.3s ease;
  }

  .position-type:hover {
    border-color: #3d54a7;
    color: #3d54a7;
    transform: scale(1.05);
  }

  /* 系列样式 */
  .path-section {
    margin-bottom: 40px;
    padding: 30px;
    border-radius: 12px;
    box-shadow: 0 2px 15px rgba(0, 0, 0, 0.08);
  }

  .natural-science-bg {
    background: linear-gradient(135deg, #f5f7ff 0%, #e8ecff 100%);
    border: 2px solid #667eea;
  }

  .engineering-bg {
    background: linear-gradient(135deg, #fff5f8 0%, #ffe8f0 100%);
    border: 2px solid #f5576c;
  }

  .staff-bg {
    background: linear-gradient(135deg, #f0faff 0%, #e0f5ff 100%);
    border: 2px solid #4facfe;
  }

  .series-description {
    margin-bottom: 25px;
    padding: 15px;
    background: rgba(255, 255, 255, 0.8);
    border-radius: 8px;
    color: #555;
    font-size: 15px;
    line-height: 1.8;
  }

  .position-category {
    margin-bottom: 30px;
    padding: 20px;
    background: white;
    border-radius: 10px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.05);
  }

  .path-diagram {
    display: flex;
    align-items: center;
    justify-content: center;
    flex-wrap: wrap;
    margin: 20px 0;
    padding: 20px;
    background: #f8f9fa;
    border-radius: 10px;
  }

  .path-node {
    padding: 12px 20px;
    color: white;
    border-radius: 8px;
    margin: 10px 8px;
    font-weight: 600;
    font-size: 15px;
    box-shadow: 0 3px 10px rgba(0, 0, 0, 0.15);
    transition: all 0.3s ease;
    min-width: 120px;
    text-align: center;
  }

  .path-node:hover {
    transform: translateY(-3px) scale(1.05);
    box-shadow: 0 5px 15px rgba(0, 0, 0, 0.25);
  }

  /* 初级岗位 - 浅蓝色系 */
  .path-node.level-junior {
    background: linear-gradient(135deg, #6BB6FF 0%, #4A9EFF 100%);
  }

  /* 中级岗位 - 中蓝色系 */
  .path-node.level-assistant {
    background: linear-gradient(135deg, #4A90E2 0%, #357ABD 100%);
  }

  /* 副高级岗位 - 深蓝色系 */
  .path-node.level-associate {
    background: linear-gradient(135deg, #2E5C8A 0%, #1E4A6B 100%);
  }

  /* 正高级岗位 - 更深蓝色系 */
  .path-node.level-senior {
    background: linear-gradient(135deg, #1E3A5F 0%, #0F2A47 100%);
  }

  /* 专家级岗位 - 最深蓝色系 */
  .path-node.level-expert {
    background: linear-gradient(135deg, #0F1E3A 0%, #050A14 100%);
  }

  .path-arrow {
    color: #3d54a7;
    font-size: 28px;
    font-weight: bold;
    margin: 0 10px;
  }

  .path-description {
    margin-top: 15px;
    color: #666;
    font-size: 14px;
    line-height: 1.7;
    text-align: justify;
  }

  /* 支持措施 */
  .support-section {
    margin-top: 50px;
    padding: 35px;
    background: linear-gradient(135deg, #ffecd2 0%, #fcb69f 100%);
    border-radius: 15px;
  }

  .support-section h3 {
    text-align: center;
    margin-bottom: 30px;
    border: none;
    padding: 0;
    color: #2c3e80;
  }

  .support-cards {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 25px;
  }

  .support-card {
    background: white;
    padding: 25px;
    border-radius: 12px;
    box-shadow: 0 3px 15px rgba(0, 0, 0, 0.1);
    text-align: center;
    transition: all 0.3s ease;
  }

  .support-card:hover {
    transform: translateY(-10px);
    box-shadow: 0 8px 25px rgba(0, 0, 0, 0.2);
  }

  .support-icon {
    font-size: 45px;
    margin-bottom: 15px;
  }

  .support-title {
    font-size: 18px;
    font-weight: bold;
    color: #3d54a7;
    margin-bottom: 12px;
  }

  .support-desc {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
  }

  p {
    margin-bottom: 15px;
    text-align: justify;
  }

  strong {
    color: #3d54a7;
    font-weight: bold;
  }

  /* 职员系列 - 岗位职级样式 */
  .staff-level-container {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
    gap: 25px;
    margin: 20px 0;
  }

  .staff-level-box {
    border-radius: 12px;
    padding: 25px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
    transition: all 0.3s ease;
    border: 3px solid;
  }

  .staff-level-box:hover {
    transform: translateY(-5px);
    box-shadow: 0 8px 20px rgba(0, 0, 0, 0.15);
  }

  .staff-level-box.technical-position {
    background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
    border-color: #42a5f5;
  }

  .staff-level-box.employee-rank {
    background: linear-gradient(135deg, #e3f2fd 0%, #bbdefb 100%);
    border-color: #42a5f5;
  }

  .level-header {
    font-size: 18px;
    font-weight: bold;
    color: #1976d2;
    text-align: center;
    margin-bottom: 20px;
    padding-bottom: 12px;
    border-bottom: 2px solid rgba(25, 118, 210, 0.3);
  }

  .level-content {
    display: flex;
    justify-content: center;
    align-items: center;
    min-height: 60px;
  }

  .level-badge {
    background: white;
    color: #1976d2;
    padding: 12px 30px;
    border-radius: 25px;
    font-weight: bold;
    font-size: 16px;
    box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
    border: 2px solid #42a5f5;
  }

  /* 行政岗位样式 */
  .admin-position-container {
    background: linear-gradient(135deg, #fce4ec 0%, #f8bbd0 100%);
    border-radius: 12px;
    padding: 25px;
    margin: 20px 0;
    border: 3px solid #ec407a;
    box-shadow: 0 4px 15px rgba(236, 64, 122, 0.2);
  }

  .admin-header {
    font-size: 18px;
    font-weight: bold;
    color: #c2185b;
    text-align: center;
    margin-bottom: 20px;
    padding: 10px;
    background: rgba(255, 255, 255, 0.6);
    border-radius: 8px;
  }

  .path-diagram.admin-path {
    background: rgba(255, 255, 255, 0.7);
  }

  /* 行政岗位等级节点样式 */
  .path-node.level-admin-1 {
    background: linear-gradient(135deg, #f8bbd0 0%, #f48fb1 100%);
  }

  .path-node.level-admin-2 {
    background: linear-gradient(135deg, #f48fb1 0%, #ec407a 100%);
  }

  .path-node.level-admin-3 {
    background: linear-gradient(135deg, #ec407a 0%, #c2185b 100%);
  }

  .path-node.level-admin-4 {
    background: linear-gradient(135deg, #c2185b 0%, #880e4f 100%);
  }

  /* 管理说明样式 */
  .management-note {
    background: rgba(255, 255, 255, 0.8);
    padding: 15px;
    border-radius: 8px;
    margin-top: 15px;
    border-left: 4px solid #3d54a7;
  }

  .management-note strong {
    color: #c2185b;
    font-size: 15px;
  }
</style>
