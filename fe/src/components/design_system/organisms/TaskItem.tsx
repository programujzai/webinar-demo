'use client';

import { Check, Edit, Trash, Archive, Calendar, RefreshCw } from 'lucide-react';
import { format } from 'date-fns';
import { Card } from '@/components/design_system/molecules/card';
import { Button } from '@/components/design_system/atoms/Button';
import { Badge } from '@/components/design_system/atoms/badge';
import { Checkbox } from '@/components/design_system/atoms/checkbox';
import { 
  DropdownMenu, 
  DropdownMenuContent, 
  DropdownMenuItem, 
  DropdownMenuTrigger 
} from '@/components/design_system/molecules/dropdown-menu';
import { Task, OneTimeTask, RecurringTask } from '@/lib/types/task';
import { cn } from '@/lib/utils';

interface TaskItemProps {
  task: Task;
  onComplete: (id: string) => void;
  onEdit: (task: Task) => void;
  onDelete: (id: string) => void;
  onArchive: (id: string) => void;
}

export function TaskItem({ task, onComplete, onEdit, onDelete, onArchive }: TaskItemProps) {
  const isCompleted = task.status === 'COMPLETED';
  const isArchived = task.status === 'ARCHIVED';
  const isRecurring = task.type === 'RECURRING';

  const getDueDate = () => {
    if (task.type === 'ONE_TIME') {
      return (task as OneTimeTask).dueDate;
    } else {
      return (task as RecurringTask).nextDueDate;
    }
  };

  const formatDueDate = (date: string) => {
    try {
      return format(new Date(date), 'MMM d, yyyy');
    } catch {
      return date;
    }
  };

  return (
    <Card className={cn(
      "p-4 transition-opacity",
      isArchived && "opacity-60"
    )}>
      <div className="flex items-start gap-3">
        <Checkbox
          checked={isCompleted}
          onCheckedChange={() => !isCompleted && onComplete(task.id)}
          disabled={isArchived || isCompleted}
          className="mt-1"
        />
        
        <div className="flex-1 min-w-0">
          <div className="flex items-start justify-between gap-2">
            <div className="flex-1">
              <h3 className={cn(
                "font-medium",
                isCompleted && "line-through text-muted-foreground"
              )}>
                {task.name}
              </h3>
              
              <div className="flex flex-wrap items-center gap-2 mt-2">
                {task.category && (
                  <Badge variant="secondary" className="text-xs">
                    {task.category}
                  </Badge>
                )}
                
                {isRecurring && (
                  <Badge variant="outline" className="text-xs">
                    <RefreshCw className="mr-1 h-3 w-3" />
                    {(task as RecurringTask).recurrencePattern}
                  </Badge>
                )}
                
                {getDueDate() && (
                  <span className="flex items-center text-xs text-muted-foreground">
                    <Calendar className="mr-1 h-3 w-3" />
                    {formatDueDate(getDueDate())}
                  </span>
                )}
                
                <Badge 
                  variant={
                    task.status === 'COMPLETED' ? 'default' : 
                    task.status === 'ARCHIVED' ? 'secondary' : 
                    'outline'
                  }
                  className="text-xs"
                >
                  {task.status}
                </Badge>
              </div>
            </div>
            
            <DropdownMenu>
              <DropdownMenuTrigger asChild>
                <Button variant="ghost" size="sm">
                  <span className="sr-only">Task options</span>
                  <svg
                    className="h-4 w-4"
                    fill="none"
                    stroke="currentColor"
                    viewBox="0 0 24 24"
                  >
                    <circle cx="12" cy="12" r="1" />
                    <circle cx="12" cy="5" r="1" />
                    <circle cx="12" cy="19" r="1" />
                  </svg>
                </Button>
              </DropdownMenuTrigger>
              <DropdownMenuContent align="end">
                {!isCompleted && !isArchived && (
                  <DropdownMenuItem onClick={() => onComplete(task.id)}>
                    <Check className="mr-2 h-4 w-4" />
                    Complete
                  </DropdownMenuItem>
                )}
                {!isArchived && (
                  <DropdownMenuItem onClick={() => onEdit(task)}>
                    <Edit className="mr-2 h-4 w-4" />
                    Edit
                  </DropdownMenuItem>
                )}
                {!isArchived && (
                  <DropdownMenuItem onClick={() => onArchive(task.id)}>
                    <Archive className="mr-2 h-4 w-4" />
                    Archive
                  </DropdownMenuItem>
                )}
                <DropdownMenuItem 
                  onClick={() => onDelete(task.id)}
                  className="text-destructive"
                >
                  <Trash className="mr-2 h-4 w-4" />
                  Delete
                </DropdownMenuItem>
              </DropdownMenuContent>
            </DropdownMenu>
          </div>
        </div>
      </div>
    </Card>
  );
}