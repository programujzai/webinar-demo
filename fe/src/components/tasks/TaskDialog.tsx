'use client';

import { Dialog, DialogContent, DialogHeader, DialogTitle } from '@/components/ui/dialog';
import { TaskForm } from './TaskForm';
import { Task, CreateTaskRequest, UpdateTaskRequest } from '@/lib/types/task';
import { CreateOneTimeTaskInput } from '@/lib/validations/task';

interface TaskDialogProps {
  open: boolean;
  onOpenChange: (open: boolean) => void;
  task?: Task;
  onSubmit: (data: CreateTaskRequest | UpdateTaskRequest) => void;
  isSubmitting?: boolean;
}

export function TaskDialog({ open, onOpenChange, task, onSubmit, isSubmitting }: TaskDialogProps) {
  const handleSubmit = (data: CreateOneTimeTaskInput | UpdateTaskRequest) => {
    if ('type' in data) {
      // It's a create request
      onSubmit(data as CreateTaskRequest);
    } else {
      // It's an update request
      onSubmit(data as UpdateTaskRequest);
    }
  };

  return (
    <Dialog open={open} onOpenChange={onOpenChange}>
      <DialogContent className="sm:max-w-[425px]">
        <DialogHeader>
          <DialogTitle>{task ? 'Edit Task' : 'Create New Task'}</DialogTitle>
        </DialogHeader>
        <TaskForm
          task={task}
          onSubmit={handleSubmit}
          onCancel={() => onOpenChange(false)}
          isSubmitting={isSubmitting}
        />
      </DialogContent>
    </Dialog>
  );
}