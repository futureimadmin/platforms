import React, { useState } from 'react';
import {
  Box,
  Card,
  CardContent,
  Typography,
  Stepper,
  Step,
  StepLabel,
  StepContent,
  TextField,
  Grid,
  Chip,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Button,
  IconButton,
  Tooltip,
  Paper,
  Divider,
} from '@mui/material';
import {
  ExpandMore as ExpandMoreIcon,
  PlayArrow as PlayArrowIcon,
  Info as InfoIcon,
  Settings as SettingsIcon,
  Api as ApiIcon,
  Security as SecurityIcon,
} from '@mui/icons-material';
import { ExecutionFlowResponse, ExecutionStep, ExecutionStepInput, Agent } from '../../types';

interface ExecutionFlowViewerProps {
  flowData: ExecutionFlowResponse;
  onExecuteStep?: (stepId: string, inputs: ExecutionStepInput) => void;
  onExecuteFlow?: (planId: string) => void;
}

const ExecutionFlowViewer: React.FC<ExecutionFlowViewerProps> = ({
  flowData,
  onExecuteStep,
  onExecuteFlow,
}) => {
  const [activeStep, setActiveStep] = useState(0);
  const [stepInputs, setStepInputs] = useState<Record<string, ExecutionStepInput>>({});

  const handleStepInputChange = (stepId: string, field: keyof ExecutionStepInput, value: any) => {
    setStepInputs(prev => ({
      ...prev,
      [stepId]: {
        ...prev[stepId],
        [field]: value,
      },
    }));
  };

  const getStepInputs = (stepId: string): ExecutionStepInput => {
    return stepInputs[stepId] || {
      instruction: '',
      configuration: {},
      externalApiUrl: '',
      apiKey: '',
      clientId: '',
      clientSecret: '',
    };
  };

  const getAgentById = (agentId: string): Agent | undefined => {
    return flowData.agents.find(agent => agent.agentId === agentId);
  };

  const getStepTypeColor = (type: string) => {
    switch (type.toLowerCase()) {
      case 'sequential':
        return 'primary';
      case 'parallel':
        return 'secondary';
      case 'conditional':
        return 'warning';
      case 'loop':
        return 'info';
      default:
        return 'default';
    }
  };

  const renderStepInputForm = (step: ExecutionStep) => {
    const inputs = getStepInputs(step.stepId);
    const agent = step.agentId ? getAgentById(step.agentId) : null;

    return (
      <Box sx={{ mt: 2 }}>
        <Grid container spacing={2}>
          {/* Instruction Field */}
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Instruction"
              multiline
              rows={3}
              value={inputs.instruction || step.instruction || ''}
              onChange={(e) => handleStepInputChange(step.stepId, 'instruction', e.target.value)}
              placeholder="Enter detailed instructions for this step..."
              variant="outlined"
              InputProps={{
                startAdornment: <InfoIcon sx={{ mr: 1, color: 'text.secondary' }} />,
              }}
            />
          </Grid>

          {/* Configuration Field */}
          <Grid item xs={12}>
            <TextField
              fullWidth
              label="Configuration (JSON)"
              multiline
              rows={2}
              value={JSON.stringify(inputs.configuration || step.parameters || {}, null, 2)}
              onChange={(e) => {
                try {
                  const config = JSON.parse(e.target.value);
                  handleStepInputChange(step.stepId, 'configuration', config);
                } catch {
                  // Invalid JSON, keep the string value for now
                }
              }}
              placeholder='{"key": "value"}'
              variant="outlined"
              InputProps={{
                startAdornment: <SettingsIcon sx={{ mr: 1, color: 'text.secondary' }} />,
              }}
            />
          </Grid>

          {/* External API URL */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="External API URL"
              value={inputs.externalApiUrl || ''}
              onChange={(e) => handleStepInputChange(step.stepId, 'externalApiUrl', e.target.value)}
              placeholder="https://api.example.com/v1"
              variant="outlined"
              InputProps={{
                startAdornment: <ApiIcon sx={{ mr: 1, color: 'text.secondary' }} />,
              }}
            />
          </Grid>

          {/* API Key */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="API Key"
              type="password"
              value={inputs.apiKey || ''}
              onChange={(e) => handleStepInputChange(step.stepId, 'apiKey', e.target.value)}
              placeholder="Enter API key..."
              variant="outlined"
              InputProps={{
                startAdornment: <SecurityIcon sx={{ mr: 1, color: 'text.secondary' }} />,
              }}
            />
          </Grid>

          {/* Client ID */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Client ID"
              value={inputs.clientId || ''}
              onChange={(e) => handleStepInputChange(step.stepId, 'clientId', e.target.value)}
              placeholder="Enter client ID..."
              variant="outlined"
            />
          </Grid>

          {/* Client Secret */}
          <Grid item xs={12} md={6}>
            <TextField
              fullWidth
              label="Client Secret"
              type="password"
              value={inputs.clientSecret || ''}
              onChange={(e) => handleStepInputChange(step.stepId, 'clientSecret', e.target.value)}
              placeholder="Enter client secret..."
              variant="outlined"
            />
          </Grid>

          {/* Agent Information */}
          {agent && (
            <Grid item xs={12}>
              <Paper sx={{ p: 2, bgcolor: 'background.default' }}>
                <Typography variant="subtitle2" gutterBottom>
                  Agent Information
                </Typography>
                <Box sx={{ display: 'flex', gap: 1, flexWrap: 'wrap' }}>
                  <Chip label={`Agent: ${agent.name}`} size="small" />
                  <Chip label={`Type: ${agent.type}`} size="small" color="primary" />
                  <Chip label={`Language: ${agent.language}`} size="small" color="secondary" />
                </Box>
                {agent.capabilities && agent.capabilities.length > 0 && (
                  <Box sx={{ mt: 1 }}>
                    <Typography variant="caption" color="text.secondary">
                      Capabilities:
                    </Typography>
                    <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap', mt: 0.5 }}>
                      {agent.capabilities.map((capability, index) => (
                        <Chip key={index} label={capability} size="small" variant="outlined" />
                      ))}
                    </Box>
                  </Box>
                )}
              </Paper>
            </Grid>
          )}

          {/* Execute Step Button */}
          <Grid item xs={12}>
            <Box sx={{ display: 'flex', gap: 1, justifyContent: 'flex-end' }}>
              <Button
                variant="contained"
                startIcon={<PlayArrowIcon />}
                onClick={() => onExecuteStep?.(step.stepId, inputs)}
                disabled={!inputs.instruction}
              >
                Execute Step
              </Button>
            </Box>
          </Grid>
        </Grid>
      </Box>
    );
  };

  const renderStep = (step: ExecutionStep, index: number) => {
    return (
      <Step key={step.stepId}>
        <StepLabel>
          <Box sx={{ display: 'flex', alignItems: 'center', gap: 1 }}>
            <Typography variant="subtitle1">
              {step.name || `Step ${index + 1}`}
            </Typography>
            <Chip
              label={step.type}
              size="small"
              color={getStepTypeColor(step.type) as any}
              variant="outlined"
            />
            {step.agentId && (
              <Chip
                label={step.agentId}
                size="small"
                color="default"
                variant="filled"
              />
            )}
          </Box>
        </StepLabel>
        <StepContent>
          <Typography variant="body2" color="text.secondary" sx={{ mb: 2 }}>
            {step.description || 'No description provided'}
          </Typography>

          {/* Step Details */}
          <Accordion>
            <AccordionSummary expandIcon={<ExpandMoreIcon />}>
              <Typography variant="subtitle2">Step Configuration</Typography>
            </AccordionSummary>
            <AccordionDetails>
              {renderStepInputForm(step)}
            </AccordionDetails>
          </Accordion>

          {/* Step Navigation */}
          <Box sx={{ mt: 2, display: 'flex', gap: 1 }}>
            <Button
              disabled={index === 0}
              onClick={() => setActiveStep(index - 1)}
            >
              Back
            </Button>
            <Button
              variant="contained"
              onClick={() => setActiveStep(index + 1)}
              disabled={index === flowData.executionFlow.steps.length - 1}
            >
              Next
            </Button>
          </Box>
        </StepContent>
      </Step>
    );
  };

  return (
    <Card>
      <CardContent>
        <Box sx={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center', mb: 3 }}>
          <Box>
            <Typography variant="h5" gutterBottom>
              {flowData.planName}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              Plan ID: {flowData.planId}
            </Typography>
          </Box>
          <Button
            variant="contained"
            color="primary"
            startIcon={<PlayArrowIcon />}
            onClick={() => onExecuteFlow?.(flowData.planId)}
            size="large"
          >
            Execute Entire Flow
          </Button>
        </Box>

        <Divider sx={{ mb: 3 }} />

        {/* Flow Type Information */}
        <Box sx={{ mb: 3 }}>
          <Typography variant="h6" gutterBottom>
            Execution Flow
          </Typography>
          <Box sx={{ display: 'flex', gap: 1, alignItems: 'center' }}>
            <Chip
              label={`Type: ${flowData.executionFlow.type}`}
              color={getStepTypeColor(flowData.executionFlow.type) as any}
              variant="filled"
            />
            <Chip
              label={`${flowData.executionFlow.steps.length} Steps`}
              color="default"
              variant="outlined"
            />
            <Chip
              label={`${flowData.agents.length} Agents`}
              color="default"
              variant="outlined"
            />
          </Box>
        </Box>

        {/* Steps Stepper */}
        <Stepper activeStep={activeStep} orientation="vertical">
          {flowData.executionFlow.steps.map((step, index) => renderStep(step, index))}
        </Stepper>

        {/* Agents Summary */}
        <Box sx={{ mt: 4 }}>
          <Typography variant="h6" gutterBottom>
            Available Agents
          </Typography>
          <Grid container spacing={2}>
            {flowData.agents.map((agent) => (
              <Grid item xs={12} md={6} lg={4} key={agent.agentId}>
                <Paper sx={{ p: 2 }}>
                  <Typography variant="subtitle1" gutterBottom>
                    {agent.name}
                  </Typography>
                  <Box sx={{ display: 'flex', gap: 0.5, flexWrap: 'wrap', mb: 1 }}>
                    <Chip label={agent.type} size="small" color="primary" />
                    <Chip label={agent.language} size="small" color="secondary" />
                  </Box>
                  <Typography variant="caption" color="text.secondary">
                    ID: {agent.agentId}
                  </Typography>
                </Paper>
              </Grid>
            ))}
          </Grid>
        </Box>
      </CardContent>
    </Card>
  );
};

export default ExecutionFlowViewer;