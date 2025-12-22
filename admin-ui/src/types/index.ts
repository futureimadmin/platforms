// Core Types for Nebula Admin UI

export interface Agent {
  agentId: string;
  name: string;
  type: AgentType;
  language: ProgrammingLanguage;
  capabilities: string[];
  tools: Tool[];
  configuration?: Record<string, any>;
  status?: AgentStatus;
  createdAt?: string;
  updatedAt?: string;
}

export interface Tool {
  toolId: string;
  name: string;
  type: ToolType;
  description: string;
  configuration?: Record<string, any>;
}

export interface ExecutionPlan {
  planId: string;
  version: string;
  metadata: ExecutionPlanMetadata;
  agents: Agent[];
  executionFlow: ExecutionFlow;
  globalContext?: Record<string, any>;
  humanInTheLoop?: HumanInTheLoop;
}

export interface ExecutionPlanMetadata {
  name: string;
  description: string;
  createdBy: string;
  createdAt: string;
  updatedAt?: string;
  tags?: string[];
}

export interface ExecutionFlow {
  type: ExecutionFlowType;
  steps: ExecutionStep[];
}

export interface ExecutionStep {
  stepId: string;
  name: string;
  description: string;
  type: string;
  agentId?: string;
  instruction?: string;
  action?: string;
  inputMappings?: Record<string, any>;
  outputMappings?: Record<string, any>;
  parameters?: Record<string, any>;
  enabled?: boolean;
  dependencies?: string[];
  timeout?: string;
  retryPolicy?: RetryPolicy;
  humanApprovalRequired?: boolean;
  condition?: string;
  errorHandling?: Record<string, any>;
  metadata?: Record<string, any>;
  flow?: ExecutionFlow;
}

export interface SequentialStep extends ExecutionStep {
  agentId: string;
  inputs?: Record<string, any>;
  outputs?: string[];
}

export interface ParallelStep extends ExecutionStep {
  parallelAgents: ParallelAgent[];
  waitForAll?: boolean;
}

export interface ParallelAgent {
  agentId: string;
  inputs?: Record<string, any>;
}

export interface ConditionalStep extends Omit<ExecutionStep, 'condition'> {
  condition: Condition;
  thenStep: ExecutionStep;
  elseStep?: ExecutionStep;
}

export interface LoopStep extends ExecutionStep {
  loopType: LoopType;
  body: ExecutionStep[];
  exitCondition: ExitCondition;
  iterationVariable?: string;
  collectionVariable?: string;
}

export interface Condition {
  expression: string;
  variables?: string[];
}

export interface ExitCondition {
  expression: string;
  checkAgentId: string;
  maxIterations?: number;
}

export interface RetryPolicy {
  maxAttempts: number;
  delay: string;
  backoffMultiplier?: number;
}

export interface HumanInTheLoop {
  enabled: boolean;
  approvalSteps?: string[];
  teamsIntegration?: TeamsIntegration;
}

export interface TeamsIntegration {
  enabled: boolean;
  meetingId?: string;
  webhookUrl?: string;
  speechToText?: boolean;
  textToSpeech?: boolean;
}

export interface ExecutionPlanStatus {
  planId: string;
  status: ExecutionStatus;
  totalSteps: number;
  completedSteps: number;
  totalAgents: number;
  activeAgents: number;
  currentStep?: string;
  context?: Record<string, any>;
  startedAt?: string;
  completedAt?: string;
  error?: string;
}

// Enums
export enum AgentType {
  CONTROL = 'control',
  DATA = 'data',
  TOOL = 'tool',
  HUMAN_INTERFACE = 'human-interface'
}

export enum ProgrammingLanguage {
  JAVA = 'java',
  PYTHON = 'python',
  JAVASCRIPT = 'javascript',
  TYPESCRIPT = 'typescript',
  GO = 'go',
  RUST = 'rust'
}

