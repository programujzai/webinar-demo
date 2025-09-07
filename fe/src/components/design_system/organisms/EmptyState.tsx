import { Plus } from 'lucide-react';
import { Button } from '@/components/design_system/atoms/button';

interface EmptyStateProps {
  onCreateClick: () => void;
}

export function EmptyState({ onCreateClick }: EmptyStateProps) {
  return (
    <div className="flex flex-col items-center justify-center py-12 px-4 text-center">
      <div className="rounded-full bg-muted p-3 mb-4">
        <Plus className="h-6 w-6 text-muted-foreground" />
      </div>
      <h3 className="text-lg font-semibold mb-2">No tasks yet</h3>
      <p className="text-muted-foreground mb-6 max-w-sm">
        Get started by creating your first task. You can organize your tasks by categories and set due dates.
      </p>
      <Button onClick={onCreateClick}>
        <Plus className="mr-2 h-4 w-4" />
        Create your first task
      </Button>
    </div>
  );
}