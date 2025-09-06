-- Create main tasks table
CREATE TABLE tasks (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    display_order INTEGER NOT NULL,
    category VARCHAR(100),
    task_type VARCHAR(50) NOT NULL CHECK (task_type IN ('ONE_TIME', 'RECURRING')),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'COMPLETED', 'ARCHIVED')),
    created_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    deleted_at TIMESTAMP WITH TIME ZONE
);

-- Create one-time task specific data table
CREATE TABLE one_time_tasks (
    task_id UUID PRIMARY KEY REFERENCES tasks(id) ON DELETE CASCADE,
    due_date DATE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE
);

-- Create recurring task specific data table
CREATE TABLE recurring_tasks (
    task_id UUID PRIMARY KEY REFERENCES tasks(id) ON DELETE CASCADE,
    recurrence_pattern VARCHAR(50) NOT NULL CHECK (recurrence_pattern IN ('DAILY', 'WEEKLY')),
    day_of_week INTEGER CHECK (day_of_week BETWEEN 1 AND 7),
    start_date DATE NOT NULL,
    end_date DATE,
    next_due_date DATE NOT NULL,
    CONSTRAINT weekly_day_required CHECK (
        (recurrence_pattern != 'WEEKLY') OR (day_of_week IS NOT NULL)
    )
);

-- Create task completion history table
CREATE TABLE task_completions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    task_id UUID NOT NULL REFERENCES tasks(id) ON DELETE CASCADE,
    completed_date DATE NOT NULL,
    completed_at TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    UNIQUE(task_id, completed_date) -- Prevent duplicate completions for same day
);

-- Create indexes for performance
CREATE INDEX idx_tasks_status ON tasks(status) WHERE deleted_at IS NULL;
CREATE INDEX idx_tasks_category ON tasks(category) WHERE deleted_at IS NULL;
CREATE INDEX idx_tasks_display_order ON tasks(display_order) WHERE deleted_at IS NULL;
CREATE INDEX idx_one_time_tasks_due_date ON one_time_tasks(due_date);
CREATE INDEX idx_recurring_tasks_next_due_date ON recurring_tasks(next_due_date);
CREATE INDEX idx_task_completions_task_id ON task_completions(task_id);
CREATE INDEX idx_task_completions_completed_date ON task_completions(completed_date);