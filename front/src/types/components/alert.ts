import { ReactNode } from 'react';

export interface AlertsProviderProps {
  children: ReactNode;
}

export interface AlertsContextProps {
  alerts: {
    type: string;
    message: string;
  }[];
  setAlerts: (type: string, message: string) => void;
  deleteAlert: (index: number) => void;
}
