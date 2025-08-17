import { CurrentPathContext } from '@context/modules/CurrentPathContext.tsx';
import { useContext } from 'react';

export const useCurrentPath = () => {
  const context = useContext(CurrentPathContext);

  if (!context) {
    throw new Error('useCurrentPath must be used within a CurrentPathProvider');
  }

  return context;
};
