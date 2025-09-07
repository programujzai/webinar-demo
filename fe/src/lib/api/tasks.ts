import { apiClient } from './client';
import {
  Task,
  CreateTaskRequest,
  UpdateTaskRequest,
  CompleteTaskRequest,
  ReorderTasksRequest,
  TaskCompletionResponse,
  TaskFilters,
} from '@/lib/types/task';

const API_V1_BASE = '/api/v1';

export const tasksApi = {
  // Get all tasks with optional filters
  getTasks: async (filters?: TaskFilters): Promise<Task[]> => {
    return apiClient.get<Task[]>(`${API_V1_BASE}/tasks`, filters);
  },

  // Get a specific task
  getTask: async (id: string): Promise<Task> => {
    return apiClient.get<Task>(`${API_V1_BASE}/tasks/${id}`);
  },

  // Create a new task
  createTask: async (data: CreateTaskRequest): Promise<Task> => {
    return apiClient.post<Task>(`${API_V1_BASE}/tasks`, data);
  },

  // Update a task
  updateTask: async (id: string, data: UpdateTaskRequest): Promise<Task> => {
    return apiClient.put<Task>(`${API_V1_BASE}/tasks/${id}`, data);
  },

  // Delete a task
  deleteTask: async (id: string): Promise<void> => {
    return apiClient.delete<void>(`${API_V1_BASE}/tasks/${id}`);
  },

  // Complete a task
  completeTask: async (id: string, data?: CompleteTaskRequest): Promise<Task> => {
    return apiClient.post<Task>(`${API_V1_BASE}/tasks/${id}/complete`, data);
  },

  // Archive a task
  archiveTask: async (id: string): Promise<Task> => {
    return apiClient.post<Task>(`${API_V1_BASE}/tasks/${id}/archive`);
  },

  // Reorder tasks
  reorderTasks: async (data: ReorderTasksRequest): Promise<Task[]> => {
    return apiClient.put<Task[]>(`${API_V1_BASE}/tasks/reorder`, data);
  },

  // Get task completions
  getTaskCompletions: async (id: string): Promise<TaskCompletionResponse[]> => {
    return apiClient.get<TaskCompletionResponse[]>(`${API_V1_BASE}/tasks/${id}/completions`);
  },
};