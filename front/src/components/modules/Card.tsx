import {ReactNode} from "react";

export function Card({children}: {children: ReactNode}) {
  return (
    <div className="border border-gray-200 rounded-md shadow-xs">
        {children}
    </div>
  );
}

export function CardBody({children}: {children: ReactNode}) {
  return (
    <div className="p-4">
        {children}
    </div>
  );
}