import { ReactNode } from 'react';

export function Pill({
  children,
  type,
}: {
  children: ReactNode;
  type: string;
}) {
  const className = getClass(type);

  return (
    <button className={`${className} px-2 py-0.5 rounded-sm border`}>
      {children}
    </button>
  );
}

function getClass(type: string) {
  if (type === 'info' || type === 'UPDATE') {
    return 'bg-indigo-100 text-indigo-800 border-indigo-300';
  } else if (type === 'danger' || type === 'DELETE') {
    return 'bg-red-100 text-red-500 border-red-300';
  } else if (type === 'success' || type === 'CREATE') {
    return 'bg-green-100 text-green-500 border-green-300';
  } else if (type === 'warning' || type === 'updated') {
    return 'bg-yellow-100 text-yellow-500 border-yellow-300';
  } else {
    return 'bg-gray-100 text-gray-800';
  }
}
