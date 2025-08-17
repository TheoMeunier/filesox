import { useContext } from 'react';
import { AuthContext } from '@context/modules/AuthContext.tsx';

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (context === null) {
    throw new Error('useAuth must be used within a AuthProvider');
  }
  return context;
};
