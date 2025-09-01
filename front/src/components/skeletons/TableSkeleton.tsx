import {
  Table,
  TableCell,
  TableHead,
  TableRow,
} from '@components/modules/Table.tsx';
import Skeleton from '@components/modules/Skeleton.tsx';

export default function TableSkeleton({ col }: { col: number }) {
  return (
    <Table>
      <TableHead>
        <TableRow>
          {Array.from({ length: col }).map((_, index) => (
            <TableCell key={index}>
              <Skeleton />
            </TableCell>
          ))}
        </TableRow>
      </TableHead>
      <tbody>
        {Array.from({ length: 8 }).map((_, index) => (
          <TableRow key={index}>
            {Array.from({ length: col }).map((_, index) => (
              <TableCell key={index}>
                <Skeleton />
              </TableCell>
            ))}
          </TableRow>
        ))}
      </tbody>
    </Table>
  );
}
