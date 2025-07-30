import {useCallback, useMemo} from "react";
import {useTranslation} from "react-i18next";

export function Pagination({currentPage, totalPage, onPageChange}: {
    currentPage: number,
    totalPage: number,
    onPageChange: (page: number) => void
}) {
    const {t} = useTranslation()

    const generatePaginationArray = useCallback((start: number, end: number) => {
        return Array.from({ length: end - start + 1 }, (_, i) => start + i);
    }, []);

    const pages = useMemo(() => {
        const maxVisiblePages = 5;
        let paginationArray: (number | string)[] = [];

        if (totalPage <= maxVisiblePages) {
            paginationArray = generatePaginationArray(1, totalPage);
        } else {
            if (currentPage <= 3) {
                paginationArray = [...generatePaginationArray(1, 4), "...", totalPage];
            } else if (currentPage >= totalPage - 2) {
                paginationArray = [1, "...", ...generatePaginationArray(totalPage - 3, totalPage)];
            } else {
                paginationArray = [1, "...", currentPage - 1, currentPage, currentPage + 1, "...", totalPage];
            }
        }

        return paginationArray;
    }, [currentPage, totalPage, generatePaginationArray]);

    const handlePageChange = useCallback(
        (page: number) => {
            if (page >= 1 && page <= totalPage) {
                onPageChange(page);
            }
        },
        [totalPage, onPageChange]
    );

    if (totalPage <= 1) return null;

    return <>
        {totalPage > 1 && (
            <div className="max-w-screen-xl mx-auto mt-12 px-4 text-gray-600 md:px-8">
                <div className="hidden items-center justify-between sm:flex" aria-label="Pagination">
                    <a onClick={currentPage > 1 ? () => handlePageChange(currentPage - 1) : undefined}
                       className={`${currentPage > 1 ? 'cursor-pointer hover:text-indigo-600 flex items-center gap-x-2' : 'text-gray-400 flex items-center gap-x-2'} `}>
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"
                             className="w-5 h-5">
                            <path fillRule="evenodd"
                                  d="M18 10a.75.75 0 01-.75.75H4.66l2.1 1.95a.75.75 0 11-1.02 1.1l-3.5-3.25a.75.75 0 010-1.1l3.5-3.25a.75.75 0 111.02 1.1l-2.1 1.95h12.59A.75.75 0 0118 10z"
                                  clipRule="evenodd"/>
                        </svg>
                        {t('title.pagination.previous')}
                    </a>

                    <ul className="flex items-center gap-1">
                        {
                            pages.map((item, idx) => (
                                <li key={idx} className="text-sm">
                                    {
                                        item === "..." ? (
                                            <div>
                                                {item}
                                            </div>
                                        ) : (

                                            <a
                                                onClick={currentPage !== item ? () => handlePageChange(Number(item)) : undefined}
                                                aria-current={currentPage === item ? "page" : false}
                                                className={`cursor-pointer px-3 py-2 rounded-lg duration-150 hover:text-indigo-600 hover:bg-indigo-50 ${currentPage === item ? "cursor-default bg-indigo-50 text-indigo-600 font-medium" : ""}`}
                                            >
                                                {item}
                                            </a>
                                        )
                                    }
                                </li>
                            ))
                        }
                    </ul>

                    <a onClick={currentPage < totalPage ? () => handlePageChange(currentPage + 1) : undefined}
                       className={`${currentPage < totalPage ? 'cursor-pointer hover:text-indigo-600 flex items-center gap-x-2' : 'text-gray-400 flex items-center gap-x-2'} `}>
                        {t('title.pagination.next')}
                        <svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 20 20" fill="currentColor"
                             className="w-5 h-5">
                            <path fillRule="evenodd"
                                  d="M2 10a.75.75 0 01.75-.75h12.59l-2.1-1.95a.75.75 0 111.02-1.1l3.5 3.25a.75.75 0 010 1.1l-3.5 3.25a.75.75 0 11-1.02-1.1l2.1-1.95H2.75A.75.75 0 012 10z"
                                  clipRule="evenodd"/>
                        </svg>
                    </a>
                </div>
                <div className="flex items-center justify-between text-sm text-gray-600 font-medium sm:hidden">
                    <a onClick={() => handlePageChange(currentPage - 1)}
                       className="px-4 py-2 border rounded-lg duration-150 hover:bg-gray-50">
                        {t('title.pagination.previous')}
                    </a>
                    <div className="font-medium">
                        Page {currentPage} of {pages.length}
                    </div>
                    <a onClick={() => handlePageChange(currentPage + 1)}
                       className="px-4 py-2 border rounded-lg duration-150 hover:bg-gray-50">
                        {t('title.pagination.next')}
                    </a>
                </div>
            </div>
        )}
    </>
}