<template>
  <div class="desk">
    <header v-if="!locked" class="mast">
      <p class="kicker">招聘本体 · {{ schema?.workflow.code || 'rms_hire' }}</p>
      <h1>从简历到面试邀请</h1>
      <p class="lead">
        投递、初筛、部门审核、人力决审、发邀请、记面试结果，收成一条智能体工作流。对象、关系、动作和工具都挂在这条链上，办理仍写入原来的申请和邀请。
      </p>
      <div class="modes">
        <button type="button" :class="{ on: mode === 'flow' }" @click="mode = 'flow'">工作流</button>
        <button type="button" :class="{ on: mode === 'ontology' }" @click="mode = 'ontology'">本体</button>
      </div>
    </header>

    <a-spin :spinning="loading">
      <section v-if="mode === 'flow'" class="flow" :class="{ 'flow--single': locked }">
        <div v-if="!locked" class="rail">
          <button
            v-for="phase in phases"
            :key="phase.key"
            type="button"
            class="step"
            :class="{ on: phase.key === activePhase }"
            :style="{ '--step': phase.color }"
            @click="activePhase = phase.key"
          >
            <span class="idx">{{ phase.order }}</span>
            <span class="step-name">{{ phase.name }}</span>
            <span class="step-agent">{{ phase.agentName }}</span>
            <b>{{ phase.count || 0 }}</b>
          </button>
        </div>

        <div class="column">
          <div class="column-head">
            <div>
              <h2>{{ currentPhase?.name || '办理' }}</h2>
              <p>{{ currentAgent?.description || '这一阶段还没有待办。' }}</p>
            </div>
            <div class="column-tools">
              <a-input v-model:value="keyword" allow-clear placeholder="搜姓名、岗位、简历" style="width: 220px" />
              <button type="button" class="text-btn" @click="load">刷新</button>
            </div>
          </div>
          <p v-if="boardDenied" class="denied">当前账号不能办理招聘流程。本体定义仍可查看。</p>
          <div v-else-if="cases.length" class="cards">
            <article v-for="item in cases" :key="item.applyId" class="card">
              <div class="who">
                <strong>{{ item.userName }}</strong>
                <span>{{ item.positionName }}</span>
              </div>
              <p class="resume">{{ item.resumeName }}</p>
              <p v-if="item.positionDept" class="meta">{{ item.positionDept }}</p>
              <div class="tags">
                <span>{{ item.approvalNode || '未进入审批' }}</span>
                <span v-if="item.approvalStatus">{{ item.approvalStatus }}</span>
                <span v-if="item.status && item.status !== item.approvalStatus">{{ item.status }}</span>
                <span v-if="item.inviteStatus">{{ item.inviteStatus }}</span>
                <span v-if="item.interviewResult">{{ item.interviewResult }}</span>
              </div>
              <div class="acts">
                <button type="button" class="ghost" @click="openResume(item)">简历</button>
                <button type="button" class="ghost" @click="openTrace(item)">过程</button>
                <button v-auth="'xgsUserResume:xgs_position_apply:exportXls'" type="button" class="ghost" @click="exportResume(item)">导出</button>
                <button
                  v-for="actionId in item.actions || []"
                  :key="actionId"
                  type="button"
                  :class="actionTone(actionId)"
                  @click="openAction(item, actionId)"
                >
                  {{ actionLabel(actionId) }}
                </button>
              </div>
              <p v-if="item.closed" class="quiet">结论已记下，不能再改</p>
              <p v-else-if="!(item.actions || []).length" class="quiet">{{ intakeHint(item) }}</p>
            </article>
          </div>
          <div v-else class="empty">最近的申请里，这一阶段是空的。</div>
        </div>
      </section>

      <section v-else class="ontology">
        <div class="onto-block">
          <h2>智能体</h2>
          <div class="agent-list">
            <article v-for="agent in schema?.agents || []" :key="agent.agentKey">
              <header>
                <b>{{ agent.order }}</b>
                <div>
                  <strong>{{ agent.label }}</strong>
                  <code>{{ agent.agentName }}</code>
                </div>
              </header>
              <p>{{ agent.description }}</p>
              <p class="quiet">{{ agent.note }}</p>
              <p class="chips">
                <span v-for="skill in agent.skills" :key="skill">技能 {{ skill }}</span>
                <span v-for="tool in agent.tools" :key="tool">工具 {{ tool }}</span>
              </p>
            </article>
          </div>
        </div>

        <div class="onto-block">
          <h2>对象与属性</h2>
          <div class="object-grid">
            <article v-for="object in schema?.objects || []" :key="object.type" :style="{ '--mark': object.color }">
              <h3>{{ object.label }}</h3>
              <code>{{ object.type }}</code>
              <p>{{ object.description }}</p>
              <ul>
                <li v-for="prop in object.properties" :key="prop.name">
                  <span>{{ prop.label }}</span>
                  <em>{{ prop.name }} · {{ prop.type }}</em>
                </li>
              </ul>
            </article>
          </div>
        </div>

        <div class="onto-block">
          <h2>关系</h2>
          <ul class="rels">
            <li v-for="rel in schema?.relations || []" :key="rel.relation">
              <code>{{ rel.from }}</code>
              <span>{{ rel.label }}</span>
              <code>{{ rel.to }}</code>
              <em>{{ rel.description }}</em>
            </li>
          </ul>
        </div>

        <div class="split">
          <div class="onto-block">
            <h2>动作</h2>
            <ul class="plain">
              <li v-for="action in schema?.actions || []" :key="action.id">
                <strong>{{ action.label }}</strong>
                <span>{{ action.agentLabel }}</span>
                <em>{{ action.submissionCriteria }}。{{ action.sideEffects }}。</em>
              </li>
            </ul>
          </div>
          <div class="onto-block">
            <h2>工具</h2>
            <ul class="plain">
              <li v-for="tool in schema?.tools || []" :key="tool.name">
                <strong>{{ tool.label }}</strong>
                <span>{{ tool.write ? '写库' : '只读' }}</span>
                <em>{{ tool.description }}</em>
              </li>
            </ul>
          </div>
        </div>

        <div class="onto-block">
          <h2>技能</h2>
          <ul class="plain">
            <li v-for="skill in schema?.skills || []" :key="skill.code">
              <strong>{{ skill.name }}</strong>
              <code>{{ skill.code }}</code>
              <em>{{ skill.body }}</em>
            </li>
          </ul>
        </div>
      </section>
    </a-spin>

    <a-modal v-model:open="modalOpen" :title="pendingAction?.label || '办理'" :confirm-loading="running" ok-text="执行" cancel-text="取消" @ok="submitAction">
      <p class="modal-copy">{{ pendingAction?.agentLabel }} · {{ pendingCase?.userName }} · {{ pendingCase?.positionName }}</p>
      <p class="modal-copy">{{ pendingAction?.sideEffects }}</p>
      <div v-if="needsInviteForm" class="invite-form">
        <label>
          面试时间
          <a-date-picker v-model:value="inviteForm.interviewDate" show-time value-format="YYYY-MM-DD HH:mm:ss" style="width: 100%" />
        </label>
        <label>
          面试地点
          <a-input v-model:value="inviteForm.interviewPlace" placeholder="例如 本部 3 号楼会议室" />
        </label>
        <label>
          邀请说明
          <a-textarea v-model:value="inviteForm.inviteLetter" :rows="4" placeholder="时间、材料、联系人" />
        </label>
      </div>
    </a-modal>
    <XgsPositionApplyModal ref="resumeModal" />
    <XgsFlowOpinionsListModal ref="traceModal" />
  </div>
