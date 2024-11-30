import { Consultant } from "./consultant.model";
import { ProjectSkill, SkillPriority } from "./skill.model";

export enum ProjectStatus {
  DRAFT = 'DRAFT',
  ACTIVE = 'ACTIVE',
  COMPLETED = 'COMPLETED',
  CANCELLED = 'CANCELLED'
}

export interface Project {
  id?: number;
  name: string;
  description?: string;
  client: string;
  startDate: Date;
  endDate?: Date;
  status: ProjectStatus;
  consultantIds: number[];
  createdAt?: Date;
  updatedAt?: Date;
}

export interface ConsultantProject {
  id: number;
  consultantId: number;
  projectId: number;
  role: string;
  startDate: Date;
  endDate?: Date;
  allocation: number;
  notes?: string;
  consultant?: Consultant;
  project?: Project;
}

export interface ProjectCreate extends Omit<Project, 'id' | 'createdAt' | 'updatedAt'> {}

export interface ProjectUpdate extends Partial<ProjectCreate> {
  id: number;
}

export interface ProjectFilter {
  searchTerm?: string;
  status?: ProjectStatus;
  clientId?: number;
  startDate?: Date;
  endDate?: Date;
}
