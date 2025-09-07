'use client';

import { useState } from 'react';
import { Plus } from 'lucide-react';
import { Button } from '@/components/design_system/atoms/Button';
import { TaskList, TaskFilters, TaskDialog } from '@/components/design_system/organisms';
import { 
  useTasks, 
  useCreateTask, 
  useUpdateTask, 
  useCompleteTask, 
  useDeleteTask, 
  useArchiveTask 
} from '@/lib/hooks/useTasks';
import { Task, TaskFilters as TaskFiltersType, CreateTaskRequest, UpdateTaskRequest } from '@/lib/types/task';

export default function HomePage() {
  const [filters, setFilters] = useState<TaskFiltersType>({});
  const [dialogOpen, setDialogOpen] = useState(false);
  const [editingTask, setEditingTask] = useState<Task | undefined>();

  const { data: tasks = [], isLoading } = useTasks(filters);
  const createTask = useCreateTask();
  const updateTask = useUpdateTask();
  const completeTask = useCompleteTask();
  const deleteTask = useDeleteTask();
  const archiveTask = useArchiveTask();

  const handleCreateClick = () => {
    setEditingTask(undefined);
    setDialogOpen(true);
  };

  const handleEditClick = (task: Task) => {
    setEditingTask(task);
    setDialogOpen(true);
  };

  const handleSubmit = async (data: CreateTaskRequest | UpdateTaskRequest) => {
    try {
      if (editingTask) {
        await updateTask.mutateAsync({ id: editingTask.id, data: data as UpdateTaskRequest });
      } else {
        await createTask.mutateAsync(data as CreateTaskRequest);
      }
      setDialogOpen(false);
      setEditingTask(undefined);
    } catch (error) {
      // Error is handled by the mutation hook
    }
  };

  const handleComplete = (id: string) => {
    completeTask.mutate({ id });
  };

  const handleDelete = (id: string) => {
    if (confirm('Are you sure you want to delete this task?')) {
      deleteTask.mutate(id);
    }
  };

  const handleArchive = (id: string) => {
    archiveTask.mutate(id);
  };

  const isSubmitting = createTask.isPending || updateTask.isPending;

  return (
    <div className="min-h-screen bg-background">
      <div className="container max-w-4xl mx-auto py-8 px-4">
        <header className="mb-8">
          <h1 className="text-3xl font-bold mb-2">My Tasks</h1>
          <p className="text-muted-foreground">
            Organize your tasks efficiently with our modern todo list app
          </p>
        </header>

        <div className="mb-6 flex justify-between items-center">
          <TaskFilters 
            filters={filters} 
            onFiltersChange={setFilters} 
          />
          <Button onClick={handleCreateClick}>
            <Plus className="mr-2 h-4 w-4" />
            New Task
          </Button>
        </div>

        <TaskList
          tasks={tasks}
          isLoading={isLoading}
          onComplete={handleComplete}
          onEdit={handleEditClick}
          onDelete={handleDelete}
          onArchive={handleArchive}
          onCreateClick={handleCreateClick}
        />

        <TaskDialog
          open={dialogOpen}
          onOpenChange={setDialogOpen}
          task={editingTask}
          onSubmit={handleSubmit}
          isSubmitting={isSubmitting}
        />
      </div>
    </div>
  );
}