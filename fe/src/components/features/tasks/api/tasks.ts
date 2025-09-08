import {apiClient} from '@/lib/api/client';
import {CompleteTaskRequest, CreateTaskRequest, Task, TaskFilters, UpdateTaskRequest,} from '../types';

const API_V1_BASE = '/api/v1';

export const tasksApi = {
    getTasks: async (filters?: TaskFilters): Promise<Task[]> => {
        return apiClient.get<Task[]>(`${API_V1_BASE}/tasks`, filters);
    },

    createTask: async (data: CreateTaskRequest): Promise<Task> => {
        return apiClient.post<Task>(`${API_V1_BASE}/tasks`, data);
    },

    updateTask: async (id: string, data: UpdateTaskRequest): Promise<Task> => {
        return apiClient.put<Task>(`${API_V1_BASE}/tasks/${id}`, data);
    },

    deleteTask: async (id: string): Promise<void> => {
        return apiClient.delete<void>(`${API_V1_BASE}/tasks/${id}`);
    },

    completeTask: async (id: string, data?: CompleteTaskRequest): Promise<Task> => {
        return apiClient.post<Task>(`${API_V1_BASE}/tasks/${id}/complete`, data);
    },

    // Archive a task
    archiveTask: async (id: string): Promise<Task> => {
        return apiClient.post<Task>(`${API_V1_BASE}/tasks/${id}/archive`);
    },
};