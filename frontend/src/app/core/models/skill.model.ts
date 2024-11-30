export interface Skill {
    id: number;
    name: string;
    description: string;
    parentId?: number;
    category: string;
    level?: number;
    isRequired?: boolean;
    children?: Skill[];
    prerequisites?: Skill[];
    createdAt?: Date;
    updatedAt?: Date;
}

export interface ConsultantSkill {
    id: number;
    consultantId: number;
    skillId: number;
    level: number;
    acquiredAt: Date;
    isValidated: boolean;
    validatedBy?: number;
    validatedAt?: Date;
    notes?: string;
    createdAt?: Date;
    updatedAt?: Date;
    skill?: Skill;
}

export interface ProjectSkill {
    id: number;
    projectId: number;
    skillId: number;
    requiredLevel: number;
    priority: SkillPriority;
    skill?: Skill;
    createdAt?: Date;
    updatedAt?: Date;
}

export enum SkillPriority {
    LOW = 'LOW',
    MEDIUM = 'MEDIUM',
    HIGH = 'HIGH',
    CRITICAL = 'CRITICAL'
}

export interface SkillCreate {
    name: string;
    description: string;
    parentId?: number;
}

export interface SkillUpdate {
    id: number;
    name?: string;
    description?: string;
    parentId?: number;
}

export interface SkillFilter {
    name?: string;
    parentId?: number;
}
