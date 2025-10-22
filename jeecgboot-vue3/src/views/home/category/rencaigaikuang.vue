<template>
  <div class="category-content">
    <h2>人才队伍情况</h2>
    <div class="content-wrapper">
      <!-- 人才队伍概况 -->
      <div class="section">
        <h3>一、人才队伍</h3>
        <p>
          研究所现已形成一支结构合理、素质优良、富有创新活力的人才队伍。截至2024年12月底，共有正式职工850人，其中拥有博士学位的342人、硕士学位的477人，正高91人、副高218人。充分利用国家和院人才计划平台，及所级人才项目，完善政策环境和保障措施，确保人才培养与引进协调推进。2024年荣获国家某创新人才培养基地荣誉称号。
        </p>
        
        <!-- 人才统计卡片 -->
        <div class="stats-section">
          <div class="stat-item">
            <div class="stat-number">850</div>
            <div class="stat-label">正式职工</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">342</div>
            <div class="stat-label">博士学位</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">477</div>
            <div class="stat-label">硕士学位</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">91</div>
            <div class="stat-label">正高职称</div>
          </div>
          <div class="stat-item">
            <div class="stat-number">218</div>
            <div class="stat-label">副高职称</div>
          </div>
        </div>

        <!-- 人才项目统计表格 -->
        <h4 class="table-title">人才项目统计表</h4>
        <div class="table-container">
          <table class="talent-table">
            <thead>
              <tr>
                <th>人才项目名称</th>
                <th>人次</th>
              </tr>
            </thead>
            <tbody>
              <tr>
                <td>国家BQW人才工程入选者</td>
                <td>1</td>
              </tr>
              <tr>
                <td>享受国务院政府特殊津贴专家</td>
                <td>13</td>
              </tr>
              <tr>
                <td>青年科学基金项目（A类）</td>
                <td>1</td>
              </tr>
              <tr>
                <td>国家级领军人才</td>
                <td>1</td>
              </tr>
              <tr>
                <td>青年科学基金项目（B类）</td>
                <td>4</td>
              </tr>
              <tr>
                <td>海外优青</td>
                <td>1</td>
              </tr>
              <tr>
                <td>国家级青年拔尖人才</td>
                <td>4</td>
              </tr>
              <tr>
                <td>国家网络安全奖（含优秀教师奖）</td>
                <td>4</td>
              </tr>
              <tr>
                <td>青年人才托举工程</td>
                <td>6</td>
              </tr>
              <tr>
                <td>中国科学院引才计划</td>
                <td>6</td>
              </tr>
              <tr>
                <td>中国科学院青年科学家奖</td>
                <td>1</td>
              </tr>
              <tr>
                <td>中国科学院青年创新促进会优秀会员</td>
                <td>3</td>
              </tr>
              <tr>
                <td>中国科学院青年创新促进会会员</td>
                <td>41</td>
              </tr>
              <tr>
                <td>中国科学院关键技术人才</td>
                <td>7</td>
              </tr>
              <tr class="total-row">
                <td><strong>合计</strong></td>
                <td><strong>93</strong></td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>

      <!-- 人才培养培训 -->
      <div class="section">
        <h3>二、注重人才培养培训</h3>
        <p>
          研究所拥有积极活跃的青年人才培养平台，通过定期举办品牌活动与对外交流，增强了青年人才凝聚力，提升了青年人才创新力和所外影响力。
        </p>
        <p>
          研究所高度重视人才培养体系建设，构建了完善的多层次、多渠道培养机制。设立专项基金支持青年人才参加国内外学术会议和专业培训，每年组织开展新员工入职培训、专业技能提升培训、管理能力培训等多种形式的培训活动。同时，实施导师制度，为青年科研人员配备经验丰富的导师，通过"传帮带"方式加速青年人才成长。研究所还建立了与国内外知名科研机构的合作交流机制，定期选派优秀青年人才进行访问交流和合作研究，拓宽国际视野，提升学术水平和创新能力。
        </p>
        
        <!-- 培养特色 -->
        <div class="training-features">
          <div class="feature-card">
            <div class="feature-icon">📚</div>
            <div class="feature-title">系统培训</div>
            <div class="feature-desc">入职培训、专业技能培训、管理能力培训等多层次培训体系</div>
          </div>
          <div class="feature-card">
            <div class="feature-icon">👨‍🏫</div>
            <div class="feature-title">导师制度</div>
            <div class="feature-desc">为青年科研人员配备经验丰富的导师，实施"传帮带"</div>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🌐</div>
            <div class="feature-title">国际交流</div>
            <div class="feature-desc">选派优秀人才进行访问交流和合作研究，拓宽国际视野</div>
          </div>
          <div class="feature-card">
            <div class="feature-icon">🎯</div>
            <div class="feature-title">专项支持</div>
            <div class="feature-desc">设立专项基金支持参加学术会议和专业培训活动</div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script setup lang="ts" name="rencaigaikuang">
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

  h4.table-title {
    color: #3d54a7;
    margin: 25px 0 15px;
    font-size: 18px;
    font-weight: bold;
    text-align: center;
  }

  .content-wrapper {
    line-height: 1.8;
    font-size: 16px;
  }

  .section {
    margin-bottom: 40px;
  }

  .stats-section {
    display: flex;
    justify-content: space-around;
    flex-wrap: wrap;
    margin: 30px 0;
    padding: 25px;
    background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
    border-radius: 12px;
    box-shadow: 0 4px 15px rgba(0, 0, 0, 0.1);
  }

  .stat-item {
    text-align: center;
    padding: 20px;
    min-width: 140px;
    background-color: rgba(255, 255, 255, 0.9);
    border-radius: 8px;
    margin: 10px;
    transition: transform 0.3s ease, box-shadow 0.3s ease;
  }

  .stat-item:hover {
    transform: translateY(-5px);
    box-shadow: 0 6px 20px rgba(61, 84, 167, 0.2);
  }

  .stat-number {
    font-size: 40px;
    font-weight: bold;
    color: #3d54a7;
    margin-bottom: 8px;
  }

  .stat-label {
    font-size: 15px;
    color: #666;
    font-weight: 500;
  }

  /* 表格样式 */
  .table-container {
    margin: 20px 0;
    overflow-x: auto;
    border-radius: 8px;
    box-shadow: 0 2px 10px rgba(0, 0, 0, 0.08);
  }

  .talent-table {
    width: 100%;
    border-collapse: collapse;
    background-color: #fff;
  }

  .talent-table thead {
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    color: white;
  }

  .talent-table th {
    padding: 15px;
    text-align: left;
    font-weight: bold;
    font-size: 16px;
  }

  .talent-table th:last-child {
    text-align: center;
    width: 120px;
  }

  .talent-table tbody tr {
    border-bottom: 1px solid #e8e8e8;
    transition: background-color 0.3s ease;
  }

  .talent-table tbody tr:hover {
    background-color: #f5f7fa;
  }

  .talent-table td {
    padding: 12px 15px;
    font-size: 15px;
  }

  .talent-table td:last-child {
    text-align: center;
    font-weight: 600;
    color: #3d54a7;
  }

  .talent-table .total-row {
    background-color: #f0f4ff;
    border-top: 2px solid #3d54a7;
  }

  .talent-table .total-row td {
    font-size: 16px;
    padding: 15px;
  }

  /* 培养特色卡片 */
  .training-features {
    display: grid;
    grid-template-columns: repeat(auto-fit, minmax(240px, 1fr));
    gap: 20px;
    margin: 30px 0;
  }

  .feature-card {
    background: white;
    padding: 25px;
    border-radius: 10px;
    box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
    text-align: center;
    transition: all 0.3s ease;
    border: 2px solid transparent;
  }

  .feature-card:hover {
    transform: translateY(-8px);
    box-shadow: 0 8px 25px rgba(61, 84, 167, 0.2);
    border-color: #3d54a7;
  }

  .feature-icon {
    font-size: 40px;
    margin-bottom: 15px;
  }

  .feature-title {
    font-size: 18px;
    font-weight: bold;
    color: #3d54a7;
    margin-bottom: 12px;
  }

  .feature-desc {
    font-size: 14px;
    color: #666;
    line-height: 1.6;
  }

  p {
    margin-bottom: 15px;
    text-align: justify;
    color: #333;
  }
</style>
