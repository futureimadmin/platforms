import React from 'react';
import { Typography, Box, Card, CardContent } from '@mui/material';

const Configuration: React.FC = () => {
  return (
    <Box>
      <Typography variant="h4" component="h1" gutterBottom>
        Configuration
      </Typography>
      <Card>
        <CardContent>
          <Typography variant="body1">
            System configuration functionality will be implemented here.
            This will include LLM settings, database connections, and API configurations.
          </Typography>
        </CardContent>
      </Card>
    </Box>
  );
};

export default Configuration;