export enum ToolType {
  DATABASE = 'database',
  API = 'api',
  FILE = 'file',
  NOTIFICATION = 'notification',
  INTEGRATION = 'integration'
}

export enum ExecutionFlowType {
  SEQUENTIAL = 'sequential',
  PARALLEL = 'parallel',
  CONDITIONAL = 'conditional',
  LOOP = 'loop',
  HYBRID = 'hybrid'
}

export enum LoopType {
  WHILE = 'while',
  FOR = 'for',
  FOREACH = 'foreach'
}

export enum ExecutionStatus {
  PENDING = 'pending',
  RUNNING = 'running',
  COMPLETED = 'completed',
  FAILED = 'failed',
  CANCELLED = 'cancelled',
  PAUSED = 'paused'
}

export enum AgentStatus {
  IDLE = 'idle',
  RUNNING = 'running',
  COMPLETED = 'completed',
  FAILED = 'failed',
  STOPPED = 'stopped'
}

// API Response Types
export interface ApiResponse<T = any> {
  success: boolean;
  message: string;
  data?: T;
  error?: string;
}

export interface ProcessPromptRequest {
  prompt: string;
  context?: Record<string, any>;
}

export interface ProcessPromptResponse {
  success: boolean;
  message: string;
  result?: string;
  planId?: string;
}

export interface ApprovalRequest {
  approved: boolean;
  feedback?: string;
}

export interface TeamsJoinRequest {
  meetingId: string;
}

export interface SpeechRequest {
  speechText: string;
}

export interface SpeechResponse {
  success: boolean;
  message: string;
  response?: string;
}

// UI State Types
export interface AppState {
  user: User | null;
  theme: ThemeMode;
  sidebarOpen: boolean;
  notifications: Notification[];
}

export interface User {
  id: string;
  name: string;
  email: string;
  role: UserRole;
  avatar?: string;
}

export enum UserRole {
  ADMIN = 'admin',
  USER = 'user',
  VIEWER = 'viewer'
}

export enum ThemeMode {
  LIGHT = 'light',
  DARK = 'dark'
}

export interface Notification {
  id: string;
  type: NotificationType;
  title: string;
  message: string;
  timestamp: string;
  read: boolean;
  actions?: NotificationAction[];
}

export enum NotificationType {
  INFO = 'info',
  SUCCESS = 'success',
  WARNING = 'warning',
  ERROR = 'error'
}

export interface NotificationAction {
  label: string;
  action: () => void;
}

// Form Types
export interface CreateAgentForm {
  name: string;
  type: AgentType;
  language: ProgrammingLanguage;
  capabilities: string[];
  description?: string;
  configuration?: Record<string, any>;
}

export interface CreateExecutionPlanForm {
  name: string;
  description: string;
  agents: string[];
  executionFlowType: ExecutionFlowType;
  humanInTheLoopEnabled: boolean;
  teamsIntegrationEnabled: boolean;
}

// Chart Data Types
export interface ChartData {
  labels: string[];
  datasets: ChartDataset[];
}

export interface ChartDataset {
  label: string;
  data: number[];
  backgroundColor?: string | string[];
  borderColor?: string | string[];
  borderWidth?: number;
}

// Dashboard Types
export interface DashboardStats {
  totalAgents: number;
  activeExecutions: number;
  completedExecutions: number;
  failedExecutions: number;
  totalExecutionTime: number;
  averageExecutionTime: number;
}

export interface ExecutionMetrics {
  executionsPerDay: ChartData;
  agentUtilization: ChartData;
  executionDuration: ChartData;
  errorRates: ChartData;
}

export interface ExecutionFlowResponse {
  planId: string;
  planName: string;
  executionFlow: ExecutionFlow;
  agents: Agent[];
}

export interface ExecutionStepInput {
  instruction: string;
  configuration: Record<string, any>;
  externalApiUrl?: string;
  apiKey?: string;
  clientId?: string;
  clientSecret?: string;
}