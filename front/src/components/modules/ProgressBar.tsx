import {useProgressBar} from "@/stores/useProgressBar.ts";

export default function ProgressBar() {
    const {value} = useProgressBar()

    return <>
        <div className="z-50 absolute inset-0 p-0 -mt-3 w-full">
            <progress className="w-full h-2 transition duration-200" value={value} max={100}/>
        </div>
    </>
}