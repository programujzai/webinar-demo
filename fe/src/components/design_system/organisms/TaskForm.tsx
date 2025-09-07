'use client';

import { useForm } from 'react-hook-form';
import { zodResolver } from '@hookform/resolvers/zod';
import { format } from 'date-fns';
import { CalendarIcon } from 'lucide-react';
import { Button } from '@/components/design_system/atoms/Button';
import { Input } from '@/components/design_system/atoms/Input';
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from '@/components/design_system/molecules/select';
import { Popover, PopoverContent, PopoverTrigger } from '@/components/design_system/molecules/popover';
import { Calendar } from '@/components/design_system/atoms/Calendar';
import { Form, FormControl, FormField, FormItem, FormLabel, FormMessage } from '@/components/design_system/molecules/form';
import { CreateOneTimeTaskInput, createOneTimeTaskSchema } from '@/lib/validations/task';
import { Task, UpdateTaskRequest } from '@/lib/types/task';
import { cn } from '@/lib/utils';

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
                  <SelectItem value="work">Work</SelectItem>
                  <SelectItem value="personal">Personal</SelectItem>
                  <SelectItem value="shopping">Shopping</SelectItem>
                  <SelectItem value="health">Health</SelectItem>
                  <SelectItem value="other">Other</SelectItem>
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
            <FormItem className="flex flex-col">
              <FormLabel>Due Date</FormLabel>
              <Popover>
                <PopoverTrigger asChild>
                  <FormControl>
                    <Button
                      variant="outline"
                      className={cn(
                        "w-full pl-3 text-left font-normal",
                        !field.value && "text-muted-foreground"
                      )}
                    >
                      {field.value ? (
                        format(new Date(field.value), "PPP")
                      ) : (
                        <span>Pick a date</span>
                      )}
                      <CalendarIcon className="ml-auto h-4 w-4 opacity-50" />
                    </Button>
                  </FormControl>
                </PopoverTrigger>
                <PopoverContent className="w-auto p-0" align="start">
                  <Calendar
                    mode="single"
                    selected={field.value ? new Date(field.value) : undefined}
                    onSelect={(date) => {
                      field.onChange(date ? format(date, 'yyyy-MM-dd') : '');
                    }}
                    disabled={(date) =>
                      date < new Date(new Date().setHours(0, 0, 0, 0))
                    }
                    initialFocus
                  />
                </PopoverContent>
              </Popover>
              <FormMessage />
            </FormItem>
          )}
        />

        <div className="flex gap-3 pt-4">
          <Button type="submit" disabled={isSubmitting}>
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