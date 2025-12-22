import axios, { AxiosInstance, AxiosResponse } from 'axios';
import { toast } from 'react-toastify';

import {
  Agent,
  ExecutionPlan,
  ExecutionPlanStatus,
  ProcessPromptRequest,
  ProcessPromptResponse,
  ApprovalRequest,
  TeamsJoinRequest,
  SpeechRequest,
  SpeechResponse,
  ApiResponse,
  DashboardStats,
  ExecutionMetrics,

} from '../types';

class ApiService {
  private api: AxiosInstance;

  constructor() {
    this.api = axios.create({
      baseURL: process.env.REACT_APP_API_BASE_URL || '/nebula-control-plane/api/v1',
      timeout: 0,
      headers: {
        'Content-Type': 'application/json',
      },
    });

    this.setupInterceptors();
  }

  private setupInterceptors() {
    // Request interceptor
    this.api.interceptors.request.use(
      (config) => {
        // Add auth token if available
        const token = localStorage.getItem('authToken');
        if (token) {
          config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
      },
      (error) => {
        return Promise.reject(error);
      }
    );

    // Response interceptor
    this.api.interceptors.response.use(
      (response: AxiosResponse) => {
        return response;
      },
      (error) => {
        if (error.response?.status === 401) {
          // Handle unauthorized access
          localStorage.removeItem('authToken');
          window.location.href = '/login';
        } else if (error.response?.status >= 500) {
          toast.error('Server error occurred. Please try again later.');
        } else if (error.response?.data?.message) {
          toast.error(error.response.data.message);
        } else {
          toast.error('An unexpected error occurred.');
        }
        return Promise.reject(error);
      }
    );
  }

  // Master Agent API
  async processPrompt(request: ProcessPromptRequest): Promise<ProcessPromptResponse> {
    const response = await this.api.post<ProcessPromptResponse>('/master-agent/process', request, {
      timeout:0
    });
    return response.data;
  }

  async getExecutionStatus(planId: string): Promise<ExecutionPlanStatus> {
    const response = await this.api.get<ExecutionPlanStatus>(`/master-agent/execution/${planId}/status`);
    return response.data;
  }

  async getAgentsForPlan(planId: string): Promise<Agent[]> {
    const response = await this.api.get<Agent[]>(`/master-agent/execution/${planId}/agents`);
    return response.data;
  }

  async stopExecution(planId: string): Promise<ApiResponse> {
    const response = await this.api.post<ApiResponse>(`/master-agent/execution/${planId}/stop`);
    return response.data;
  }

  async handleHumanApproval(planId: string, stepId: string, request: ApprovalRequest): Promise<ApiResponse> {
    const response = await this.api.post<ApiResponse>(
      `/master-agent/execution/${planId}/steps/${stepId}/approval`,
      request
    );
    return response.data;
  }

  async joinTeamsMeeting(planId: string, request: TeamsJoinRequest): Promise<ApiResponse> {
    const response = await this.api.post<ApiResponse>(`/master-agent/execution/${planId}/teams/join`, request);
    return response.data;
  }

  async processSpeechInput(planId: string, request: SpeechRequest): Promise<SpeechResponse> {
    const response = await this.api.post<SpeechResponse>(`/master-agent/execution/${planId}/teams/speech`, request);
    return response.data;
  }

  async healthCheck(): Promise<ApiResponse> {
    const response = await this.api.get<ApiResponse>('/master-agent/health');
    return response.data;
  }

  // Agent Management API
  async getAgents(): Promise<Agent[]> {
    const response = await this.api.get<Agent[]>('/agents');
    return response.data;
  }

  async getAgent(agentId: string): Promise<Agent> {
    const response = await this.api.get<Agent>(`/agents/${agentId}`);
    return response.data;
  }

  async createAgent(agent: Omit<Agent, 'agentId' | 'createdAt' | 'updatedAt'>): Promise<Agent> {
    const response = await this.api.post<Agent>('/agents', agent);
    return response.data;
  }

  async updateAgent(agentId: string, agent: Partial<Agent>): Promise<Agent> {
    const response = await this.api.put<Agent>(`/agents/${agentId}`, agent);
    return response.data;
  }

  async deleteAgent(agentId: string): Promise<ApiResponse> {
    const response = await this.api.delete<ApiResponse>(`/agents/${agentId}`);
    return response.data;
  }

  async startAgent(agentId: string): Promise<ApiResponse> {
    const response = await this.api.post<ApiResponse>(`/agents/${agentId}/start`);
    return response.data;
  }

  async stopAgent(agentId: string): Promise<ApiResponse> {
    const response = await this.api.post<ApiResponse>(`/agents/${agentId}/stop`);
    return response.data;
  }

  // Execution Plan API
  async getExecutionPlans(): Promise<ExecutionPlan[]> {
    const response = await this.api.get<ExecutionPlan[]>('/execution-plans');
    return response.data;
  }

  async getExecutionPlan(planId: string): Promise<ExecutionPlan> {
    const response = await this.api.get<ExecutionPlan>(`/execution-plans/${planId}`);
    return response.data;
  }

  async createExecutionPlan(plan: Omit<ExecutionPlan, 'planId' | 'metadata'>): Promise<ExecutionPlan> {
    const response = await this.api.post<ExecutionPlan>('/execution-plans', plan);
    return response.data;
  }

  async updateExecutionPlan(planId: string, plan: Partial<ExecutionPlan>): Promise<ExecutionPlan> {
    const response = await this.api.put<ExecutionPlan>(`/execution-plans/${planId}`, plan);
    return response.data;
  }

  async deleteExecutionPlan(planId: string): Promise<ApiResponse> {
    const response = await this.api.delete<ApiResponse>(`/execution-plans/${planId}`);
    return response.data;
  }

  async executeExecutionPlan(planId: string): Promise<ApiResponse> {
    const response = await this.api.post<ApiResponse>(`/execution-plans/${planId}/execute`);
    return response.data;
  }



  // Monitoring API
  async getActiveExecutions(): Promise<ExecutionPlanStatus[]> {
    const response = await this.api.get<ExecutionPlanStatus[]>('/monitoring/active-executions');
    return response.data;
  }

  async getExecutionHistory(): Promise<ExecutionPlanStatus[]> {
    const response = await this.api.get<ExecutionPlanStatus[]>('/monitoring/execution-history');
    return response.data;
  }

  async getExecutionLogs(planId: string): Promise<string[]> {
    const response = await this.api.get<string[]>(`/monitoring/execution/${planId}/logs`);
    return response.data;
  }

  // Analytics API
  async getDashboardStats(): Promise<DashboardStats> {
    const response = await this.api.get<DashboardStats>('/analytics/dashboard-stats');
    return response.data;
  }

  async getExecutionMetrics(timeRange: string = '7d'): Promise<ExecutionMetrics> {
    const response = await this.api.get<ExecutionMetrics>(`/analytics/execution-metrics?timeRange=${timeRange}`);
    return response.data;
  }

  // Configuration API
  async getConfiguration(): Promise<Record<string, any>> {
    const response = await this.api.get<Record<string, any>>('/configuration');
    return response.data;
  }

  async updateConfiguration(config: Record<string, any>): Promise<ApiResponse> {
    const response = await this.api.put<ApiResponse>('/configuration', config);
    return response.data;
  }

  async testConfiguration(): Promise<ApiResponse> {
    const response = await this.api.post<ApiResponse>('/configuration/test');
    return response.data;
  }

  // Teams Integration API
  async getTeamsConfiguration(): Promise<Record<string, any>> {
    const response = await this.api.get<Record<string, any>>('/teams/configuration');
    return response.data;
  }

  async updateTeamsConfiguration(config: Record<string, any>): Promise<ApiResponse> {
    const response = await this.api.put<ApiResponse>('/teams/configuration', config);
    return response.data;
  }

  async testTeamsConnection(): Promise<ApiResponse> {
    const response = await this.api.post<ApiResponse>('/teams/test-connection');
    return response.data;
  }

  async getActiveMeetings(): Promise<any[]> {
    const response = await this.api.get<any[]>('/teams/active-meetings');
    return response.data;
  }

  // File Upload API
  async uploadFile(file: File, type: string): Promise<{ url: string; filename: string }> {
    const formData = new FormData();
    formData.append('file', file);
    formData.append('type', type);

    const response = await this.api.post<{ url: string; filename: string }>('/files/upload', formData, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
    });
    return response.data;
  }

  // WebSocket connection for real-time updates
  createWebSocketConnection(planId?: string): WebSocket {
    const wsUrl = process.env.REACT_APP_WS_URL || 'ws://localhost:8080/nebula-control-plane/ws';
    const url = planId ? `${wsUrl}?planId=${planId}` : wsUrl;
    return new WebSocket(url);
  }
}

export const apiService = new ApiService();
export default apiService;