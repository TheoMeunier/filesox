import {ReactNode} from "react";

export function Table({children}: { children: ReactNode }) {
  return <div className="shadow-sm border rounded-lg overflow-x-auto">
      <table className="w-full table-auto text-sm text-left">
          {children}
      </table>
  </div>
}

export function TableHead({children}: { children: ReactNode }) {
  return <thead className="bg-gray-50 text-gray-600 font-medium border-b">
      {children}
  </thead>
}

export function TableHeader({children}: { children: ReactNode }) {

    return <th className="py-3 px-6"> {children} </th>
}

export function TableRow({children}: { children: ReactNode }) {

    return <tr>
        {children}
    </tr>
}

export function TableBody({children}: { children: ReactNode }) {

    return <tbody className="text-gray-600 divide-y">
        {children}
    </tbody>
}

export function TableCell({children, height = "py-4"}: { children: ReactNode, height?: string }) {

    return <td className={`px-6 ${height} whitespace-nowrap`}>
        {children}
    </td>
}

export function TableNoData({text, colspan = 8}: { text: string, colspan: number }) {

    return <tr>
        <td colSpan={colspan} className="p-6 text-center">{text}</td>
    </tr>
}