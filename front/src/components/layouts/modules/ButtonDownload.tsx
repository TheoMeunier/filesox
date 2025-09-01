import { Download } from 'lucide-react';
import { ButtonIcon } from '../../modules/Button.tsx';
import { useStorage } from '@hooks/useStorage.ts';

export function ButtonDownload() {
  const token = localStorage.getItem('token');
  const { isFolder, getPathOrName } = useStorage();

  const downloadInSameWindow = () => {
    const url = isFolder()
      ? `/api/download${getPathOrName()}?token=${token}`
      : `/api/download/${getPathOrName()}?token=${token}`;
    window.location.href = url;
  };

  return (
    <>
      <ButtonIcon
        svg={Download}
        title="Download"
        onClick={() => downloadInSameWindow()}
      />
    </>
  );
}
