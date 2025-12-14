import React from 'react';
import { Typography, Box, Card, CardContent } from '@mui/material';

const AgentManagement: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Agent Management
      </Typography>
      <Card>
        <CardContent>
          <Typography variant="body1">
            Agent management functionality will be implemented here.
            This will include creating, editing, and monitoring agents.
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default AgentManagement;