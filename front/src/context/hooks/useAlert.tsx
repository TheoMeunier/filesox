import { useContext } from 'react';
import { AlertContext } from '@context/modules/AlertContext.tsx';

export const useAlerts = () => {
  const context = useContext(AlertContext);

  if (!context) {
    throw new Error('useAlerts must be used within an AlertsProvider');
  }

  return context;
};
