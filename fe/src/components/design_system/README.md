# Design System - Atomic Design Architecture

## Overview

This design system follows the **Atomic Design** methodology created by Brad Frost. It organizes UI components into a hierarchical structure that reflects how interfaces are naturally constructed.

## Directory Structure

```
design_system/
├── atoms/          # Basic building blocks
├── molecules/      # Simple component combinations
├── organisms/      # Complex UI sections
├── templates/      # Page layouts (future)
├── pages/          # Specific page instances (future)
└── README.md       # This documentation
```

## Component Hierarchy

### 🔵 Atoms
The smallest, indivisible components. These are the basic building blocks of our interface.

**Location:** `design_system/atoms/`

| Component | Description | Usage |
|-----------|-------------|-------|
| `Button` | Base button component with variants | Primary actions, form submissions |
| `Input` | Text input field | Form fields, search boxes |
| `Label` | Form label component | Input labels, field descriptions |
| `Badge` | Status/category indicator | Tags, statuses, counts |
| `Checkbox` | Checkbox input | Selections, toggles |
| `Typography` | Text styling component | Headings, body text, captions |
| `Icon` | Icon wrapper component | UI icons with consistent sizing |
| `Skeleton` | Loading placeholder | Loading states |
| `Calendar` | Date picker calendar | Date selection |

**Example Usage:**
```tsx
import { Button, Input, Typography } from '@/components/design_system/atoms';

<Button variant="primary" onClick={handleClick}>
  Click Me
</Button>

<Input type="email" placeholder="Enter email" />

<Typography variant="h2">Welcome</Typography>
```

### 🟢 Molecules
Groups of atoms working together as a unit. These components are relatively simple and serve a single purpose.

**Location:** `design_system/molecules/`

| Component | Description | Composed Of |
|-----------|-------------|-------------|
| `Card` | Content container | Typography, spacing utilities |
| `Dialog` | Modal dialog | Button, Typography |
| `Select` | Dropdown selection | Button, list components |
| `Form` | Form wrapper with validation | Input, Label, error states |
| `FormInput` | Input with label and error | Input + Label atoms |
| `FormSelect` | Select with label and error | Select + Label atoms |
| `DatePicker` | Date selection with calendar | Calendar + Popover + Button |
| `Popover` | Floating content panel | Positioning utilities |
| `DropdownMenu` | Action menu | Button, list items |

**Example Usage:**
```tsx
import { Card, FormInput, DatePicker } from '@/components/design_system/molecules';

<Card>
  <FormInput 
    label="Task Name"
    error={errors.name}
    required
  />
  <DatePicker
    label="Due Date"
    value={date}
    onChange={setDate}
  />
</Card>
```

### 🔴 Organisms
Complex components that form distinct sections of an interface. These combine molecules and atoms to create feature-complete UI sections.

**Location:** `design_system/organisms/`

| Component | Description | Composed Of |
|-----------|-------------|-------------|
| `TaskList` | List of tasks with actions | TaskItem, EmptyState, Skeleton |
| `TaskItem` | Individual task display | Card, Badge, Checkbox, DropdownMenu |
| `TaskForm` | Task creation/edit form | FormInput, FormSelect, DatePicker |
| `TaskDialog` | Modal for task operations | Dialog, TaskForm |
| `TaskFilters` | Filter controls for tasks | Select, Badge, Button |
| `EmptyState` | No content placeholder | Typography, Icon, Button |

**Example Usage:**
```tsx
import { TaskList, TaskDialog } from '@/components/design_system/organisms';

<TaskList
  tasks={tasks}
  isLoading={isLoading}
  onComplete={handleComplete}
  onEdit={handleEdit}
/>

<TaskDialog
  open={dialogOpen}
  task={editingTask}
  onSubmit={handleSubmit}
/>
```

## Design Principles

### 1. **Single Responsibility**
Each component should do one thing well. Atoms handle basic UI rendering, molecules combine atoms for simple features, and organisms handle complex business logic.

### 2. **Composition Over Inheritance**
Build complex components by composing simpler ones rather than extending base components.

### 3. **Consistency**
- Use consistent naming conventions (PascalCase for components)
- Follow the same prop patterns across similar components
- Maintain consistent spacing and styling tokens

### 4. **Accessibility**
- All interactive elements must be keyboard accessible
- Use semantic HTML elements
- Include proper ARIA labels and descriptions
- Maintain proper focus management

