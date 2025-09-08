// Task-related constants
export const TASK_CATEGORIES = [
  'work',
  'personal',
  'shopping',
  'health',
  'other'
] as const;

export type TaskCategory = typeof TASK_CATEGORIES[number];

export const TASK_CATEGORY_LABELS: Record<TaskCategory, string> = {
  work: 'Work',
  personal: 'Personal',
  shopping: 'Shopping',
  health: 'Health',
  other: 'Other'
};

export const TASK_STATUSES = ['PENDING', 'COMPLETED', 'ARCHIVED'] as const;
export type TaskStatusConstant = typeof TASK_STATUSES[number];

export const TASK_STATUS_LABELS: Record<TaskStatusConstant, string> = {
  PENDING: 'Pending',
  COMPLETED: 'Completed',
  ARCHIVED: 'Archived'
};

export const TASK_TYPES = ['ONE_TIME', 'RECURRING'] as const;
export type TaskTypeConstant = typeof TASK_TYPES[number];

export const RECURRENCE_PATTERNS = ['DAILY', 'WEEKLY'] as const;
export type RecurrencePatternConstant = typeof RECURRENCE_PATTERNS[number];

export const RECURRENCE_PATTERN_LABELS: Record<RecurrencePatternConstant, string> = {
  DAILY: 'Daily',
  WEEKLY: 'Weekly'
};

export const DAYS_OF_WEEK = [
  'MONDAY',
  'TUESDAY',
  'WEDNESDAY',
  'THURSDAY',
  'FRIDAY',
  'SATURDAY',
  'SUNDAY'
] as const;
export type DayOfWeekConstant = typeof DAYS_OF_WEEK[number];

export const DAY_OF_WEEK_LABELS: Record<DayOfWeekConstant, string> = {
  MONDAY: 'Monday',
  TUESDAY: 'Tuesday',
  WEDNESDAY: 'Wednesday',
  THURSDAY: 'Thursday',
  FRIDAY: 'Friday',
  SATURDAY: 'Saturday',
  SUNDAY: 'Sunday'
};

// Task form defaults
export const DEFAULT_TASK_CATEGORY: TaskCategory = 'personal';
export const DEFAULT_TASK_TYPE: TaskTypeConstant = 'ONE_TIME';