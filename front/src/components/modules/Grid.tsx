import {RowProps} from "@/types/components/grid.ts";

export function Row({ children, cols = 2  }: RowProps) {
    return <div className={`grid grid-${cols} gap-4`} >{children}</div>;
}