</template>

<script lang="ts" setup>
  import { computed, onMounted, reactive, ref } from 'vue';
  import { useMessage } from '/@/hooks/web/useMessage';
  import { useGlobSetting } from '/@/hooks/setting';
  import { getToken } from '/@/utils/auth';
  import XgsPositionApplyModal from '@/views/xgsResumeApproval/departApproval/components/XgsPositionApplyModal.vue';
  import XgsFlowOpinionsListModal from '@/views/xgsResumeApproval/opinions/components/XgsFlowOpinionsListModal.vue';
  import {
    fetchHireBoard,
    fetchHireSchema,
    runHireAction,
    type HireBoard,
    type HireCase,
    type HireSchema,
    type OntologyAction,
  } from './rmsAgent.api';

  const props = defineProps({
    initialPhase: { type: String, default: '' },
    myDept: { type: Boolean, default: false },
    /** 只办当前这一段，不展示整条工作流和本体说明 */
    locked: { type: Boolean, default: false },
  });

  const { createMessage } = useMessage();
  const globSetting = useGlobSetting();
  const resumeModal = ref();
  const traceModal = ref();
  const mode = ref<'flow' | 'ontology'>('flow');
  const loading = ref(false);
  const running = ref(false);
  const boardDenied = ref(false);
  const schema = ref<HireSchema>();
  const board = ref<HireBoard>();
  const activePhase = ref(props.initialPhase || 'hr_screen');
  const keyword = ref('');
  const modalOpen = ref(false);
  const pendingCase = ref<HireCase>();
  const pendingAction = ref<OntologyAction>();
  const inviteForm = reactive({
    interviewDate: '',
    interviewPlace: '',
    inviteLetter: '',
  });

  const phases = computed(() => board.value?.phases || schema.value?.phases || []);
  const currentPhase = computed(() => phases.value.find((phase) => phase.key === activePhase.value));
  const cases = computed(() => {
    const rows = currentPhase.value?.cases || [];
    const query = keyword.value.trim();
    if (!query) {
      return rows;
    }
    return rows.filter((item) =>
      [item.userName, item.positionName, item.resumeName, item.positionDept].some((value) => (value || '').includes(query)),
    );
  });
  const currentAgent = computed(() => schema.value?.agents.find((agent) => agent.phaseKey === activePhase.value));
  const needsInviteForm = computed(() => (pendingAction.value?.parameters.length || 0) > 0);

  function actionOf(actionId: string) {
    return schema.value?.actions.find((action) => action.id === actionId);
  }

  function actionLabel(actionId: string) {
    if (actionId === 'SendInterviewInvite') {
      return '邀请';
    }
    return actionOf(actionId)?.label || actionId;
  }

  function intakeHint(item: HireCase) {
    if (item.approvalNode === '申请人') {
      return '已退回申请人，等重新提交';
    }
    return '还没进入审批';
  }

  function actionTone(actionId: string) {
    return actionId.endsWith('Reject') || actionId.endsWith('Fail') ? 'ghost' : 'solid';
  }

  function applyRecord(item: HireCase) {
    return {
      id: item.applyId,
      resumeId: item.resumeId,
      positionType: item.positionType,
      positionName: item.positionName,
      userName: item.userName,
      resumeName: item.resumeName,
    };
  }

  function openResume(item: HireCase) {
    if (!item.resumeId) {
      createMessage.warning('这条申请没有简历');
      return;
    }
    resumeModal.value.disableSubmit = true;
    resumeModal.value.detail(applyRecord(item));
  }

  function openTrace(item: HireCase) {
    traceModal.value.open({ record: applyRecord(item), isUpdate: false, showFooter: false });
  }

  function exportResume(item: HireCase) {
    const token = getToken();
    const url = `${globSetting.apiUrl}/xgsExport/exportResumeWord?applyId=${item.applyId}&token=${token}`;
    window.open(url, '_blank');
  }

  function openAction(item: HireCase, actionId: string) {
    pendingCase.value = item;
    pendingAction.value = actionOf(actionId);
    inviteForm.interviewDate = '';
    inviteForm.interviewPlace = '';
    inviteForm.inviteLetter = '';
    modalOpen.value = true;
  }

  async function submitAction() {
    if (!pendingCase.value || !pendingAction.value) {
      return;
    }
    if (needsInviteForm.value) {
      if (!inviteForm.interviewDate || !inviteForm.interviewPlace.trim() || !inviteForm.inviteLetter.trim()) {
        createMessage.warning('请填写面试时间、地点和说明');
        return Promise.reject(new Error('邀请信息不完整'));
      }
    }
    running.value = true;
    try {
      const result = await runHireAction({
        actionId: pendingAction.value.id,
        applyId: pendingCase.value.applyId,
        interviewDate: inviteForm.interviewDate,
        interviewPlace: inviteForm.interviewPlace,
        inviteLetter: inviteForm.inviteLetter,
      });
      createMessage.success(`${result.agentName}：${result.message}`);
      modalOpen.value = false;
      if (!props.locked) {
        activePhase.value = result.phaseAfter || activePhase.value;
      }
      await loadBoard();
    } finally {
      running.value = false;
    }
  }

  async function loadBoard() {
    try {
      board.value = await fetchHireBoard({ myDept: props.myDept });
      boardDenied.value = false;
    } catch (error) {
      boardDenied.value = true;
      board.value = undefined;
    }
  }

  async function load() {
    loading.value = true;
    try {
      schema.value = await fetchHireSchema();
      await loadBoard();
      if (!props.locked && !phases.value.some((phase) => phase.key === activePhase.value)) {
        activePhase.value = phases.value[0]?.key || 'hr_screen';
      }
    } catch (error) {
      createMessage.error('招聘工作流没有加载出来');
    } finally {
      loading.value = false;
    }
  }

  onMounted(load);
