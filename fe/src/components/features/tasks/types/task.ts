export type TaskStatus = 'PENDING' | 'COMPLETED' | 'ARCHIVED';
export type RecurrencePattern = 'DAILY' | 'WEEKLY';
export type DayOfWeek = 'MONDAY' | 'TUESDAY' | 'WEDNESDAY' | 'THURSDAY' | 'FRIDAY' | 'SATURDAY' | 'SUNDAY';

export interface BaseTask {
    id: string;
    name: string;
    displayOrder: number;
    createdAt: string;
    updatedAt: string;
    status: TaskStatus;
    category?: string;
    type: 'ONE_TIME' | 'RECURRING';
}

export interface OneTimeTask extends BaseTask {
    type: 'ONE_TIME';
    dueDate: string;
    completedAt?: string;
}

export interface RecurringTask extends BaseTask {
    type: 'RECURRING';
    recurrencePattern: RecurrencePattern;
    dayOfWeek?: DayOfWeek;
    startDate: string;
    endDate?: string;
    nextDueDate: string;
    lastCompletedDate?: string;
}

export type Task = OneTimeTask | RecurringTask;

export interface CreateOneTimeTaskRequest {
    type: 'ONE_TIME';
    name: string;
    dueDate: string;
    category?: string;
}

export interface CreateRecurringTaskRequest {
    type: 'RECURRING';
    name: string;
    recurrencePattern: RecurrencePattern;
    dayOfWeek?: DayOfWeek;
    startDate: string;
    endDate?: string;
    category?: string;
}

export type CreateTaskRequest = CreateOneTimeTaskRequest | CreateRecurringTaskRequest;

export interface UpdateTaskRequest {
    name?: string;
    category?: string;
    dueDate?: string;
    recurrencePattern?: RecurrencePattern;
    dayOfWeek?: DayOfWeek;
    endDate?: string;
}

export interface CompleteTaskRequest {
    notes?: string;
}

export interface TaskOrderUpdate {
    id: string;
    displayOrder: number;
}

export interface ReorderTasksRequest {
    taskOrders: TaskOrderUpdate[];
}

export interface TaskCompletionResponse {
    id: string;
    completedDate: string;
    completedAt: string;
    notes?: string;
}

export interface ApiError {
    status: number;
    error: string;
    message: string;
    code: string;
    timestamp: string;
    errors?: Array<{
        field: string;
        message?: string;
    }>;
}

export interface TaskFilters {
    category?: string;
    status?: TaskStatus;
    dueDate?: string;
    startDate?: string;
    endDate?: string;
}