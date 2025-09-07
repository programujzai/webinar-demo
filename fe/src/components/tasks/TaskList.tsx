'use client';

import { Task } from '@/lib/types/task';
import { TaskItem } from './TaskItem';
import { EmptyState } from './EmptyState';
import Skeleton from '@/components/Skeleton';

interface TaskListProps {
  tasks: Task[];
  isLoading: boolean;
  onComplete: (id: string) => void;
  onEdit: (task: Task) => void;
  onDelete: (id: string) => void;
  onArchive: (id: string) => void;
  onCreateClick: () => void;
}

export function TaskList({
  tasks,
  isLoading,
  onComplete,
  onEdit,
  onDelete,
  onArchive,
  onCreateClick,
}: TaskListProps) {
  if (isLoading) {
    return (
      <div className="space-y-3">
        {[...Array(3)].map((_, i) => (
          <Skeleton key={i} className="h-24 w-full" />
        ))}
      </div>
    );
  }

  if (tasks.length === 0) {
    return <EmptyState onCreateClick={onCreateClick} />;
  }

  return (
    <div className="space-y-3">
      {tasks.map((task) => (
        <TaskItem
          key={task.id}
          task={task}
          onComplete={onComplete}
          onEdit={onEdit}
          onDelete={onDelete}
          onArchive={onArchive}
        />
      ))}
    </div>
  );
}