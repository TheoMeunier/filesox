import {LoaderCircle, Search} from "lucide-react";
import {useTranslation} from "react-i18next";
import {ChangeEvent, useState} from "react";
import {useQueryClient} from "react-query";
import {useSearchStorageApi} from "@/api/storageApi.ts";

export default function () {
    const queryClient = useQueryClient()
    const {t} = useTranslation()
    const [search, setSearch] = useState('')

    const {isLoading} = useSearchStorageApi(search)

    const handleSearch = (e: ChangeEvent<HTMLInputElement>) => {
        const value = e.target.value;
        setSearch(value);

        if (value.length <= 2) {
            queryClient.invalidateQueries('storage');
        }
    }

    return (
        <div className="relative ml-[6.5rem]">
            <div className="absolute inset-y-0 left-0 pl-3 flex items-center pointer-events-none">
                {isLoading ?
                    <LoaderCircle  className="animate-spin text-indigo-500"/>:
                    <Search size={20} strokeWidth={1.75} className="text-gray-400"/>
                }
            </div>
            <input
                type="text"
                value={search}
                onChange={handleSearch}
                placeholder={t('input.placeholder.search')}
                className="block w-full rounded-md border-0 py-1.5 pl-10 text-gray-900 shadow-sm ring-1 ring-inset ring-gray-300 placeholder:text-gray-400 focus:ring-2 focus:ring-inset focus:ring-indigo-600 sm:text-sm sm:leading-6"
            />
        </div>
    )
}