import { z } from 'zod';

export const createOneTimeTaskSchema = z.object({
  type: z.literal('ONE_TIME'),
  name: z.string().min(1, 'Task name is required'),
  dueDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Date must be in YYYY-MM-DD format'),
  category: z.string().optional(),
});

export const createRecurringTaskSchema = z.object({
  type: z.literal('RECURRING'),
  name: z.string().min(1, 'Task name is required'),
  recurrencePattern: z.enum(['DAILY', 'WEEKLY']),
  dayOfWeek: z.enum(['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']).optional(),
  startDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Date must be in YYYY-MM-DD format'),
  endDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/, 'Date must be in YYYY-MM-DD format').optional(),
  category: z.string().optional(),
});

export const createTaskSchema = z.discriminatedUnion('type', [
  createOneTimeTaskSchema,
  createRecurringTaskSchema,
]);

export const updateTaskSchema = z.object({
  name: z.string().min(1).optional(),
  category: z.string().optional(),
  dueDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).optional(),
  recurrencePattern: z.enum(['DAILY', 'WEEKLY']).optional(),
  dayOfWeek: z.enum(['MONDAY', 'TUESDAY', 'WEDNESDAY', 'THURSDAY', 'FRIDAY', 'SATURDAY', 'SUNDAY']).optional(),
  endDate: z.string().regex(/^\d{4}-\d{2}-\d{2}$/).optional(),
});

export type CreateOneTimeTaskInput = z.infer<typeof createOneTimeTaskSchema>;
export type CreateRecurringTaskInput = z.infer<typeof createRecurringTaskSchema>;
export type CreateTaskInput = z.infer<typeof createTaskSchema>;
export type UpdateTaskInput = z.infer<typeof updateTaskSchema>;