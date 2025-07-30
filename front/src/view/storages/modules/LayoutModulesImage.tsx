import {useEffect, useState} from "react";
import {FileType} from "@/types/api/storageType.ts";
import {useAxios} from "@config/axios.ts";

export function LayoutModules({file, height = 48, width = 48}: { file: FileType, height: number | string, width: number | string }) {
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
                width={width}
                height={height}
            />
        ) : file.icon === "file" ? (
                <img src={`images/file-icon.png`}  className="object-cover" alt="file-icon.png" width={width} height={height}/>
            )
            : (
                <img src={`images/${file.icon}-icon.png`} alt={`${file.icon}-icon.png`} width={width} height={height}/>
            )}
    </div>
}