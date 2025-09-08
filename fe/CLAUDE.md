---
name: frontend-architect
description: Use this agent when you need to design the frontent architecture for a new feature or significant enhancement. This includes defining new components, adjustment the frontend, integrating with the backend.
model: inherit
color: blue
---

# Frontend Architecture Best Practices - Complete Documentation

## Table of Contents
1. [Architecture Overview](#architecture-overview)
2. [Project Structure](#project-structure)
3. [Component Architecture](#component-architecture)
4. [Data Flow Patterns](#data-flow-patterns)
5. [API Layer Architecture](#api-layer-architecture)
6. [State Management](#state-management)
7. [TypeScript Patterns](#typescript-patterns)
8. [Testing Strategy](#testing-strategy)
9. [Performance Optimizations](#performance-optimizations)
10. [Code Quality Standards](#code-quality-standards)

---

## 1. Architecture Overview

### Tech Stack
```typescript
{
  runtime: "Node.js v20.x",
  framework: "Next.js 14.2.3",
  language: "TypeScript",
  ui: {
    library: "React 18",
    components: "Material-UI (MUI)",
    styling: "Tailwind CSS",
    design: "Atomic Design Pattern"
  },
  state: {
    global: "Zustand",
    server: "TanStack Query (React Query)"
  },
  validation: "Zod",
  charts: "Recharts",
  auth: "Azure AD B2C",
  testing: {
    unit: "Jest",
    e2e: "Playwright"
  }
}
```

### Key Architectural Principles
1. **Layered Architecture**: Clear separation between presentation, business logic, and data layers
2. **Type Safety First**: Comprehensive TypeScript usage with Zod runtime validation
3. **Component Composition**: Atomic design methodology for scalable UI components
4. **Data Fetching Abstraction**: Custom hooks wrapping API clients
5. **Environment Configuration**: Centralized config management

---

## 2. Project Structure

### Directory Organization
```
src/
├── clients/                 # API client implementations (treat as 3rd party SDKs)
│   ├── backend-client.ts    # Main backend API client factory
│   ├── base/               # Base API client utilities
│   └── dashboard/          # Feature-specific clients
├── components/             # Legacy components (atomic design)
│   ├── atoms/             # Basic building blocks
│   ├── molecules/         # Simple component combinations
│   └── organisms/         # Complex UI sections
├── redesign-components/    # New component system (migration in progress)
│   ├── atoms/            # Core UI elements
│   ├── molecules/        # Composite components
│   ├── organisms/        # Page sections/templates
│   └── icons/           # Icon components
├── config/               # Configuration management
│   ├── index.ts         # Main config with Zod validation
│   ├── appConfig.ts     # Application settings
│   ├── featureFlags.ts  # Feature toggles
│   └── muiTheme.ts     # Theme configuration
├── domain/              # Domain models and types
│   ├── Customer.ts
│   ├── TransportTrip.ts
│   └── ...
├── dtos/                # Data Transfer Objects with Zod schemas
│   ├── backend-api-*.ts # Backend API DTOs
│   └── jwt-claims-scheme.ts
├── hooks/               # Custom React hooks
│   ├── useMe.ts        # User authentication
│   ├── useDashboard*.ts # Dashboard data hooks
│   └── ...
├── pages/               # Next.js pages and routing
│   ├── index.tsx
│   └── [dynamic]/      # Dynamic routes
├── utils/               # Utility functions
│   ├── react-query.ts
│   ├── logger.ts
│   └── ...
└── styles/             # Global styles
    └── globals.css
```

---

## 3. Component Architecture

### Atomic Design Pattern Implementation

#### 1. Atoms - Basic Building Blocks
```typescript
// src/redesign-components/atoms/Button.tsx
import cx from "classnames";
import { ComponentProps, forwardRef } from "react";

export const Button = forwardRef<
  HTMLButtonElement,
  ComponentProps<"button"> & {
    variant: ButtonVariant;
    colorScheme?: "default" | "error";
  }
>(({ variant, children, className, colorScheme = "default", ...props }, ref) => {
  return (
    <button
      ref={ref}
      className={cx(
        "flex flex-row items-center justify-center gap-2 rounded-lg",
        variant === "outline" && getOutlineStyles(colorScheme),
        variant === "filled" && getFilledStyles(),
        className
      )}
      style={{ minHeight: "2.5rem" }}
      {...props}
    >
      {children}
    </button>
  );
});

Button.displayName = "Button";
```

**Best Practices:**
- Use `forwardRef` for DOM element wrappers
- Extend native HTML props with `ComponentProps<"element">`
- Provide sensible defaults
- Use `cx` (classnames) for conditional styling
- Always set `displayName` for debugging

#### 2. Molecules - Composite Components
```typescript
// src/redesign-components/molecules/InfoBox.tsx
export const InfoBox = ({ 
  variant, 
  title, 
  children 
}: {
  variant: InfoBoxVariant;
  title: string;
  children: React.ReactNode;
}) => {
  return (
    <div className={getVariantStyles(variant)}>
      <Typography variant="text-md" weight="semibold">
        {title}
      </Typography>
      <div className="mt-2">{children}</div>
    </div>
  );
};
```

#### 3. Organisms - Complex Templates
```typescript
// src/redesign-components/organisms/TransportOperationsDashboardTemplate.tsx
export const TransportOperationsDashboardTemplate = ({
  role,
  onboardingStatus,
  sectorId,
  canGenerateReport,
  getReport,
  getDashboard,
  getCustomers,
  getTransportTrips,
  getDashboardHealth,
}: TransportOperationsDashboardProps) => {
  // State management
  const [filters, setFilters] = useState<FilterData>(defaultFilters);
  
  // Data fetching
  const { data: dashboard, isLoading } = useDashboard({
    filters,
    getDashboard,
  });
  
  // Computed values
  const insights = useMemo(() => 
    transformInsights(dashboard?.businessPerformance),
    [dashboard]
  );
  
  // Early returns for edge cases
  if (!featureFlags.redesignDashboardEnabled) {
    return null;
  }
  
  if (onboardingStatus === OnboardingStatus.IN_PROGRESS) {
    return <OnboardingStatusInfo sectorId={sectorId} />;
  }
  
  // Main render
  return (
    <Filters onChange={setFilters}>
      {/* Dashboard content */}
    </Filters>
  );
};
```

### Component Development Guidelines

1. **Component Responsibility**: Components should be primarily concerned with presentation
2. **Business Logic Separation**: Keep business logic in custom hooks
3. **Prop Types**: Use TypeScript interfaces over PropTypes
4. **Component Composition**: Favor composition over inheritance
5. **Styling Approach**: Use TailwindCSS classes with fallback to styled-components

---

## 4. Data Flow Patterns

### API Client → Hook → Component Pattern

```typescript
// Step 1: API Client (src/clients/backend-client.ts)
export const backendClientFactory = ({ apiClient }: { apiClient: ApiClient }) => {
  return {
    async getTransportTrips({
      jwt,
      pagination,
      filters,
      role,
      resourceId,
    }: GetTransportTripsParams): Promise<TransportTripsResponse> {
      const url = buildUrl(role, resourceId);
      const payload = validatePayload(filters);
      
      return apiClient({
        url: url.href,
        zodSchema: backendApiGetTransportTripsResponseDto,
        method: "POST",
        headers: { Authorization: `Bearer ${jwt}` },
        payload,
      });
    }
  };
};

// Step 2: Custom Hook (src/hooks/useTransportTrips.ts)
export const useTransportTrips = ({ 
  filters, 
  getTransportTrips 
}: UseTransportTripsParams) => {
  const jwt = useAuthStore(store => store.jwt);
  
  return useQuery({
    queryKey: ["transport-trips", filters],
    queryFn: () => {
      if (!jwt) throw new Error("Not authenticated");
      return getTransportTrips({ jwt, filters });
    },
    placeholderData: keepPreviousData,
    retry: retryConfig,
    staleTime: appConfig.backendClient.staleTime,
  });
};

// Step 3: Component Usage
const MyComponent = () => {
  const { data, isLoading, error } = useTransportTrips({
    filters: currentFilters,
    getTransportTrips: backendClient.getTransportTrips,
  });
  
  if (isLoading) return <Spinner />;
  if (error) return <ErrorMessage />;
  
  return <TripsList trips={data.transportTrips} />;
};
```

### Filter Management Pattern

```typescript
// Centralized filter context with type safety
export const Filters = ({ 
  children, 
  onChange, 
  onSubmit,
  defaultValues 
}: FiltersProps) => {
  const [filters, setFilters] = useState<FilterData>({
    dateFrom: defaultValues?.dateFrom ?? appConfig.defaultDateRange.from,
    dateTo: defaultValues?.dateTo ?? appConfig.defaultDateRange.to,
    customers: defaultValues?.customers,
    // ... other filters
  });
  
  // Individual setters for each filter type
  const setDateRange = useCallback((data: { from: Date; to: Date }) => {
    setFilters(prev => ({ ...prev, dateFrom: data.from, dateTo: data.to }));
  }, []);
  
  // Change tracking
  const changedFiltersCount = getNumberOfChangedFilters(filters, prevFilters);
  
  // Context provider for child components
  return (
    <FilterContext.Provider value={{ filters, setters }}>
      {children}
    </FilterContext.Provider>
  );
};
```

---

## 5. API Layer Architecture

### DTO Pattern with Zod Validation

```typescript
// src/dtos/backend-api-get-transport-dashboard-response-dto.ts
export const backendApiGetTransportDashboardResponseDto = z
  .object({
    data: z.array(
      z.object({
        title: z.string(),
        summary_title: z.string(),
        xAxisLabel: z.string().nullable(),
        yAxisLabel: z.string().nullable(),
        unit: z.string(),
        type: z.nativeEnum(ChartType),
        data: z.array(/* ... */)
      })
    ),
    summaries: z.array(/* ... */),
    is_data_available: z.boolean(),
  })
  .transform(({ data, summaries, is_data_available }) => ({
    // Transform snake_case to camelCase
    data: data.map(({ summary_title, ...rest }) => ({
      ...rest,
      summaryTitle: summary_title,
    })),
    isDataAvailable: is_data_available,
  }));
```

### API Client Factory Pattern

```typescript
// Dependency injection for testability
export const backendClientFactory = ({ 
  apiClient 
}: { 
  apiClient: ApiClient 
}) => {
  const buildUrl = (path: string, params?: Record<string, any>) => {
    const { backendApiUrl } = getConfig();
    return parseUrl({
      endpoint: `${backendApiUrl}${path}`,
      schema: paramsSchema,
      data: params,
    });
  };
  
  return {
    // Method grouping by feature
    // Transport operations
    getTransportTrips: /* ... */,
    getTransportReport: /* ... */,
    getTransportDashboard: /* ... */,
    
    // Hub operations
    getHubOverview: /* ... */,
    getHubReport: /* ... */,
    
    // Admin operations
    getAdminReports: /* ... */,
    inviteUser: /* ... */,
  };
};

// Singleton instance
export const backendClient = backendClientFactory({ apiClient });
```

---

## 6. State Management

### Zustand Store Pattern

```typescript
// src/stores/authStore.ts
interface AuthState {
  jwt: string | null;
  user: User | null;
  isAuthenticated: boolean;
  setAuth: (jwt: string, user: User) => void;
  clearAuth: () => void;
}

export const useAuthStore = create<AuthState>((set) => ({
  jwt: null,
  user: null,
  isAuthenticated: false,
  
  setAuth: (jwt, user) => set({ 
    jwt, 
    user, 
    isAuthenticated: true 
  }),
  
  clearAuth: () => set({ 
    jwt: null, 
    user: null, 
    isAuthenticated: false 
  }),
}));
```

### React Query Configuration

```typescript
// src/utils/react-query.ts
export const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      staleTime: 5 * 60 * 1000, // 5 minutes
      cacheTime: 10 * 60 * 1000, // 10 minutes
      retry: (failureCount, error) => {
        if (error.status === 401) return false;
        return failureCount < 3;
      },
      refetchOnWindowFocus: false,
    },
  },
});
```

---

## 7. TypeScript Patterns

### Domain Modeling

```typescript
// Strong typing with enums and branded types
export enum SystemRole {
  ADMIN = "ADMIN",
  BASIC = "BASIC",
  CUSTOMER = "CUSTOMER",
}

// Type predicates for narrowing
export const isAdmin = (role: SystemRole): role is SystemRole.ADMIN => 
  role === SystemRole.ADMIN;

// Utility types for common patterns
export type Nullable<T> = T | null;
export type Optional<T> = T | undefined;

// Discriminated unions for state management
export type LoadingState<T> = 
  | { status: "idle" }
  | { status: "loading" }
  | { status: "success"; data: T }
  | { status: "error"; error: Error };
```

### Generic Hook Patterns

```typescript
export function useApiCall<TParams, TResponse>(
  apiCall: (params: TParams) => Promise<TResponse>,
  options?: UseQueryOptions<TResponse>
) {
  return useQuery({
    ...options,
    queryFn: () => apiCall(params),
  });
}
```

---

## 8. Testing Strategy

### Unit Testing Components

```typescript
// Component.test.tsx
describe("Button", () => {
  it("should render with correct variant styles", () => {
    const { container } = render(
      <Button variant="filled" colorScheme="error">
        Click me
      </Button>
    );
    
    expect(container.firstChild).toHaveClass("bg-error-500");
  });
});
```

### Hook Testing

```typescript
// useTransportTrips.test.ts
const wrapper = ({ children }) => (
  <QueryClientProvider client={queryClient}>
    {children}
  </QueryClientProvider>
);

describe("useTransportTrips", () => {
  it("should fetch trips with filters", async () => {
    const { result } = renderHook(
      () => useTransportTrips({ filters }),
      { wrapper }
    );
    
    await waitFor(() => {
      expect(result.current.isSuccess).toBe(true);
    });
    
    expect(result.current.data).toHaveLength(10);
  });
});
```

---

## 9. Performance Optimizations

### Code Splitting

```typescript
// Dynamic imports for route-based splitting
const DashboardTemplate = dynamic(
  () => import("src/redesign-components/organisms/DashboardTemplate"),
  { 
    loading: () => <DashboardSkeleton />,
    ssr: false 
  }
);
```

### Memoization Patterns

```typescript
// UseMemo for expensive computations
const insights = useMemo(
  () => firstInsight?.items.map(transformItem) ?? [],
  [firstInsight]
);

// UseCallback for stable references
const handleFilterChange = useCallback(
  (filters: FilterData) => {
    setFilters(filters);
    refetch();
  },
  [refetch]
);
```

### Query Optimization

```typescript
// Placeholder data to prevent loading states
const { data, isPlaceholderData } = useQuery({
  queryKey: ["dashboard", filters],
  queryFn: fetchDashboard,
  placeholderData: keepPreviousData,
});

// Parallel queries
const results = useQueries({
  queries: [
    { queryKey: ["insights"], queryFn: fetchInsights },
    { queryKey: ["charts"], queryFn: fetchCharts },
  ],
});
```

---

## 10. Code Quality Standards

### Naming Conventions

```typescript
// Files: kebab-case
backend-api-get-transport-trips-dto.ts

// Components: PascalCase
TransportOperationsDashboard.tsx

// Hooks: camelCase with 'use' prefix
useTransportTrips.ts

// Constants: UPPER_SNAKE_CASE
const MAX_RETRY_ATTEMPTS = 3;

// Types/Interfaces: PascalCase
interface FilterData { }
type TransportTrip = { }
```

### Import Organization

```typescript
// 1. External libraries
import { useState, useEffect } from "react";
import { useQuery } from "@tanstack/react-query";

// 2. Internal absolute imports
import { backendClient } from "src/clients/backend-client";
import { Button } from "src/redesign-components/atoms/Button";

// 3. Relative imports
import { transformData } from "./utils";

// 4. Type imports
import type { FilterData } from "src/types";
```

### Error Handling

```typescript
// Consistent error boundaries
export class ErrorBoundary extends Component<Props, State> {
  static getDerivedStateFromError(error: Error): State {
    return { hasError: true, error };
  }
  
  componentDidCatch(error: Error, info: ErrorInfo) {
    logger.error("Component error:", { error, info });
  }
}

// API error handling
const handleApiError = (error: unknown): ApiError => {
  if (error instanceof ZodError) {
    return new ValidationError(error.issues);
  }
  if (error instanceof Error) {
    return new ApiError(error.message);
  }
  return new ApiError("Unknown error occurred");
};
```

### Configuration Management

```typescript
// Centralized environment config with validation
export function getConfig() {
  return configSchema.parse({
    environment: getEnvVariable("NEXT_PUBLIC_ENVIRONMENT"),
    backendApiUrl: getEnvVariable("NEXT_PUBLIC_BACKEND_API_URL"),
    azure: {
      b2c: {
        clientId: getEnvVariable("NEXT_PUBLIC_AZURE_AD_B2C_CLIENT_ID"),
        // ... other config
      }
    }
  });
}

// Never read process.env directly in components
// ❌ Bad
const apiUrl = process.env.NEXT_PUBLIC_API_URL;

// ✅ Good
import { getConfig } from "src/config";
const { backendApiUrl } = getConfig();
```

---

## Implementation Checklist for New Projects

### Initial Setup
- [ ] Configure TypeScript with strict mode
- [ ] Set up Next.js with app directory structure
- [ ] Install and configure Tailwind CSS
- [ ] Set up Zustand for state management
- [ ] Configure TanStack Query
- [ ] Set up Zod for validation
- [ ] Configure ESLint and Prettier
- [ ] Set up Husky for pre-commit hooks

### Architecture Implementation
- [ ] Create folder structure following atomic design
- [ ] Implement base API client with Zod validation
- [ ] Create authentication store
- [ ] Set up configuration management
- [ ] Implement error boundary components
- [ ] Create base hook patterns
- [ ] Set up feature flags system

### Component Development
- [ ] Create atom components library
- [ ] Build molecule components
- [ ] Develop organism templates
- [ ] Implement filter management system
- [ ] Create loading skeletons
- [ ] Build error states

### Data Layer
- [ ] Define domain models
- [ ] Create DTOs with Zod schemas
- [ ] Implement API client factory
- [ ] Create custom hooks for data fetching
- [ ] Set up caching strategies

### Testing & Quality
- [ ] Configure Jest and React Testing Library
- [ ] Set up Playwright for E2E tests
- [ ] Create test utilities and mocks
- [ ] Implement CI/CD pipeline
- [ ] Set up code coverage reporting

---

## Migration Strategy from Legacy Code

### Phase 1: Parallel Development
- Keep legacy components in `components/`
- Develop new components in `redesign-components/`
- Use feature flags to toggle between old and new

### Phase 2: Gradual Migration
- Migrate page by page
- Update imports progressively
- Maintain backward compatibility

### Phase 3: Cleanup
- Remove legacy components
- Update documentation
- Remove feature flags

---

## Conclusion

This architecture provides a robust, scalable foundation for React/Next.js applications with:
- **Type safety** through TypeScript and Zod
- **Maintainability** via atomic design and clear separation of concerns
- **Performance** with optimized data fetching and memoization
- **Developer experience** through consistent patterns and tooling
- **Testability** with isolated components and dependency injection

Following these patterns ensures code consistency, reduces bugs, and accelerates development across teams.