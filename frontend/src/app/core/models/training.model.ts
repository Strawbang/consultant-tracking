import { Consultant } from "./consultant.model";

export enum TrainingStatus {
    PLANNED = 'PLANNED',
    IN_PROGRESS = 'IN_PROGRESS',
    COMPLETED = 'COMPLETED',
    CANCELLED = 'CANCELLED'
}

export interface Training {
    id: number;
    name: string;
    description?: string;
    startDate: Date;
    endDate: Date;
    status: TrainingStatus;
    consultant: Consultant;
    certificationObtained?: boolean;
    completionDate?: Date;
    provider?: string;
    cost?: number;
    notes?: string;
}
