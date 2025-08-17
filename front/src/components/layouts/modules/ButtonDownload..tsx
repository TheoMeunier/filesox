import { Download } from 'lucide-react';
import { ButtonIcon } from '../../modules/Button.tsx';
import { useStorage } from '@hooks/useStorage.ts';

export function ButtonDownload() {
  const { isFolder, getPathOrName } = useStorage();

  const downloadInSameWindow = () => {
    const url = isFolder()
      ? `/api/download${getPathOrName()}`
      : `/api/download/${getPathOrName()}`;
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
