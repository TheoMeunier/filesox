import {useState} from "react";

export const usePagination = () => {
    const [pages, setPages] = useState<(number | string)[]>([])

    const arrayNumberPage = (datas: { from: number; to: number; total_pages: number }) => {
        const arrayPage: (number | string)[] = []

        arrayPage.push(1)

        if (datas.from > 2) {
            arrayPage.push('...');
        }


        for (let i = datas.from; i <= datas.to; i++) {
            if (i !== 1 && i !== datas.total_pages) {
                arrayPage.push(i)
            }
        }


        if (datas.to < datas.total_pages - 1) {
            arrayPage.push('...');
        }


        if (datas.total_pages !== 1) {
            arrayPage.push(datas.total_pages);
        }

        return setPages(arrayPage)
    }

    return {pages, arrayNumberPage}
}