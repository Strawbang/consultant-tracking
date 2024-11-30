import { Project } from './project.model';
import { Skill } from './skill.model';
import { Evaluation } from './evaluation.model';
import { Training } from './training.model';

export enum ConsultantStatus {
    ACTIVE = 'ACTIVE',
    INACTIVE = 'INACTIVE',
    ON_LEAVE = 'ON_LEAVE',
    TERMINATED = 'TERMINATED'
}

export interface Consultant {
  id: number;
  firstName: string;
  lastName: string;
  email: string;
  phone?: string;
  address?: string;
  name?: string;
  rating?: number;
  skills?: Skill[];
  skillIds?: number[];
  projectIds?: number[];
  createdAt?: Date;
}

export interface ConsultantSkill {
    id: number;
    consultantId: number;
    skillId: number;
    level: number;
    skill?: Skill;
    createdAt?: Date;
    updatedAt?: Date;
}

export interface ConsultantCreate extends Omit<Consultant, 'id' | 'createdAt' | 'updatedAt'> {}

export interface ConsultantUpdate extends Partial<ConsultantCreate> {}

export interface ConsultantFilter {
    searchTerm?: string;
    isActive?: boolean;
    skillId?: number;
    projectId?: number;
}
