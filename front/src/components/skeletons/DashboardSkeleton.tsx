import { useTranslation } from 'react-i18next';
import Skeleton from '@components/modules/Skeleton.tsx';

export default function DashboardSkeleton() {
  const { t } = useTranslation();

  return (
    <div>
      <div>
        <h1 className="text-2xl font-bold text-gray-800">
          {t('title.folders')}
        </h1>
        <hr className="mb-4" />

        <div className="flex flex-wrap items-center gap-5">
          {Array.from({ length: 8 }).map((_, index) => (
            <div
              key={index}
              tabIndex={0}
              className={`min-w-full md:min-w-72 flex gap-3 items-center px-4 py-2 rounded-lg border border-gray-200 cursor-pointer shadow-md bg-white text-black'}`}
            >
              <Skeleton className="h-12 w-12 rounded-full" />
              <Skeleton className="h-4 w-3/4" />
            </div>
          ))}
        </div>
      </div>
      <div className="mt-7">
        <h1 className="text-2xl font-bold text-gray-800">{t('title.files')}</h1>
        <hr className="mb-4" />

        <div className="flex flex-wrap items-center gap-5">
          {Array.from({ length: 8 }).map((_, index) => (
            <div
              key={index}
              tabIndex={0}
              className={`min-w-full md:min-w-72 flex gap-3 items-center px-4 py-2 rounded-lg border border-gray-200 cursor-pointer shadow-md bg-white text-black'}`}
            >
              <Skeleton className="h-12 w-12 rounded-full" />
              <Skeleton className="h-4 w-3/4" />
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}
