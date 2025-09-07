import { LucideIcon } from 'lucide-react';
import { cn } from '@/lib/utils';
import { forwardRef } from 'react';

export type IconSize = 'xs' | 'sm' | 'md' | 'lg' | 'xl';

interface IconProps {
  icon: LucideIcon;
  size?: IconSize;
  className?: string;
  color?: 'default' | 'muted' | 'primary' | 'error' | 'success';
}

const sizeStyles: Record<IconSize, string> = {
  xs: 'h-3 w-3',
  sm: 'h-4 w-4',
  md: 'h-5 w-5',
  lg: 'h-6 w-6',
  xl: 'h-8 w-8',
};

const colorStyles: Record<string, string> = {
  default: 'text-foreground',
  muted: 'text-muted-foreground',
  primary: 'text-primary',
  error: 'text-destructive',
  success: 'text-green-600',
};

export const Icon = forwardRef<SVGSVGElement, IconProps>(
  ({ icon: IconComponent, size = 'md', color = 'default', className }, ref) => {
    return (
      <IconComponent
        ref={ref}
        className={cn(
          sizeStyles[size],
          colorStyles[color],
          className
        )}
      />
    );
  }
);

Icon.displayName = 'Icon';