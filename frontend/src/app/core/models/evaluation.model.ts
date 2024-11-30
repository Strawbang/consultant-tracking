import { Consultant } from "./consultant.model";
import { Skill } from "./skill.model";

export enum EvaluationType {
    PERIODIC = 'PERIODIC',
    PROJECT = 'PROJECT',
    SKILL = 'SKILL',
    PERFORMANCE = 'PERFORMANCE',
    ANNUAL = 'ANNUAL',
    SEMI_ANNUAL = 'SEMI_ANNUAL',
    QUARTERLY = 'QUARTERLY',
    PROJECT_END = 'PROJECT_END',
    PROBATION = 'PROBATION'
}

export enum EvaluationStatus {
    DRAFT = 'DRAFT',
    SCHEDULED = 'SCHEDULED',
    PENDING = 'PENDING',
    IN_PROGRESS = 'IN_PROGRESS',
    COMPLETED = 'COMPLETED',
    CANCELLED = 'CANCELLED',
    ARCHIVED = 'ARCHIVED'
}

export interface Evaluation {
    id: number;
    consultantId: number;
    projectId: number;
    evaluatorId?: number;
    type: EvaluationType;
    evaluationDate: Date;
    status: EvaluationStatus;
    score: number;
    overallPerformance?: number;
    technicalSkills?: number;
    softSkills?: number;
    projectContribution?: number;
    teamwork?: number;
    comments: string;
    createdAt?: Date;
    updatedAt?: Date;
}

export interface EvaluationCreate {
    consultantId: number;
    projectId: number;
    evaluatorId?: number;
    type: EvaluationType;
    evaluationDate: Date;
    status: EvaluationStatus;
    score: number;
    overallPerformance?: number;
    technicalSkills?: number;
    softSkills?: number;
    projectContribution?: number;
    teamwork?: number;
    comments: string;
}

export interface EvaluationUpdate {
    evaluationDate?: Date;
    score?: number;
    comments?: string;
}

export interface EvaluationFilter {
    consultantId?: number;
    projectId?: number;
    type?: EvaluationType;
    fromDate?: Date;
    toDate?: Date;
    startDate?: Date;
    endDate?: Date;
    status?: EvaluationStatus;
}

export interface SkillEvaluation {
    id: number;
    evaluationId: number;
    skillId: number;
    rating: number;
    comments?: string;
    createdAt?: Date;
    updatedAt?: Date;
    skill?: Skill;
}

export interface SkillEvaluationCreate {
    evaluationId: number;
    skillId: number;
    rating: number;
    comments?: string;
}

export interface SkillEvaluationUpdate {
    id: number;
    rating?: number;
    comments?: string;
}
