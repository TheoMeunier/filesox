import axios from 'axios';
import { useAuth } from '@context/hooks/useAuth.tsx';

export enum AuthEnum {
  TOKEN = 'token',
  REFRESH_TOKEN = 'refresh_token',
}

export const useAxios = () => {
  const { setAllTokens, logout } = useAuth();

  const token = localStorage.getItem(AuthEnum.TOKEN);

  const axiosInstance = axios.create({
    baseURL: '/api',
    headers: {
      Authorization: `Bearer ${token}`,
    },
  });

  axiosInstance.defaults.headers.post['Content-Type'] = 'application/json';

  axiosInstance.interceptors.request.use(
    (config) => {
      if (token) {
        config.headers.Authorization = `Bearer ${token}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );

  axiosInstance.interceptors.response.use(
    (res) => res,
    async (error) => {
      const baseReq = error.config;

      if (error.response && error.response.status === 401 && !baseReq._retry) {
        baseReq._retry = true;

        try {
          const response = await axios.post('/api/auth/refresh', {
            refresh_token: localStorage.getItem(AuthEnum.REFRESH_TOKEN),
          });

          if (response.data.token == null) {
            logout();
            return Promise.reject(error);
          }

          setAllTokens(response.data.token, response.data.refresh_token);

          baseReq.headers.Authorization = `Bearer ${response.data.token}`;

          return axios(baseReq);
        } catch (e) {
          logout();
          return Promise.reject(error);
        }
      }

      return Promise.reject(error);
    }
  );

  return axiosInstance;
};
