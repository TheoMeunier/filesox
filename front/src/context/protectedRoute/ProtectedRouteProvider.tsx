import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '@context/hooks/useAuth.tsx';

export function ProtectedRouteProvider() {
  const { token } = useAuth();

  if (!token) {
    return <Navigate replace to="/login" />;
  }

  return (
    <>
      <Outlet />
    </>
  );
}
