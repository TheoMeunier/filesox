import {Table, TableBody, TableCell, TableHead, TableHeader, TableRow} from "@components/modules/Table.tsx";
import {useQuery} from "react-query";
import {Pagination} from "@components/modules/Pagination.tsx";
import {useState} from "react";
import {Pill} from "@components/modules/Pill.tsx";
import {useAxios} from "@config/axios.ts";
import {logsProfileSchemaType} from "@/types/api/userType.ts";
import {Loader} from "@components/modules/Loader/Loader.tsx";

export function ProfileLog() {
    const API = useAxios()
    const [page , setPage] = useState(1)
    const {data, isLoading} = useQuery(
        ['logs', page],
        async () => {
            const response = await API.get('/profile/logs?page=' + page)
            return logsProfileSchemaType.parse(response.data)
        },
    );

    if (isLoading) {
        return <Loader/>;
    }

    return <div className="px-7 py-4">
        <Table>
            <TableHead>
                <TableRow>
                    <TableHeader>Subject</TableHeader>
                    <TableHeader>Action</TableHeader>
                    <TableHeader>Created At</TableHeader>
                </TableRow>
            </TableHead>
            <TableBody>
                {data && data.data.map((log) =>
                    <TableRow key={log.id}>
                        <TableCell>{log.subject}</TableCell>
                        <TableCell>
                            <Pill type={log.action}>{log.action}</Pill>
                        </TableCell>
                        <TableCell>{log.created_at}</TableCell>
                    </TableRow>
                )}
            </TableBody>
        </Table>

        {data &&
            <Pagination
                from={data.from}
                to={data.to}
                currentPage={data.current_page}
                totalPage={data.total_pages}
                onPageChange={(p) => {setPage(p)}}
            />
        }
    </div>;
}