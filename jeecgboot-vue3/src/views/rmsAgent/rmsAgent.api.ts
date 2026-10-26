import { defHttp } from '/@/utils/http/axios';

export interface OntologyProperty {
  name: string;
  label: string;
  type: string;
}

export interface OntologyObject {
  type: string;
  label: string;
  color: string;
  description: string;
  properties: OntologyProperty[];
}

export interface OntologyRelation {
  relation: string;
  label: string;
  from: string;
  to: string;
  description: string;
}

export interface OntologyAction {
  id: string;
  label: string;
  agentLabel: string;
  agentKey: string;
  phaseKey: string;
  submissionCriteria: string;
  sideEffects: string;
  parameters: { name: string; label: string; required: boolean }[];
}

export interface OntologyTool {
  name: string;
  label: string;
  description: string;
  write: boolean;
}

export interface OntologySkill {
  code: string;
  name: string;
  body: string;
}

export interface OntologyAgent {
  order: number;
  agentKey: string;
  agentName: string;
  label: string;
  phaseKey: string;
  usesLlm: boolean;
  description: string;
  skills: string[];
  tools: string[];
  note: string;
}

export interface OntologyPhase {
  order: number;
  key: string;
  name: string;
  agentName: string;
  color: string;
  count?: number;
  cases?: HireCase[];
}

export interface HireCase {
  applyId: string;
  resumeId?: string;
  positionType?: string;
  userName: string;
  resumeName: string;
  positionName: string;
  positionDept: string;
  approvalNode: string;
  approvalStatus: string;
  status?: string;
  phaseKey: string;
  inviteId?: string;
  inviteStatus?: string;
  interviewResult?: string;
  closed: boolean;
  actions: string[];
}

export interface HireSchema {
  version: string;
  description: string;
  workflow: { code: string; name: string; description: string; status: string };
  objects: OntologyObject[];
  relations: OntologyRelation[];
  actions: OntologyAction[];
  tools: OntologyTool[];
  skills: OntologySkill[];
  agents: OntologyAgent[];
  phases: OntologyPhase[];
}

export interface HireBoard {
  workflowCode: string;
  limit: number;
  phases: OntologyPhase[];
}

export interface ActionResult {
  actionId: string;
  agentName: string;
  message: string;
  phaseBefore: string;
  phaseAfter: string;
  applyId: string;
}

export const fetchHireSchema = () => defHttp.get<HireSchema>({ url: '/recruitment/ontology/schema' });

export const fetchHireBoard = (params?: { myDept?: boolean }) =>
  defHttp.get<HireBoard>({ url: '/recruitment/ontology/board', params });

export const runHireAction = (data: {
  actionId: string;
  applyId: string;
  interviewDate?: string;
  interviewPlace?: string;
  inviteLetter?: string;
}) => defHttp.post<ActionResult>({ url: '/recruitment/ontology/actions/run', data });
