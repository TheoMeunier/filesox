import { LayoutList, LayoutTemplate } from 'lucide-react';
import { ButtonIcon } from '../../modules/Button.tsx';
import { useUserStore } from '@/stores/useUserStore.ts';
import { useAxios } from '@config/axios.ts';
import { useMutation } from '@tanstack/react-query';

export function ButtonLayout() {
  const { user, setUser } = useUserStore();
  const API = useAxios();

  const { mutate } = useMutation({
    mutationFn: async ({ layout }: { layout: boolean }) => {
      await API.post('/profile/update', {
        name: user!.username,
        email: user!.email,
        layout: layout,
      });
    },
    onSuccess: () => {
      setUser({ ...user!, layout: !user!.layout });
    },
  });

  const handleClickLayout = (e: MouseEvent) => {
    e.preventDefault();

    mutate({
      layout: !user!.layout,
    });
  };

  return (
    <>
      <ButtonIcon
        svg={user!.layout ? LayoutTemplate : LayoutList}
        title="Switch template"
        onClick={handleClickLayout}
      />
    </>
  );
}
