import {useEffect, useState} from "react";
import {FileType} from "@/types/api/storageType.ts";
import {useAxios} from "@config/axios.ts";

export function LayoutModules({file}: { file: FileType }) {
    const API = useAxios()
    const [imageUrl, setImageUrl] = useState<{ [key: string]: any }>({});

    useEffect(() => {
            const fetchImages = async () => {
                try {
                    const urls: Record<string, any> = {};
                    const type = file.type === 'image/svg+xml' ? 'text' : 'blob';

                    if (file.icon === 'image') {
                        const response = await API.post("/images", {
                            path: file.id,
                            type: file.type
                        }, {
                            responseType: type
                        });

                        if (file.type === 'image/svg+xml') {
                            const blob = new Blob([response.data], {type: 'image/svg+xml'});
                            urls[file.id] = URL.createObjectURL(blob);
                        } else {
                            urls[file.id] = URL.createObjectURL(response.data);
                        }
                    }

                    setImageUrl(urls);
                } catch (error) {
                    console.error('Error on loading file : ', error);
                }
            }

            fetchImages();
        }, [file]
    )

    return <div>
        {file.icon === "image" ? (
            <img
                src={`${imageUrl[file.id]}`}
                alt={file.name}
                className="object-cover"
                width="48"
                height="48"
            />
        ) : file.icon === "file" ? (
                <img src={`images/file-icon.png`} alt="file-icon.png" width={48} height={48}/>
            )
            : (
                <img src={`images/${file.icon}-icon.png`} alt={`${file.icon}-icon.png`} width={48} height={48}/>
            )}
    </div>
}