</script>

<style scoped>
  .desk {
    min-height: 100%;
    padding: 28px 32px 48px;
    background:
      radial-gradient(circle at top left, rgba(0, 64, 152, 0.08), transparent 32%),
      #f3efe6;
    color: #1c1917;
  }
  .mast { max-width: 920px; margin-bottom: 22px; }
  .kicker {
    margin: 0 0 8px;
    letter-spacing: 0.14em;
    text-transform: uppercase;
    font-size: 12px;
    color: #0f5c56;
  }
  h1 {
    margin: 0;
    font-family: 'Iowan Old Style', 'Palatino Linotype', 'Songti SC', serif;
    font-size: 40px;
    font-weight: 600;
    letter-spacing: -0.03em;
  }
  .lead { margin: 10px 0 0; max-width: 680px; line-height: 1.6; color: #44403c; }
  .modes { display: flex; gap: 8px; margin-top: 18px; }
  .modes button, .text-btn, .acts button {
    border: 1px solid #d6d0c4;
    background: transparent;
    border-radius: 999px;
    padding: 6px 14px;
    cursor: pointer;
  }
  .modes button.on { background: #1c1917; color: #f6f3ec; border-color: #1c1917; }
  .flow { display: grid; grid-template-columns: 240px 1fr; gap: 22px; }
  .flow--single { grid-template-columns: 1fr; }
  .rail { display: flex; flex-direction: column; gap: 8px; }
  .step {
    display: grid;
    grid-template-columns: auto 1fr auto;
    grid-template-rows: auto auto;
    column-gap: 10px;
    text-align: left;
    padding: 12px;
    border: 1px solid #e4ddd0;
    border-radius: 14px;
    background: rgba(255, 252, 247, 0.8);
    cursor: pointer;
  }
  .step.on { border-color: var(--step); box-shadow: inset 3px 0 0 var(--step); }
  .idx { grid-row: span 2; align-self: center; color: var(--step); font-weight: 700; }
  .step-name { font-weight: 650; }
  .step-agent { grid-column: 2; color: #78716c; font-size: 12px; }
  .step b { grid-row: span 2; align-self: center; }
  .column-head, .who, article header, .column-tools { display: flex; justify-content: space-between; gap: 12px; align-items: center; }
  .column-head h2, .onto-block h2 { margin: 0; font-size: 22px; }
  .column-head p, .card p, .agent-list p { margin: 4px 0 0; color: #57534e; }
  .cards, .object-grid, .agent-list, .split { display: grid; gap: 12px; }
  .cards, .object-grid { grid-template-columns: repeat(auto-fill, minmax(260px, 1fr)); margin-top: 14px; }
  .card, .object-grid article, .agent-list article, .onto-block {
    background: #fffdf8;
    border: 1px solid #e7e0d4;
    border-radius: 16px;
    padding: 16px;
  }
  .card {
    display: flex;
    flex-direction: column;
    height: 100%;
  }
  .object-grid article { border-top: 4px solid var(--mark); }
  .resume { font-size: 14px; }
  .meta, .quiet, .modal-copy { color: #78716c; font-size: 13px; }
  .tags, .chips { display: flex; flex-wrap: wrap; gap: 6px; margin-top: 10px; }
  .tags span, .chips span {
    background: #f4f0e6;
    border-radius: 999px;
    padding: 2px 8px;
    font-size: 12px;
  }
  .acts { display: flex; flex-wrap: wrap; align-items: center; gap: 6px; margin-top: auto; padding-top: 12px; }
  .acts button { padding: 6px 10px; font-size: 13px; white-space: nowrap; }
  .acts button.ghost { background: #fff; color: #1c1917; }
  .acts button.solid { background: #004098; color: #f6fffd; border-color: #004098; }
  .empty, .denied {
    margin-top: 16px;
    padding: 28px;
    border: 1px dashed #d6d0c4;
    border-radius: 16px;
    color: #57534e;
  }
  .ontology { display: flex; flex-direction: column; gap: 16px; }
  .agent-list { grid-template-columns: repeat(auto-fill, minmax(280px, 1fr)); margin-top: 12px; }
  .object-grid h3, .plain strong { margin: 0; }
  .object-grid ul, .rels, .plain { list-style: none; padding: 0; margin: 12px 0 0; }
  .object-grid li, .rels li, .plain li {
    display: flex;
    flex-wrap: wrap;
    gap: 8px;
    padding: 8px 0;
    border-top: 1px solid #f0ebe3;
  }
  code { font-size: 12px; color: #0f5c56; }
  em { color: #78716c; font-style: normal; }
  .split { grid-template-columns: 1.2fr 0.8fr; }
  .invite-form { display: flex; flex-direction: column; gap: 10px; margin-top: 12px; }
  .invite-form label { display: flex; flex-direction: column; gap: 6px; font-size: 13px; }
  @media (max-width: 900px) {
    .desk { padding: 16px; }
    .flow, .split { grid-template-columns: 1fr; }
    h1 { font-size: 32px; }
  }
</style>
