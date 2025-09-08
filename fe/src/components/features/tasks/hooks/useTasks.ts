import {useMutation, useQuery, useQueryClient} from '@tanstack/react-query';
import {tasksApi} from '../api';
import {CompleteTaskRequest, CreateTaskRequest, TaskFilters, UpdateTaskRequest} from '../types';
import {toast} from 'sonner';

const TASKS_QUERY_KEY = 'tasks';

export function useTasks(filters?: TaskFilters) {
    return useQuery({
        queryKey: [TASKS_QUERY_KEY, filters],
        queryFn: () => tasksApi.getTasks(filters),
    });
}

export function useCreateTask() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (data: CreateTaskRequest) => tasksApi.createTask(data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [TASKS_QUERY_KEY]});
            toast.success('Task created successfully');
        },
        onError: (error: any) => {
            toast.error(error.message || 'Failed to create task');
        },
    });
}

export function useUpdateTask() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({id, data}: { id: string; data: UpdateTaskRequest }) =>
            tasksApi.updateTask(id, data),
        onSuccess: (_, variables) => {
            queryClient.invalidateQueries({queryKey: [TASKS_QUERY_KEY]});
            queryClient.invalidateQueries({queryKey: [TASKS_QUERY_KEY, variables.id]});
            toast.success('Task updated successfully');
        },
        onError: (error: any) => {
            toast.error(error.message || 'Failed to update task');
        },
    });
}

export function useDeleteTask() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: string) => tasksApi.deleteTask(id),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [TASKS_QUERY_KEY]});
            toast.success('Task deleted successfully');
        },
        onError: (error: any) => {
            toast.error(error.message || 'Failed to delete task');
        },
    });
}

export function useCompleteTask() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: ({id, data}: { id: string; data?: CompleteTaskRequest }) =>
            tasksApi.completeTask(id, data),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [TASKS_QUERY_KEY]});
            toast.success('Task completed');
        },
        onError: (error: any) => {
            toast.error(error.message || 'Failed to complete task');
        },
    });
}

export function useArchiveTask() {
    const queryClient = useQueryClient();

    return useMutation({
        mutationFn: (id: string) => tasksApi.archiveTask(id),
        onSuccess: () => {
            queryClient.invalidateQueries({queryKey: [TASKS_QUERY_KEY]});
            toast.success('Task archived');
        },
        onError: (error: any) => {
            toast.error(error.message || 'Failed to archive task');
        },
    });
}
