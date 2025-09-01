import { twMerge } from 'tailwind-merge';

interface SkeletonProps {
  className?: string;
}

export default function Skeleton({ className }: SkeletonProps) {
  return (
    <div
      className={twMerge(
        'h-4 bg-gray-200 rounded w-3/4 animate-pulse ',
        className
      )}
    ></div>
  );
}
