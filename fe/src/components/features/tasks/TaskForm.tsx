'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { format } from 'date-fns';
import { Button } from '@/components/design_system/atoms/Button';
import { Input } from '@/components/design_system/atoms/Input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/design_system/molecules/select';
import { DatePicker } from '@/components/design_system/molecules/DatePicker';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/design_system/molecules/form';
import { CreateOneTimeTaskInput, createOneTimeTaskSchema } from './validations';
import { Task, UpdateTaskRequest } from './types';
import { TASK_CATEGORIES, TASK_CATEGORY_LABELS } from './constants';

interface TaskFormProps {
  task?: Task;
  onSubmit: (data: CreateOneTimeTaskInput | UpdateTaskRequest) => void;
  onCancel: () => void;
  isSubmitting?: boolean;
}

export function TaskForm({ task, onSubmit, onCancel, isSubmitting }: TaskFormProps) {
  const isEditing = !!task;
  
  const form = useForm<CreateOneTimeTaskInput>({
    resolver: zodResolver(createOneTimeTaskSchema),
    defaultValues: {
      type: 'ONE_TIME',
      name: task?.name || '',
      category: task?.category || '',
      dueDate: task && task.type === 'ONE_TIME' ? task.dueDate : format(new Date(), 'yyyy-MM-dd'),
    },
  });

  const handleSubmit = (data: CreateOneTimeTaskInput) => {
    if (isEditing) {
      // For editing, only send changed fields
      const updateData: UpdateTaskRequest = {
        name: data.name,
        category: data.category,
        dueDate: data.dueDate,
      };
      onSubmit(updateData);
    } else {
      onSubmit(data);
    }
  };

  return (
    <Form {...form}>
      <form onSubmit={form.handleSubmit(handleSubmit)} className="space-y-4">
        <FormField
          control={form.control}
          name="name"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Task Name</FormLabel>
              <FormControl>
                <Input placeholder="Enter task name" {...field} />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="category"
          render={({ field }) => (
            <FormItem>
              <FormLabel>Category</FormLabel>
              <Select onValueChange={field.onChange} defaultValue={field.value}>
                <FormControl>
                  <SelectTrigger>
                    <SelectValue placeholder="Select a category" />
                  </SelectTrigger>
                </FormControl>
                <SelectContent>
                  {TASK_CATEGORIES.map((category) => (
                    <SelectItem key={category} value={category}>
                      {TASK_CATEGORY_LABELS[category]}
                    </SelectItem>
                  ))}
                </SelectContent>
              </Select>
              <FormMessage />
            </FormItem>
          )}
        />

        <FormField
          control={form.control}
          name="dueDate"
          render={({ field }) => (
            <FormItem>
              <FormControl>
                <DatePicker
                  label="Due Date"
                  value={field.value ? new Date(field.value) : undefined}
                  onChange={(date) => {
                    field.onChange(date ? format(date, 'yyyy-MM-dd') : '');
                  }}
                  minDate={new Date(new Date().setHours(0, 0, 0, 0))}
                  placeholder="Pick a date"
                />
              </FormControl>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex gap-3 pt-4">
          <Button type="submit" variant="default" disabled={isSubmitting}>
            {isSubmitting ? 'Saving...' : isEditing ? 'Update Task' : 'Create Task'}
          </Button>
          <Button type="button" variant="outline" onClick={onCancel}>
            Cancel
          </Button>
        </div>
      </form>
    </Form>
  );
}