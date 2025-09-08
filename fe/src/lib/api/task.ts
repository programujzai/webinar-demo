export interface ApiError {
    status: number;
    error: string;
    message: string;
    code: string;
    timestamp: string;
    errors?: Array<{
        field: string;
        message?: string;
    }>;
}