### 5. **Type Safety**
All components are fully typed with TypeScript for better developer experience and fewer runtime errors.

## Component Development Guidelines

### Creating a New Atom
```tsx
// atoms/NewAtom.tsx
import { forwardRef } from 'react';
import { cn } from '@/lib/utils';

interface NewAtomProps extends React.HTMLAttributes<HTMLElement> {
  variant?: 'default' | 'special';
  // ... other props
}

export const NewAtom = forwardRef<HTMLElement, NewAtomProps>(
  ({ variant = 'default', className, ...props }, ref) => {
    return (
      <element
        ref={ref}
        className={cn(
          // base styles
          'base-classes',
          // variant styles
          variant === 'special' && 'special-classes',
          className
        )}
        {...props}
      />
    );
  }
);

NewAtom.displayName = 'NewAtom';
```

### Creating a New Molecule
```tsx
// molecules/NewMolecule.tsx
import { Atom1, Atom2 } from '../atoms';

interface NewMoleculeProps {
  // Molecule-specific props
}

export function NewMolecule({ ...props }: NewMoleculeProps) {
  return (
    <div className="molecule-wrapper">
      <Atom1 />
      <Atom2 />
    </div>
  );
}
```

### Creating a New Organism
```tsx
// organisms/NewOrganism.tsx
import { Molecule1, Molecule2 } from '../molecules';
import { Atom1 } from '../atoms';

interface NewOrganismProps {
  // Business logic props
  data: any[];
  onAction: (id: string) => void;
}

export function NewOrganism({ data, onAction }: NewOrganismProps) {
  // Business logic here
  const processedData = useMemo(() => {
    return data.map(transformData);
  }, [data]);

  return (
    <section className="organism-section">
      <Molecule1 />
      {processedData.map(item => (
        <Molecule2 key={item.id} {...item} />
      ))}
    </section>
  );
}
```

## Import Strategy

### Recommended Import Pattern
```tsx
// Import from specific level for clarity
import { Button, Input } from '@/components/design_system/atoms';
import { Card, FormInput } from '@/components/design_system/molecules';
import { TaskList } from '@/components/design_system/organisms';

// Or use the barrel export for convenience
import { 
  Button, 
  Input, 
  Card, 
  TaskList 
} from '@/components/design_system';
```

## Styling Guidelines

### TailwindCSS Classes
- Use Tailwind utility classes for styling
- Keep component-specific styles minimal
- Use the `cn()` utility for conditional classes

### Theme Tokens
- Colors: Use semantic color names (primary, secondary, destructive)
- Spacing: Use consistent spacing scale (2, 4, 6, 8, etc.)
- Typography: Use predefined text sizes and weights

## Testing Strategy

### Unit Tests
- Atoms: Test rendering and prop variations
- Molecules: Test composition and interaction between atoms
- Organisms: Test business logic and user interactions

### Integration Tests
- Test complete user flows using organisms
- Verify data flow between components
- Test error states and edge cases

## Migration from Legacy Components

If you have existing components outside the design system:

1. **Identify the atomic level** - Determine if it's an atom, molecule, or organism
2. **Refactor if needed** - Break down complex components into smaller pieces
3. **Move to appropriate directory** - Place in the correct atomic level
4. **Update imports** - Change all import paths to use the design system
5. **Add to index file** - Export from the appropriate index.ts file

## Future Enhancements

- [ ] **Templates**: Add page layout templates
- [ ] **Pages**: Add specific page implementations
- [ ] **Storybook**: Document components with interactive examples
- [ ] **Design Tokens**: Centralize design decisions
- [ ] **Component Variants**: Add more style variants
- [ ] **Animation Library**: Add consistent animations
- [ ] **Theme System**: Implement dark/light theme support

## Contributing

When contributing to the design system:

1. Follow the atomic design principles
2. Ensure components are accessible
3. Add TypeScript types
4. Update this documentation
5. Add unit tests
6. Consider reusability

## Resources

- [Atomic Design by Brad Frost](https://bradfrost.com/blog/post/atomic-web-design/)
- [Component Driven Development](https://www.componentdriven.org/)
- [React TypeScript Cheatsheet](https://react-typescript-cheatsheet.netlify.app/)
- [Tailwind CSS Documentation](https://tailwindcss.com/docs)